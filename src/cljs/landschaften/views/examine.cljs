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


(defn NO-IMAGE-AVAILABLE []
  [:img {:src "/img/no_image_available.jpeg"
         :style {:max-width 200
                 :max-height 200}}])


(defn NO-INFO-AVAILABLE []
  [rc/label :label "Painting info not available."])


(defn info [painting]
  (let [->ui-label (fn [k] [rc/label :label (str (name k) ": " (k painting))])
        info-categories '(:title :author :date :timeframe :type :school)]
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


;; NOW THAT THESE IMAGES ARE SHOWING IN A MODAL WITH OTHER INFORMATION...
;; need to set a max height
(defn responsive-image [jpg]
  [utils/max-responsive-image
  ;[utils/responsive-image
    jpg
    ;utils/widths->vw
     utils/mid-widths->vw
     ;utils/larger-widths->vw
    #(dispatch [::events/show-max-image])])


;; the 'show' value needs to be determined by a different value in db
(defn image [painting show?]
  [rc/v-box
   :align-self :center
   :children [[responsive-image (:jpg painting)]]])
              ;(when show? [utils/modal-image-view (:jpg painting)])]])

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


(defn slide-buttons [painting]
  [rc/h-box
   :justify :center
   :align :center
   :gap "8px"
   :children [[prev-slide-button painting]
              [done-button]
              [next-slide-button painting]]])


(defn concept-table [painting]
  [utils/button-table
   (reverse (sort-by :value (:concepts painting)))
   3
   bubble-button])

(defn info-and-concepts [painting]
  [rc/v-box
   :gap "16px"
   ;:justify :between
   :children [[info painting]
              [concept-table painting]]])


(defn display-painting [painting show-max?]
  {:pre [(s/valid? ::specs/painting painting)]}
  [rc/h-box
   :gap "32px"
   :justify :between
   :children [[image painting show-max?]
              [info-and-concepts painting]]])


;(defn display-painting [painting show-max?]
;  {:pre [(s/valid? ::specs/painting painting)]}
;  [rc/v-box
;       :gap "8px"
;       :children [[image painting show-max?]
;                  [info-and-done-button painting]
;                  [utils/button-table (:concepts painting) 3 bubble-button]]])

;; MAIN
(defn examine-painting [current-painting show-max?]
  [rc/v-box
     :gap "16px"
     :children [[display-painting current-painting show-max?]]])
                ;[slide-buttons current-painting]]])



(defn details-button [painting]
  [rc/button
   :label "Details"
   :class "btn btn-success"
   :on-click #(dispatch [::events/go-to-details painting])])

;; just put the buttons on the LEFT and RIGHT SIDE OF THE MODAL
;; and add an "X" in the top right corner

(defn details-slideshow-modal-image [painting show-max?]
  {:pre [(s/valid? ::specs/painting painting)]}
  (do
    (utils/log "details-slideshow-modal-image called")
    [rc/modal-panel
     :backdrop-on-click #(dispatch [::events/hide-max-image])
     :child [rc/h-box
             :align :center
             :justify :between
             :gap "8px"
             :children [;[:img {:on-click #(dispatch [::events/hide-max-image])
                         ;      :style {:max-height "600px"}
                          ;     :src (:jpg painting)
                        [prev-slide-button painting]
                        [examine-painting painting show-max?]
                        [next-slide-button]]]]))


