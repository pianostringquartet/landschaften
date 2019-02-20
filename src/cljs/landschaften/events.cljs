(ns landschaften.events
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub reg-event-fx reg-fx dispatch]]
            [landschaften.db :as db]
            ; [ajax.core :refer [POST]]
            [ajax.core :refer [POST GET]]))

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

(defn selections->constraints [db]
  (remove
    #(empty? (:values %))
    #{{:column "type" :values (into [] (:selected-types db))}
      {:column "school" :values (into [] (:selected-schools db))}
      {:column "timeframe" :values (into [] (:selected-timeframes db))}
      {:column "author" :values (into [] (:selected-artists db))}
      {:column "name" :values (into [] (:selected-concepts db))}}))

;; need failure handlers...
(reg-event-fx
  ::retrieve-artists-names
  (fn query [cofx _]
    (let [db (:db cofx)]
      {:get-request {:uri "/artists"
                     :handler #(dispatch [::artists-names-retrieved %])}})))

(reg-event-fx
  ::retrieve-concepts
  (fn query [cofx _]
    (let [db (:db cofx)]
      {:get-request {:uri "/concepts"
                     :handler #(dispatch [::concepts-retrieved %])}})))

(reg-event-fx
  ::query
  (fn query [cofx _]
    (let [db (:db cofx)]
      {:db (assoc db :query-loading true)
       :post-request
        {:uri "/query"
         :params {:constraints (selections->constraints db)}
         :handler #(dispatch [::query-succeeded %])}})))
         ;; use default error handler otherwise for now

;; assumes paintings is list of paintings satisfying ::painting spec
; (defn get-paintings-concepts [paintings]
;   (into #{} (map :name (mapcat :concepts paintings))))

(reg-event-db
  ::query-succeeded
  (fn query-succeeded [db [_ paintings]]
    (-> db
     (assoc :query-loading false)
     (assoc :paintings paintings)
     ; (assoc :concepts (get-paintings-concepts paintings))
     (assoc :current-painting nil))))

(reg-event-db
 ::initialize-db
 (fn initialize-db [_ _]
   db/default-db))

(reg-event-db
 ::update-selected-types
 (fn update-selected-types [db [_ selected-types]]
   (do
     (js/console.log "update-selected-types received: " selected-types)
     (js/console.log "update-selected-types received, type: " (type selected-types))
     (js/console.log "update-selected-types: db was: " (:selected-types db))
     (assoc db :selected-types selected-types)
     (assoc db :selected-types selected-types))))


;;
; (defn update-group [group])

;; could do assoc-in
;; but don't want to hardcode path each time
;; ... use fn to introduce layer of indirection

;; where new-schools
; (defn update-schools [group new-schools]
;   (assoc group :schools new-schools))
;
; (defn update-group-key [group new-schools]
;   (assoc group :schools new-schools))

(reg-event-db
 ::update-selected-schools
 (fn update-selected-schools [db [_ selected-schools]]
   (assoc-in db [:current-group :schools] selected-schools)))
   ; (assoc db :selected-schools selected-schools)))

(reg-event-db
 ::update-selected-timeframes
 (fn update-selected-timeframes [db [_ selected-timeframes]]
   (assoc-in db [:current-group :timeframes] selected-timeframes)))
   ; (assoc db :selected-timeframes selected-timeframes)))

(reg-event-db
 ::update-selected-concepts
 (fn update-selected-concepts [db [_ selected-concept]]
     (update-in db [:current-group :concepts] conj selected-concept)))
   ; (update db :selected-concepts conj selected-concept)))

(reg-event-db
 ::remove-selected-concept
 (fn remove-selected-concept [db [_ selected-concept]]
   (do
     (js/console.log "remove-selected-concept received: " selected-concept)
     (js/console.log "remove-selected-concept: db was: " (:selected-concepts db))
     ; (update db :selected-concepts disj selected-concept))))
     (update-in db [:current-painting :concepts] disj selected-concept))))

(reg-event-db
 ::concepts-retrieved
 (fn concepts-retrieved [db [_ artists]]
   (do
       (js/console.log "concepts-retrieved: artists was " (into #{} artists))
       ; (assoc db :concepts (into #{} artists)))))
       (assoc db :all-concepts (into #{} artists)))))

(reg-event-db
 ::artists-names-retrieved
 (fn artists-names-retrieved [db [_ artists]]
   (do
       (js/console.log "artists-names-retrieved: artists was " (into #{} artists))
       ; (assoc db :artists (into #{} artists)))))
       (assoc db :all-artists (into #{} artists)))))

(reg-event-db
 ::update-selected-artists
 (fn update-selected-artists [db [_ selected-artist]]
   (do
     (js/console.log "update-selected-artists: selected-artist was " selected-artist)
     ; (update db :selected-artists conj selected-artist))))
     (update-in db [:current-group :artists] conj selected-artist))))

(reg-event-db
 ::remove-selected-artist
 (fn remove-selected-artist [db [_ selected-artist]]
   (do
     (js/console.log "remove-selected-artist received: " selected-artist)
     (js/console.log "remove-selected-artist: db was: " (:selected-artists db))
     ; (update db :selected-artists disj selected-artist))))
     (update-in db [:current-group :artist] disj selected-artist))))

(reg-event-db
 ::selections-cleared
 (fn selections-cleared [db _]
  (-> db
     (assoc-in [:current-group :types] #{})
     (assoc-in [:current-group :schools] #{})
     (assoc-in [:current-group :timeframes] #{})
     (assoc-in [:current-group :concepts] #{})
     (assoc-in [:current-group :artists] #{}))))


;; collected current selections into
;; - check that satisfies ::group
;; - user should provide actual name
; (defn extract-current-group [db group-name]
;   {(keyword group-name)
;    (select-keys db
;      [:paintings
;       :selected-types
;       :selected-schools
;       :selected-timeframes
;       :selected-concepts
;       :selected-artists])})

;;
; (defn save-current-group [db group-name]
;   (let [current-group (extract-current-group db group-name)]
;     (update db :other-groups conj current-group)))


; (defn save-current-group [db group-name]
;   (update db :saved-groups conj {(keyword group-name) (:current-group db)}))

(defn save-current-group [db]
  (let [current-group (:current-group db)]
    (assoc-in db [:saved-groups (keyword (:group-name current-group))] current-group)))

;; just take current keys
(reg-event-db
 ::group-saved
 (fn group-saved [db [_ group-name]]
   ; (save-current-group db group-name)))
   (save-current-group db)))

(defn bring-in-group [db group-name]
  ; (merge db group))
  (let [new-current-group ((keyword group-name) (:saved-groups db))
        new-db
          (assoc db :current-group new-current-group)]

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

;; group should satisfy ::group
;; returns db
; (defn bring-in-group [db group]
;   (-> db
;      (assoc :paintings (::paintings group))
;      (assoc :selected-types (::selected-types group))
;      (assoc :selected-schools (::selected-schools group))
;      (assoc :selected-timeframes (::selected-timeframes group))
;      (assoc :selected-concepts (::selected-concepts group))
;      (assoc :selected-artists (::selected-artists group))))
; (defn bring-in-group [db group]
;   (merge db group))





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



;; Action handlers

; (update {:a #{3 1 5}} :a conj 988)
; (update {:a #{3 1 5}} :a disj 5)

; (conj #{3 1 5} 999)

; (disj #{3 1 5 9} 9)

(reg-event-db
  :navigate
  (fn [db [_ route]]
    (assoc db :route route)))

(reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))
