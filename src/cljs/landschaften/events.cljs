(ns landschaften.events
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub reg-event-fx reg-fx dispatch]]
            [landschaften.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [ajax.core :refer [POST GET]]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.ui-specs :as ui-specs]))


;; ------------------------------------------------------
;; High level events
;; ------------------------------------------------------


(reg-event-db
 ::initialize-db
 (fn initialize-db [_ _]
   db/default-db))


(reg-event-db
  ::mode-changed
  (fn mode-changed [db [_ new-mode]]
    {:pre [(s/valid? ::ui-specs/mode new-mode)]}
    (assoc db :current-mode new-mode)))


(reg-event-db
  :navigate
  (fn [db [_ route]]
    (assoc db :route route)))


(reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))



;; ------------------------------------------------------
;; Communicating with server
;; ------------------------------------------------------


;; taken from cardy
(defn default-error-handler [response]
  (js/console.log "Encountered unexpected error: " response))


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
  (fn concepts-retrieved [db [_ artists]]
    (assoc db :all-concepts (into #{} artists))))


(reg-event-db
  ::artists-names-retrieved
  (fn artists-names-retrieved [db [_ artists]]
    (assoc db :all-artists (into #{} artists))))


(defn ->query-constraints
  "Put group's constraints in backend API's expected format."
  [db]
  ;{:pre [(s/valid? ::specs/group group)]}
  (remove
   #(empty? (:values %))
   #{{:column "type" :values (into [] (get-in db db/path:type-constraints))}
     {:column "school" :values (into [] (get-in db db/path:school-constraints))}
     {:column "timeframe" :values (into [] (get-in db db/path:timeframe-constraints))}
     {:column "author" :values (into [] (get-in db db/path:artist-constraints))}
     {:column "name" :values (into [] (get-in db db/path:concept-constraints))}}))


(reg-event-fx
  ::query-started
  (fn query [cofx _]
    (let [db (:db cofx)]
          ;constraints (if-let [group (:current-group db)]
          ;              (->query-constraints group)
          ;              #{})] ;; if no group, then no constraints
      {:db (assoc db :query-loading true)
       :post-request
        {:uri "/query"
         :params {:constraints (->query-constraints db)}
         ;:params {:constraints constraints}
         :handler #(dispatch [::query-succeeded %])}})))

(reg-event-db
  ::query-succeeded
  (fn query-succeeded [db [_ paintings]]
      (-> db
        (assoc :query-loading false)
        (assoc-in db/path:current-paintings paintings)
        (assoc :current-painting nil))))


;; ------------------------------------------------------
;; Updating constraints
;; ------------------------------------------------------


;; handler's inner fns need to be separated out to produce a generic
;; 'works on any group' version

(reg-event-db
 ::update-selected-types
 (fn-traced update-selected-types [db [_ selected-types]]
   (assoc-in db db/path:type-constraints selected-types)))


(reg-event-db
 ::update-selected-schools
 (fn update-selected-schools [db [_ selected-schools]]
   (assoc-in db db/path:school-constraints selected-schools)))


(reg-event-db
 ::update-selected-timeframes
 (fn update-selected-timeframes [db [_ selected-timeframes]]
   (assoc-in db db/path:timeframe-constraints selected-timeframes)))


(reg-event-db
 ::update-selected-concepts
 (fn update-selected-concepts [db [_ selected-concept]]
   (update-in db db/path:concept-constraints conj selected-concept)))


(reg-event-db
 ::remove-selected-concept
 (fn remove-selected-concept [db [_ selected-concept]]
   (update-in db db/path:concept-constraints disj selected-concept)))


(reg-event-db
 ::update-selected-artists
 (fn update-selected-artists [db [_ selected-artist]]
   (update-in db db/path:artist-constraints conj selected-artist)))


(reg-event-db
 ::remove-selected-artist
 (fn remove-selected-artist [db [_ selected-artist]]
   (update-in db db/path:artist-constraints disj selected-artist)))


(reg-event-db
 ::selections-cleared
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

(defn save-current-group [db]
  (let [current-group (:current-group db)]
    (assoc-in db [:saved-groups (keyword (:group-name current-group))] current-group)))


(reg-event-db
 ::group-saved
 (fn group-saved [db [_ group-name]]
   (save-current-group db)))

(defn bring-in-group [db group-name]
  (let [new-current-group ((keyword group-name) (:saved-groups db))
        new-db (assoc db :current-group new-current-group)]
    (do
      (js/console.log "bring-in-group group-name: " group-name)
      (js/console.log "bring-in-group new-db: " new-db)
      (js/console.log "bring-in-group new-current-group: " new-current-group)
      new-db)))


;; when we switch groups,
;; prompt user (via dialogue) for name of group;
;; (if group already had name, then prefill the input slot with that name)
(reg-event-db
 ::switch-groups
 (fn switch-groups [db [_ destination-group-name]]
   (do
    (js/console.log "destination-group-name: " destination-group-name)
    (-> db
       (save-current-group) ;; add current group to group-history i.e. :other-groups
       ; then take destination group and make current group
       (bring-in-group destination-group-name)))))


;; ------------------------------------------------------
;; Examining a single painting
;; ------------------------------------------------------


;; when examine's done button is clicked,
;; we no longer have a 'current painting' that we're examining
(reg-event-db
  ::done-button-clicked
  (fn done-button-clicked [db _]
    (assoc db :current-painting nil)))


(reg-event-db
  ::painting-tile-clicked
  (fn painting-tile-clicked [db [_ painting]]
    (assoc db :current-painting painting)))


(reg-event-db
  ::show-max-image
  (fn show-max-image [db _]
    (assoc db :show-max? true)))


(reg-event-db
  ::hide-max-image
  (fn hide-max-image [db _]
    (assoc db :show-max? false)))
