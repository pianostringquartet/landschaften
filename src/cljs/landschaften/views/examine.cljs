(ns landschaften.views.examine
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.db :as db]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]))


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
  [:div "Painting info not available."])

(defn page-title []
  [rc/label
      :label "EXAMINE PAINTING"
      :class "h1"])

(defn image [jpg]
  [:img {:src jpg
        ;; todo: check landscape vs. portrait orientation
         :style {:max-width "auto"
                 :max-height 500}}])

(defn kv-str [map-entry]
  (let [key-name (name (key map-entry))]
   (str key-name ": " (val map-entry))))

(defn info [painting]
  [:div
    (map
      (fn [datum] [:div (kv-str (find painting datum))])
      '(:title :author :date :timeframe :type :school))])

(defn bubble-button [{:keys [name value]}]
  {:pre [(string? name)]}
  [rc/button
      :label (str name " (" value ")")
      :on-click (fn [] (js/console.log "clicked " name))
      :class "btn btn-primary" ; Bootstrap
      :style {:border-radius "30px"}]) ; curvier corners

(defn concept-bubbles [concepts]
  {:pre [(s/valid? ::specs/concepts concepts)]}
  [:div (map bubble-button concepts)])

;; should source from e.g. db 'current painting',
;; not take all paintings then take first one
(defn examine-painting []
  (let [paintings (rf/subscribe [::subs/paintings])]
    [:div
     ; [page-title]
     (spec-map ::specs/jpg (:jpg (first @paintings))
       image
       NO-IMAGE-AVAILABLE)
     (spec-map ::specs/painting (first @paintings)
       info
       NO-INFO-AVAILABLE)
     (concept-bubbles (:concepts (first @paintings)))]))
