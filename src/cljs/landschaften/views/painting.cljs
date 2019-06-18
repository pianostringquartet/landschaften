(ns landschaften.views.painting
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.specs :as specs]
            [landschaften.events :as events]
            [landschaften.semantic-ui :as semantic-ui]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]
            [landschaften.views.utils :as utils]))


(>defn info [painting]
  [::specs/painting => vector?]
  (let [->ui-label      (fn [[k v]] [rc/label :label (str v ": " (k painting))])
        info-categories {:title     "title"
                         :author    "artist"
                         :date      "date"
                         :timeframe "timeframe"
                         :type      "genre"
                         :school    "school"}]
    [rc/v-box :children (mapv ->ui-label info-categories)]))


(>defn image! [jpg]
  [string? => vector?]
  [:> semantic-ui/image
   {:fluid    true
    :wrapped  true
    :src      jpg
    :style    {:padding "8px"}
    :on-click #(dispatch [::events/toggle-image-zoomed])}])


(>defn image-modal! [jpg zoomed?]
  [string? boolean? => vector?]
  [:> semantic-ui/modal {:open                    zoomed?
                         :close-on-document-click true
                         :centered                false
                         :on-close                #(dispatch [::events/toggle-image-zoomed])
                         :image                   true
                         :content                 (r/as-component [image! jpg])}])


(>defn prev-slide-button! [painting]
  [::specs/painting => vector?]
  [:> semantic-ui/icon
   {:name "caret left" :size "big" :on-click #(dispatch [::events/go-to-previous-slide painting])}])


(>defn next-slide-button! [painting]
  [::specs/painting => vector?]
  [:> semantic-ui/icon
   {:name "caret right" :size "big" :on-click #(dispatch [::events/go-to-next-slide painting])}])


;;; TODO: highlight in blue instead of green (":positive" param)
(defn concept-table-row [current-concepts {:keys [name value]}]
  {:pre [(string? name) (float? value)]}
  ^{:key name}
  [:> semantic-ui/table-row {:positive (contains? current-concepts name)}
   [:> semantic-ui/table-cell
    {:on-click #(dispatch [::events/toggle-concept-selection name])}
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
   [rc/label :style {:color "lightGrey"} :label "Click to add as search term:"]
   [concept-table painting selected-concepts]])


(>defn painting-details
  "Details about a painting: image, author, concepts etc."
  [painting zoomed? selected-concepts]
  [::specs/painting boolean? ::specs/concept-constraints => vector?]
  [:> semantic-ui/grid {:columns 4 :stackable true :vertical-align "middle"}
   [:> semantic-ui/grid-column {:width 1} [prev-slide-button! painting]]
   [:> semantic-ui/grid-column {:width 8} [painting-details-image (:jpg painting) zoomed?]]
   [:> semantic-ui/grid-column {:width 6} [painting-details-info painting selected-concepts]]
   [:> semantic-ui/grid-column {:width 1} [next-slide-button! painting]]])


(defn painting-modal! [painting show?]
  ;[::specs/painting boolean? => vector?]
  (let [zoomed?           (subscribe [::subs/image-zoomed?])
        selected-concepts (subscribe [::subs/concept-constraints])]
    [:> semantic-ui/modal {:open                    show?
                           :close-on-document-click true    ; not working?
                           :close-icon              true
                           :centered                false
                           :on-close                #(dispatch [::events/toggle-slideshow])}
     (do
       (utils/log "@zoomed?: " @zoomed?)
       [:> semantic-ui/modal-content {:image true}
        [painting-details painting @zoomed? @selected-concepts]])]))


(check)