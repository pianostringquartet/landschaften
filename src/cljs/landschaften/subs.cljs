(ns landschaften.subs
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub]]))


;; ------------------------------------------------------
;; Subscriptions
;; - node graph for sourcing from db
;; - should source directly (not change db data etc.)
;; ------------------------------------------------------



;; ------------------------------------------------------
;; High level subs
;; ------------------------------------------------------


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
  ::current-mode
  (fn current-mode [db _]
    (:current-mode db)))


;; ------------------------------------------------------
;; Examine
;; ------------------------------------------------------


(reg-sub
  ::current-painting
  (fn current-painting [db _]
    (:current-painting db)))


(reg-sub
  ::show-max?
  (fn show-max? [db _]
    (:show-max? db)))


;; ------------------------------------------------------
;; Constraint choices
;; ------------------------------------------------------


(reg-sub
  ::all-types
  (fn all-types [db _]
    (:all-types db)))


(reg-sub
  ::all-schools
  (fn all-schools [db _]
    (:all-schools db)))


(reg-sub
  ::all-timeframes
  (fn all-timeframes [db _]
    (:all-timeframes db)))


(reg-sub
  ::all-concepts
  (fn all-concepts [db _]
    (:all-concepts db)))


(reg-sub
  ::all-artists
  (fn all-artists [db _]
    (:all-artists db)))



;; ------------------------------------------------------
;; Groups (selected constraints, retrieved paintings)
;; ------------------------------------------------------


(reg-sub
  ::saved-groups
  (fn saved-groups [db _]
    (or (:saved-groups db)
        #{})))


(reg-sub
  ::current-group
  (fn [db _]
    (let [g (:current-group db)]
      (do
        (js/console.log "reg-sub ::current-group: " g)
        g))))


(reg-sub
  ::paintings
  :<- [::current-group]
  (fn paintings [current-group _]
    (:paintings current-group)))


(reg-sub
  ::types
  :<- [::current-group]
  (fn types [current-group _]
    (:types current-group)))


(reg-sub
  ::schools
  :<- [::current-group]
  (fn schools [current-group _]
    (:schools current-group)))


(reg-sub
  ::timeframes
  :<- [::current-group]
  (fn timeframes [current-group _]
    (:timeframes current-group)))


(reg-sub
  ::concepts
  :<- [::current-group]
  (fn concepts [current-group _]
    :concepts current-group))


(reg-sub
  ::artists
  :<- [::current-group]
  (fn artists [current-group _]
    (:artists current-group)))


(reg-sub
  ::group-name
  :<- [::current-group]
  (fn group-name [current-group _]
    (:group-name current-group)))

