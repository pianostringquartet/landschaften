(ns landschaften.views.examine
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.db :as db]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
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
  ; [:div "Painting info not available."])
  [rc/label :label "Painting info not available."])

(defn page-title []
  [rc/label
      :label "EXAMINE PAINTING"
      :class "h1"])

(defn image [jpg]
  ; [:img {:src jpg}
  [:img {:src  "https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546353103/mubxgjzbk3d9mzxtdofs.jpg"
        ;; todo: check landscape vs. portrait orientation
         :style {:max-width 500
                 :max-height 500}}])

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
        ->ui-row (fn [xs] [rc/h-box :gap "8px" :width "500px" :children (into [] xs)])]
    [rc/v-box
      :gap "8px"
      :justify :center
      :children (mapv ->ui-row bubble-rows)]))

(defn done-button []
  [rc/button
    :label "DONE"
    :on-click #(dispatch [::events/done-button-clicked])
    :class "btn btn-warning"])

(defn display-painting [painting]
  [rc/v-box
     :gap "10px"
     :children
      [[page-title]
       (spec-map ::specs/jpg (:jpg painting)
         image
         NO-IMAGE-AVAILABLE)
       [rc/h-box
         :justify :between ; spread them far apart
         :children
          [(spec-map ::specs/painting painting
             info
             NO-INFO-AVAILABLE)
           [done-button]]]
       (concept-bubbles (:concepts painting))]])

;; should actually source from e.g. "::subs/current-painting"
(defn examine-painting [current-painting]
  (let [default-painting (subscribe [::subs/default-painting])]
    ;paintings (subscribe [::subs/paintings])

  ; (display-painting (or (first @paintings) @default-painting)))
    ; (do
      ; (js/console.log "current-painting was: " current-painting)
      (display-painting (or current-painting @default-painting))))

; (defn examine-painting [current-painting]
;   (display-painting current-painting))
