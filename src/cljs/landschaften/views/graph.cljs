(ns landschaften.views.graph
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [clojure.spec.alpha :as s]
            [landschaften.events :as events]
            [landschaften.specs :as specs]
            [landschaften.views.utils :as utils]))

;; this component will actually be part of 'preview' screen/panel, later.

; (ns quickfrontend.google-chart
;   (:require [re-frame.core :as re-frame]
;             [quickfrontend.subs :as subs]
;             [reagent.core :as reagent]))

; (ns google-chart-example.core
;     (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def day
  (r/atom 3))

;; this makes
(def some-data
  (r/atom [["Day", "Clicks"],
                 [1 10000]
                 [2 35000]
                 [3 44000]]))

(defonce ready?
  (r/atom false))

(defonce initialize
  (do
    (println "google-chart: initialize called")
    (js/google.charts.load (clj->js {:packages ["corechart"]}))
    (js/google.charts.setOnLoadCallback
      (fn google-visualization-loaded []
        (do
            (println "google-visualization-loaded called")
            (reset! ready? true))))))

(defn data-table [data]
  (cond
    (map? data) (js/google.visualization.DataTable. (clj->js data))
    (string? data) (js/google.visualization.Query. data)
    (seqable? data) (js/google.visualization.arrayToDataTable (clj->js data))))

(defn draw-chart [chart-type data options]
  [:div
   (if @ready?
     [:div
      {:ref
       (fn [this]
         (when this
           (.draw (new (aget js/google.visualization chart-type) this)
                  (data-table data)
                  (clj->js options))))}]
     [:div "Loading..."])])

(defn hello-world []
  [:div
   [:h1 "Google Chart Example"]

   [:button
    {:on-click
     (fn [e]
       (swap! day inc)
       (swap! some-data conj [@day (/ (rand-int 300000) @day)]))}
    "click me!"]

   [draw-chart
    "LineChart"
    @some-data
    {:title (str "Clicks as of day " @day)}]])

; (r/render-component
;   [hello-world]
;   (js/document.getElementById "app"))

(defn on-js-reload [])
