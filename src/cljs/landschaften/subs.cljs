(ns landschaften.subs
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub]]))

;; Source data from the database

;;subscriptions

(reg-sub
  :route
  (fn [db _]
    (-> db :route)))

(reg-sub
  :page
  :<- [:route]
  (fn [route _]
    (-> route :data :name)))

(reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(reg-sub
  ::paintings
  (fn retrieve-paintings [db _]
    (:paintings db)))

(reg-sub
  ::types
  (fn retrieve-types [db _]
    (:types db)))

(reg-sub
  ::schools
  (fn retrieve-schools [db _]
    (:schools db)))

(reg-sub
  ::timeframes
  (fn retrieve-timeframes [db _]
    (:timeframes db)))
