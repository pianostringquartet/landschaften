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


(defn responsive-image [jpg]
  (utils/responsive-image
    jpg
    utils/widths->vw
    #(dispatch [::events/show-max-image])))


(defn image [painting show?]
  [rc/v-box
   :align-self :center
   :children [[responsive-image (:jpg painting)]
              (when show? [utils/modal-image-view (:jpg painting)])]])


(defn info-and-done-button [painting]
 [rc/h-box
  :justify :between
  :children [[info painting] [done-button]]])


(defn display-painting [painting show-max?]
  {:pre [(s/valid? ::specs/painting painting)]}
  [rc/v-box
       :gap "8px"
       :children [[image painting show-max?]
                  [info-and-done-button painting]
                  [utils/button-table (:concepts painting) 3 bubble-button]]])

;; MAIN
(defn examine-painting [current-painting show-max?]
    [display-painting current-painting show-max?])

