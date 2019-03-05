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
            [landschaften.views.utils :as utils]))


;; ------------------------------------------------------
;; Control buttons
;; - e.g. searching for paintings satisfying constraints,
;;   save a new group, etc.
;; ------------------------------------------------------


(defn search-button []
  [rc/button
      :label "SEARCH"
      :on-click #(dispatch [::events/query])
      :class "btn btn-success"]) ; Bootstrap


(defn clear-button []
  [rc/button
    :label "CLEAR"
    :on-click #(dispatch [::events/selections-cleared])
    :class "btn btn-danger"])


(defn add-group-name [popover-showing?]
  (let [val (r/atom "")]
    (fn add-group-name-input []
      [rc/input-text
        :model val
        :change-on-blur? true
        :placeholder "Enter name for group"
        :attr {:auto-focus "true"}
        :on-change
          #(when (not (empty? %))
             (do
              (dispatch [::events/group-saved (reset! val %)])
              (reset! popover-showing? false)))])))


(defn save-group-button []
  (let [popover-showing? (r/atom false)]
    (fn save-group-button []
      [rc/popover-anchor-wrapper
        :showing? popover-showing?
        :position :below-center
        :anchor [rc/button
                  :label "SAVE GROUP"
                  :class "btn btn-secondary"
                  :on-click #(reset! popover-showing? true)]
        :popover [rc/popover-content-wrapper
                    :on-cancel #(reset! popover-showing? false)
                    :backdrop-opacity 0.3
                    :body [add-group-name popover-showing?]]])))


(defn ui-buttons []
  [rc/h-box :children [[clear-button]
                       [search-button]
                       [save-group-button]]])


;; ------------------------------------------------------
;; Saved groups
;; ------------------------------------------------------


(defn group-button [group-name]
  [rc/button
      :label group-name
      :on-click #(dispatch [::events/switch-groups (utils/displayable->keyword group-name)])
      :class "btn btn-warning" ; Bootstrap
      :style {:border-radius "30px"}]) ; curvier


(defn saved-groups []
  (let [saved-groups (subscribe [::subs/saved-groups])]
    (do
     (js/console.log "saved-groups: " @saved-groups)
     [utils/button-table
       (map utils/keyword->displayable (keys @saved-groups))
       2
       group-button])))


;; ------------------------------------------------------
;; Sidebar
;; - 'control center' for exploring paintings
;; ------------------------------------------------------


(defn sidebar []
  [rc/v-box
    :children [[constraints/constraints] ; genre, school, timeframe constraints
               [ui-buttons]
               [constraints/concept-typeahead]
               [constraints/selected-concepts]
               [constraints/artist-typeahead]
               [constraints/selected-artists]
               [saved-groups]]])
