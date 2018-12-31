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

(defn selections->constraints [db]
  (remove
    #(empty? (:values %))
    #{{:column "type" :values (into [] (:selected-types db))}
      {:column "school" :values (into [] (:selected-schools db))}
      {:column "timeframe" :values (into [] (:selected-timeframes db))}
      {:column "name" :values (into [] (:selected-concepts db))}}))


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

(reg-event-db
  ::query-succeeded
  (fn query-succeeded [db [_ paintings]]
    (-> db
     (assoc :query-loading false)
     (assoc :paintings paintings)
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

(reg-event-db
 ::update-selected-schools
 (fn update-selected-schools [db [_ selected-schools]]
   (assoc db :selected-schools selected-schools)))

(reg-event-db
 ::update-selected-timeframes
 (fn update-selected-timeframes [db [_ selected-timeframes]]
   (assoc db :selected-timeframes selected-timeframes)))

(reg-event-db
 ::update-selected-concepts
 (fn update-selected-concepts [db [_ selected-concept]]
   (update db :selected-concepts conj selected-concept)))

(reg-event-db
 ::remove-selected-concept
 (fn remove-selected-concept [db [_ selected-concept]]
   (do
     (js/console.log "remove-selected-concept received: " selected-concept)
     (js/console.log "remove-selected-concept: db was: " (:selected-concepts db))
     (update db :selected-concepts disj selected-concept))))

(reg-event-db
 ::selections-cleared
 (fn selections-cleared [db _]
  (-> db
     (assoc :selected-types #{})
     (assoc :selected-schools #{})
     (assoc :selected-timeframes #{})
     (assoc :selected-concepts #{}))))


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
