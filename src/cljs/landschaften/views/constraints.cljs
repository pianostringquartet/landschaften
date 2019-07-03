(ns landschaften.views.constraints
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.events.explore-events :as expore-events]
            [landschaften.views.utils :as utils]
            [landschaften.semantic-ui :as semantic-ui]
            [landschaften.helpers :as helpers]))


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


(defn labeled-constraints [label selection-list-component]
  [:> semantic-ui/slist
   [:> semantic-ui/slist-item [rc/label :label label :class "h5"]]
   [:> semantic-ui/slist-item [selection-list-component]]])


(defn genre-constraints []
  (let [genre-choices    (subscribe [::subs/all-types])
        genre-selections (subscribe [::subs/types])]
    [->selection-list
     (apply sorted-set @genre-choices)
     @genre-selections
     #(dispatch [::expore-events/update-selected-types %])]))


(defn school-constraints []
  (let [school-choices   (subscribe [::subs/all-schools])
        selected-schools (subscribe [::subs/school-constraints])]
    [->selection-list
     (apply sorted-set @school-choices)
     @selected-schools
     #(dispatch [::expore-events/update-selected-schools %])]))


(defn timeframe-constraints []
  (let [timeframe-choices   (subscribe [::subs/all-timeframes])
        selected-timeframes (subscribe [::subs/timeframe-constraints])]
    [->selection-list
     (apply sorted-set @timeframe-choices)
     @selected-timeframes
     #(dispatch [::expore-events/update-selected-timeframes %])]))


(defn constraints []
  [:> semantic-ui/grid {:padded true}
   [labeled-constraints "genres" genre-constraints]
   [labeled-constraints "schools" school-constraints]
   [labeled-constraints "timeframes" timeframe-constraints]])


;; ------------------------------------------------------
;; Concepts, Artists
;; - many choices, so selected using typeahead
;; ------------------------------------------------------

(defn suggestions [input options n-suggestions]
  (let [matches-input?
        (fn [x] (re-find (re-pattern (str "(?i)" input)) x))]
    (into #{}
          (take n-suggestions
                (for [option options :when (matches-input? (helpers/replace-special-chars option))]
                  {:title option})))))


(defn on-search-change [options search-val result-set obj]
  (let [user-input (.-value obj)]
    (do
      (reset! search-val user-input)
      (reset! result-set (suggestions user-input options 6)))))

(defn get-result [semantic-ui-object]
  {:post [(string? %)]}
  (get-in (js->clj semantic-ui-object) ["result" "title"]))

(defn on-result-select [text-val-atom dispatch-event]
  (do
    (dispatch-event)
    (reset! text-val-atom "")))

(defn concept-typeahead []
  (let [text-val (r/atom "")
        results  (r/atom #{})                               ; semantic-ui-react expects 'results' as "array of {:title :description}"
        concepts (subscribe [::subs/all-concepts])]
    (fn deck-search-typeahead []
      [:> semantic-ui/search
       {:on-result-select #(on-result-select text-val
                                             (fn [] (dispatch [::expore-events/update-selected-concepts (get-result %2)])))
        :on-search-change #(on-search-change @concepts text-val results %2)
        :placeholder      "Search for concepts"
        :results          @results
        :value            @text-val}])))


(defn artist-typeahead []
  (let [text-val (r/atom "")
        results  (r/atom #{})                               ; semantic-ui-react expects 'results' as "array of {:title :description}"
        artists  (subscribe [::subs/all-artists])]
    (fn artist-search []
      [:> semantic-ui/search
       {:on-result-select #(on-result-select text-val
                                             (fn [] (dispatch [::expore-events/update-selected-artists (get-result %2)])))
        :on-search-change #(on-search-change @artists text-val results %2)
        :placeholder      "Search for artists"
        :results          @results
        :value            @text-val}])))


(defn concept-button [concept]
  [:> semantic-ui/button
   {:color         "teal"
    :icon          true
    :labelPosition "right"
    :style         {:border-radius "30px" :padding "4px"}}
   [:> semantic-ui/icon {:name     "close"
                         :on-click #(dispatch [::expore-events/remove-selected-concept concept])}]
   concept])


(defn artist-button [artist]
  [:> semantic-ui/button
   {:color         "teal"
    :icon          true
    :labelPosition "right"
    :style         {:border-radius "30px" :padding "8px"}}
   [:> semantic-ui/icon {:name     "close"
                         :on-click #(dispatch [::expore-events/remove-selected-artist artist])}]
   artist])


(defn selected-concepts []
  (let [selected-concepts (subscribe [::subs/concept-constraints])]
    [utils/bubble-table (map concept-button @selected-concepts) 2]))


(defn selected-artists []
  (let [selected-artists (subscribe [::subs/artist-constraints])]
    (do
      (js/console.log "selected-artists: " @selected-artists)
      [utils/bubble-table
       (map artist-button @selected-artists)
       2])))


(defn accordion-constraints []
  (let [->accordion-panel (fn [constraint]
                            {:key     (:name constraint)
                             :title   {:content (:name constraint)}
                             :content {:content (r/as-component [(:component constraint)])}})
        constraints       [{:name      "genre constraints"
                            :component genre-constraints}
                           {:name      "school constraints"
                            :component school-constraints}
                           {:name      "timeframe constraints"
                            :component timeframe-constraints}]]
    (fn []
      [:> semantic-ui/accordion
       {:panels (mapv ->accordion-panel constraints)}])))