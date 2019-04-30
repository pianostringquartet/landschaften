(ns landschaften.views.graph
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.sample :as sample]
            [landschaften.views.utils :as utils]))

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
       [:div {:style {:height "500px"} :ref ref-fn}
        [rc/label :label "Loading..."]])]))


(defn chart [some-data chart-type title]
  [draw-google-chart
   chart-type
   some-data
   {:title     title
    :legend    {:position "none"}
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

(defn paintings->concepts-frequencies
  "Return the n-many concepts' frequencies,
    where each concept's certainty is above some level."
  [paintings n-many certainty-above]
  ;{:pre [(s/valid? ::specs/paintings paintings)
  {:pre [(utils/valid? ::specs/paintings paintings)
         (int? n-many)
         (float? certainty-above)]}
  (->> (frequencies-of-concepts-with-certainty-above paintings certainty-above)
       (sort-by second)                                     ; meaningless
       (reverse)
       (take n-many)))


(defn paintings->error-data [paintings n-many certainty-above]
  (->> (paintings->concepts-frequencies paintings n-many certainty-above)
       (into {})))

;(= (paintings->error-data (:paintings sample/sample-group) 20 0.94)
;   {"leader" 1, "lid" 2, "adult" 2, "painting" 2, "people" 2, "wear" 2, "man" 2, "veil" 2, "one" 2, "art" 2, "portrait" 2}

(defn paintings->chart-data [paintings n-many certainty-above]
  (-> (paintings->concepts-frequencies paintings n-many certainty-above)
      (frequencies->google-chart-data)))


;; (->percent (/ certainty total))
(defn ->percent [frequency total]
  (->> (/ frequency total)
       (double)
       (* 100)
       (goog.string/format "%.1f")
       (js/parseFloat)))


(defn paintings->percentage-chart-data [paintings n-many certainty-above]
  (let [total (count paintings)]
    (->> (paintings->concepts-frequencies paintings n-many certainty-above)
         (mapv
           (fn [[concept frequency]]
             [concept (->percent frequency total)])))))

; chart-axes ["Concept" "Frequency"]
(defn frequencies-chart [chart-type chart-data title chart-axes]
  (let [axes+data (into [chart-axes] chart-data)]
    [chart axes+data chart-type title]))
