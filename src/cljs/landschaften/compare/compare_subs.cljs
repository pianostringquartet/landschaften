(ns landschaften.compare.compare-subs
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub]]
            [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.compare.variance :as stats]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]))


;; ------------------------------------------------------
;; Subscriptions
;; - node graph for sourcing from db
;; - should source directly (not change db data etc.)
;; ------------------------------------------------------


;; ------------------------------------------------------
;; Compare
;; ------------------------------------------------------

(reg-sub
  ::compared-group-names
  (fn compared-group-names [db _]
    (:compared-group-names db)))


;; should only allow two at a time
(>defn compared-groups-sub! [db]
  [any? => (s/nilable (s/coll-of ::specs/group))]
  (let [names  (:compared-group-names db)
        groups (:saved-groups db)]
    (map #(get groups %) names)))


(reg-sub
  ::compared-groups
  (fn current-painting [db _]
    (compared-groups-sub! db)))


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


(reg-sub
  ::variance
  :<- [::compared-groups]
  (fn variance [groups]
    {:pre [(s/valid? (s/coll-of ::specs/group) groups)]}
    (when (<= 2 (count groups))
      (stats/variance
        (into {} (:concept-frequencies (first groups)))
        (into {} (:concept-frequencies (second groups)))))))


(defn max-variance [group-1 group-2]
  (stats/variance
    ;; scramble to ensure that no concepts are shared in common
    (into {} (map (fn [[concept-name concept-value]] [(str concept-name "#") concept-value])
                  (:concept-frequencies group-1)))
    (into {} (:concept-frequencies group-2))))


(reg-sub
  ::max-variance
  :<- [::compared-groups]
  (fn max-variance-handler [groups]
    {:pre [(s/valid? (s/coll-of ::specs/group) groups)]}
    (do
      (js/console.log "::max-variance sub called, groups: " groups)
      (when (<= 2 (count groups))
        (max-variance (first groups) (second groups))))))


(reg-sub
  ::similarity
  :<- [::variance]
  :<- [::max-variance]
  (fn similarity-measurement [[variance max-variance] _]
    (when (and (some? variance) (some? max-variance))
      (let [as-percent (* 100 (/ variance max-variance))]
        (- 100 as-percent)))))