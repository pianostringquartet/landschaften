(ns landschaften.explore.sidebar
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.explore.explore-subs :as explore-subs]
            [landschaften.explore.explore-events :as explore-events]
            [landschaften.explore.constraints :as constraints]
            [landschaften.view-utils :as utils]
            [landschaften.semantic-ui :as semantic-ui]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]))


;; ------------------------------------------------------
;; Control buttons
;; - e.g. searching for paintings satisfying constraints,
;;   save a new group, etc.
;; ------------------------------------------------------

;; need a separate event
(defn search-button []
  [:> semantic-ui/button
   {:color "green"
    :on-click #(dispatch [::explore-events/query-started])}
   "SEARCH"])


(defn clear-button []
  [:> semantic-ui/button
   {:color "red"
    :on-click #(dispatch [::explore-events/selections-cleared])}
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
          (dispatch [::explore-events/query-started (reset! val %)]))])))


(defn save-search-button-trigger []
  [:> semantic-ui/button
   {:color    "blue"
    :on-click #(dispatch [::explore-events/show-save-group-popover])}
   "SAVE SEARCH"])


(defn save-search-button-input [existing-group-name]
  [:> semantic-ui/input
   {:autoFocus    true
    :placeholder  existing-group-name
    :on-key-press (fn [react-synthetic-event]
                    (let [enter-pressed? (= "Enter" (aget react-synthetic-event "key"))
                          input          (aget react-synthetic-event "target" "value")]
                      (do
                        (when (and enter-pressed? (empty? input))
                          (dispatch [::explore-events/hide-save-group-popover]))
                        (when (and enter-pressed? (not (empty? input)))
                          (dispatch [::explore-events/save-search input])))))}])


(defn save-search-button [existing-group-name popover-showing?]
  [:> semantic-ui/popup
   {:trigger  (r/as-component [save-search-button-trigger])
    :open     popover-showing?
    :on       "click"
    :position "bottom left" ; to avoid re-com selection-list CSS conflict
    :on-close #(dispatch [::explore-events/hide-save-group-popover])
    :content  (r/as-component [save-search-button-input existing-group-name])}])


(defn ui-buttons []
  (let [existing-group-name         (subscribe [::explore-subs/group-name])
        save-group-popover-showing? (subscribe [::explore-subs/save-group-popover-showing?])]
    [:> semantic-ui/slist {:horizontal true :relaxed true}
     [clear-button]
     [search-button]
     [save-search-button @existing-group-name @save-group-popover-showing?]]))


;; ------------------------------------------------------
;; Saved groups
;; ------------------------------------------------------

;; BUG: icon click dispatches first remove-group then switch-current-group events
(>defn group-button! [group-name color]
  [string? string? => vector?]
  [:> semantic-ui/button
   {:color         color
    :icon          true
    :labelPosition "right"
    :style         {:border-radius "30px" :padding "4px"}
    :on-click      #(dispatch [::explore-events/switch-current-group group-name])}
   [:> semantic-ui/icon
      {:name     "close"
       :on-click #(dispatch [::explore-events/remove-group group-name])}]
   group-name])


(defn saved-groups-buttons []
  (let [saved-groups       (subscribe [::subs/saved-groups])
        current-group-name (subscribe [::explore-subs/group-name])
        color              #(if (= % @current-group-name) "orange" "grey")]
    (when-not (empty? @saved-groups)
      [:div
       [rc/label :label "Saved searches:"]
       [utils/bubble-table
          (mapv (fn [group-name] [group-button! group-name (color group-name)])
                (keys @saved-groups))
        2]])))


;; ------------------------------------------------------
;; Sidebar
;; - 'control center' for exploring paintings
;; ------------------------------------------------------

(def sidebar-components
  (list [ui-buttons]
        [:div [constraints/concept-typeahead]
              [constraints/selected-concepts]]
        [:div [constraints/artist-typeahead]
              [constraints/selected-artists]]
        [saved-groups-buttons]))


(defn desktop-sidebar [paintings]
  (let [concepts-frequencies [:> semantic-ui/grid {:padded true}
                              [utils/table-with-header "Frequency (%) of concepts in these paintings: "]] ;paintings]]
        components (if (> (count paintings) 0)
                     (concat sidebar-components
                             (list concepts-frequencies))
                     sidebar-components)]

    [:> semantic-ui/slist {:relaxed true}
     (utils/as-semantic-ui-list-items (concat (list [constraints/constraints])
                                              components))]))


(defn mobile-sidebar []
  [:> semantic-ui/slist {:relaxed true}
   (utils/as-semantic-ui-list-items (concat (list [constraints/accordion-constraints])
                                            sidebar-components))])


