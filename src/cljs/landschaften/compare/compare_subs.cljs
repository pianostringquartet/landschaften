(ns landschaften.compare.compare-subs
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub]]
            [cljs.spec.alpha :as s]
            [landschaften.view-utils :as utils]
            [landschaften.specs :as specs]
            [landschaften.compare.variance :as stats]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]
            [landschaften.compare.chart :as chart]
            [landschaften.db :as db]))


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
  [any? => (s/coll-of ::specs/group)]
  (let [names (:compared-group-names db)
        groups (:saved-groups db)]
      (map #(get groups %) names)))


(reg-sub
  ::compared-groups
  (fn current-painting [db _]
      (compared-groups-sub! db)))


(defn paintings->variance-data [paintings n-many certainty-above]
  (->> (utils/paintings->concepts-frequencies paintings n-many certainty-above)
       (into {})))


(defn variance-calculation-ready-data [paintings]
  (paintings->variance-data paintings 20 0.94))


(reg-sub
  ::variance
  :<- [::compared-groups]
  (fn variance [groups]
    {:pre [(s/valid? (s/coll-of ::specs/group) groups)]}
    (when (<= 2 (count groups))
      (stats/variance
        (variance-calculation-ready-data (:paintings (first groups)))
        (variance-calculation-ready-data (:paintings (second groups)))))))


(defn scramble-concept-names [painting]
  {:post [(s/valid? ::specs/painting %)]}
  (let [scramble (fn [concept-set]
                   (->> concept-set
                        (map (fn [concept]
                               (update concept :name #(str "#" %))))
                        (into #{})))]
    (update painting :concepts scramble)))


(defn max-variance [paintings-1 paintings-2]
       (stats/variance
         ;; scramble to ensure that no concepts are shared in common
         (variance-calculation-ready-data (map scramble-concept-names paintings-1))
         (variance-calculation-ready-data paintings-2)))


(reg-sub
  ::max-variance
  :<- [::compared-groups]
  (fn max-variance-handler [groups]
    {:pre [(s/valid? (s/coll-of ::specs/group) groups)]}
    (when (<= 2 (count groups))
      (max-variance (:paintings (first groups)) (:paintings (second groups))))))

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


;;; NEW SUBS:

;; takes in variance, max-variance
;; replaces the calcs done in the similarity-measurement component
;; if variance etc. not used separately,
;; then remove and just keep this
;; HOLLOWED OUT IN PREP FOR SERVER SIDE CALCULATING
(reg-sub
  ::similarity
  :<- [::variance]
  :<- [::max-variance]
  (fn similarity-measurement [[variance max-variance] _]
    66.88
    #_(do
        (js/console.log "variance: " variance)
        (js/console.log "max-variance: " max-variance)
        (when-not (or (zero? variance) (zero? max-variance))
          (let [as-percent (* 100 (/ variance max-variance))]
            (- 100 as-percent))))))


(reg-sub
  ::radar-chart-data
  :<- [::compared-groups]
  (fn radar-chart-data [compared-groups _]
    (when (and
            (not (empty? compared-groups))
            (<= 2 (count compared-groups)))
      (chart/compared-groups->radar-chart-data!))
    #_(chart/compared-groups->radar-chart-data!
        (first compared-groups)
        (second compared-groups)
        db/SHOW-N-CHARTPOINTS
        db/CONCEPT-CERTAINTY-ABOVE)))
