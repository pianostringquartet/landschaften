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
  [rc/v-box :children [[rc/label :label label :class "h4"]
                       [selection-list-from-set
                         choices
                         selections
                         on-change]]])

;; should this component access the state directly?
(defn selection-lists []
  (let [types (subscribe [::subs/types])
        selected-types (subscribe [::subs/selected-types])
        schools (subscribe [::subs/schools])
        selected-schools (subscribe [::subs/selected-schools])
        timeframes (subscribe [::subs/timeframes])
        selected-timeframes (subscribe [::subs/selected-timeframes])]
    [rc/h-box
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
      :on-click #(js/console.log "search-button clicked")
      :class "btn btn-primary" ; Bootstrap
      :style {:border-radius "30px"}]) ; curvier corners

(defn suggestions-for-search [s coll]
  (into []
    (take 16
      (for [n coll
            :when (re-find (re-pattern (str "(?i)" s)) n)]
        ; {:name n}))))
        n))))


(suggestions-for-search "cast" sample/sample-concepts)
; (fn [s
;          (into []
;                (take 16
;                      (for [n md-icon-names
;                            :when (re-find (re-pattern (str "(?i)" s)) n)]
;                        (md-icon-result n))))])

(defn concept-typeahead []
  ; [rc/label :label "fuzzy concept search"])
  (let [concepts (subscribe [::subs/concepts])]
        ; model (r/atom "love")]
    [rc/typeahead
      ; :model (r/atom {})
      ; :model model
      :data-source #(suggestions-for-search % @concepts)
      :placeholder "Add concept to search by"
      :change-on-blur? true
      :on-change #(do
                    (js/console.log "concept typeahead: chose: " %)
                    (dispatch [::events/update-selected-concepts %]))]))
                    ; (reset! model ""))]))
                    ;; this clears the model everytime you type,
                    ;; after initially selecting something


      ; :suggestion-to-string #(:name %)
      ; :render-suggestion
      ;   (fn [{:keys [name]}]
      ;     [:span
      ;      [:i {:style {:width "40px"} :class (@concepts name)}]
      ;      name])

     ; :immediate-model-update? true]))

(defn selected-concepts []
  (let [selected-concepts (subscribe [::subs/selected-concepts])]
    [rc/v-box
     :children (map
                 (fn [concept] [rc/label :label concept])
                 @selected-concepts)]))

(defn sidebar []
  [rc/v-box
    :padding "10px"
    :children [[selection-lists]
               [search-button]
               [concept-typeahead]
               [selected-concepts]]])
