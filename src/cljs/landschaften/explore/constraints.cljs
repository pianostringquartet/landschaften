(ns landschaften.explore.constraints
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.explore.explore-subs :as explore-subs]
            [landschaften.explore.explore-events :as explore-events]
            [landschaften.view-utils :as utils]
            [landschaften.semantic-ui :as semantic-ui]
            [landschaften.helpers :as helpers]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]))


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
  (let [genre-choices    (subscribe [::explore-subs/all-types])
        genre-selections (subscribe [::explore-subs/types])]
    [->selection-list
     (apply sorted-set @genre-choices)
     @genre-selections
     #(dispatch [::explore-events/update-selected-types %])]))


(defn school-constraints []
  (let [school-choices   (subscribe [::explore-subs/all-schools])
        selected-schools (subscribe [::explore-subs/school-constraints])]
    [->selection-list
     (apply sorted-set @school-choices)
     @selected-schools
     #(dispatch [::explore-events/update-selected-schools %])]))


(defn timeframe-constraints []
  (let [timeframe-choices   (subscribe [::explore-subs/all-timeframes])
        selected-timeframes (subscribe [::explore-subs/timeframe-constraints])]
    [->selection-list
     (apply sorted-set @timeframe-choices)
     @selected-timeframes
     #(dispatch [::explore-events/update-selected-timeframes %])]))


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
        concepts (subscribe [::explore-subs/all-concepts])]
    (fn deck-search-typeahead []
      [:> semantic-ui/search
       {:on-result-select #(on-result-select text-val
                                             (fn [] (dispatch [::explore-events/update-selected-concepts (get-result %2)])))
        :on-search-change #(on-search-change @concepts text-val results %2)
        :placeholder      "Search for concepts"
        :results          @results
        :value            @text-val}])))


(defn artist-typeahead []
  (let [text-val (r/atom "")
        results  (r/atom #{})                               ; semantic-ui-react expects 'results' as "array of {:title :description}"
        artists  (subscribe [::explore-subs/all-artists])]
    (fn artist-search []
      [:> semantic-ui/search
       {:on-result-select #(on-result-select text-val
                                             (fn [] (dispatch [::explore-events/update-selected-artists (get-result %2)])))
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
                         :on-click #(dispatch [::explore-events/remove-selected-concept concept])}]
   concept])


(defn artist-button [artist]
  [:> semantic-ui/button
   {:color         "teal"
    :icon          true
    :labelPosition "right"
    :style         {:border-radius "30px" :padding "8px"}}
   [:> semantic-ui/icon {:name     "close"
                         :on-click #(dispatch [::explore-events/remove-selected-artist artist])}]
   artist])


(defn selected-concepts []
  (let [selected-concepts (subscribe [::explore-subs/concept-constraints])]
    [utils/bubble-table (map concept-button @selected-concepts) 2]))


(defn selected-artists []
  (let [selected-artists (subscribe [::explore-subs/artist-constraints])]
    [utils/bubble-table
     (map artist-button @selected-artists)
     2]))


(defn accordion-constraint [active? title on-title-click component selections]
  (let [panel {:key title
               :active active?
               :title {:content title}
               :onTitleClick on-title-click
               :content {:content (r/as-component [component])}}]
    [:div
     [:> semantic-ui/accordion {:panels (vector panel)}]
     (when (not active?)
       (utils/as-semantic-ui-list-items (for [selection selections]
                                          ^{:key (str selection "accordion-constraint-key")}
                                          [:p {:style {:color "red"}} selection])))]))


(defn accordion-constraints []
  (let [active-accordion-constraint (subscribe [::explore-subs/active-accordion-constraint])]
    (fn []
      [:div
       (let [genre-active? (= @active-accordion-constraint "genre constraints")]
         [accordion-constraint
            genre-active?
            "genre constraints"
            #(dispatch [::explore-events/active-accordion-constraint-updated (when-not genre-active? "genre constraints")])
            genre-constraints
            @(subscribe [::explore-subs/types])])
       (let [timeframe-active? (= @active-accordion-constraint "timeframe constraints")]
         [accordion-constraint timeframe-active?
            "timeframe constraints"
            #(dispatch [::explore-events/active-accordion-constraint-updated (when-not timeframe-active? "timeframe constraints")])
            timeframe-constraints
            @(subscribe [::explore-subs/timeframe-constraints])])
       (let [school-active? (= @active-accordion-constraint "school constraints")]
         [accordion-constraint school-active?
            "school constraints"
            #(dispatch [::explore-events/active-accordion-constraint-updated (when-not school-active? "school constraints")])
            school-constraints
            @(subscribe [::explore-subs/school-constraints])])])))
