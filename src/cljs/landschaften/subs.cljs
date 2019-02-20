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
  ::current-group
  (fn [db _]
    (let [g (:current-group db)]
      (do
        (js/console.log "::current-group sub: " g)
        g))))

; (reg-sub
;   ::paintings
;   (fn paintings [db _]
;     (:paintings db)))
(reg-sub
  ::paintings
  :<- [::current-group]
  (fn paintings [current-group _]
    (:paintings current-group)))

(reg-sub
  ::default-painting
  (fn default-painting [db _]
    (:default-painting db)))

(reg-sub
  ::current-painting
  (fn current-painting [db _]
    (:current-painting db)))

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


;; for the concept typeahead
(reg-sub
  ::all-concepts
  (fn all-concepts [db _]
    (:all-concepts db)))

;; these artists is only for the artist-typeahead search suggestions
;; ... retrieved from backend
(reg-sub
  ::all-artists
  (fn all-artists [db _]
    (:all-artists db)))

(reg-sub
  ::types
  :<- [::current-group]
  (fn types [current-group _]
    (or (:types current-group)
      #{})))

(reg-sub
  ::schools
  :<- [::current-group]
  (fn schools [current-group _]
    (or (:schools current-group)
      #{})))

(reg-sub
  ::timeframes
  :<- [::current-group]
  (fn timeframes [current-group _]
    (or (:timeframes current-group)
      #{})))

(reg-sub
  ::concepts
  :<- [::current-group]
  (fn concepts [current-group _]
    (or (:concepts current-group)
      #{})))

;; i.e. selectd artists' NAMES
(reg-sub
  ::artists
  :<- [::current-group]
  (fn artists [current-group _]
    (or (:artists current-group)
      #{})))

(reg-sub
  ::saved-groups
  (fn saved-groups [db _]
    (or (:saved-groups db)
      #{})))

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
