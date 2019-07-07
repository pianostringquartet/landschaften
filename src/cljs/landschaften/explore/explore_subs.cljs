(ns landschaften.explore.explore-subs
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub]]
            [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.helpers :as helpers]))



(reg-sub
  ::mobile-search?
  (fn mobile-search? [db] ; show 'search' controls vs 'results' paintings on mobile
    (:mobile-search? db)))


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
;; Currently selected constraints
;; ------------------------------------------------------

(reg-sub
  ::types
  (fn types [db _]
    {:post [(s/valid? ::specs/type-constraints %)]}
    (:selected-types db)))

(reg-sub
  ::school-constraints
  (fn schools [db _]
    {:post [(s/valid? ::specs/school-constraints %)]}
    (:selected-schools db)))

(reg-sub
  ::timeframe-constraints
  (fn timeframes [db _]
    {:post [(s/valid? ::specs/timeframe-constraints %)]}
    (:selected-timeframes db)))


(reg-sub
  ::concept-constraints
  (fn concepts [db _]
    {:post [(s/valid? ::specs/concept-constraints %)]}
    (:selected-concepts db)))

(reg-sub
  ::artist-constraints
  (fn artists [db _]
    {:post [(s/valid? ::specs/artist-constraints %)]}
    (:selected-artists db)))




;;; ------------------------------------------------------
;;; Groups (selected constraints, retrieved paintings)
;;; ------------------------------------------------------


(reg-sub
  ::save-group-popover-showing?
  (fn save-group-popover-showing? [db _]
    (:show-group-name-prompt? db)))


(reg-sub
  ::group-name
  (fn group-name [db _]
    (:current-group-name db)))


(reg-sub
  ::paintings
  (fn paintings [db _]
    (let [current-paintings (:paintings db)]
      (if current-paintings
        (helpers/sort-by-author current-paintings)
        []))))


(reg-sub
  ::query-loading?
  (fn query-loading? [db _]
    (:query-loading? db)))


;; ------------------------------------------------------
;; Examine
;; ------------------------------------------------------

(reg-sub
  ::current-painting
  (fn current-painting [db _]
    {:post [(s/valid? (s/nilable ::specs/painting) %)]}
    (:current-painting db)))

(reg-sub
  ::show-painting-modal?
  (fn show-slideshow? [db _]
    (:show-painting-modal? db)))

(reg-sub
  ::image-zoomed?
  (fn image-zoomed? [db _]
    (:image-zoomed? db)))