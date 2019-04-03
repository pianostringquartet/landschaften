(ns landschaften.subs
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub]]
            [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.db :as db]
            [landschaften.helpers :as helpers]
            [landschaften.views.utils :as utils]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [landschaften.views.graph :as graph]
            [landschaften.views.stats :as stats]))



;; ------------------------------------------------------
;; Subscriptions
;; - node graph for sourcing from db
;; - should source directly (not change db data etc.)
;; ------------------------------------------------------


;; ------------------------------------------------------
;; High level subs
;; ------------------------------------------------------

(reg-sub
  ::current-mode
  (fn current-mode [db _]
    (:current-mode db)))

(reg-sub
  ::mobile?
  (fn mobile? [db]
    (< js/window.innerWidth 800)))

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
  (fn saved-groups [db _]
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

(reg-sub
  ::paintings
  (fn paintings [db _]
    {:post [(s/valid? ::specs/paintings %)]}
    (helpers/sort-by-author (get-in db db/path:current-paintings))))


(reg-sub
  ::query-loading?
  (fn query-loading? [db _]
    (:query-loading? db)))

;; ------------------------------------------------------
;; Examine
;; ------------------------------------------------------


;; current painting must be able to be nil
;; i.e. sometimes we don't have a current painting
(reg-sub
  ::current-painting
  (fn current-painting [db _]
    {:post [(s/valid? (s/nilable ::specs/painting) %)]}
    (:current-painting db)))


(reg-sub
  ::examining?
  (fn examining? [db _]
    (:examining? db)))

;; i.e. show details
(reg-sub
  ::show-slideshow?
  (fn show-slideshow? [db _]
    (do
      (utils/log "(:show-slideshow? db): " (:show-slideshow? db))
      (:show-slideshow? db))))

(reg-sub
  ::image-zoomed?
  (fn image-zoomed? [db _]
    (do
      (utils/log "image-zoomed?: " (::db/image-zoomed? db))
      (::db/image-zoomed? db))))



;; ------------------------------------------------------
;; Slideshow
;; ------------------------------------------------------


;; current slide should just be the current painting
;(reg-sub
;  ::current-slide)

;; don't need "paintings vs. slideshow paintings"

;; try namespace qualified keywords
;(reg-sub
;  :<- [:paintings]
;  ::slideshow-paintings
;  (fn slideshow-paintings [paintings _]
;    {:post [(s/valid? (s/nilable ::specs/paintings) %)]}
;    (::db/slideshow-paintings db)))



;; ------------------------------------------------------
;; Compare
;; ------------------------------------------------------

(reg-sub
  ::compared-group-names
  (fn compared-group-names [db _]
    (:compared-group-names db)))

;; look at the names in the db, then just

;; should only allow two at a time
(reg-sub
  ::compared-groups
  (fn current-painting [db _]
    {:post [(s/valid? (s/coll-of ::specs/group) %)]}
    (let [names (:compared-group-names db)
          groups (:saved-groups db)]
      (map #(get groups %) names))))



(defn error-ready-data [group]
  (graph/paintings->error-data
    (:paintings group)
    20
    0.94))


(reg-sub
  ::error-rate
  :<- [::compared-groups]
  (fn error-rate [groups]
    {:pre [(s/valid? (s/coll-of ::specs/group) groups)]}
    (when (<= 2 (count groups))
      (stats/error-rate
        (error-ready-data (first groups))
        (error-ready-data (second groups))))))


(reg-sub
  ::show-n-chart-points
  (fn show-n-chart-points [db _]
    {:post [(int? %)]}
    (:show-n-chart-points db)))


(reg-sub
  ::concept-certainty-above
  (fn concept-certainty-above [db _]
    {:post [(float? %)]}
    (:concept-certainty-above db)))

