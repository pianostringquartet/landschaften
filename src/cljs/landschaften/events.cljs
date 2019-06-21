(ns landschaften.events
  (:require [re-frame.core :refer [reg-cofx inject-cofx after dispatch reg-event-db reg-sub reg-event-fx reg-fx]]
            [landschaften.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [ajax.core :refer [POST GET]]
            [landschaften.ui-specs :as ui-specs]
            [landschaften.specs :as specs]
            [landschaften.helpers :as helpers]
            [cljs.spec.alpha :as s]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]))


;; ------------------------------------------------------
;; Interceptors
;; ------------------------------------------------------

(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def spec? (after (partial check-and-throw ::specs/app-db)))

(def interceptors [spec?])


;; ------------------------------------------------------
;; Persisting data
;; ------------------------------------------------------

(def ls-auth-key "landschaften-session-data")

(>defn ->localstore! [state]
  [::specs/app-db => nil?]
  (do
    (.setItem js/localStorage ls-auth-key state)))

(reg-fx
  :persist-state
  ->localstore!)

(reg-cofx
  :user-session
  (fn user-session [cofx _]
    (let [data-from-local-storage (cljs.reader/read-string
                                    (some->> (.getItem js/localStorage ls-auth-key)))]
        (assoc cofx :user-session data-from-local-storage))))


;; ------------------------------------------------------
;; HTTP Requests
;; ------------------------------------------------------


;; TODO: Handle subset of errors; log rest to external logs
(defn default-error-handler [response]
  (helpers/log "Encountered unexpected error: " response))

(reg-fx
  :post-request
  (fn post-request-handler
    [{uri :uri params :params handler :handler error-handler :error-handler
      :or {error-handler default-error-handler}}]
    (POST uri {:params params :handler handler :error-handler error-handler})))


(reg-fx
  :get-request
  (fn get-request-handler
    [{uri :uri handler :handler error-handler :error-handler
      :or {error-handler default-error-handler}}]
    (GET uri {:handler handler :error-handler error-handler})))


;; ------------------------------------------------------
;; Initializing the app
;; ------------------------------------------------------

(reg-event-fx
  ::initialize-app
  [(inject-cofx :user-session)] ; an interceptor
  (fn initialize-app [cofx _]
    (let [persisted-db (:user-session cofx)]
      (if (s/valid? ::specs/app-db persisted-db)
        {:db persisted-db}
        {:db db/default-db}))))


(reg-event-fx
  ::retrieve-artists-names
  (fn query [cofx _]
    {:get-request {:uri "/artists"
                   :handler #(dispatch [::artists-names-retrieved %])}}))


(reg-event-fx
  ::retrieve-concepts
  (fn query [cofx _]
    {:get-request {:uri "/concepts"
                   :handler #(dispatch [::concepts-retrieved %])}}))


(reg-event-db
  ::concepts-retrieved
  interceptors
  (fn concepts-retrieved [db [_ artists]]
    (assoc db :all-concepts (into #{} artists))))


(reg-event-db
  ::artists-names-retrieved
  interceptors
  (fn artists-names-retrieved [db [_ artists]]
    (assoc db :all-artists (into #{} artists))))


;; ------------------------------------------------------
;; UI-related events
;; ------------------------------------------------------

(reg-event-db
  ::mode-changed
  interceptors
  (fn mode-changed [db [_ new-mode]]
    {:pre [(s/valid? ::ui-specs/mode new-mode)]}
    (assoc db :current-mode new-mode)))


(reg-event-db
  ::toggle-mobile-search
  interceptors
  (fn mobile-search-toggled [db]
    (update db :mobile-search? not)))


;; ------------------------------------------------------
;; Querying server for paintings
;; ------------------------------------------------------


(defn ->query-constraints
  "Put group's constraints in backend API's expected format."
  [db]
  (remove
   #(empty? (:values %))
   #{{:column "type" :values (into [] (get-in db db/path:type-constraints))}
     {:column "school" :values (into [] (get-in db db/path:school-constraints))}
     {:column "timeframe" :values (into [] (get-in db db/path:timeframe-constraints))}
     {:column "author" :values (into [] (get-in db db/path:artist-constraints))}
     {:column "name" :values (into [] (get-in db db/path:concept-constraints))}}))


(>defn on-query-started [db]
  [::specs/app-db => ::specs/app-db]
  (-> db
      (assoc :query-loading? true)
      (assoc :show-group-name-prompt? false)))


(reg-event-fx
  ::query-started
  (fn query [cofx [_ group-name]]
    (let [db (:db cofx)]
      {:db (on-query-started db)
       :post-request
        {:uri "/query"
         :params {:constraints (->query-constraints db)}
         :handler #(dispatch [::query-succeeded % group-name])}})))


;; need to think about how to add a default group, make it a compared group etc.
;(reg-event-fx
;  ::add-default-group
;  (fn add-default-group [cofx [_ default-group]]
;    (let [db (:db cofx)]
;      (if-not (:current-group db)
;       {:db (assoc db :current-group default-group)
;        :dispatch [::query-started (:group-name default-group)]}
;       db))))

(declare add-compare-group-name)




(reg-event-fx
  ::add-default-group
  (fn add-default-group [cofx [_ default-group]]
    (let [db (:db cofx)
          update-current-group (fn [x] (if-not (:current-group x)
                                         (assoc x :current-group default-group)
                                         x))]
      ;(if-not (:current-group db)
        {:db (-> db
               (update-current-group)
               (assoc :compared-group-names (add-compare-group-name (:compared-group-names db)
                                                                    (:group-name default-group))))

         ;; query-started logic always brings in result as new current-group?
         ;; another issue: query-started is dispatched, but we

         :dispatch [::query-started (:group-name default-group)]})))
        ;db)))

;; ah, -- maybe?
;; need to do two searches IN ORDER,
;; not dispatched at same time;
;; don't know when server will come back with them?


(declare toggle-save-group-popover-showing save-current-group)

(defn on-query-succeeded [db paintings group-name]
  (let [db-with-query-results
          (-> db (assoc :query-loading? false)
                 (assoc-in db/path:current-paintings paintings)
                 (assoc :mobile-search? false) ; switch back to paintings
                 (assoc :examining? false))]
    (if group-name
      (-> db-with-query-results
          (toggle-save-group-popover-showing false)
          (save-current-group group-name))
      db-with-query-results)))


(reg-event-fx
  ::query-succeeded
  interceptors
  (fn query-succeeded [cofx [_ paintings group-name]]
    (let [db (on-query-succeeded (:db cofx) paintings group-name)]
        {:persist-state db
         :db db})))


;; ------------------------------------------------------
;; Updating constraints
;; ------------------------------------------------------


(reg-event-db
 ::update-selected-types
 interceptors
 (fn update-selected-types [db [_ selected-types]]
   (assoc-in db db/path:type-constraints selected-types)))


(reg-event-db
 ::update-selected-schools
 interceptors
 (fn update-selected-schools [db [_ selected-schools]]
   (assoc-in db db/path:school-constraints selected-schools)))


(reg-event-db
 ::update-selected-timeframes
 interceptors
 (fn update-selected-timeframes [db [_ selected-timeframes]]
   (assoc-in db db/path:timeframe-constraints selected-timeframes)))


(>defn update-selected-concepts [db selected-concept]
  [::specs/app-db string? => ::specs/app-db]
  (update-in db db/path:concept-constraints conj selected-concept))

(reg-event-db
 ::update-selected-concepts
 interceptors
 (fn [db [_ selected-concept]]
   (update-selected-concepts db selected-concept)))

(defn remove-selected-concept [db selected-concept]
  (update-in db db/path:concept-constraints disj selected-concept))


(reg-event-db
 ::remove-selected-concept
 interceptors
 (fn [db [_ selected-concept]]
   (remove-selected-concept db selected-concept)))


(reg-event-db
  ::toggle-concept-selection
  interceptors
  (fn toggle-concept-selection [db [_ concept]]
    (let [currently-selected-concepts (get-in db db/path:concept-constraints)]
      (if (contains? currently-selected-concepts concept)
        (remove-selected-concept db concept)
        (update-selected-concepts db concept)))))


(reg-event-db
 ::update-selected-artists
 interceptors
 (fn update-selected-artists [db [_ selected-artist]]
   (update-in db db/path:artist-constraints conj selected-artist)))


(reg-event-db
 ::remove-selected-artist
 interceptors
 (fn remove-selected-artist [db [_ selected-artist]]
   (update-in db db/path:artist-constraints disj selected-artist)))


(reg-event-db
 ::selections-cleared
 interceptors
 (fn selections-cleared [db _]
  (-> db
     (assoc-in db/path:type-constraints #{})
     (assoc-in db/path:school-constraints #{})
     (assoc-in db/path:timeframe-constraints #{})
     (assoc-in db/path:concept-constraints #{})
     (assoc-in db/path:artist-constraints #{}))))


;; ------------------------------------------------------
;; Updating groups
;; ------------------------------------------------------


(defn toggle-save-group-popover-showing [db showing?]
  (assoc db :show-group-name-prompt? showing?))


(reg-event-db
  ::hide-save-group-popover
  interceptors
  (fn hide-save-group-popover [db _]
    (toggle-save-group-popover-showing db false)))


(reg-event-db
  ::show-save-group-popover
  interceptors
  (fn show-save-group-popover [db _]
    (toggle-save-group-popover-showing db true)))


(defn save-current-group [db group-name]
  {:pre [(string? group-name)]}
  (let [current-group (:current-group db)
        updated-group (assoc current-group :group-name group-name)]
    (-> db
       (assoc-in [:saved-groups group-name] updated-group)
       (assoc :current-group updated-group))))


(defn bring-in-group [db group-name]
  (let [new-current-group (get (:saved-groups db) group-name)]
    (assoc db :current-group new-current-group)))


(reg-event-db
 ::switch-groups
 interceptors
 (fn switch-groups [db [_ destination-group-name]]
   (-> db
     (bring-in-group destination-group-name))))



;; ------------------------------------------------------
;; Comparing groups
;; ------------------------------------------------------


(defn add-compare-group-name [group-names group-name]
  {:pre [(string? group-name)]
   :post [(s/valid? (s/coll-of string?) %)
          (>= 2 (count %))]}
  (let [already-comparing? (boolean (some #{group-name} group-names))
        already-full? (boolean (= 2 (count group-names)))]
      (cond
        already-comparing? group-names
        already-full? (conj (drop-last group-names) group-name)
        :else (conj group-names group-name))))


(reg-event-db
  ::add-compare-group-name
  interceptors
  (fn add-compare-group [db [_ group-name]]
    {:pre [(string? group-name)]}
    (let [group-names (:compared-group-names db)]
      (assoc db :compared-group-names (add-compare-group-name group-names group-name)))))


(>defn remove-compare-group-name [db group-name]
  [::specs/app-db string? => ::specs/app-db]
  (assoc db :compared-group-names (remove #{group-name} (:compared-group-names db))))


(reg-event-db
  ::remove-compare-group-name
  interceptors
  (fn remove-compare-group-name-handler [db [_ group-name]]
    (remove-compare-group-name db group-name)))


(reg-event-db
  ::comparisons-cleared
  interceptors
  (fn comparisons-cleared [db _]
     (assoc db :compared-group-names '())))


(>defn remove-group [db group-name]
  [::specs/app-db string? => ::specs/app-db]
  (let [old-saved-groups (:saved-groups db)
        updated-saved-groups (dissoc old-saved-groups group-name)]
    (assoc db :saved-groups updated-saved-groups)))


(reg-event-fx
  ::remove-group
  interceptors
  (fn remove-group-handler [cofx [_ group-name]]
    (let [db (:db cofx)
          updated-db (remove-compare-group-name
                       (remove-group db group-name)
                       group-name)]
      {:db updated-db
       :persist-state updated-db})))



;; ------------------------------------------------------
;; Examining a single painting
;; ------------------------------------------------------


(reg-event-db
  ::done-button-clicked
  interceptors
  (fn done-button-clicked [db _]
    (-> db
        (assoc :examining? false)
        (assoc :show-painting-modal? false))))


(reg-event-db
  ::painting-tile-clicked
  interceptors
  (fn painting-tile-clicked [db [_ painting]]
    (-> db
        (assoc :current-painting painting)
        (assoc :show-painting-modal? true))))


(reg-event-db
  ::toggle-painting-modal
  interceptors
  (fn toggle-painting-modal [db _]
    (update db :show-painting-modal? not)))


(reg-event-db
  ::toggle-image-zoomed
  interceptors
  (fn toggle-image-zoomed [db _]
    (update db :image-zoomed? not)))


;; ------------------------------------------------------
;; Navigating through paintings' details (modals)
;; ------------------------------------------------------


(>defn previous-painting [db]
  [::specs/app-db => ::specs/app-db]
  (let [paintings (helpers/sort-by-author
                    (get-in db db/path:current-paintings))
        current-painting (:current-painting db)
        prev-slide (or (last (take-while #(not= % current-painting) paintings))
                     (last paintings))]
    (assoc db :current-painting prev-slide)))


(reg-event-db
  ::go-to-previous-painting
  (fn [db _] (previous-painting db)))


(>defn next-painting [db]
  [::specs/app-db => ::specs/app-db]
  (let [paintings (helpers/sort-by-author
                    (get-in db db/path:current-paintings))
        current-painting (:current-painting db)
        next-slide (or (second (drop-while #(not= % current-painting) paintings))
                     (first paintings))]
    (assoc db :current-painting next-slide)))


(reg-event-db
  ::go-to-next-painting
  interceptors
  (fn [db _] (next-painting db)))


(check)