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
      ;:on-click #(dispatch [::events/query-started])
       ;; when just searching, pass in nil for the group-name
       ;; cuz don't want to force user to name group until they save it
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
        :placeholder "Enter name for group"
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
                  :label "SAVE GROUP"
                  :class "btn btn-secondary"
                  :on-click #(dispatch [::events/show-save-group-popover])]
        :popover [rc/popover-content-wrapper
                    :on-cancel #(dispatch [::events/hide-save-group-popover])
                    :backdrop-opacity 0.3
                    :body [add-group-name existing-group-name]]]))


(defn ui-buttons []
  (let [existing-group-name (subscribe [::subs/group-name])
        save-group-popover-showing? (subscribe [::subs/save-group-popover-showing?])]
    [rc/h-box :children [[clear-button]
                         [search-button]
                         [save-group-button
                            @existing-group-name
                            save-group-popover-showing?]]]))


;; ------------------------------------------------------
;; Saved groups
;; ------------------------------------------------------


(defn group-button [group-name]
 [rc/button
     :label group-name
     :on-click #(dispatch [::events/switch-groups group-name])
     :class "btn btn-warning" ; Bootstrap
     :style {:border-radius "30px"}]) ; curvier


(defn saved-groups []
  (let [saved-groups (subscribe [::subs/saved-groups])]
     [rc/v-box
       :children [(when-not (empty? @saved-groups)
                   [rc/label :label "Saved Groups:"])
                  [utils/button-table (keys @saved-groups) 2 group-button]]]))


;; ------------------------------------------------------
;; Sidebar
;; - 'control center' for exploring paintings
;; ------------------------------------------------------


(defn barchart []
  (let [paintings (subscribe [::subs/paintings])
        chart-data (graph/->chart-data @paintings 20 0.94)]
    (do
      (utils/log "chart-data: " (str chart-data));
      (when (> (count @paintings) 0)
        [rc/h-box
         :children [[graph/frequencies-chart "BarChart" chart-data]]]))))


(defn sidebar []
  [rc/v-box
    :children [[constraints/constraints] ; genre, school, timeframe constraints
               [ui-buttons]
               [constraints/concept-typeahead]
               [constraints/selected-concepts]
               [constraints/artist-typeahead]
               [constraints/selected-artists]
               [saved-groups]
               [barchart]]])
