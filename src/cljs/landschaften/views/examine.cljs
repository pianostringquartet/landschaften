(ns landschaften.views.examine
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.db :as db]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.views.utils :as utils]
            [landschaften.events :as events]
            [landschaften.semantic-ui :as semantic-ui :refer [container grid grid-column grid-row button message segment]]))


(defn info [painting]
  (let [->ui-label      (fn [[k v]] [rc/label :label (str v ": " (k painting))])
        info-categories {:title     "title"
                         :author    "artist"
                         :date      "date"
                         :timeframe "timeframe"
                         :type      "genre"
                         :school    "school"}]
    [rc/v-box :children (mapv ->ui-label info-categories)]))




#_(defn bubble-button [{:keys [name value]}]
    {:pre [(string? name)]}
    (let [formatted-number (goog.string/format "%.2f" value)]
      [rc/button
       :label (str name " (" formatted-number ")")
       :on-click #(dispatch [::events/update-selected-concepts name])
       :class "btn btn-info"                                ; Bootstrap
       :style {:border-radius "30px"}]))                    ; curvier corners

#_(defn responsive-image [jpg]
    [utils/max-responsive-image
     ;[utils/responsive-image
     jpg
     ;utils/widths->vw
     utils/mid-widths->vw
     ;utils/larger-widths->vw
     #(dispatch [::events/toggle-image-zoomed])])


(defn bubble-button [{:keys [name value]}]
  (let [formatted-number (goog.string/format "%.2f" value)]
    [:> button
     {:color    "teal"
      :style    {:border-radius "20px" :padding "6px"}
      :on-click #(dispatch [::events/update-selected-concepts name])}
     (str name " (" formatted-number ")")]))

;; NEEDS TO BE UPDATED TO USE Semantic modal

(defn modal-image-view [jpg zoomed?]
  [:> semantic-ui/modal {:open                    zoomed?
                         :close-on-document-click true
                         :centered                false
                         :on-close                #(dispatch [::events/toggle-image-zoomed])}
   [:> semantic-ui/modal-content {:image true}
    [:> semantic-ui/image
     {:fluid    true
      :wrapped  true
      :src      jpg
      :style    {:padding "8px"}
      :on-click #(dispatch [::events/toggle-image-zoomed])}]]])

#_(defn modal-image-view [jpg]
    [rc/modal-panel
     :backdrop-on-click #(dispatch [::events/toggle-image-zoomed])
     :child [:img
             {:on-click #(dispatch [::events/toggle-image-zoomed])
              :style    {:max-height "600px"}
              :src      jpg}]])


;; just pass in jpg?
(defn image [painting zoomed?]
  ;[:div
  [:> semantic-ui/image
   {:fluid    true
    :wrapped  true                                          ;; added for semantic ui modal
    :src      (:jpg painting)
    :style    {:padding "8px"}
    :on-click #(dispatch [::events/toggle-image-zoomed])}])
;[modal-image-view (:jpg painting) zoomed?]])

;(when image-zoomed? [modal-image-view (:jpg painting)])]]])


;; the 'show' value needs to be determined by a different value in db

;[:img {:on-click #(dispatch [::events/hide-max-image])
;       :style {:max-width "300px"}
;       :src (:jpg painting)}]


(defn prev-slide-button [painting]
  [:> semantic-ui/icon
   {:name "caret left" :size "big" :on-click #(dispatch [::events/go-to-previous-slide painting])}])

(defn next-slide-button [painting]
  [:> semantic-ui/icon
   {:name "caret right" :size "big" :on-click #(dispatch [::events/go-to-next-slide painting])}])

;; redo with semantic ui?
#_(defn concept-table [painting]
    [utils/button-table
     (reverse (sort-by :value (:concepts painting)))
     3
     bubble-button])



;; BETTER?: accordion, unfolds to show progress bar for each concept
#_(defn concept-table [painting]
    {:pre [(s/valid? ::specs/painting painting)]}
    (let [concepts (take 20 (reverse (sort-by :value (:concepts painting))))
          ->row    (fn [concept] [:> grid-column {:width 4}
                                  (bubble-button concept)])]
      [:> grid {:columns 3 :stackable true}
       (for [concept concepts]
         (->row concept))]))

#_(defn concept-table [painting]
    {:pre [(s/valid? ::specs/painting painting)]}
    (let [concepts (take 20 (reverse (sort-by :value (:concepts painting))))
          ->row    (fn [xs] [:> grid-row
                             (into [] (map bubble-button xs))])]
      [:> grid {:stackable true}
       (for [xs (partition-all 3 concepts)]
         [:grid-row
          (when-let [x1 (first xs)] (bubble-button x1))
          (when-let [x2 (second xs)] (bubble-button x2))
          (when-let [x3 (nth xs 2 nil)] (bubble-button x3))])]))
;(->row xs))]))


#_(defn concept-table [painting]
    {:pre [(s/valid? ::specs/painting painting)]}
    (let [concepts (take 20 (reverse (sort-by :value (:concepts painting))))
          ->row    (fn [concept]
                     [:> grid-column
                      [:> semantic-ui/progress {:size    "small"
                                                :percent (* 100 (:value concept))}]])]
      [:> grid {:columns 1 :stackable true}
       (for [concept concepts]
         (->row concept))]))


(defn concept-table-row [current-concepts {:keys [name value]}]
  {:pre [(string? name) (float? value)]}
  (do
    (js/console.log "concept-table-row called")
    (let [];current-concepts (subscribe [::subs/concept-constraints])
          ;selected? (contains? @current-concepts name)]
          ;selected? (contains? current-concepts name)]
      [:> semantic-ui/table-row {:positive (contains? current-concepts name)}
       [:> semantic-ui/table-cell
        {:on-click #(dispatch [::events/toggle-concept-selection name])}
        name]
       [:> semantic-ui/table-cell
        (goog.string/format "%.2f" value)]])))


;; use semantic ui table
;; when you click on a concept / the concept is something you're searching for already,
;; the concept name (and value?) should be in blue
(defn concept-table [painting selected-concepts]
  {:pre [(s/valid? ::specs/painting painting)]}
  (let [concepts (take 15 (reverse (sort-by :value (:concepts painting))))
        ;current-concepts (subscribe [::subs/concept-constraints])
        ->row    (fn [concept] [:> semantic-ui/table-row
                                [:> semantic-ui/table-cell
                                 {:on-click #(js/console.log "clicked...")}
                                 (:name concept)]
                                [:> semantic-ui/table-cell
                                 (goog.string/format "%.2f" (:value concept))]])]
    [:> semantic-ui/table {:selectable true :compact "very" :size "small"}
     [:> semantic-ui/table-body
      (for [concept concepts] ^{:key (:name concept)} (concept-table-row selected-concepts concept))]]))


;; ":columns 2" means "1 row has 2 or less columns"
#_(defn semantic-details [painting image-zoomed?]
    {:pre [(s/valid? ::specs/painting painting)]}
    (let [prev           [:> grid-column {:width 1}
                          [prev-slide-button painting]]
          next           [:> grid-column {:width 1}
                          [next-slide-button painting]]
          painting-image [:> grid-column {:width 8}
                          [image painting image-zoomed?]
                          [modal-image-view (:jpg painting) image-zoomed?]
                          [rc/label
                           :style {:color "lightGrey"}
                           :label "Click to enlarge"]]
          painting-info  [:> grid-column {:width 6}
                          [info painting]
                          [rc/label
                           :style {:color "lightGrey"}
                           :label "Click to add as search term:"]
                          [concept-table painting]]]
      [:> grid {:columns 4 :stackable true :vertical-align "middle"}
       prev
       painting-image
       painting-info
       next]))

(defn semantic-details [painting image-zoomed? selected-concepts]
  {:pre [(s/valid? ::specs/painting painting)
         (s/valid? ::specs/concept-constraints selected-concepts)]}
  [:> grid {:columns 4 :stackable true :vertical-align "middle"}
     [:> grid-column {:width 1}
      [prev-slide-button painting]]
     [:> grid-column {:width 8}
      [image painting image-zoomed?]
      [modal-image-view (:jpg painting) image-zoomed?]
      [rc/label
       :style {:color "lightGrey"}
       :label "Click to enlarge"]]
     [:> grid-column {:width 6}
      [info painting]
      [rc/label
       :style {:color "lightGrey"}
       :label "Click to add as search term:"]
      [concept-table painting selected-concepts]]
     [:> grid-column {:width 1}
      [next-slide-button painting]]])

;; NEED TO ADD BACK PT-INFO, CONCEPT-TABLE ETC.
;; MAY NEED TO DO THE 'AS REACT ELEM' STUFF

(defn details-slideshow-modal-image [painting show?]
  {:pre [(s/valid? ::specs/painting painting)]}
  (let [image-zoomed? (subscribe [::subs/image-zoomed?])
        selected-concepts (subscribe [::subs/concept-constraints])]
    [:> semantic-ui/modal {:open                    show?
                           :close-on-document-click true    ; not working?
                           :close-icon              true
                           :centered                false
                           ;:size "small"
                           :on-close                #(dispatch [::events/toggle-slideshow])}
     [:> semantic-ui/modal-content {:image true}
      [semantic-details painting @image-zoomed? @selected-concepts]]]))