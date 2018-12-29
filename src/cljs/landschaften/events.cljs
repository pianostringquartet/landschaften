(ns landschaften.events
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub]]
            [landschaften.db :as db]))


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

;; Action handlers

; (update {:a #{3 1 5}} :a conj 988)

; (conj #{3 1 5} 999)

(reg-event-db
  :navigate
  (fn [db [_ route]]
    (assoc db :route route)))

(reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))
