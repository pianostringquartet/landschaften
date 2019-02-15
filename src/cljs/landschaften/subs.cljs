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
  (fn paintings [db _]
    (:paintings db)))

(reg-sub
  ::default-painting
  (fn default-painting [db _]
    (:default-painting db)))

(reg-sub
  ::current-painting
  (fn current-painting [db _]
    (:current-painting db)))

(reg-sub
  ::types
  (fn types [db _]
    (:types db)))

(reg-sub
  ::schools
  (fn schools [db _]
    (:schools db)))

(reg-sub
  ::timeframes
  (fn timeframes [db _]
    (:timeframes db)))

(reg-sub
  ::concepts
  (fn concepts [db _]
    (:concepts db)))

(reg-sub
  ::selected-types
  (fn selected-types [db _]
    (:selected-types db)))

(reg-sub
  ::selected-schools
  (fn selected-schools [db _]
    (:selected-schools db)))

(reg-sub
  ::selected-timeframes
  (fn selected-timeframes [db _]
    (:selected-timeframes db)))

(reg-sub
  ::selected-concepts
  (fn selected-concepts [db _]
    (:selected-concepts db)))


(reg-sub
 ::show-max?
 (fn show-max? [db _]
   (:show-max? db)))

;; want a second-layer sub
;; takes paintings in app-db,
;; looks at their 20 high-certainty concepts,
;; and returns those concepts names and how often they appear

;; line/column-chart wants vector of vectors like:
; [[x-col-name y-col-name]
;  [x-col-val-1 y-col-val-1]
;  [x-col-val-2 y-col-val-2]]

;; set of maps like #{{:concept-name "some-str" :frequency-of-concept "some-str"}}
