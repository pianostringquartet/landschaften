(ns landschaften.views.utils
  (:require [reagent.core :as r]
            [re-com.core :as rc]
            [re-frame.core :refer [dispatch]]
            [landschaften.events :as events]
            [landschaften.specs :as specs]
            [cljs.spec.alpha :as s]))


;; ------------------------------------------------------
;; Utility functions and components
;; ------------------------------------------------------

(def log js/console.log)

;(defn keyword->displayable [kw]
;  (clojure.string/replace (name kw) "-" " "))
;
;(defn displayable->keyword [displayable]
;  (keyword (clojure.string/replace displayable " " "-")))

;; color is a css bootstrap class e.g. "btn btn-warning", "btn btn-info", etc.
(defn ->bubble-button [datum on-button-press color]
  {:pre (string? datum)}
  [rc/button
   :label datum
   :on-click on-button-press
   :class color
   :style {:border-radius "30px"}])


(defn ->table-row [data]
  [rc/h-box :children (into [] data)]) ;; should already be in a vector?


;; where button-fn is e.g. artist button
(defn button-table [data row-size button-fn]
  (let [buttons (map button-fn data)
        rows (mapv ->table-row (partition-all row-size buttons))]
    [rc/v-box :children rows]))


;; create the element first,
;; THEN hand it over to be UI-arranged

(defn ->table-column [data]
  ;(let [boxes (map (fn [datum] [rc/box :size "auto" :child datum]) data)]
  ; [rc/h-box :size "auto" :children (into [] boxes)]) ;; should already be in a vector?
   [rc/v-box :children (into [] data)]) ;; should already be in a vector?


(defn image-table [data column-size]
  (let [columns (mapv ->table-column (partition-all column-size data))]
    [rc/h-box :children columns]))


;; two separate modals;
;; one just shows larger image
;; other shows large image + details + next + back buttons
(defn modal-image-view [jpg]
  [rc/modal-panel
   :backdrop-on-click #(dispatch [::events/hide-max-image])
   :child [:img
             {:on-click #(dispatch [::events/hide-max-image])
              :style {:max-height "600px"}
              :src jpg}]])


(defn prev-slide-button [painting]
  [rc/button
   :label "Previous"
   :on-click #(dispatch [::events/go-to-previous-slide painting])])


(defn next-slide-button [painting]
  [rc/button
   :label "Next"
   :on-click #(dispatch [::events/go-to-next-slide painting])])


(defn details-button [painting]
  [rc/button
   :label "Details"
   :on-click #(dispatch [::events/go-to-details painting])])


(defn slide-buttons [painting]
  [rc/h-box
     :children [[prev-slide-button painting]
                [details-button painting]
                [next-slide-button painting]]])


(defn slideshow-modal-image [painting]
  {:pre [(s/valid? ::specs/painting painting)]}
  [rc/modal-panel
   :backdrop-on-click #(dispatch [::events/hide-max-image])
   :child [rc/v-box
             :children [[:img {:on-click #(dispatch [::events/hide-max-image])
                               :style {:max-height "600px"}
                               :src (:jpg painting)}]
                        [slide-buttons painting]]]])




(defn search-suggestions [s coll]
  (into []
    (take 16
      (for [n coll
            :when (re-find (re-pattern (str "(?i)" s)) n)]
        n))))


(defn typeahead [placeholder choices on-choose]
  [rc/typeahead
    :data-source #(search-suggestions % choices)
    :placeholder placeholder
    :change-on-blur? true
    :on-change on-choose])
    ; (reset! model ""))]))
    ;; this clears the model everytime you type,
    ;; after initially selecting something



;; sample url:
; (def cu  "https://res.cloudinary.com/dgpqnl8ul/image/upload/gmllxpcdzouaanz0syip.jpg")

; ^^^ to create a part of the string we need for :srcSet
(defn src-set-part [cloudinary-url width]
  (-> cloudinary-url
    (clojure.string/replace #"upload/" (str "upload/f_auto,q_70,w_" width "/"))
    (str " " width "w")))

;; works
; (=
;   (src-set-part cu "256")
;   "https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_256/gmllxpcdzouaanz0syip.jpg 256w")

;; still need to generate:
;; :sizes, min-width constraints etc.,
;; :src

(defn sizes-part [{:keys [width vw]}]
  (str "(min-width: " width "px) " vw "vw"))

;; used for responsive images
(def widths->vw [{:width 256 :vw 20}
                 {:width 512 :vw 40}
                 {:width 768 :vw 50}
                 {:width 1024 :vw 70}
                 {:width 1280 :vw 80}])

(defn responsive-image [image-url widths->vw on-click]
  [:img
    {:on-click on-click
     :sizes (clojure.string/join ", " (map sizes-part widths->vw))
     :src-set (clojure.string/join ", "
                (map
                  #(src-set-part image-url (:width %))
                  widths->vw))
     :src image-url}])



;; REPL PLAY
(+ 1 1)


