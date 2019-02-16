(ns landschaften.views.sidebar
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.db :as db]
            [clojure.spec.alpha :as s]
            [landschaften.events :as events]
            [landschaften.specs :as specs]
            [landschaften.sample :as sample]))

;; sidebar with mostly dropdown selects etc.

(defn selection-list-from-set [choice-set selected-set on-change]
  {:pre [set? choice-set]}
  (let [choices (map (fn [v] {:id v :label v}) choice-set)]
    [rc/selection-list
      :choices choices ; must be coll of maps
      :model selected-set
      :on-change #(do
                    (js/console.log "on-change received: " %)
                    (on-change %))]))

(defn labeled-selection [label choices selections on-change]
  [rc/v-box
      :children [[rc/label :label label :class "h4"]
                 [selection-list-from-set choices selections on-change]]])

;; should this component access the state directly?
(defn selection-lists []
  (let [types (subscribe [::subs/types])
        selected-types (subscribe [::subs/selected-types])
        schools (subscribe [::subs/schools])
        selected-schools (subscribe [::subs/selected-schools])
        timeframes (subscribe [::subs/timeframes])
        selected-timeframes (subscribe [::subs/selected-timeframes])]
    [rc/h-box
      :gap "8px"
      :children [[labeled-selection
                   "genres"
                   (apply sorted-set @types)
                   @selected-types
                   #(dispatch [::events/update-selected-types %])]
                 [labeled-selection
                   "schools"
                   (apply sorted-set @schools)
                   @selected-schools
                   #(dispatch [::events/update-selected-schools %])]
                 [labeled-selection
                   "timeframes"
                   (apply sorted-set @timeframes)
                   @selected-timeframes
                   #(dispatch [::events/update-selected-timeframes %])]]]))

(defn search-button []
  [rc/button
      :label "SEARCH"
      ; :on-click #(js/console.log "search-button clicked")
      :on-click #(dispatch [::events/query])
      :class "btn btn-success"]) ; Bootstrap

(defn search-suggestions [s coll]
  (into []
    (take 16
    ; (take 32
      (for [n coll
            :when (re-find (re-pattern (str "(?i)" s)) n)]
        n))))

(defn concept-typeahead []
  (let [concepts (subscribe [::subs/concepts])]
    [rc/typeahead
      :data-source #(search-suggestions % @concepts)
      :placeholder "Add concept to search by"
      :change-on-blur? true
      :on-change #(do
                    (js/console.log "concept typeahead: chose: " %)
                    (dispatch [::events/update-selected-concepts %]))]))
                    ; (reset! model ""))]))
                    ;; this clears the model everytime you type,
                    ;; after initially selecting something

(defn artist-typeahead []
  (let [artists (subscribe [::subs/artists])]
    [rc/typeahead
      :data-source #(search-suggestions % @artists)
      :placeholder "Add artist(s) to search by"
      :change-on-blur? true
      :on-change #(do
                    (js/console.log "artist typeahead: chose: " %)
                    (dispatch [::events/update-selected-artists %]))]))


(defn bubble-button [concept]
  [rc/button
      :label concept
      :on-click #(dispatch [::events/remove-selected-concept concept])
      :class "btn btn-info" ; Bootstrap
      :style {:border-radius "30px"}]) ; curvier corners

;; better: pass in the color (:class) and the dispatch-event (:on-click)
(defn artist-button [artist]
  [rc/button
      :label artist
      :on-click #(dispatch [::events/remove-selected-artist artist])
      :class "btn btn-warning" ; Bootstrap
      :style {:border-radius "30px"}]) ; curvier corners

(defn concepts-table [concepts]
  (let [concepts-bubbles (partition-all 3 (map bubble-button concepts))
        ->ui-row (fn [xs] [rc/h-box :gap "8px" :width "500px" :children (into [] xs)])]
    [rc/v-box
      :gap "8px"
      :justify :center
      :children (mapv ->ui-row concepts-bubbles)]))

(defn artists-table [artists]
  (let [artists-bubbles (partition-all 3 (map artist-button artists))
        ->ui-row (fn [xs] [rc/h-box :gap "8px" :width "500px" :children (into [] xs)])]
    [rc/v-box
      :gap "8px"
      :justify :center
      :children (mapv ->ui-row artists-bubbles)]))

(defn selected-concepts []
  (let [selected-concepts (subscribe [::subs/selected-concepts])]
    [concepts-table @selected-concepts]))

(defn selected-artists []
  (let [selected-artists (subscribe [::subs/selected-artists])]
    (do
     (js/console.log "selected-artists: " @selected-artists)
     [artists-table @selected-artists])))

(defn clear-button []
  [rc/button
    :label "CLEAR"
    :on-click #(dispatch [::events/selections-cleared])
    :class "btn btn-danger"])

(defn sidebar []
  [rc/v-box
    :padding "10px"
    :gap "10px"
    :children [[selection-lists]
               [rc/h-box :gap  "30px" :children [[clear-button] [search-button]]]
               [concept-typeahead]
               [selected-concepts]
               [artist-typeahead]
               [selected-artists]]])
