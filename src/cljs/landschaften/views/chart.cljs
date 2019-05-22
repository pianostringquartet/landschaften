(ns landschaften.views.chart
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.ui-specs :as ui-specs]
            [landschaften.subs :as subs]
            [landschaften.views.utils :as utils]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]))


;(defonce google-chart-ready? (r/atom false))
;
;;; Asynchronously initialize Google Chart
;;(defonce initialize
;(def initialize
;  (do
;   (js/google.charts.load (clj->js {:packages ["corechart" "table"]}))
;   (js/google.charts.setOnLoadCallback
;     (fn google-visualization-loaded []
;         (reset! google-chart-ready? true)))))
;
;
;;; Reagent :ref is not demounted during React's lifecycle methods
;;; See: https://presumably.de/reagent-mysteries-part-3-manipulating-the-dom.html
;(defn draw-google-chart [chart-type data options]
;  (let [ref-fn (fn [this]
;                 (when this
;                   (.draw (new (aget js/google.visualization chart-type) this)
;                          ;; assumes `data` is vector of vectors
;                          (js/google.visualization.arrayToDataTable (clj->js data))
;                          (clj->js options))))]
;    [rc/box :child
;     (if @google-chart-ready?
;       [:div {:style {:height "500px"} :ref ref-fn}]
;       [rc/label :label "Loading..."])]))
;
;
;(>defn google-chart [chart-type title chart-axes chart-data]
;  [::ui-specs/google-chart-type string? vector? vector? => vector?]
;  (let [axes+data (into [chart-axes] chart-data)]
;    [draw-google-chart
;     chart-type
;     axes+data
;     {:title     title
;      :legend    {:position "none"}
;      :chartArea {:height "80%"}}]))
;
;
;(defn ->percent [frequency total]
;  (->> (/ frequency total)
;       (double)
;       (* 100)
;       (goog.string/format "%.1f")
;       (js/parseFloat)))
;
;
;(defn paintings->percentage-chart-data [paintings n-many certainty-above]
;  (let [total (count paintings)]
;    (->> (utils/paintings->concepts-frequencies paintings n-many certainty-above)
;         (mapv
;           (fn [[concept frequency]]
;             [concept (->percent frequency total)])))))
;
;
;(defn table-chart [paintings]
;  (let [n-chartpoints     (subscribe [::subs/show-n-chart-points])
;        concept-certainty (subscribe [::subs/concept-certainty-above])]
;    [google-chart
;     "Table"
;     "Concepts' Frequencies"
;     ["Concepts" "Frequencies (%)"]
;     (paintings->percentage-chart-data paintings @n-chartpoints @concept-certainty)]))
;
;
;(defn bar-chart [paintings]
;  (let [n-chartpoints     (subscribe [::subs/show-n-chart-points])
;        concept-certainty (subscribe [::subs/concept-certainty-above])]
;    [google-chart
;     "BarChart"
;     "Search's most frequent concepts"
;     ["Concepts" "Frequencies (%)"]
;     (paintings->percentage-chart-data paintings @n-chartpoints @concept-certainty)]))
