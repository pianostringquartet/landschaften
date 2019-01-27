(ns landschaften.views.graph
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [clojure.spec.alpha :as s]
            [landschaften.events :as events]
            [landschaften.specs :as specs]
            [landschaften.views.utils :as utils]
            [landschaften.subs :as subs]))

;; this component will actually be part of 'preview' screen/panel, later.

; (ns google-chart-example.core
;     (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; needed for async load of
(defonce google-chart-ready?
  (r/atom false))

;; asychronously initialize the Google Chart
(defonce initialize
  (do
    (println "google-chart: initialize called")
    (js/google.charts.load (clj->js {:packages ["corechart"]}))
    (js/google.charts.setOnLoadCallback
      (fn google-visualization-loaded []
        (do
          (println "google-visualization-loaded called")
          (reset! google-chart-ready? true))))))

;; not sure what map? and string? parts used for...
; (defn data->google-data-table [data]
;   (cond
;     ; (map? data) (js/google.visualization.DataTable. (clj->js data))
;     ; (string? data) (js/google.visualization.Query. data)
;     (seqable? data) (js/google.visualization.arrayToDataTable (clj->js data))))



(defn draw-google-chart [chart-type data options]
  [rc/box
   :child
    (if @google-chart-ready?
   ; (when @google-chart-ready?
     [:div
     ; [rc/box
      ; :child
       {:ref ;; what is this :ref, and how to make it work well with re-com?
        (fn [this]
          (when this
            (.draw (new (aget js/google.visualization chart-type) this)
                   ; (data->google-data-table data)
                   ;; assumes `data` is vector of vectors
                   (js/google.visualization.arrayToDataTable (clj->js data))
                   (clj->js options))))}]
     [rc/label :label "Loading..."])])


;; this data should come from app-db
; (def day
;   (r/atom 3))

(def some-data
  (r/atom [["Day", "Clicks"],
           [1 10000]
           [2 35000]
           [3 44000]]))

(s/def ::google-chart-type
  #(contains? #{"LineChart" "PieChart" "ColumnChart" "AreaChart"} %))

(defn chart [some-data]
  [draw-google-chart
   "ColumnChart"
   some-data
   ; {:title (str "Clicks as of day " @day)}])
   {:title (str "Concept Frequency")}])

(defn concepts-above [painting n]
   (filter
    #(> (:value %) n)
    (:concepts painting)))

(defn frequencies-of-concepts-with-certainty-above [paintings n]
  (frequencies
   (map
    :name
    (mapcat
     #(concepts-above % n)
     paintings))))

(defn frequencies->google-chart-data [concept-frequencies]
  (mapv #(into [] %) concept-frequencies))

(defn frequencies-chart [paintings]
  (let [chart-axes ["Concept" "Frequency"]
        chart-data (frequencies-of-concepts-with-certainty-above paintings 0.85)]
  ; [chart @some-data])
    [chart (into [chart-axes] (frequencies->google-chart-data chart-data))]))
