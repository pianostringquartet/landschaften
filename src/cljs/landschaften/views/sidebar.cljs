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
            [landschaften.views.graph :as graph]
            [landschaften.semantic-ui :as semantic-ui]))


;; ------------------------------------------------------
;; Control buttons
;; - e.g. searching for paintings satisfying constraints,
;;   save a new group, etc.
;; ------------------------------------------------------

(defn search-button []
  [:> semantic-ui/button
   {:color    "green"
    :on-click #(dispatch [::events/query-started nil])}
   "SEARCH"])

(defn clear-button []
  [:> semantic-ui/button
   {:color "red" :on-click #(dispatch [::events/selections-cleared])}
   "CLEAR"])


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


(defn save-group-button-trigger []
  [:> semantic-ui/button
   {:color "blue" :on-click #(dispatch [::events/show-save-group-popover])}
   "SAVE SEARCH"])


;; needs to be a modal, not a popup
;; the popup
(defn save-group-button [existing-group-name popover-showing?]
  [:> semantic-ui/popup
   {:trigger  (r/as-component [save-group-button-trigger])
    :open     popover-showing?
    :on "click"
    :position "bottom left"                                 ; to avoid re-com selection-list CSS conflict
    :on-close #(dispatch [::events/hide-save-group-popover])
    :content  (r/as-component
                [:> semantic-ui/input {:on-change    #(js/console.log "ON CHANGE")
                                       :placeholder  existing-group-name
                                       :on-key-press (fn [react-synthetic-event]
                                                       (let [enter-pressed? (= "Enter" (aget react-synthetic-event "key"))
                                                             input          (aget react-synthetic-event "target" "value")]
                                                         (do
                                                           (when (and enter-pressed? (empty? input))
                                                             (dispatch [::events/hide-save-group-popover]))
                                                           (when (and enter-pressed? (not (empty? input)))
                                                             (dispatch [::events/query-started input])))))}])}])


(defn ui-buttons []
  (let [existing-group-name         (subscribe [::subs/group-name])
        save-group-popover-showing? (subscribe [::subs/save-group-popover-showing?])]
    [:> semantic-ui/grid
     [:> semantic-ui/grid-row
      [clear-button]
      [search-button]
      [save-group-button @existing-group-name @save-group-popover-showing?]]]))


;; ------------------------------------------------------
;; Saved groups
;; ------------------------------------------------------


(defn group-button [name color]
  [:> semantic-ui/button
   {:color    color
    :style    {:border-radius "30px"}                       ; curvier
    :on-click #(dispatch [::events/switch-groups name])}
   name])


(defn saved-groups []
  (let [saved-groups       (subscribe [::subs/saved-groups])
        current-group-name (subscribe [::subs/group-name])]
    [rc/v-box
     :gap "8px"
     :children [(when-not (empty? @saved-groups)
                  [rc/label :label "Saved searches:"])
                [utils/button-table
                 (keys @saved-groups)
                 2
                 #(group-button % (if (= % @current-group-name)
                                    "blue"
                                    "grey"))]]]))


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
  (let [paintings  (subscribe [::subs/paintings])
        ;chart-data (graph/paintings->chart-data @paintings 20 0.94)]
        chart-data (graph/paintings->percentage-chart-data @paintings 20 0.94)]
    (do
      (utils/log "chart-data: " (str chart-data))           ;
      (when (> (count @paintings) 0)
        ;[graph/frequencies-chart "BarChart" chart-data "Search's most frequent concepts" ["Concepts" "Frequencies"]]))))
        [graph/frequencies-chart
         "BarChart"
         chart-data
         "Search's most frequent concepts"
         ["Concepts" "Frequencies (%)"]]))))


;(defn sidebar []
; [:> semantic-ui/grid
;  [:> semantic-ui/grid-row
;   [constraints/constraints]]
;  [:> semantic-ui/grid-row
;   [ui-buttons]]
;  [:> semantic-ui/grid-row
;   [constraints/concept-typeahead]]
;  [:> semantic-ui/grid-row
;   [constraints/selected-concepts]]
;  [:> semantic-ui/grid-row
;   [constraints/artist-typeahead]]
;  [:> semantic-ui/grid-row
;   [constraints/selected-artists]]
;  [:> semantic-ui/grid-row
;   [saved-groups]]
;  [:> semantic-ui/grid-row
;   [barchart]]])


;; a smui LIST might be better here than smui Grid
;; and lists can be made HORIZONTAL
#_(defn sidebar []
    [:> semantic-ui/slist {:relaxed true}
     [constraints/constraints]
     [ui-buttons]
     [constraints/concept-typeahead]
     [constraints/selected-concepts]
     [constraints/artist-typeahead]
     [constraints/selected-artists]
     [saved-groups]
     [barchart]])


(defn sidebar []
  [:> semantic-ui/slist {:relaxed true}                     ;{:horizontal true};{}:padding "4px"
   [:> semantic-ui/slist-item
    [constraints/constraints]]
   [:> semantic-ui/slist-item
    [ui-buttons]]
   [:> semantic-ui/slist-item
    [constraints/concept-typeahead]]
   [:> semantic-ui/slist-item
    [constraints/selected-concepts]]
   [:> semantic-ui/slist-item
    [constraints/artist-typeahead]]
   [:> semantic-ui/slist-item
    [constraints/selected-artists]]
   [:> semantic-ui/slist-item
    [saved-groups]]
   [:> semantic-ui/slist-item
    [barchart]]])


#_(defn sidebar []
    [rc/v-box
     :gap "8px"
     :children [[constraints/constraints]                   ; genre, school, timeframe constraints
                [ui-buttons]
                [constraints/concept-typeahead]
                [constraints/selected-concepts]
                [constraints/artist-typeahead]
                [constraints/selected-artists]
                [saved-groups]
                [barchart]]])
