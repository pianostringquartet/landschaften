(ns landschaften.views.utils)


;; sample url:
(def cu  "https://res.cloudinary.com/dgpqnl8ul/image/upload/gmllxpcdzouaanz0syip.jpg")

; ^^^ to create a part of the string we need for :srcSet
(defn src-set-part [cloudinary-url width]
  (-> cloudinary-url
    (clojure.string/replace #"upload/" (str "upload/f_auto,q_70,w_" width "/"))
    (str " " width "w")))

;; works
(=
  (src-set-part cu "256")
  "https://res.cloudinary.com/dgpqnl8ul/image/upload/f_auto,q_70,w_256/gmllxpcdzouaanz0syip.jpg 256w")

;; still need to generate:
;; :sizes, min-width constraints etc.,
;; :src

; (defn sizes-part [width vw]
(defn sizes-part [{:keys [width vw]}]
  (str "(min-width: " width "px) " vw "vw"))

(= (sizes-part {:width 256 :vw 20})
   "(min-width: 256px) 20vw")

(def widths->vw [{:width 256 :vw 20}
                 {:width 512 :vw 40}
                 {:width 768 :vw 50}
                 {:width 1024 :vw 70}
                 {:width 1280 :vw 80}])

(clojure.string/join ", " (map sizes-part widths->vw))

(clojure.string/join ", "
  (map
    #(src-set-part cu (:width %))
    widths->vw))


; (responsive-image
;    (:jpg painting)
;    widths->vw
;    #(dispatch [::events/painting-tile-clicked painting]))

(defn responsive-image [image-url widths->vw on-click]
  [:img
    {:on-click on-click
     :sizes (clojure.string/join ", " (map sizes-part widths->vw))
     :src-set (clojure.string/join ", "
                (map
                  #(src-set-part image-url (:width %))
                  widths->vw))
     :src image-url}])
