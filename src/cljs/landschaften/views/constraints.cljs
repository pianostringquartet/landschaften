(ns landschaften.views.constraints
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.events :as events]
            [landschaften.views.utils :as utils]
            [landschaften.semantic-ui :as semantic-ui]))


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
     :choices choices                                       ; must be coll of maps
     :model selections
     :on-change #(on-change %)]))


(defn labeled-selection [label choices selections on-change]
  [rc/v-box
   :children [[rc/label :label label :class "h5"]
              [->selection-list choices selections on-change]]])


(defn genre-constraints []
  (let [genre-choices    (subscribe [::subs/all-types])
        genre-selections (subscribe [::subs/types])]
    [labeled-selection
     "genres"
     (apply sorted-set @genre-choices)
     @genre-selections
     #(dispatch [::events/update-selected-types %])]))


(defn school-constraints []
  (let [school-choices   (subscribe [::subs/all-schools])
        selected-schools (subscribe [::subs/school-constraints])]
    [labeled-selection
     "schools"
     (apply sorted-set @school-choices)
     @selected-schools
     #(dispatch [::events/update-selected-schools %])]))


(defn timeframe-constraints []
  (let [timeframe-choices   (subscribe [::subs/all-timeframes])
        selected-timeframes (subscribe [::subs/timeframe-constraints])]
    [labeled-selection
     "timeframes"
     (apply sorted-set @timeframe-choices)
     @selected-timeframes
     #(dispatch [::events/update-selected-timeframes %])]))


(defn constraints []
  [:> semantic-ui/grid
   [:> semantic-ui/grid-row
    [genre-constraints]
    [school-constraints]
    [timeframe-constraints]]])

;(defn constraints []
;  [rc/h-box
;    :gap "8px"
;    :children [[genre-constraints]
;               [school-constraints]
;               [timeframe-constraints]]])


;; ------------------------------------------------------
;; Concepts, Artists
;; - many choices, so selected using typeahead
;; ------------------------------------------------------

(defn suggestions [input options n-suggestions]
  (let [matches-input?
        (fn [x] (re-find (re-pattern (str "(?i)" input)) x))]
    (into #{}
          (take n-suggestions
                (for [option options :when (matches-input? (utils/replace-special-chars option))]
                  {:title option})))))

(defn on-search-change [options search-val result-set obj]
  (let [user-input (.-value obj)]
    (do
      (reset! search-val user-input)
      (reset! result-set (suggestions user-input options 6)))))

(defn get-result [semantic-ui-object]
  {:post [(string? %)]}
  (get-in (js->clj semantic-ui-object) ["result" "title"]))

(defn concept-typeahead []
  (let [text-val         (r/atom "")
        results          (r/atom #{})                       ; semantic-ui-react expects 'results' as "array of {:title :description}"
        concepts         (subscribe [::subs/all-concepts])]
    (fn deck-search-typeahead []
      [:> semantic-ui/search
       {:on-result-select #(dispatch [::events/update-selected-concepts (get-result %2)])
        :on-search-change #(on-search-change @concepts text-val results %2)
        :results          @results
        :value            @text-val}])))

(defn artist-typeahead []
  (let [text-val         (r/atom "")
        results          (r/atom #{})                       ; semantic-ui-react expects 'results' as "array of {:title :description}"
        artists          (subscribe [::subs/all-artists])]
    (fn artist-search []
      [:> semantic-ui/search
       {:on-result-select #(dispatch [::events/update-selected-artists (get-result %2)])
        :on-search-change #(on-search-change @artists text-val results %2)
        :results          @results
        :value            @text-val}])))


(defn closable-button-label [label]
  [rc/h-box
   :align :center
   :gap "8px"
   :children [[rc/label :label label]
              [:i.zmdi.zmdi-close]]])


(defn concept-button [concept]
  [rc/button
   :label [closable-button-label concept]
   :on-click #(dispatch [::events/remove-selected-concept concept])
   :class "btn btn-info"                                    ; Bootstrap
   :style {:border-radius "30px"}])                         ; curvier corners


(defn artist-button [artist]
  [rc/button
   :label [closable-button-label artist]
   :on-click #(dispatch [::events/remove-selected-artist artist])
   :class "btn btn-warning"                                 ; Bootstrap
   :style {:border-radius "30px"}])                         ; curvier corners


(defn selected-concepts []
  (let [selected-concepts (subscribe [::subs/concept-constraints])]
    [utils/button-table @selected-concepts 3 concept-button]))


(defn selected-artists []
  (let [selected-artists (subscribe [::subs/artist-constraints])]
    [utils/button-table @selected-artists 2 artist-button]))


;(defn selected-artists []
;  (let [selected-artists (subscribe [::subs/artist-constraints])
;        buttons (map artist-button @selected-artists)]
;    [mui/material-grid buttons 2]))


;(defn selected-artists []
;  (let [selected-artists (subscribe [::subs/artist-constraints])
;        buttons (map artist-button @selected-artists)]
;    [semantic-ui/slist {:horizontal true}
;     buttons]))

