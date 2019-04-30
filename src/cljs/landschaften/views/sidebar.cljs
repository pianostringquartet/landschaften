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
          (dispatch [::events/query-started (reset! val %)]))])))


;; add logic here for removing a group!
;; needs to be removed from local storage too
(defn save-group-button-trigger []
  [:> semantic-ui/button
   {:color    "blue"
    :on-click #(dispatch [::events/show-save-group-popover])}
   "SAVE SEARCH"])


(defn save-group-button [existing-group-name popover-showing?]
  [:> semantic-ui/popup
   {:trigger  (r/as-component [save-group-button-trigger])
    :open     popover-showing?
    :on       "click"
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
    ;[:> semantic-ui/grid
    ; [:> semantic-ui/grid-row
    [:> semantic-ui/slist {:horizontal true :relaxed true}
     [clear-button]
     [search-button]
     [save-group-button @existing-group-name @save-group-popover-showing?]]))


;; ------------------------------------------------------
;; Saved groups
;; ------------------------------------------------------


(defn group-button [name color]
  [:> semantic-ui/button
   {:color         color
    :icon          true
    :labelPosition "right"
    :style         {:border-radius "30px" :padding "4px"}   ; curvier
    :on-click      #(dispatch [::events/switch-groups name])}
   [:> semantic-ui/icon {:name     "close"
                         :on-click #(utils/log "Remove Group: " name)}]
   name])

(defn saved-groups []
  (let [saved-groups       (subscribe [::subs/saved-groups])
        current-group-name (subscribe [::subs/group-name])
        color              #(if (= % @current-group-name) "orange" "grey")]
    (when-not (empty? @saved-groups)
      ;[:> semantic-ui/slist
      [:div
       [rc/label :label "Saved searches:"]
       [utils/table
        (map #(group-button % (color %)) (keys @saved-groups))
        2]])))

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


;(defn -sidebar []
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


#_(defn -sidebar []
    [:> semantic-ui/grid
     [:> semantic-ui/grid-row
      [constraints/constraints]]
     [:> semantic-ui/grid-row
      [ui-buttons]]
     [:> semantic-ui/grid-column
      [:> semantic-ui/slist-item
       [constraints/concept-typeahead]
       [constraints/selected-concepts]
       [constraints/artist-typeahead]
       [constraints/selected-artists]
       [saved-groups]]]

     [:> semantic-ui/grid-row
      [barchart]]])

;[:> semantic-ui/grid-row



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



;; the constraints are not aligned,
;; because they're re-com?
(defn -sidebar []
  [:> semantic-ui/slist {:relaxed true}                     ;{:horizontal true};{}:padding "4px"
   [:> semantic-ui/slist-item
    ;[:> semantic-ui/segment
    [constraints/constraints]]
   [:> semantic-ui/slist-item
    [ui-buttons]]
   [:> semantic-ui/slist-item
    ;[:> semantic-ui/segment
    [constraints/concept-typeahead]
    ;[:> semantic-ui/slist-item
    [constraints/selected-concepts]]
   [:> semantic-ui/slist-item
    ;[:> semantic-ui/segment
    [constraints/artist-typeahead]
    ;[:> semantic-ui/slist-item
    [constraints/selected-artists]]
   [:> semantic-ui/slist-item
    [saved-groups]]
   [:> semantic-ui/slist-item
    [barchart]]])


;; great -- but very noisy / verbose
;; do the shorthand version instead?
(defn accordion-constraints []
  (let [->accordion-panel (fn [group]
                            {:key (:name group)
                             :title {:content (:name group)}
                             :content {:content (r/as-component [(:component group)])}})
        constraints [{:name "genre constraints"
                      :component constraints/mobile-genre-constraints}
                     {:name "school constraints"
                      :component constraints/mobile-school-constraints}
                     {:name "timeframe constraints"
                      :component constraints/mobile-timeframe-constraints}]]
    (fn []
      [:> semantic-ui/accordion
       {:panels (mapv ->accordion-panel constraints)}])))


(defn mobile-sidebar []
  [:> semantic-ui/slist {:relaxed true}                     ;{:horizontal true};{}:padding "4px"
   [:> semantic-ui/slist-item
    [accordion-constraints]]
   ;[:> semantic-ui/segment
   ;[constraints/constraints]]
   [:> semantic-ui/slist-item
    [ui-buttons]]
   [:> semantic-ui/slist-item
    ;[:> semantic-ui/segment
    [constraints/concept-typeahead]
    ;[:> semantic-ui/slist-item
    [constraints/selected-concepts]]
   [:> semantic-ui/slist-item
    ;[:> semantic-ui/segment
    [constraints/artist-typeahead]
    ;[:> semantic-ui/slist-item
    [constraints/selected-artists]]
   [:> semantic-ui/slist-item
    [saved-groups]]])


(defn sidebar []
  [-sidebar])
