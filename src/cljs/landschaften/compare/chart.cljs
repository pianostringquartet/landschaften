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

(def RADAR-CHARTPOINTS-COUNT 16)
(def COLOR-1 "rgba(255, 99, 132, 0.3)")
(def COLOR-2 "rgba(54, 162, 235, 0.3)")


(s/def ::chart-js-dataset
  (s/and
    (s/coll-of float?)
    vector?))


(s/def ::chart-js-labelset
  (s/and
    (s/coll-of string?)
    vector?))


(>defn chartjs-radar-chart [data-1 data-1-name data-2 data-2-name labels]
  [::chart-js-dataset string? ::chart-js-dataset string? ::chart-js-labelset => any?] ; returns a js object
  (let [context (.getContext (.getElementById js/document "rev-chartjs") "2d")]
    (js/Chart. context
               (clj->js {:type "radar"
                         :data {:labels   labels
                                :datasets [{:data            data-1
                                            :label           data-1-name
                                            :backgroundColor COLOR-1}
                                           {:data            data-2
                                            :label           data-2-name
                                            :backgroundColor COLOR-2}]}}))))


(defn get-labels [frequency-data-1 frequency-data-2]
  (into [] (into #{} (flatten [(map first frequency-data-1)
                               (map first frequency-data-2)]))))

;; more like: "order by provided list of labels, adding absent labels"
(>defn add-missing-labels
  "Given frequency-data and an ordered collection of labels,
    returns frequency-data in same order as labels.

  If label not already in frequency-data,
    adds label with default 0.0 value."
  [labels frequency-data]
  [(s/coll-of string?) any? => vector?]
  (let [get-existing-label (fn [label]
                             (first (filter #(= label (first %)) frequency-data)))]
    (mapv #(or (get-existing-label %) % 0.0)
          labels)))


(>defn compared-groups->radar-chart-data
  "Returns the two groups' data in a Chart.js Radar-chart-friendly form.

  First retrieves the frequencies of concepts with certainty-above.
  Datasets' numbers will be arranged in same order as labelset.
  "
  [group-1 group-2]
  [::specs/group ::specs/group => map?]
  (let [group-1-frequencies (take RADAR-CHARTPOINTS-COUNT (:concept-frequencies group-1))
        group-2-frequencies (take RADAR-CHARTPOINTS-COUNT (:concept-frequencies group-2))
        labels              (get-labels group-1-frequencies group-2-frequencies)
        as-dataset          (fn [frequency-data] (->> frequency-data
                                                   (add-missing-labels labels)
                                                   (mapv #(utils/count->percent %))
                                                   (mapv second)))]
      {:data-1      (as-dataset group-1-frequencies)
       :data-1-name (:group-name group-1)
       :data-2      (as-dataset group-2-frequencies)
       :data-2-name (:group-name group-2)
       :labels      labels}))


(defn radar-chart [{:keys [data-1 data-1-name data-2 data-2-name labels]}]
  (r/create-class
    {:component-did-mount #(chartjs-radar-chart data-1 data-1-name data-2 data-2-name labels)
     :display-name        "chartjs-component"
     :reagent-render      (fn []
                            [:canvas {:id "rev-chartjs"
                                      :width "500"
                                      :height "400"}])}))
