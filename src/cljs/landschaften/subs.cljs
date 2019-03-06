(ns landschaften.subs
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub]]
            [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.db :as db]
            [landschaften.views.utils :as utils]
            [day8.re-frame.tracing :refer-macros [fn-traced]]))



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
    {:post [(s/valid? ::specs/painting %)]}
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
  ::save-group-popover-showing?
  (fn save-group-popover-showing? [db _]
    (:show-group-name-prompt? db)))


(reg-sub
  ::saved-groups
  (fn-traced saved-groups [db _]
    {:post [(s/valid? map? %)]}
    (:saved-groups db)))


(reg-sub
  ::current-group
  (fn [db _]
    {:post [(s/valid? ::specs/group %)]}
    (let [g (:current-group db)]
      (do
        (js/console.log "reg-sub ::current-group: " g)
        (js/console.log "(s/valid? ::specs/group g): " (s/explain-str ::specs/group g))
        g))))


(reg-sub
  ::group-name
  (fn group-name [db _]
    {:post [(s/valid? ::specs/group-name %)]}
    (get-in db db/path:current-group-name)))


(reg-sub
  ::paintings
  (fn paintings [db _]
    {:post [(s/valid? ::specs/paintings %)]}
    (get-in db db/path:current-paintings)))


(reg-sub
  ::types
  (fn types [db _]
    {:post [(s/valid? ::specs/type-constraints %)]}
    (get-in db db/path:type-constraints)))


(reg-sub
  ::school-constraints
  (fn schools [db _]
    {:post [(s/valid? ::specs/school-constraints %)]}
    (get-in db db/path:school-constraints db)))


(reg-sub
  ::timeframe-constraints
  (fn timeframes [db _]
    {:post [(s/valid? ::specs/timeframe-constraints %)]}
    (get-in db db/path:timeframe-constraints db)))


(reg-sub
  ::concept-constraints
  (fn concepts [db _]
    {:post [(s/valid? ::specs/concept-constraints %)]}
    (get-in db db/path:concept-constraints db)))


(reg-sub
  ::artist-constraints
  (fn artists [db _]
    {:post [(s/valid? ::specs/artist-constraints %)]}
    (get-in db db/path:artist-constraints db)))


