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
  (let [->ui-label (fn [[k v]] [rc/label :label (str v ": " (k painting))])
        info-categories {:title "title"
                         :author "artist"
                         :date "date"
                         :timeframe "timeframe"
                         :type "genre"
                         :school "school"}]
    [rc/v-box :children (mapv ->ui-label info-categories)]))


(defn bubble-button [{:keys [name value]}]
  (let [formatted-number (goog.string/format "%.2f" value)]
    [:> button
       {:color "teal"
        :style {:border-radius "20px" :padding "4px"}
        :on-click #(dispatch [::events/update-selected-concepts name])}
       (str name " (" formatted-number ")")]))

#_(defn bubble-button [{:keys [name value]}]
    {:pre [(string? name)]}
    (let [formatted-number (goog.string/format "%.2f" value)]
      [rc/button
          :label (str name " (" formatted-number ")")
          :on-click #(dispatch [::events/update-selected-concepts name])
          :class "btn btn-info" ; Bootstrap
          :style {:border-radius "30px"}])) ; curvier corners

#_(defn responsive-image [jpg]
    [utils/max-responsive-image
    ;[utils/responsive-image
      jpg
      ;utils/widths->vw
       utils/mid-widths->vw
       ;utils/larger-widths->vw
      #(dispatch [::events/toggle-image-zoomed])])

(defn modal-image-view [jpg]
  [rc/modal-panel
   :backdrop-on-click #(dispatch [::events/toggle-image-zoomed])
   :child [:img
           {:on-click #(dispatch [::events/toggle-image-zoomed])
            :style {:max-height "600px"}
            :src jpg}]])


;; just pass in jpg?
(defn image [painting zoomed?]
  [:div
   [:> semantic-ui/image
      {:fluid true
       :src (:jpg painting)
       :style {:padding "8px"}
       :on-click #(dispatch [::events/toggle-image-zoomed])}]
   (when zoomed? [modal-image-view (:jpg painting)])])

   ;(when image-zoomed? [modal-image-view (:jpg painting)])]]])


;; the 'show' value needs to be determined by a different value in db

;[:img {:on-click #(dispatch [::events/hide-max-image])
;       :style {:max-width "300px"}
;       :src (:jpg painting)}]

(defn prev-slide-button [painting]
  [rc/md-icon-button
   :md-icon-name "zmdi-arrow-left"
   :size :larger
   :on-click #(dispatch [::events/go-to-previous-slide painting])])

(defn next-slide-button [painting]
  [rc/md-icon-button
   :md-icon-name "zmdi-arrow-right"
   ;:label "Next"
   :size :larger
   :on-click #(dispatch [::events/go-to-next-slide painting])])


;; redo with semantic ui?
(defn concept-table [painting]
  [utils/button-table
   (reverse (sort-by :value (:concepts painting)))
   3
   bubble-button])




;; old version
;(defn details-slideshow-modal-image [painting]
;  {:pre [(s/valid? ::specs/painting painting)]}
;  (let [image-zoomed? (subscribe [::subs/image-zoomed?])]
;    [rc/modal-panel
;     ;:backdrop-on-click #(dispatch [::events/hide-slideshow])
;     :backdrop-on-click #(dispatch [::events/toggle-slideshow])
;     :child [rc/h-box
;             :align :center
;             :justify :between
;             :gap "8px"
;             :children [[prev-slide-button painting]
;                        [examine-painting painting @image-zoomed?]
;                        [next-slide-button]]]]))

;; will re-com modal work with semantic comps?

;; ":columns 2" means "1 row has 2 or less columns"
(defn semantic-details [painting image-zoomed?]
  [:> grid {:columns 4 :stackable true :vertical-align "middle"}
   [:> grid-column {:width 1} ;:vertical-align "middle"} ;{:floated "left"} ;{:width 2}
    [prev-slide-button painting]]
   [:> grid-column {:width 8} ;{:centered true} ;{:floated "center"} ;
    [image painting image-zoomed?]
    [rc/label :style {:color "lightGrey"}
              :label "Click to enlarge"]]
   [:> grid-column {:width 6} ;{:floated "center"}
    ;[:> segment
     [info painting]
     [rc/label :label "Click to add as search term:"]
     [concept-table painting]] ;; need to add concept table
   [:> grid-column {:width 1} ;{:floated "right"}
    [next-slide-button painting]]])


;; semantic ui site examples show columns in a row;
;; but in my app the columns are already stacked

;; grid [ grid-column-1, grid-column-2 ] arranges grid-columns left to right AS VERTICAL COLUMNS
;; if grid :columns <int> arg is supplied, then the grid puts the grid-columns into ROWS,
;; where :columns arg partition-all's the columns into the row;
;; e.g. :columns 2, when three grid-columns are provided,
;; means we have 2 rows and each row should contain 2 grid-columns
;; (since we only have 3 grid-columns total, we 2 rows, first row with 2 grid-columns and
;(defn semantic-details [painting image-zoomed?]
;  [:> container
;   [:> grid {:columns 2} ;:stackable true}
;    [:> grid-column
;     [:> button "love"]
;     [:> button "love"]
;     [:> button "love"]
;     [:> button "love"]
;     [:> button "love"]
;     [:> button "love"]]
;    [:> grid-column
;     [:> button "joy"]
;     [:> button "second joy"]]
;    [:> grid-column
;     [:> button "pain"]]]])


#_(defn semantic-details [painting image-zoomed?]
    [:> container
     [:> grid {:columns 2} ;:stackable true}
      [:> grid-row
       [:> button "love"]]
      [:> grid-row
       [:> button "joy"]
       [:> button "second joy"]]
      [:> grid-row
       [:> button "pain"]]]])


;;; need to use a scrollable semantic ui modal...

;(defn details-slideshow-modal-image [painting]
;  {:pre [(s/valid? ::specs/painting painting)]}
;  (let [image-zoomed? (subscribe [::subs/image-zoomed?])]))
;    [:> semantic-ui/modal-content
;      [:> semantic-details painting @image-zoomed?]]))

(defn details-slideshow-modal-image [painting show?]
  {:pre [(s/valid? ::specs/painting painting)]}
  ;(let [image-zoomed? (subscribe [::subs/image-zoomed?])]
  ;  (fn []
  [:> semantic-ui/modal {:open show?
                         :close-on-document-click true ; not working?
                         :close-icon true
                         :on-close #(dispatch [::events/toggle-slideshow])}
   [:> semantic-ui/modal-content {:image true}
    [:> semantic-ui/image
       {:fluid true
        :wrapped true
        :src (:jpg painting)
        :style {:padding "8px"}
        ;; works, but we're not including that as part of this modal atm
        :on-click #(dispatch [::events/toggle-image-zoomed])}]]])


;; very close to what you need,
;; without having to use semantic ui modal...

;; semantic-ui react
#_(defn details-slideshow-modal-image [painting]
    {:pre [(s/valid? ::specs/painting painting)]}
    (let [image-zoomed? (subscribe [::subs/image-zoomed?])]
      [rc/modal-panel
       ;:backdrop-on-click #(dispatch [::events/hide-slideshow])
       ;:attr {:on-click #(dispatch [::events/toggle-slideshow])}
       :backdrop-on-click #(dispatch [::events/toggle-slideshow])
       :child [rc/scroller
                 :v-scroll :auto
                 :h-scroll :off
                 ;:height "600px" ; better?: if greater than current window height
                 :child [semantic-details painting @image-zoomed?]]]))
     ;
     ; :child [semantic-details painting @image-zoomed?]]))
       ;[rc/h-box
       ; :align :center
       ; :justify :between
       ; :gap "8px"
       ; :children [[prev-slide-button painting]
       ;            [semantic-details painting @image-zoomed?]
       ;            [next-slide-button]]]]))

;[rc/h-box
; :align :center
; :justify :between
; :gap "8px"
; :children [[prev-slide-button painting]
;            [examine-painting painting @image-zoomed?]
;            [next-slide-button]]]]))