(ns landschaften.views.compare
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.events :as events]
            [landschaften.views.graph :as graph]
            [landschaften.views.utils :as utils]))


(defn group-button [group-name color on-click]
  [rc/button
    :label group-name
     :on-click on-click
     :class color ; Bootstrap
    :style {:border-radius "30px"}]) ; curvier


(defn selected-button [group-name compared-group-names]
  {:pre [(set? compared-group-names)
         (string? group-name)]}
  (let [being-compared? (contains? compared-group-names group-name)
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
     (graph/paintings->chart-data paintings @n-chartpoints @concept-certainty)
     "Concepts' Frequencies"]))


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


(defn error-rate []
  [rc/label :label "Error rate to be added"])


(defn display-data []
  (let [compared-groups (subscribe [::subs/compared-groups])]
    [rc/h-box
       :gap "8px"
       :children (conj (labeled-tables @compared-groups) [error-rate])]))


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

