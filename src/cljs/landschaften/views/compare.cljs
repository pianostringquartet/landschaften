(ns landschaften.views.compare
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.events :as events]
            [landschaften.views.graph :as graph]
            [landschaften.views.stats :as stats]
            [landschaften.views.utils :as utils]
            [landschaften.specs :as specs]
            [clojure.spec.alpha :as s]))


(defn group-button [group-name color on-click]
  [rc/button
    :label group-name
     :on-click on-click
     :class color ; Bootstrap
    :style {:border-radius "30px"}]) ; curvier


(defn selected-button [group-name compared-group-names]
  {:pre [(string? group-name)]}
  (let [being-compared? (some #{group-name} compared-group-names)
        on-click (if being-compared?
                   #(dispatch [::events/remove-compare-group-name group-name])
                   #(dispatch [::events/add-compare-group-name group-name]))
        color (if being-compared?
                    "btn btn-info"
                    "btn btn-warning")]
    [group-button group-name color on-click]))


(defn saved-groups []
  (let [saved-groups (subscribe [::subs/saved-groups])
        compared-group-names (subscribe [::subs/compared-group-names])]
    [rc/v-box
     :gap "8px"
     :children [[utils/button-table
                   (keys @saved-groups)
                   2
                   #(selected-button % @compared-group-names)]]]))


(defn table [paintings]
  (let [n-chartpoints (subscribe [::subs/show-n-chart-points])
        concept-certainty (subscribe [::subs/concept-certainty-above])]
    [graph/frequencies-chart
     "Table"
     ;(graph/paintings->chart-data paintings @n-chartpoints @concept-certainty)
     (graph/paintings->percentage-chart-data paintings @n-chartpoints @concept-certainty)
     "Concepts' Frequencies"
     ["Concepts" "Frequencies (%)"]]))


(defn labeled-table [name paintings]
  [rc/v-box
     :children [[rc/label :label name]
                [table paintings]]])


(defn clear-button []
  [rc/button
   :label "CLEAR"
   :on-click #(dispatch [::events/comparisons-cleared])
   :class "btn btn-danger"])


(defn labeled-tables [groups]
  (mapv
    (fn [group] [labeled-table (:group-name group) (:paintings group)])
    groups))

;

;; when there are no compared-groups,
;; everything blows up

;; a couple issues here:
;; - the big calc is basically a "get me the relevant data" step
;; - fn blows up when no compared groups
;; - need to provide (in UI) context around the error rate

;; michel + dutch = 0.048
(defn error-ready-data [group]
  (graph/paintings->error-data
    (:paintings group)
    20
    0.94))



;; make error rate a subscription;
;; get this logic out of the view
(defn error-between-groups [group-1 group-2]
  {:pre [(s/valid? ::specs/group group-1)
         (s/valid? ::specs/group group-2)]}
  (let [error (stats/error-rate (error-ready-data group-1)
                                (error-ready-data group-2))]
    [rc/label :label
       (str "Error rate: "
            (goog.string/format "%.5f" (* error 100)))]))


(defn display-data []
  (let [compared-groups (subscribe [::subs/compared-groups])]
    [rc/h-box
       :gap "8px"
       :children (conj (labeled-tables @compared-groups)
                       (when (<= 2 (count @compared-groups))
                         [error-between-groups (first @compared-groups)
                                               (second @compared-groups)]))]))


(defn compare-panel []
  [rc/v-box
   :justify :between
   :gap "8px"
   :style {:padding-left "16px" :padding-right "16px"}
   :children [[rc/h-box
                 :gap "32px"
                 :children [[clear-button]
                            [saved-groups]]]
              [display-data]]])

