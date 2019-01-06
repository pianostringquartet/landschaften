(ns landschaften.views.preview
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [clojure.spec.alpha :as s]
            [landschaften.events :as events]
            [landschaften.specs :as specs]
            [landschaften.views.utils :as utils]))

;; a tile
;; one per painting,
;; when clicked, update :current-painting

;; then rows of tiles e.g. 5 long each
;; re-use code from examine.cljs;
; https://res.cloudinary.com/dgpqnl8ul/image/upload/v1546390187/gmllxpcdzouaanz0syip.jpg


;; takes a (cloudinary jpg)
;; returns a {:srcSet :src :sizes} map

;; take the
; (defn responsive-image []
;   {:pre [(s/valid?)]})

; ;; sample url:
; (def cu  "https://res.cloudinary.com/dgpqnl8ul/image/upload/gmllxpcdzouaanz0syip.jpg")
;
; ; ^^^ to create a part of the string we need for :srcSet
; (defn src-set-part [cloudinary-url width]
;   (-> cloudinary-url
;     (clojure.string/replace #"upload/" (str "upload/f_auto,q_70,w_" width "/"))
;     (str " " width "w")))
;
; ;; works
; (=
;   (src-set-part cu "256")
;   "https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_256/gmllxpcdzouaanz0syip.jpg 256w")
;
; ;; still need to generate:
; ;; :sizes, min-width constraints etc.,
; ;; :src
;
; ; (defn sizes-part [width vw]
; (defn sizes-part [{:keys [width vw]}]
;   (str "(min-width: " width "px) " vw "vw"))
;
; (= (sizes-part {:width 256 :vw 20})
;    "(min-width: 256px) 20vw")
;
; (def widths->vw [{:width 256 :vw 20}
;                  {:width 512 :vw 40}
;                  {:width 768 :vw 50}
;                  {:width 1024 :vw 70}
;                  {:width 1280 :vw 80}])
;
; (clojure.string/join ", " (map sizes-part widths->vw))
;
; (clojure.string/join ", "
;   (map
;     #(src-set-part cu (:width %))
;     widths->vw))
;
;
; ; (responsive-image
; ;    (:jpg painting)
; ;    widths->vw
; ;    #(dispatch [::events/painting-tile-clicked painting]))
;
; (defn responsive-image [image-url widths->vw on-click]
;   [:img
;     {:on-click on-click
;      :sizes (clojure.string/join ", " (map sizes-part widths->vw))
;      :src-set (clojure.string/join ", "
;                 (map
;                   #(src-set-part image-url (:width %))
;                   widths->vw))
;      :src image-url}])


;; need to dynamically generated the srcSet
(defn tile [painting]
  [rc/box
   :size "auto" ;;
   :width "100%"
   :height "auto"
   :child (utils/responsive-image
             (:jpg painting)
             utils/widths->vw
             #(dispatch [::events/painting-tile-clicked painting]))])

;; need to dynamically generated the srcSet
; (defn tile [painting]
;   [rc/box
;    :size "auto" ;;
;    :width "100%"
;    :height "auto"
;    :child [:img
;             {:on-click #(dispatch [::events/painting-tile-clicked painting])
;              :sizes
;              "(min-width: 256px) 20vw,
;              (min-width: 512px) 40vw,
;              (min-width: 768px) 50vw,
;              (min-width: 1024px) 70vw,
;              (min-width: 1280px) 80vw,
;              33.3vw"
;              :src-set "https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_256/gmllxpcdzouaanz0syip.jpg 256w, https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_512/gmllxpcdzouaanz0syip.jpg 512w, https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_768/gmllxpcdzouaanz0syip.jpg 768w, https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_1024/gmllxpcdzouaanz0syip.jpg 1024w, https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_1280/gmllxpcdzouaanz0syip.jpg 1280w,"
;              :src "https://res.cloudinary.com/dgpqnl8ul/image/upload/gmllxpcdzouaanz0syip.jpg"}]])
;               ;"https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70/gmllxpcdzouaanz0syip.jpg"}]])
;



(defn tiles [paintings]
  (let [painting-tiles (partition-all 3 (map tile paintings))
        ->ui-row (fn [xs] [rc/h-box :gap "8px" :justify :between :children (into [] xs)])]
    [rc/v-box
      :gap "8px"
      ; :justify :center
      :children (mapv ->ui-row painting-tiles)]))

(defn paintings-found [n]
  (let [x (if (= n 1) "PAINTING" "PAINTINGS")]
   ; [rc/label
   ;    :label (clojure.string/join " " [n x "(150 limit)"])
   ;    :class "h1"]))
   [rc/box
    :size "auto"
    :child [rc/title
             :label (clojure.string/join " " [n x "limit"])
             :level :level1]]))

(defn preview [paintings]
  ; [rc/v-box
  ;  :align :center
  ;  ; :gap "4px"
  ;  :children [[paintings-found (count paintings)]
  ;             [tiles paintings]]])
  [rc/v-box
   :align :center
   ; :gap "4px"
   ; :size "auto"
   :children [[paintings-found (count paintings)]
              [tiles paintings]]])
