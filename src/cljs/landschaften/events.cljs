(ns landschaften.events
  (:require [re-frame.core :refer [reg-cofx inject-cofx after dispatch reg-event-db reg-sub reg-event-fx reg-fx]]
            [landschaften.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [ajax.core :refer [POST GET]]
            [landschaften.ui-specs :as ui-specs]
            [landschaften.specs :as specs]
            [landschaften.helpers :as helpers]
            [cljs.spec.alpha :as s]
            [landschaften.views.utils :as utils]
            [re-frame.core :as rf]
            [cljs.spec.test.alpha :as st]
            [ghostwheel.core
              :as g
              :refer [check >defn >defn- >fdef => | <- ?]]))

;; ------------------------------------------------------
;; Interceptors
;; ------------------------------------------------------


;; persist via local storage
;(def ->local-store (after todos->local-store))

(def ls-auth-key "session-info")


(>defn ->localstore! [state]
  [::specs/app-db => nil?]
  (do (.setItem js/localStorage ls-auth-key state)))

(reg-fx
  :persist-state
  ->localstore!)

;; rename to ':persisted-data'?
(reg-cofx
  :user-session
  (fn user-session [cofx _]
    (let [data-from-local-storage (cljs.reader/read-string
                                    (some->> (.getItem js/localStorage ls-auth-key)))]
        (assoc cofx :user-session data-from-local-storage))))


;; NOTE:
;; Since we use email-address strings in localStorage to
;; indicate an active session, and '@' is not valid Clojure,
;; we don't use (cljs.reader/read-string <localStorage content>).

;(defn ls->cljs [a-str]
;  (if (= "false" a-str) false a-str))

(reg-event-fx
  ::retrieve-user-session
  [(inject-cofx :user-session)]
  (fn retrieve-user-session [cofx [_ _]]
    (let [db (:db cofx)
          session (:user-session cofx)]
          ;session (cljs.reader/read-string (:user-session cofx))]
      (do
        (utils/log "retrieved session type: " (type session))
        {:db (assoc db :session session)}))))
      ;{:db (assoc db :session (ls->cljs session))})))

;; spec check
(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

;; now we create an interceptor using `after`
;(def check-spec-interceptor (after (partial check-and-throw :todomvc.db/db)))
(def spec? (after (partial check-and-throw ::specs/app-db)))

(def interceptors [spec?])



;; ------------------------------------------------------
;; High level events
;; ------------------------------------------------------



;; if persisted data, use that
;; else do init-db
;; (NOTE: currently persisting ENTIRE db)
(reg-event-fx
  ::initialize-app
  ;interceptors
  [(inject-cofx :user-session)] ;; an interceptor
  (fn initialize-app [cofx _]
    (let [persisted-db (:user-session cofx)]
      (do
        (utils/log "(keys (:saved-groups persisted-db): " (keys (:saved-groups persisted-db)))
        (if (s/valid? ::specs/app-db persisted-db)
          {:db persisted-db}
          {:db db/default-db})))))


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


;; not really using :navigate here, so don't include in app
(reg-event-db
  :navigate
  (fn [db [_ route]]
    db))
    ; (assoc db :route route)))


(reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))


;; putting in and pulling from local storage
;;



;; ------------------------------------------------------
;; Communicating with server
;; ------------------------------------------------------


;; not real error handling...
(defn default-error-handler [response]
  (utils/log "Encountered unexpected error: " response))


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


;; need failure handlers...
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


(reg-event-fx
  ::query-started
  (fn query [cofx [_ group-name]]
    (let [db (:db cofx)]
      {:db (assoc db :query-loading? true)
       :post-request
        {:uri "/query"
         :params {:constraints (->query-constraints db)}
         :handler #(dispatch [::query-succeeded % group-name])}})))


(declare toggle-save-group-popover-showing)
(declare save-current-group)


(defn on-query-succeeded [db paintings group-name]
  (let [db-with-query-results
          (-> db (assoc :query-loading? false)
                 (assoc-in db/path:current-paintings paintings)
                 (assoc :mobile-search? false) ; switch back to paintings
                 (assoc :examining? false))]
    (if group-name
      (-> db-with-query-results
          (toggle-save-group-popover-showing false) ;; hide the popover
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


;; handler's inner fns need to be separated out to produce a generic
;; 'works on any group' version

(reg-event-db
 ::update-selected-types
 interceptors
 (fn update-selected-types [db [_ selected-types]]
   ;(update-in db db/path:type-constraints conj selected-types)
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


(defn update-selected-concepts [db selected-concept]
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


;; override :current-group's name with the provided group-name
;; when we 'save the current group',
;; we use the user-provided name;
(defn save-current-group [db group-name]
  {:pre [(string? group-name)]} ;; group-name's should always be strings

  (let [current-group (:current-group db)
        updated-group (assoc current-group :group-name group-name)
        x (-> db
            (assoc-in [:saved-groups group-name] updated-group)
            (assoc :current-group updated-group))]
    (do
      (utils/log "save-current-group: returning x: " x)
      x)))


(defn bring-in-group [db group-name]
  {:pre [(string? group-name)]}
  (let [new-current-group (get (:saved-groups db) group-name)
        new-db (assoc db :current-group new-current-group)]

    (do
     (utils/log "bring-in-group group-name: " group-name)
     (utils/log "bring-in-group new-db: " new-db)
     (utils/log "bring-in-group new-current-group: " new-current-group)
     new-db)))


(reg-event-db
 ::switch-groups
 interceptors
 (fn switch-groups [db [_ destination-group-name]]
   (-> db
     (bring-in-group destination-group-name))))

;; ------------------------------------------------------
;; Comparing groups
;; ------------------------------------------------------


;; just returns (potentially updated) vector of names
(defn add-compare-group-name [group-names group-name]
  {:pre [(string? group-name)]
         ;(list? group-names)] ;; group-names is a list, why is this failing?!
   :post [(s/valid? (s/coll-of string?) %)
          (>= 2 (count %))]}
  (let [already-comparing? (boolean (some #{group-name} group-names))
        already-full? (boolean (= 2 (count group-names)))]

    (do
      (utils/log "already-comparing?: " already-comparing?)
      (utils/log "already-full?: " already-full?)
      (cond
        already-comparing? group-names
        ;; group-names MUST BE A LIST, not a vector,
        ;; we want to prepend the group-name
        already-full? (conj (drop-last group-names) group-name)
        :else (conj group-names group-name)))))


(reg-event-db
  ::add-compare-group-name
  interceptors
  (fn add-compare-group [db [_ group-name]]
    {:pre [(string? group-name)]}
    (let [group-names (:compared-group-names db)]
      (do
        (utils/log "add-compare-group-name called")
        (assoc
          db
          :compared-group-names
          (add-compare-group-name group-names group-name))))))


(reg-event-db
  ::remove-compare-group-name
  interceptors
  (fn remove-compare-group-name [db [_ group-name]]
    (do
      (utils/log "remove-compare-group-name called; group-name: " group-name)
      (assoc
        db
        :compared-group-names
        (remove #{group-name} (:compared-group-names db))))))


(reg-event-db
  ::comparisons-cleared
  interceptors
  (fn comparisons-cleared [db _]
     (assoc db :compared-group-names '())))


;; ------------------------------------------------------
;; Examining a single painting
;; ------------------------------------------------------

(reg-event-db
  ::done-button-clicked
  interceptors
  (fn done-button-clicked [db _]
    (-> db
      (assoc :examining? false)
      (assoc :show-slideshow? false))))


(reg-event-db
  ::painting-tile-clicked
  interceptors
  (fn painting-tile-clicked [db [_ painting]]
    (-> db
      (assoc :current-painting painting)
      (assoc :show-slideshow? true))))

(reg-event-db
  ::toggle-slideshow
  interceptors
  (fn toggle-slidehow [db _]
    (update db :show-slideshow? not)))


(reg-event-db
  ::toggle-image-zoomed
  interceptors
  (fn toggle-image-zoomed [db _]
    (update db ::db/image-zoomed? not)))

;; ------------------------------------------------------
;; Slidesow
;; ------------------------------------------------------

(>defn previous-slide [db]
  [::specs/app-db => ::specs/app-db]
  (let [paintings (helpers/sort-by-author
                    (get-in db db/path:current-paintings))
        current-painting (:current-painting db)
        prev-slide (or (last (take-while #(not= % current-painting) paintings))
                     (last paintings))]
    (assoc db :current-painting prev-slide)))

(reg-event-db
  ::go-to-previous-slide
  (fn [db _] (previous-slide db)))


(>defn next-slide [db]
  [::specs/app-db => ::specs/app-db]
  (let [paintings (helpers/sort-by-author
                    (get-in db db/path:current-paintings))
        current-painting (:current-painting db)
        next-slide (or (second (drop-while #(not= % current-painting) paintings))
                     (first paintings))]
    (assoc db :current-painting next-slide)))

(reg-event-db
  ::go-to-next-slide
  interceptors
  (fn [db _] (next-slide db)))

;#_(reg-event-db
;    ::go-to-next-slide
;    (fn next-slide [db]
;      (let [paintings (helpers/sort-by-author
;                        (get-in db db/path:current-paintings))
;            current-painting (:current-painting db)
;            next-slide (or (second (drop-while #(not= % current-painting) paintings))
;                         (first paintings))]
;        (do
;          (utils/log "next-slide: " next-slide)
;          (assoc db :current-painting next-slide)))))


;; don't need interceptors per se
;; just gw-spec an event handler,
;; then turn on g/check in the namespace


;; fails, and can see why in js console :-)
;(>defn addition [a b]
;  [pos-int? pos-int? => int? | #(> % a) #(> % b)]
;  (- a b))


;; passes and can see in js console :-)
;(>defn addition [a b]
;  [pos-int? pos-int? => int? | #(> % a) #(> % b)]
;  (+ a b))

;; make sure you have the following in cljs :compiler options
;:external-config {:ghostwheel {:check     true
;                               :outstrument true
;                               :num-tests 10}}
;(g/check)
(check)