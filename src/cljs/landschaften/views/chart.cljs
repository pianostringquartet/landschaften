(ns landschaften.views.chart
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.views.utils :as utils]
            [cljsjs.chartjs]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]))


(s/def ::chart-js-dataset
  (s/and
    (s/coll-of float?)
    vector?))

(s/def ::chart-js-labelset
  (s/and
    (s/coll-of string?)
    vector?))

(>defn show-radar-chart [data-1 data-1-name data-2 data-2-name labels]
  [::chart-js-dataset string? ::chart-js-dataset string? ::chart-js-labelset => any?] ;; returns a js object
  (let [context (.getContext (.getElementById js/document "rev-chartjs") "2d")]
    (js/Chart. context
               (clj->js {:type "radar"
                         :data {:labels   labels
                                :datasets [{:data            data-1
                                            :label           data-1-name
                                            :backgroundColor "rgba(255, 99, 132, 0.3)"}
                                           {:data            data-2
                                            :label           data-2-name
                                            :backgroundColor "rgba(54, 162, 235, 0.3)"}]}}))))


(defn frequency-data? [xs]
  (and (seq? xs)
       (string? (first (first xs)))))


(defn get-labels [frequency-data-1 frequency-data-2]
  {:pre [(frequency-data? frequency-data-1)
         (frequency-data? frequency-data-2)]}
  (into [] (into #{} (flatten [(map first frequency-data-1)
                               (map first frequency-data-2)]))))


(defn arrange-by-label
  "Given frequency-data and an ordered collection of labels,
    returns frequency-data in same order as labels.

  If label not already in frequency-data,
    adds label with default 0.0 value."
  [frequency-data labels]
  {:pre [(frequency-data? frequency-data) (vector? labels)]}
  [frequency-data? (s/coll-of string?) => frequency-data?]
  (let [get-existing-label (fn [label] (first (filter #(= label (first %)) frequency-data)))]
    (mapv #(or (get-existing-label %)
               [% 0.0])
          labels)))


(>defn compared-groups->radar-chart-data!
  "Returns the two groups' data in a Chart.js Radar-chart-friendly form.

  First retrieves the frequencies of concepts with certainty-above.

  Datasets' numbers are returned arranged in same order as labelset.
  "
  [group-1 group-2 n-many certainty-above]
  [::specs/group ::specs/group int? float? => map?]
  (let [concept-frequency->percent       (fn [[concept frequency] total]
                                           [concept (utils/->percent frequency total)])
        frequency-data                   (fn [paintings]
                                           (utils/paintings->concepts-frequencies paintings n-many certainty-above))
        group-1-frequencies              (frequency-data (:paintings group-1))
        group-2-frequencies              (frequency-data (:paintings group-2))
        labels                           (get-labels group-1-frequencies group-2-frequencies)
        arranged-frequencies-as-percents (fn [frequency-data paintings-total]
                                           (mapv #(concept-frequency->percent % paintings-total)
                                                 (arrange-by-label frequency-data labels)))]
    {:data-1      (mapv second
                        (arranged-frequencies-as-percents group-1-frequencies
                                                          (count (:paintings group-1)))) ;final-data-1
     :data-1-name (:group-name group-1)
     :data-2      (mapv second
                        (arranged-frequencies-as-percents group-2-frequencies
                                                          (count (:paintings group-2)))) ;final-data-1
     :data-2-name (:group-name group-2)
     :labels      labels}))


(defn radar-chart-component [{:keys [data-1 data-1-name data-2 data-2-name labels]}]
  (r/create-class
    {:component-did-mount #(show-radar-chart data-1 data-1-name data-2 data-2-name labels)
     :display-name        "chartjs-component"
     :reagent-render      (fn []
                            [:canvas {:id "rev-chartjs" :width "700" :height "380"}])}))

