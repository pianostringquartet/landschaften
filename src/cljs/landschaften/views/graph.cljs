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

(enable-console-print!)

;; needed for async load of
;; ... take a different, better approach?
(defonce google-chart-ready?
  (r/atom false))

;; asychronously initialize the Google Chart
;; need initialize-chart and initialize-table versions
(defonce initialize
  (do
    (println "google-chart: initialize called")
    ; (js/google.charts.load (clj->js {:packages ["corechart"]}))
    (js/google.charts.load (clj->js {:packages ["table"]}))
    (js/google.charts.setOnLoadCallback
      (fn google-visualization-loaded []
        (do
          (println "google-visualization-loaded called")
          (reset! google-chart-ready? true))))))


(defn draw-google-chart [chart-type data options]
  [rc/box
   :align-self :stretch
   :child
    (if @google-chart-ready?
     [:div
; :ref is how Reagent handles the imperative backing instances, which aren't (de)mounted via React's lifecycle methods
; see: https://presumably.de/reagent-mysteries-part-3-manipulating-the-dom.html
       {:style {:height "500px"}
        :ref
        (fn [this]
          (when this
            (.draw (new (aget js/google.visualization chart-type) this)
                   ;; assumes `data` is vector of vectors
                   (js/google.visualization.arrayToDataTable (clj->js data))
                   (clj->js options))))}]
     [rc/label :label "Loading..."])])

(s/def ::google-chart-type
  #(contains? #{"LineChart" "PieChart" "ColumnChart" "AreaChart", "BarChart"} %))

(defn chart [some-data chart-type]
  [draw-google-chart
   ; "BarChart"
   chart-type
   some-data
   {:title (str "Concept Frequency")
    :legend {:position "none"}
    ; :chartArea {:height "90%"}
    ; :height "100%"
    ; :bar {:groupWidth "50%"}
    ; :vAxis {:title "Concept" :showTextEvery 1}}])
    ; :isStacked "relative"}])
    :vAxis {:title "Concept"}}])
            ; :gridlines {:count (count some-data)}
            ; :viewWindow {:max}}}])

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
        chart-data (take 20
                     (reverse
                        (sort-by
                         second
                         (frequencies-of-concepts-with-certainty-above   paintings 0.94))))
        prepared-chart-data (into [chart-axes] (frequencies->google-chart-data chart-data))]
    (do
     (js/console.log "chart-data is:" chart-data)
     ; [chart prepared-chart-data "BarChart"]))) ;; must use initialize-chart
     [chart prepared-chart-data "Table"]))) ;; must use initialize-table
