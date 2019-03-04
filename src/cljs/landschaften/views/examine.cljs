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
  [rc/button
      :label (str name " (" value ")")
      :on-click #(dispatch [::events/update-selected-concepts name])
      :class "btn btn-info" ; Bootstrap
      :style {:border-radius "30px"}]) ; curvier corners

(defn concept-bubbles [concepts]
  {:pre [(s/valid? ::specs/concepts concepts)]}
  (let [bubble-rows  (partition-all 3 (map bubble-button concepts))
        ->ui-row (fn [xs] [rc/h-box :gap "8px" :children (into [] xs)])]
    [rc/v-box
      :gap "8px"
      :justify :center
      :children (mapv ->ui-row bubble-rows)]))

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

;; TODO:
;; loads slow; can't do max-height as 50% of screen?; may also need to be responsive?
;; workaround: pick a size that works for both small and large screens
(defn modal-image-view [jpg]
  [rc/modal-panel
    :backdrop-on-click #(dispatch [::events/hide-max-image])
    :child [:img
              {:on-click #(dispatch [::events/hide-max-image])
               :style {:max-height "600px"}
               :src jpg}]])

;; re-com so verbose and clojure so parens-heavy
;; that separating 'components from ui' seems impossible
(defn image [painting show?]
  [rc/v-box
   :align-self :center
   :children [[responsive-image (:jpg painting)]
              (when show? [modal-image-view (:jpg painting)])]])

(defn info-and-done-button [painting]
 [rc/h-box
  :justify :between
  :children [[info painting] [done-button]]])

(defn display-painting [painting show-max?]
  [rc/v-box
     :style {:margin-top "32px"}
     :gap "10px"
     :children [[image painting show-max?]
                [info-and-done-button painting]
                [concept-bubbles (:concepts painting)]]])

;; MAIN
(defn examine-painting [current-painting]
  (let [default-painting (subscribe [::subs/default-painting])
        show-max? (subscribe [::subs/show-max?])]
    [display-painting
      (or current-painting @default-painting)
      @show-max?]))
