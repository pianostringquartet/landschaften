(ns landschaften.screens.examine
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

;; return data if data specifies spec, else nil
(defn coerce [some-spec data]
  (when (s/valid? some-spec data) data))

(defn NO-IMAGE-AVAILABLE []
  [:img {:src "/img/no_image_available.jpeg"
         :style {:max-width 200
                 :max-height 200}}])

(defn NO-INFO-AVAILABLE []
  [:div "Painting info not available."])

; (declare app)
;
; (defn hello-world []
;  [app])

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

(def p
  {:date "c. 1469",
   :school "Italian",
   :type "portrait",
   :title "Portrait of a Young Man",
   :author "BOTTICELLI, Sandro",
   :concepts #{{:name "gown (clothing)", :value 0.90708596}
               {:name "one", :value 0.986664}
               {:name "cape", :value 0.87464726}
               {:name "adult", :value 0.98579407}
               {:name "side view", :value 0.8062773}
               {:name "religion", :value 0.93637943}
               {:name "sculpture", :value 0.86673677}
               {:name "lid", :value 0.9411217}
               {:name "people", :value 0.9946501}
               {:name "painting", :value 0.9754119}
               {:name "wear", :value 0.95125747}
               {:name "portrait", :value 0.9801239}
               {:name "facial expression", :value 0.8723508}
               {:name "man", :value 0.9584564}
               {:name "veil", :value 0.96336377}
               {:name "facial hair", :value 0.8060329}
               {:name "woman", :value 0.874543}
               {:name "illustration", :value 0.8150852}
               {:name "art", :value 0.96110016}
               {:name "leader", :value 0.8733945}},
   :id 5623,
   :timeframe "1451-1500",
   :form "painting",
   :jpg "https://www.wga.hu/art/b/botticel/7portrai/01youngm.jpg"})


(defn examine-painting []
  (let [paintings (rf/subscribe [::subs/paintings])]
    [:div
     ; [:div "... I am the real app"]
     [page-title]
     (spec-map ::specs/jpg (:jpg (first @paintings))
       image
       NO-IMAGE-AVAILABLE)
     (spec-map ::specs/painting (first @paintings)
       info
       NO-INFO-AVAILABLE)
     (concept-bubbles (:concepts (first @paintings)))]))
