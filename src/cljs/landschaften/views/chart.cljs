(ns landschaften.views.chart
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.ui-specs :as ui-specs]
            [landschaften.subs :as subs]
            [landschaften.views.utils :as utils]
            [cljsjs.chartjs]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]))


(defn show-revenue-chart
  []
  (let [context    (.getContext (.getElementById js/document "rev-chartjs") "2d")
        ;chart-data {:type "bar"
        chart-data {:type "horizontalBar"
                    :data {:labels   ["2012" "2013" "2014" "2015" "2016"]
                           :datasets [{:data            [5 10 15 20 25]
                                       :label           "Rev in MM"
                                       :backgroundColor "#90EE90"}
                                      {:data            [3 6 9 12 15]
                                       :label           "Cost in MM"
                                       :backgroundColor "#F08080"}]}}]
    (js/Chart. context (clj->js chart-data))))

(defn rev-chartjs-component
  []
  (r/create-class
    {:component-did-mount #(show-revenue-chart)
     :display-name        "chartjs-component"
     :reagent-render      (fn []
                            [:canvas {:id "rev-chartjs" :width "700" :height "380"}])}))



;;; try a radar chart?
;; some concepts will not overlap
;; how many concepts will you be using / showing?
;; at what point does the radar chart become unreadable?
;;
;; pros: radar chart overlap can visualize relationship between two datasets;
;; literally the shape will be the similar!




;; works
#_(defn show-radar-chart []
    (let [context    (.getContext (.getElementById js/document "rev-chartjs") "2d")
          chart-data {:type "radar"
                      :data {:labels   ["2012" "2013" "2014" "2015" "2016"]
                             :datasets [{:data            [5 10 15 20 25]
                                         :label           "dataset 1"
                                         ;:backgroundColor "#90EE90"
                                         :backgroundColor "rgba(255, 99, 132, 0.3)"}
                                        {:data            [3 6 9 12 15]
                                         :label           "dataset 2"
                                         :backgroundColor "rgba(54, 162, 235, 0.3)"}]}}] ;;"#F08080"}]}}]
      (js/Chart. context (clj->js chart-data))))



;; works; now pass in data instead and use real data
;; can you pass in data instead?

;; it's fine to define a spec write above
(s/def ::chart-data
  (s/coll-of float?))


;; need to also pass in group-names
(>defn show-radar-chart [data-1 data-2 labels]
  [::chart-data ::chart-data (s/coll-of string?) => any?]   ;; returns a js object
  (let [context (.getContext (.getElementById js/document "rev-chartjs") "2d")]
     ;labels  ["2009" "2010" "2011" "2012" "2013" "2014" "2015" "2016" "2017" "2018"]]
     ;data-1 [0.2 0.4 0.6 0.8 0.10 0.12 0.14 0.16 0.18 0.20]
     ;data-2 [0.1 0.3 0.5 0.7 0.7 0.9 0.1 0.3 0.17 0.19]]
    (js/Chart. context
               (clj->js {:type "radar"
                         :data {:labels   labels
                                :datasets [{:data            data-1
                                            :label           "dataset 1"
                                            :backgroundColor "rgba(255, 99, 132, 0.3)"}
                                           {:data            data-2
                                            :label           "dataset 2"
                                            :backgroundColor "rgba(54, 162, 235, 0.3)"}]}}))))



;(defn radar-chart []
;  (r/create-class
;    {:component-did-mount #(show-radar-chart)
;     :display-name        "chartjs-component"
;     :reagent-render      (fn []
;                            [:canvas {:id "rev-chartjs" :width "700" :height "380"}])}))


;;; chart-data should be the :data key
(defn radar [chart-data]
  (let [context (.getContext (.getElementById js/document "rev-chartjs") "2d")]
    (js/Chart. context (clj->js {:type "radar" :data chart-data}))))


(declare paintings->percentage-chart-data)

;; may end up with more labels than data points;
;; eg label X might be in dataset 1 but not dataset 2;
;; in that case, give dataset 2 a zero for that label

;; needs two datasets

;; no errors thrown in console, and printed data looks good; but component not showing up in UI
;; ... maybe issue of WHEN subscription becomes available vs. when chart is rendered?
;; ... test this by hardcoding in regular data
#_(defn radar-chart []
    ;(let [n-chartpoints     (subscribe [::subs/show-n-chart-points])
    ;      concept-certainty (subscribe [::subs/concept-certainty-above])
    ;      compared-groups   (subscribe [::subs/compared-groups])
    ;      paintings-1       (:paintings (first @compared-groups))
    ;      paintings-2       (:paintings (second @compared-groups))
    ;      data-1            (paintings->percentage-chart-data paintings-1 @n-chartpoints @concept-certainty)
    ;      data-2            (paintings->percentage-chart-data paintings-2 @n-chartpoints @concept-certainty)
    ;      data              (into data-1 data-2)
    ;      labels            (into [] (take 5 (into #{} (map first data))))]
    ;  ;->dataset (fn [label data color] {:label label :data data :backgroundColor color})]
    ;  (do
    ;    (js/console.log "radar-chart-2: data-1 is: " data-1)
    ;    (js/console.log "radar-chart-2: data-2 is: " data-2)
    ;    (js/console.log "radar-chart-2: data is: " data)
    ;    (js/console.log "radar-chart-2: labels is: " labels)
    ;    (js/console.log "radar-chart-2: (mapv second data-1) is: " (mapv second data-1))

    [radar {:labels   ["2012" "2013" "2014" "2015" "2016"]
            :datasets [{:data [5 10 15 20 25]}
                       :label "dataset 1"
                       ;:backgroundColor "#90EE90"}
                       :backgroundColor "rgba(255, 99, 132, 0.3)"
                       {:data            [3 6 9 12 15]
                        :label           "dataset 2"
                        :backgroundColor "rgba(54, 162, 235, 0.3)"}]}]

    #_[radar {:labels   labels
              :datasets [{:label           "p1"
                          ;:data            (mapv second (take 5 data-1))
                          :data            [1 2 3 4 5]      ;(mapv second (take 5 data-1))
                          :backgroundColor "rgba(255, 99, 132, 0.3)"}
                         {:label           "p2"
                          :data            [10 11 17 6 1]   ; (mapv second (take 5 data-2))
                          :backgroundColor "rgba(54, 162, 235, 0.3)"}]}])


(def data-1 [0.2 0.4 0.6 0.8 0.10 0.12 0.14 0.16 0.18 0.20 0.81 0.54 0.91 0.85 0.99])
(def data-2 [0.1 0.3 0.5 0.7 0.7 0.9 0.1 0.3 0.17 0.19 0.91 0.92 0.9 0.9889 0.99])
(def labels ["2009" "2010" "2011" "2012" "2013" "2014" "2015" "2016" "2017" "2018" "2019" "2020" "2021" "2022" "2023"])
;; called in compare screen

;; do you want to just convert the data, or have this render the comp as well?
;; just return a map

;; compared groups should only ever be 2 members long?
;; you officially are just grabbing the first and second members...


;; group -> list of concepts for each
;(group->)

;(s/def ::radar-chart-data
;  (s/keys :req-un [:data-1 :data-2 :]))


(defn ->percent [frequency total]
  (->> (/ frequency total)
       (double)
       (* 100)
       (goog.string/format "%.1f")
       (js/parseFloat)))


(defn frequency-data? [pts]
  (and (seq? pts)
       (string? (first (first pts)))))

;; takes:
;; where each pts is vector of vectors: [[concept frequency-as-percent] ...]
(defn get-labels [pts-1 pts-2]
  {:pre [(frequency-data? pts-1) (frequency-data? pts-2)]}
  (into [] (flatten [(map first pts-1)
                     (map first pts-2)])))


;; where pts is vector of vectors [[
;; returns pts with a vector-member added for any label that was missing

;; since we're iterating through the labels in the same order,
;; resultant pts are always in same order
(defn add-missing-labels [pts labels]
  {:pre [(frequency-data? pts)]}
  (let [get-existing-label (fn [label]
                             (or (first (filter #(= label (first %)) pts))
                                 [label 0.0]))]
    (into []
      (map
        get-existing-label
        labels))))


(>defn compared-groups->radar-chart-data! [compared-groups]
  [(s/coll-of ::specs/group) => map?]
  (let [n-many                     15
        certainty-above            0.80
        concept-frequency->percent (fn [[concept frequency] total]
                                     [concept (->percent frequency total)])
        group-1                    (first compared-groups)
        group-2                    (second compared-groups)
        pts-1                      (:paintings group-1)
        pts-2                      (:paintings group-2)
        freqs-1                    (utils/paintings->concepts-frequencies pts-1 n-many certainty-above)
        freqs-2                    (utils/paintings->concepts-frequencies pts-2 n-many certainty-above)
        all-labels (get-labels freqs-1 freqs-2)
        all-labels-freqs-1 (add-missing-labels freqs-1 all-labels)
        all-labels-freqs-2 (add-missing-labels freqs-2 all-labels)
        ;; this gives us vector of vectors i.e [[c f] ...]
        freq-percents-1            (mapv #(concept-frequency->percent % (count pts-1)) all-labels-freqs-1)
        freq-percents-2            (mapv #(concept-frequency->percent % (count pts-2)) all-labels-freqs-2)
        final-data-1 (mapv second freq-percents-1)
        final-data-2 (mapv second freq-percents-2)]

    (do
      (utils/log "freqs-1:" freqs-1)
      (utils/log "freqs-2:" freqs-2)
      (utils/log "all-labels:" all-labels)
      (utils/log "all-labels-freqs-1: " all-labels-freqs-1)
      (utils/log "all-labels-freqs-2: " all-labels-freqs-2)

      ;; what we want
      (utils/log "freq-percents-1:" freq-percents-1)
      (utils/log "freq-percents-2:" freq-percents-2)
      (utils/log "final-data-1: " final-data-1)
      (utils/log "final-data-2: " final-data-2)


      ;{:data-1 data-1
      ; :data-2 data-2
      ; :labels labels})))
      {:data-1 final-data-1
       :data-2 final-data-2
       :labels all-labels})))




;; vector-float, vector-float, vector-strings
;(defn radar-chart-component [data-1 data-2 labels]

;; chart.js effectively requires a mutable array,
;; and updates to the chart must be updates to that array.

;; so, separate out the array as a reagent atom?

;; contains the mutable arrays as reagent-atoms

;; examples show
;; have to call chart.update

;; have to retrieve the original chart?
;; like this?: context (.getContext (.getElementById js/document "rev-chartjs") "2d")
;;

;; get the comp-did-mount logic + ratom combo correct first

;; presumably, I can't just mutate the ratoms,
;; because I have to explicitly call chart.update();
;; ... theoretically, could update the ratoms, then call chart.update()>

;; is there a quicker workaround -- e.g. giving them different keys,
;; forcing a total redraw of the component?
;; seems preferable to messing with mutable data etc. here

;(defn radar-chart-component-inner
;  (let [data-1-ratom (r/atom [])
;        data-2-ratom (r/atom [])
;        labels-ratom (r/atom [])
;        update-component (fn [comp]
;                           ;;
;                           (let [{:keys [data-1 data-2 labels]} (r/props comp)
;                           ;; retrieve the chart
;                                 charty (js/document.getElementById js/document "rev-chartjs")]
;                             (do
;                               (js/console.log "(.data.labels chart): " (.data.labels charty)))))]
;                             ;(.update charty)))]
;
;    (r/create-class
;      {:reagent-render (fn []
;                         [:canvas {:id "rev-chartjs" :width "700" :height "380"}])
;       :display-name "chartjs-component"
;       :component-did-mount (fn [comp]
;                              ;; how to actually retrieve the chart created by reagent-render?
;                              ;; simplest approach: just find it on the dom?
;                              (let [chart-itself (js/document.getElementById js/document "rev-chartjs")]))})))





;; AKA "radar-chart-component-outer"
;(defn radar-chart-component [{:keys [data-1 data-2 labels]}]
;(defn radar-chart-component [props]
;  {:pre [(map? props)]} ;; props must be a map
;  (fn []
;    (radar-chart-component-inner props)))


;; WORKS, but not good enough for mutable-heavy chart.js approach?

(defn radar-chart-component [{:keys [data-1 data-2 labels]}]
  (r/create-class
    ;{:component-did-mount #(show-radar-chart)
    {:component-did-mount #(show-radar-chart data-1 data-2 labels)
     :display-name        "chartjs-component"
     :reagent-render      (fn []
                            [:canvas {:id "rev-chartjs" :width "700" :height "380"}])}))




;; returns vector of vectors ?
;; where each member is ["concept" "frequency"] ?
;; don't reuse this for radar chart...
(defn paintings->percentage-chart-data [paintings n-many certainty-above]
  (let [total (count paintings)]
    (->> (utils/paintings->concepts-frequencies paintings n-many certainty-above)
         (mapv
           (fn [[concept frequency]]
             [concept (->percent frequency total)])))))

