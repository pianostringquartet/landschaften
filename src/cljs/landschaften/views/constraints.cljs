(ns landschaften.views.constraints
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.events :as events]
            [landschaften.views.utils :as utils]))


;; ------------------------------------------------------
;; Selecting and deselecting search constraints
;; ------------------------------------------------------


;; ------------------------------------------------------
;; Genre, School, Time
;; - few choices, so selected using list
;; ------------------------------------------------------


(defn ->selection-list [choices selections on-change]
  {:pre [(and (set? choices) (set? selections))]}
  (let [choices (map (fn [x] {:id x :label x}) choices)]
    [rc/selection-list
      :choices choices ; must be coll of maps
      :model selections
      :on-change #(do
                    (js/console.log "->selection-list on-change received: " %)
                    (on-change %))]))


(defn labeled-selection [label choices selections on-change]
  [rc/v-box
   :children [[rc/label :label label :class "h5"]
              [->selection-list choices selections on-change]]])


(defn genre-constraints []
  (let [genre-choices (subscribe [::subs/all-types])
        genre-selections (subscribe [::subs/types])]
    [labeled-selection
      "genres"
      (apply sorted-set @genre-choices)
      @genre-selections
      #(dispatch [::events/update-selected-types %])]))


(defn school-constraints []
  (let [school-choices (subscribe [::subs/all-schools])
        selected-schools (subscribe [::subs/schools])]
    [labeled-selection
      "schools"
      (apply sorted-set @school-choices)
      @selected-schools
      #(dispatch [::events/update-selected-schools %])]))


(defn timeframe-constraints []
  (let [timeframe-choices (subscribe [::subs/all-timeframes])
        selected-timeframes (subscribe [::subs/timeframes])]
    [labeled-selection
      "timeframes"
      (apply sorted-set @timeframe-choices)
      @selected-timeframes
      #(dispatch [::events/update-selected-timeframes %])]))


(defn constraints []
  [rc/h-box
    :gap "8px"
    :children [[genre-constraints]
               [school-constraints]
               [timeframe-constraints]]])


;; ------------------------------------------------------
;; Concepts, Artists
;; - many choices, so selected using typeahead
;; ------------------------------------------------------


(defn concept-typeahead []
  (let [concepts (subscribe [::subs/all-concepts])]
    [utils/typeahead
      "Add concept(s) to search by"
      @concepts
      #(do
         (js/console.log "concept typeahead: chose: " %)
         (dispatch [::events/update-selected-concepts %]))]))


(defn artist-typeahead []
  (let [artists (subscribe [::subs/all-artists])]
    [utils/typeahead
      "Add artist(s) to search by"
      @artists
      #(do
         (js/console.log "artist typeahead: chose: " %)
         (dispatch [::events/update-selected-artists %]))]))


(defn concept-button [concept]
  [rc/button
      :label concept
      :on-click #(dispatch [::events/remove-selected-concept concept])
      :class "btn btn-info" ; Bootstrap
      :style {:border-radius "30px"}]) ; curvier corners


(defn artist-button [artist]
  [rc/button
      :label artist
      :on-click #(dispatch [::events/remove-selected-artist artist])
      :class "btn btn-warning" ; Bootstrap
      :style {:border-radius "30px"}]) ; curvier corners


(defn selected-concepts []
  (let [selected-concepts (subscribe [::subs/concepts])]
    [utils/button-table @selected-concepts 3 concept-button]))


(defn selected-artists []
  (let [selected-artists (subscribe [::subs/artists])]
    (do
     (js/console.log "selected-artists: " @selected-artists)
     [utils/button-table @selected-artists 2 artist-button])))
