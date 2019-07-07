(ns landschaften.subs
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub]]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]
            [cljs.spec.alpha :as s]))



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


;(reg-sub
;  ::mobile-search?
;  (fn mobile-search? [db] ; show 'search' controls vs 'results' paintings on mobile
;    (:mobile-search? db)))


;; ------------------------------------------------------
;; Groups (selected constraints, retrieved paintings)
;; ------------------------------------------------------


;(reg-sub
;  ::save-group-popover-showing?
;  (fn save-group-popover-showing? [db _]
;    (:show-group-name-prompt? db)))


(reg-sub
  ::saved-groups
  (fn saved-groups [db _]
    {:post [(s/valid? map? %)]}
    (:saved-groups db)))


;(reg-sub
;  ::current-group
;  (fn [db _]
;    {:post [(s/valid? ::specs/group %)]}
;    (:current-group db)))
;
;
;(reg-sub
;  ::group-name
;  (fn group-name [db _]
;    (:current-group-name db)))

;
;;; ------------------------------------------------------
;;; Constraint choices
;;; ------------------------------------------------------
;
;
;(reg-sub
;  ::all-types
;  (fn all-types [db _]
;    (:all-types db)))
;
;
;(reg-sub
;  ::all-schools
;  (fn all-schools [db _]
;    (:all-schools db)))
;
;
;(reg-sub
;  ::all-timeframes
;  (fn all-timeframes [db _]
;    (:all-timeframes db)))
;
;
;(reg-sub
;  ::all-concepts
;  (fn all-concepts [db _]
;    (:all-concepts db)))
;
;
;(reg-sub
;  ::all-artists
;  (fn all-artists [db _]
;    (:all-artists db)))
;
;
;;; ------------------------------------------------------
;;; Currently selected constraints
;;; ------------------------------------------------------
;
;(reg-sub
;  ::types
;  (fn types [db _]
;    {:post [(s/valid? ::specs/type-constraints %)]}
;    (:selected-types db)))
;
;(reg-sub
;  ::school-constraints
;  (fn schools [db _]
;    {:post [(s/valid? ::specs/school-constraints %)]}
;    (:selected-schools db)))
;
;(reg-sub
;  ::timeframe-constraints
;  (fn timeframes [db _]
;    {:post [(s/valid? ::specs/timeframe-constraints %)]}
;    (:selected-timeframes db)))
;
;
;(reg-sub
;  ::concept-constraints
;  (fn concepts [db _]
;    {:post [(s/valid? ::specs/concept-constraints %)]}
;    (:selected-concepts db)))
;
;(reg-sub
;  ::artist-constraints
;  (fn artists [db _]
;    {:post [(s/valid? ::specs/artist-constraints %)]}
;    (:selected-artists db)))
;
;
;
;
;;; ------------------------------------------------------
;;; Groups (selected constraints, retrieved paintings)
;;; ------------------------------------------------------
;
;
;(reg-sub
;  ::save-group-popover-showing?
;  (fn save-group-popover-showing? [db _]
;    (:show-group-name-prompt? db)))
;
;
;(reg-sub
;  ::saved-groups
;  (fn saved-groups [db _]
;    {:post [(s/valid? map? %)]}
;    (:saved-groups db)))
;
;
;(reg-sub
;  ::current-group
;  (fn [db _]
;    {:post [(s/valid? ::specs/group %)]}
;    (:current-group db)))
;
;
;(reg-sub
;  ::group-name
;  (fn group-name [db _]
;    (:current-group-name db)))
;
;
;(reg-sub
;  ::paintings
;  (fn paintings [db _]
;    (let [current-paintings (:paintings db)]
;      (if current-paintings
;        (helpers/sort-by-author current-paintings)
;        []))))
;
;
;(reg-sub
;  ::query-loading?
;  (fn query-loading? [db _]
;    (:query-loading? db)))
;
;
;;; ------------------------------------------------------
;;; Examine
;;; ------------------------------------------------------
;
;(reg-sub
;  ::current-painting
;  (fn current-painting [db _]
;    {:post [(s/valid? (s/nilable ::specs/painting) %)]}
;    (:current-painting db)))
;
;(reg-sub
;  ::show-painting-modal?
;  (fn show-slideshow? [db _]
;    (:show-painting-modal? db)))
;
;(reg-sub
;  ::image-zoomed?
;  (fn image-zoomed? [db _]
;    (:image-zoomed? db)))


;;; ------------------------------------------------------
;;; Compare
;;; ------------------------------------------------------
;
;(reg-sub
;  ::compared-group-names
;  (fn compared-group-names [db _]
;    (:compared-group-names db)))
;
;;; should only allow two at a time
;(reg-sub
;  ::compared-groups
;  (fn current-painting [db _]
;    {:post [(s/valid? (s/coll-of ::specs/group) %)]}
;    (let [names (:compared-group-names db)
;          groups (:saved-groups db)]
;      (map #(get groups %) names))))
;
;
;(defn paintings->variance-data [paintings n-many certainty-above]
;  (->> (utils/paintings->concepts-frequencies paintings n-many certainty-above)
;       (into {})))
;
;
;(defn variance-calculation-ready-data [paintings]
;  (paintings->variance-data paintings 20 0.94))
;
;
;(reg-sub
;  ::variance
;  :<- [::compared-groups]
;  (fn variance [groups]
;    {:pre [(s/valid? (s/coll-of ::specs/group) groups)]}
;    (when (<= 2 (count groups))
;      (stats/variance
;        (variance-calculation-ready-data (:paintings (first groups)))
;        (variance-calculation-ready-data (:paintings (second groups)))))))
;
;
;(defn scramble-concept-names [painting]
;  {:post [(s/valid? ::specs/painting %)]}
;  (let [scramble (fn [concept-set]
;                   (->> concept-set
;                        (map (fn [concept]
;                               (update concept :name #(str "#" %))))
;                        (into #{})))]
;    (update painting :concepts scramble)))
;
;
;(>defn max-variance [paintings-1 paintings-2]
;  [::specs/paintings ::specs/paintings => ::stats/variance]
;  (stats/variance
;    ;; scramble to ensure that no concepts are shared in common
;    (variance-calculation-ready-data (map scramble-concept-names paintings-1))
;    (variance-calculation-ready-data paintings-2)))
;
;
;(reg-sub
;  ::max-variance
;  :<- [::compared-groups]
;  (fn max-variance-handler [groups]
;    {:pre [(s/valid? (s/coll-of ::specs/group) groups)]}
;    (when (<= 2 (count groups))
;      (max-variance (:paintings (first groups)) (:paintings (second groups))))))
;
;
;(reg-sub
;  ::show-n-chart-points
;  (fn show-n-chart-points [db _]
;    {:post [(int? %)]}
;    (:show-n-chart-points db)))
;
;
;(reg-sub
;  ::concept-certainty-above
;  (fn concept-certainty-above [db _]
;    {:post [(float? %)]}
;    (:concept-certainty-above db)))
