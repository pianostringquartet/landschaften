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


;; spec is nice... but none of this is enforced
;; spec map
;; f should be single-arity function
(defn spec-map [some-spec data f fallback-fn]
  (if (s/valid? some-spec data)
    (f data)
    (fallback-fn)))

(defn NO-IMAGE-AVAILABLE []
  [:img {:src "/img/no_image_available.jpeg"
         :style {:max-width 200
                 :max-height 200}}])

(defn NO-INFO-AVAILABLE []
  [rc/label :label "Painting info not available."])

(defn page-title []
 [rc/box
   :align-self :center
   :child [rc/title
            :label "EXAMINE PAINTING"
            :level :level1]])

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

(defn image [painting show?]
  [rc/v-box
   :align-self :center
   :children
    [(utils/responsive-image
        (:jpg painting) utils/widths->vw
       ;; better: 'examine-image-clicked'
        #(dispatch [::events/show-max-image]))
     (when show?
       (do
         (js/console.log "modal showing")
         [rc/modal-panel
          :backdrop-on-click
            #(dispatch [::events/hide-max-image])
          ;; TODO:
          ;; loads slow; can't do max-height as 50% of screen?; may also need to be responsive?
          ;; workaround: pick a size that works for both small and large screens
           :child [:img
                    {:on-click #(dispatch [::events/hide-max-image])
                     :style {:max-height "600px"}
                     :src (:jpg painting)}]]))]])

(defn display-painting [painting show-max?]
  [rc/v-box
     :gap "10px"
     :children
      [ ; [page-title]
       (if (s/valid? ::specs/jpg (:jpg painting))
         (image painting show-max?)
         NO-IMAGE-AVAILABLE)
       [rc/h-box
         :justify :between ; spread them far apart
         :children
          [(spec-map ::specs/painting painting
             info
             NO-INFO-AVAILABLE)
           [done-button]]]
       [rc/box
         ; :size "auto" ;;
         ; :width "100%"
         ; :height "auto"
         :child (concept-bubbles (:concepts painting))]]])

;; should actually source from e.g. "::subs/current-painting"
(defn examine-painting [current-painting]
  (let [default-painting (subscribe [::subs/default-painting])
        show-max? (subscribe [::subs/show-max?])]
    (do
      (js/console.log "current-painting was: " current-painting)
      (display-painting
        (or current-painting @default-painting)
        @show-max?))))
