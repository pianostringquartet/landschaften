(ns landschaften.views.preview
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.events :as events]))
;; a tile
;; one per painting,
;; when clicked, update :current-painting

;; then rows of tiles e.g. 5 long each
;; re-use code from examine.cljs

; (defn tile [painting]
;    [:img {:src (:jpg painting)
;           :style {:max-width 150
;                   :max-height 150}
;           :on-click #(dispatch [::events/painting-tile-clicked painting])}])

; https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546390187/gmllxpcdzouaanz0syip.jpg
; (defn tile [painting])
   ; [:img { ;:src (:jpg painting)
   ;        ; :style {:max-width 150
   ;        ;         :max-height 150}
   ;        ; :sizes "(min-width: 30em) 28em, 100vw"
   ;        ; :sizes "(min-width: 150px) 28em, 100vw"
   ;        :sizes
   ;        ; "(min-width: 15em) 13em, 13em
   ;        ;  (min-width: 30em) 28em, 28em
   ;        ;  (min-width: 45em) 43em, 43em
   ;        ;  (min-width: 60em) 58em, 58em"
   ;        "(min-width: 256px) 250px, 250px
   ;         (min-width: 512px) 506px, 506px
   ;         (min-width: 45em) 43em, 43em
   ;         (min-width: 60em) 58em, 58em"
   ;        ; :sizes "(max-height: 150px) 28em, 100vw"
   ;        :on-click #(dispatch [::events/painting-tile-clicked painting])
   ;        "srcSet" "https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_256/gmllxpcdzouaanz0syip.jpg 256w, https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_512/gmllxpcdzouaanz0syip.jpg 512w, https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_768/gmllxpcdzouaanz0syip.jpg 768w, https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_1024/gmllxpcdzouaanz0syip.jpg 1024w, https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_1280/gmllxpcdzouaanz0syip.jpg 1280w"
   ;        :src  "https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_512/gmllxpcdzouaanz0syip.jpg"}])

;; want to set a max height as well

;; the problem: i'm only working with widths
;; ... that's fine -- how do i let the image simply be the max height it naturally is?

;; the media-constraint goes by the width of the entire view port (browser window),
;; NOT the flexbox size
;; ... that's bad -- the :img now has to know about the total browser window size?!
(defn tile [painting]
  [rc/box
   ; :size "auto"
   ; :max-height "auto"
   ; :max-height "250px"
   ; :max-height "100%"
   :size "auto" ;;
   ; :size "none"
   ; :size "initial"
   :width "100%"
   :height "auto"
   :child [:img
            { ; :style {:max-height "100%"} ; {:height "auto"} ; {:max-height 150}
             :sizes
                  ; "
                  ; (min-width: 256px) 250px, 100vw
                  ; (min-width: 512px) 506px, 100vw
                  ; (min-width: 768px) 762px, 100vw
                  ; (min-width: 1024px) 1018px, 100vw
                  ; (min-width: 1280px) 1274px, 100vw
                  ; "
                  ; "
                  ; (min-width: 256px) 250px
                  ; (min-width: 512px) 506px
                  ; (min-width: 768px) 762px
                  ; (min-width: 1024px) 1018px
                  ; (min-width: 1280px) 1274px
                  ; "

                  ; "
                  ; (min-width: 256px) 250px,
                  ; (min-width: 512px) 506px,
                  ; (min-width: 768px) 762px,
                  ; (min-width: 1024px) 1018px,
                  ; (min-width: 1280px) 1274px,
                  ; 33.3vw
                  ; "

                  "
                  (min-width: 256px) 20vw,
                  (min-width: 512px) 40vw,
                  (min-width: 768px) 50vw,
                  (min-width: 1024px) 70vw,
                  (min-width: 1280px) 80vw,
                  33.3vw
                  "

                  ; "
                  ; (min-width: 256px) 256px, 20vw
                  ; (min-width: 512px) 512px, 40vw
                  ; (min-width: 768px) 768px, 50vw
                  ; (min-width: 1024px) 1024px, 70vw
                  ; (min-width: 1280px) 1280px, 90vw
                  ; "
             :on-click #(dispatch [::events/painting-tile-clicked painting])
             "srcSet" "https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_256/gmllxpcdzouaanz0syip.jpg 256w, https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_512/gmllxpcdzouaanz0syip.jpg 512w, https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_768/gmllxpcdzouaanz0syip.jpg 768w, https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_1024/gmllxpcdzouaanz0syip.jpg 1024w, https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_1280/gmllxpcdzouaanz0syip.jpg 1280w"
             :src  "https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_512/gmllxpcdzouaanz0syip.jpg"}]])


(defn tiles [paintings]
  (let [painting-tiles (partition-all 3 (map tile paintings))
  ; (let [painting-tiles (partition-all 1 (map tile paintings))
        ; ->ui-row (fn [xs] [rc/h-box :gap "8px" :width "500px" :children (into [] xs)])]
        ->ui-row (fn [xs] [rc/h-box :gap "8px" :justify :between :children (into [] xs)])]
    [rc/v-box
      :gap "8px"
      ; :justify :center
      :children (mapv ->ui-row painting-tiles)]))

(defn paintings-found [n]
  (let [x (if (= n 1) "PAINTING" "PAINTINGS")]
   [rc/label
      :label (clojure.string/join " " [n x "(150 limit)"])
      :class "h1"]))

(defn preview [paintings]
  [rc/v-box
   :align :center
   ; :gap "4px"
   :children [[paintings-found (count paintings)]
              [tiles paintings]]])
