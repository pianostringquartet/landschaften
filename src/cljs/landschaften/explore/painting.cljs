(ns landschaften.explore.painting
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.explore.explore-subs :as explore-subs]
            [landschaften.specs :as specs]
            [landschaften.explore.explore-events :as explore-events]
            [landschaften.semantic-ui :as semantic-ui]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]))


(>defn info [painting]
  [::specs/painting => vector?]
  (let [->ui-label      (fn [[k v]] [rc/label :label (str v ": " (k painting))])
        info-categories {:title     "title"
                         :author    "artist"
                         :date      "date"
                         :timeframe "timeframe"
                         :genre      "genre"
                         :school    "school"}]
    [rc/v-box :children (mapv ->ui-label info-categories)]))


(>defn image! [jpg]
  [string? => vector?]
  [:> semantic-ui/image
   {:fluid    true
    :wrapped  true
    :src      jpg
    :style    {:padding "8px"}
    :on-click #(dispatch [::explore-events/toggle-image-zoomed])}])


(>defn image-modal! [jpg zoomed?]
  [string? boolean? => vector?]
  [:> semantic-ui/modal {:open                    zoomed?
                         :close-on-document-click true
                         :centered                false
                         :on-close                #(dispatch [::explore-events/toggle-image-zoomed])
                         :image                   true
                         :content                 (r/as-component [image! jpg])}])


(>defn prev-painting-button! [painting]
  [::specs/painting => vector?]
  [:> semantic-ui/icon
   {:name "caret left" :size "big" :on-click #(dispatch [::explore-events/go-to-previous-painting painting])}])


(>defn next-button-button! [painting]
  [::specs/painting => vector?]
  [:> semantic-ui/icon
   {:name "caret right" :size "big" :on-click #(dispatch [::explore-events/go-to-next-painting painting])}])


(defn concept-table-row [current-concepts {:keys [name value]}]
  {:pre [(string? name) (float? value)]}
  ^{:key name}
  [:> semantic-ui/table-row
       {:style (when (contains? current-concepts name)
                 {:color "#fff" :background-color "teal"})}
   [:> semantic-ui/table-cell
    {:on-click #(dispatch [::explore-events/toggle-concept-selection name])}
    name]
   [:> semantic-ui/table-cell
    (goog.string/format "%.2f" value)]])


(>defn concept-table [painting selected-concepts]
  [::specs/painting ::specs/concept-constraints => vector?]
  (let [concepts (take 15 (reverse (sort-by :value (:concepts painting))))]
    [:> semantic-ui/table {:selectable true :compact "very" :size "small"}
     [:> semantic-ui/table-body
      (for [concept concepts]
        ^{:key (:name concept)} (concept-table-row selected-concepts concept))]]))


(>defn painting-details-image [jpg zoomed?]
  [string? boolean? => vector?]
  [:div
   [image! jpg]
   [image-modal! jpg zoomed?]
   [rc/label :style {:color "lightGrey"} :label "Click to enlarge"]])


(>defn painting-details-info [painting selected-concepts]
  [::specs/painting ::specs/concept-constraints => vector?]
  [:div
   [info painting]
   [concept-table painting selected-concepts]
   [rc/label :style {:color "lightGrey"} :label "Click to add as search term"]])


(>defn painting-details-desktop
  "Details about a painting: image, author, concepts etc."
  [painting zoomed? selected-concepts]
  [::specs/painting boolean? ::specs/concept-constraints => vector?]
  [:> semantic-ui/grid {:columns 4 :stackable true :vertical-align "middle"}
   [:> semantic-ui/grid-column {:width 1} [prev-painting-button! painting]]
   [:> semantic-ui/grid-column {:width 8} [painting-details-image (:jpg painting) zoomed?]]
   [:> semantic-ui/grid-column {:width 6} [painting-details-info painting selected-concepts]]
   [:> semantic-ui/grid-column {:width 1} [next-button-button! painting]]])


(>defn painting-details-mobile
  "Details about a painting: image, author, concepts etc."
  [painting zoomed? selected-concepts]
  [::specs/painting boolean? ::specs/concept-constraints => vector?]
  [:> semantic-ui/grid {:columns 4
                        :stackable true
                        :vertical-align "middle"
                        :centered true}
   [:> semantic-ui/grid-column {:width 8} [painting-details-image (:jpg painting) zoomed?]]
   [:> semantic-ui/grid-column {:width 1}
    [:> semantic-ui/slist
      [prev-painting-button! painting]
      [next-button-button! painting]]]
   [:> semantic-ui/grid-column {:width 6} [painting-details-info painting selected-concepts]]])


(defn painting-modal [painting show?]
  (let [zoomed?           (subscribe [::explore-subs/image-zoomed?])
        selected-concepts (subscribe [::explore-subs/concept-constraints])]
    [:> semantic-ui/modal {:open                    show?
                           :close-on-document-click true
                           :close-icon              true
                           :centered                false
                           :on-close                #(dispatch [::explore-events/toggle-painting-modal])}
       [:> semantic-ui/modal-content {:image true}
        [:> semantic-ui/slist
         [:> semantic-ui/responsive
          {:max-width 799}
          [painting-details-mobile painting @zoomed? @selected-concepts]]
         [:> semantic-ui/responsive
          {:min-width 800}
          [painting-details-desktop painting @zoomed? @selected-concepts]]]]]))


;(check)