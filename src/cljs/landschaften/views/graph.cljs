(ns landschaften.views.graph
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]))

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
    (js/google.charts.load (clj->js {:packages ["corechart" "table"]}))
    (js/google.charts.setOnLoadCallback
      (fn google-visualization-loaded []
        (do
          (println "google-visualization-loaded called")
          (reset! google-chart-ready? true))))))


;; :ref is how Reagent handles the imperative backing instances, which aren't (de)mounted via React's lifecycle methods
;; see: https://presumably.de/reagent-mysteries-part-3-manipulating-the-dom.html
;; ... there must be a better way...
(defn draw-google-chart [chart-type data options]
  (let [ref-fn (fn [this] (when this
                            (.draw (new (aget js/google.visualization chart-type) this)
                                           ;; assumes `data` is vector of vectors
                                   (js/google.visualization.arrayToDataTable (clj->js data))
                                   (clj->js options))))]
    [rc/box :child
       (if @google-chart-ready?
        [:div {:style {:height "500px"} :ref ref-fn}]
        [rc/label :label "Loading..."])]))


(defn chart [some-data chart-type title]
  [draw-google-chart
   chart-type
   some-data
   {:title title
    :legend {:position "none"}
    :chartArea {:height "80%"}}])


;; total mess ...
(defn concepts-above [painting n]
   (filter
    #(> (:value %) n)
    (:concepts painting)))


(defn frequencies-of-concepts-with-certainty-above [paintings n]
  (->> paintings
    (mapcat #(concepts-above % n))
    (map :name)
    (frequencies)))

(defn frequencies->google-chart-data [concept-frequencies]
  (mapv #(into [] %) concept-frequencies))


(defn paintings->chart-data [paintings n-many certainty-above]
  {:pre [(s/valid? ::specs/paintings paintings)
         (float? certainty-above)
         (int? n-many)]}
  (->> (frequencies-of-concepts-with-certainty-above paintings certainty-above)
    (sort-by second) ; meaningless
    (reverse)
    (take n-many)
    (frequencies->google-chart-data)))


(defn frequencies-chart [chart-type chart-data title]
  (let [chart-axes ["Concept" "Frequency"]
        axes+data (into [chart-axes] chart-data)]
     [chart axes+data chart-type title]))
