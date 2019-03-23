(ns landschaften.views.examine
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.db :as db]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.views.utils :as utils]
            [landschaften.events :as events]))


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
  {:pre [(string? name)]}
  (let [formatted-number (goog.string/format "%.2f" value)]
    [rc/button
        :label (str name " (" formatted-number ")")
        :on-click #(dispatch [::events/update-selected-concepts name])
        :class "btn btn-info" ; Bootstrap
        :style {:border-radius "30px"}])) ; curvier corners


(defn done-button []
  [rc/button
    :label "DONE"
    :on-click #(dispatch [::events/done-button-clicked])
    :class "btn btn-warning"])


(defn responsive-image [jpg]
  [utils/max-responsive-image
  ;[utils/responsive-image
    jpg
    ;utils/widths->vw
     utils/mid-widths->vw
     ;utils/larger-widths->vw

   ;; now that you have a modal-within-a-modal,

    #(dispatch [::events/toggle-image-zoomed])])

(defn modal-image-view [jpg]
  [rc/modal-panel
   :backdrop-on-click #(dispatch [::events/toggle-image-zoomed])
   :child [:img
           {:on-click #(dispatch [::events/toggle-image-zoomed])
            :style {:max-height "600px"}
            :src jpg}]])


;; the 'show' value needs to be determined by a different value in db
(defn image [painting image-zoomed?]
  [rc/v-box
   :align-self :center
   :children [[responsive-image (:jpg painting)]
              (when image-zoomed? [modal-image-view (:jpg painting)])]])

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


(defn concept-table [painting]
  [utils/button-table
   (reverse (sort-by :value (:concepts painting)))
   3
   bubble-button])

(defn info-and-concepts [painting]
  [rc/v-box
   :gap "8px"
   ;:justify :center
   :children [[info painting]
              [rc/label
                 :style {:color "lightGrey"}
                 :label "Click on a concept to add as search term:"]
              [concept-table painting]]])


(defn info+chart [painting]
  [rc/v-box
   :gap "8px"
   :children [[info painting]
              [rc/label :label "Click to add as search term:"]
              [concept-table painting]]])



(defn image+label [painting image-zoomed?]
  [rc/v-box
     :align :center
     :justify :center
     :gap "8px"
     :children [[image painting image-zoomed?]
                [rc/label
                   :style {:color "lightGrey"}
                   :label "Click to enlarge"]]])


(defn display-painting [painting image-zoomed?]
  {:pre [(s/valid? ::specs/painting painting)]}
  [rc/h-box
   :gap "32px"
   :justify :between
   :children [[image+label painting image-zoomed?]
              [info-and-concepts painting]]])


;; MAIN
(defn examine-painting [current-painting image-zoomed?]
  [rc/v-box
     :gap "16px"
     :children [[display-painting current-painting image-zoomed?]]])


(defn details-button [painting]
  [rc/button
   :label "Details"
   :class "btn btn-success"
   :on-click #(dispatch [::events/go-to-details painting])])

;; just put the buttons on the LEFT and RIGHT SIDE OF THE MODAL
;; and add an "X" in the top right corner

(defn details-slideshow-modal-image [painting]
  {:pre [(s/valid? ::specs/painting painting)]}
  (let [image-zoomed? (subscribe [::subs/image-zoomed?])]
    [rc/modal-panel
     :backdrop-on-click #(dispatch [::events/hide-slideshow])
     :child [rc/h-box
             :align :center
             :justify :between
             :gap "8px"
             :children [[prev-slide-button painting]
                        [examine-painting painting @image-zoomed?]
                        [next-slide-button]]]]))
