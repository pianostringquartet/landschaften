(ns landschaften.views.sidebar
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.db :as db]
            [clojure.spec.alpha :as s]
            [landschaften.events :as events]
            [landschaften.specs :as specs]
            [landschaften.views.constraints :as constraints]
            [landschaften.views.utils :as utils]
            [landschaften.views.graph :as graph]))


;; ------------------------------------------------------
;; Control buttons
;; - e.g. searching for paintings satisfying constraints,
;;   save a new group, etc.
;; ------------------------------------------------------


(defn search-button []
  [rc/button
      :label "SEARCH"
       ;; when searching, don't pass in group-name;
       ;; i.e. don't force user to save search prematurely
       :on-click #(dispatch [::events/query-started nil])
      :class "btn btn-success"]) ; Bootstrap


(defn clear-button []
  [rc/button
    :label "CLEAR"
    :on-click #(dispatch [::events/selections-cleared])
    :class "btn btn-danger"])


(defn add-group-name [existing-group-name]
  {:pre [(string? existing-group-name)]}
  (let [val (r/atom existing-group-name)]
    (fn add-group-name-input []
      [rc/input-text
        :model val
        :change-on-blur? true
        :placeholder "Enter name"
        :attr {:auto-focus "true"}
        :on-change
          #(when (not (empty? %))
             (do
              ;(dispatch [::events/group-saved (reset! val %)])))])))
               (dispatch [::events/query-started (reset! val %)])))])))


(defn save-group-button [existing-group-name popover-showing?]
    (fn save-group-button []
      [rc/popover-anchor-wrapper
        :showing? popover-showing? ; must be reagent atom or reframe subscription
        :position :below-center
        :anchor [rc/button
                  :label "SAVE SEARCH"
                  :class "btn btn-secondary"
                  :on-click #(dispatch [::events/show-save-group-popover])]
        :popover [rc/popover-content-wrapper
                    :on-cancel #(dispatch [::events/hide-save-group-popover])
                    :backdrop-opacity 0.3
                    :body [add-group-name existing-group-name]]]))


(defn ui-buttons []
  (let [existing-group-name (subscribe [::subs/group-name])
        save-group-popover-showing? (subscribe [::subs/save-group-popover-showing?])]
    [rc/h-box :gap "8px" :children [[clear-button]
                                    [search-button]
                                    [save-group-button
                                       @existing-group-name
                                       save-group-popover-showing?]]]))


;; ------------------------------------------------------
;; Saved groups
;; ------------------------------------------------------


(defn group-button [group-name color]
  [rc/button
    :label group-name
    :on-click #(dispatch [::events/switch-groups group-name])
    :class color
    :style {:border-radius "30px"}]) ; curvier


(defn saved-groups []
  (let [saved-groups (subscribe [::subs/saved-groups])
        current-group-name (subscribe [::subs/group-name])]
     [rc/v-box
       :gap "8px"
       :children [(when-not (empty? @saved-groups)
                   [rc/label :label "Saved searches:"])
                  [utils/button-table
                   (keys @saved-groups)
                   2
                   #(group-button % (if (= % @current-group-name)
                                      "btn btn-info"
                                      "btn btn-warning"))]]]))


;; ------------------------------------------------------
;; Sidebar
;; - 'control center' for exploring paintings
;; ------------------------------------------------------


;; The chart apparently interferes with Flexbox alignment.
;; e.g. introduces a larger margin on the right
;; ... need to revisit Google chart, especially not have it in own :div
;; not sure how to do that...
;; it's okay to have the push the side...
;; also, may be hiding this anyway
(defn barchart []
  (let [paintings (subscribe [::subs/paintings])
        chart-data (graph/paintings->chart-data @paintings 20 0.94)]
    (do
      (utils/log "chart-data: " (str chart-data));
      (when (> (count @paintings) 0)
        [graph/frequencies-chart "BarChart" chart-data "Search's most frequent concepts"]))))


(defn sidebar []
  [rc/v-box
    :gap "8px"
    :children [[constraints/constraints] ; genre, school, timeframe constraints
               [ui-buttons]
               [constraints/concept-typeahead]
               [constraints/selected-concepts]
               [constraints/artist-typeahead]
               [constraints/selected-artists]
               [saved-groups]
               [barchart]]])
