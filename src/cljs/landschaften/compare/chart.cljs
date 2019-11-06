(ns landschaften.compare.chart
  (:require [reagent.core :as r]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.view-utils :as utils]
            [cljsjs.chartjs]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]))


;; ------------------------------------------------------
;; Charts for displaying data
;; - Chart.js radar charts
;; ------------------------------------------------------


(s/def ::chart-js-dataset
  (s/and
    (s/coll-of float?)
    vector?))


(s/def ::chart-js-labelset
  (s/and
    (s/coll-of string?)
    vector?))


(def color-1 "rgba(255, 99, 132, 0.3)")
(def color-2 "rgba(54, 162, 235, 0.3)")


(>defn chartjs-radar-chart [data-1 data-1-name data-2 data-2-name labels]
  [::chart-js-dataset string? ::chart-js-dataset string? ::chart-js-labelset => any?] ;; returns a js object
  (let [context (.getContext (.getElementById js/document "rev-chartjs") "2d")]
    (js/Chart. context
               (clj->js {:type "radar"
                         :data {:labels   labels
                                :datasets [{:data            data-1
                                            :label           data-1-name
                                            :backgroundColor color-1}
                                           {:data            data-2
                                            :label           data-2-name
                                            :backgroundColor color-2}]}}))))

(defn frequency-data? [xs]
  (and (seq? xs)
       (string? (first (first xs)))))


(defn get-labels [frequency-data-1 frequency-data-2]
  {:pre [(frequency-data? frequency-data-1)
         (frequency-data? frequency-data-2)]}
  (into [] (into #{} (flatten [(map first frequency-data-1)
                               (map first frequency-data-2)]))))

;; more like: "order by provided list of labels, adding absent labels"
(defn add-missing-labels
  "Given frequency-data and an ordered collection of labels,
    returns frequency-data in same order as labels.

  If label not already in frequency-data,
    adds label with default 0.0 value."
  [labels frequency-data]
  {:pre [(frequency-data? frequency-data) (vector? labels)]}
  [frequency-data? (s/coll-of string?) => frequency-data?]
  (let [get-existing-label (fn [label]
                             (first (filter #(= label (first %)) frequency-data)))]
    (mapv #(or (get-existing-label %) % 0.0)
          labels)))


;; TODO: cleanup, simplify into smaller, well-named functions
;; NOV 2019: THIS CALCULATION REQUIRES DATA TOO LARGE TO HOLD IN CLIENT
;; AND SO MUCH BE CALCULATED ON THE SERVER
(defn compared-groups->radar-chart-data! []
  {:data-1 [30.33 60.60 90.90]
   :data-1-name "dogs"
   :data-2 [35.33 40.60 80.80]
   :data-2-name "cats"
   :labels ["cuteness" "sociability" "klimacrisis"]})


#_(>defn compared-groups->radar-chart-data!
    "Returns the two groups' data in a Chart.js Radar-chart-friendly form.

  First retrieves the frequencies of concepts with certainty-above.
  Datasets' numbers will be arranged in same order as labelset.
  "
    [group-1 group-2 n-many certainty-above]
    [::specs/group ::specs/group int? float? => map?]
    (let [as-frequency-data   (fn [paintings]
                                (utils/paintings->concepts-frequencies paintings n-many certainty-above))
          group-1-paintings   (:paintings group-1)
          group-2-paintings   (:paintings group-2)
          group-1-frequencies (as-frequency-data group-1-paintings)
          group-2-frequencies (as-frequency-data group-2-paintings)
          ;; only want labels for concepts with 'certainty-above'
          labels              (get-labels group-1-frequencies group-2-frequencies)
          as-dataset          (fn [frequency-data total] (->> frequency-data
                                                              (add-missing-labels labels)
                                                              (mapv #(utils/count->percent % total))
                                                              (mapv second)))]
      (do
        (js/console.log "(as-dataset group-1-frequencies (count group-1-paintings)): " (as-dataset group-1-frequencies (count group-1-paintings)))
        {:data-1      (as-dataset group-1-frequencies (count group-1-paintings))
         :data-1-name (:group-name group-1)
         :data-2      (as-dataset group-2-frequencies (count group-2-paintings))
         :data-2-name (:group-name group-2)
         :labels      labels})))


(defn radar-chart [{:keys [data-1 data-1-name data-2 data-2-name labels]}]
  (r/create-class
    {:component-did-mount #(chartjs-radar-chart data-1 data-1-name data-2 data-2-name labels)
     :display-name        "chartjs-component"
     :reagent-render      (fn []
                            [:canvas {:id "rev-chartjs"
                                      :width "500"
                                      :height "400"}])}))


