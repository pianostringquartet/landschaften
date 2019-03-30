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
     ;; this can't be made a sub,
     ;; because an essential component (paintings)
     ;; is dynamically determined
     ;; ... could be made a sub, but
     (graph/paintings->percentage-chart-data paintings @n-chartpoints @concept-certainty)
     "Concepts' Frequencies"
     ["Concepts" "Frequencies (%)"]]))


(defn labeled-table [name paintings]
  [rc/v-box
     :gap "8px"
     :children [[rc/title :label name :level :level3]
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


(defn error-ready-data [group]
  (graph/paintings->error-data
    (:paintings group)
    20
    0.94))


;; want to do a progress bar etc. for error rate;
;; some kind of visual that makes it intelligible to user;
;; but error rate isn't a percent...
;; could you try e.g. (actual error rate / maximum error rate)
;; 'max error rate' would be different for each group

(defn error-rate-label [error]
  [rc/v-box
     :children
       [[rc/p
           {:style {:color "lightGrey"}}
           "Error measures similarity of two groups of paintings."]
        [rc/p
           {:style {:color "lightGrey"}}
           "Smaller error -> greater similarity"]
        [rc/label
           :label (str "Error rate: "
                    ;   doesn't have to be "times 100"
                    ; error rate isn't actually a percent,
                    ; but the extremely long decimals were hard to read
                    ;(goog.string/format "%.3f" (* error 100)))]]])
                    (goog.string/format "%.4f" error))]]])


(defn display-data []
  (let [compared-groups (subscribe [::subs/compared-groups])
        error-rate (subscribe [::subs/error-rate])]
    [rc/h-box
       :gap "16px"
       :children (conj (labeled-tables @compared-groups)
                       (when @error-rate [error-rate-label @error-rate]))]))


(defn compare-panel []
  [rc/v-box
   :justify :between
   :gap "32px"
   :padding "16px"
   :style {:padding-left "16px" :padding-right "16px"}
   :children [[rc/h-box
                 :gap "32px"
                 :children [[clear-button]
                            [saved-groups]]]
              [display-data]]])
