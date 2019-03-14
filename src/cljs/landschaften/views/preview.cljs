(ns landschaften.views.preview
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.events :as events]
            [landschaften.subs :as subs]
            [landschaften.views.utils :as utils]))

;; WANT TO MAKE IT S.T. when image clicked,
;; we see popover dialogue with big image
;; and buttons 'Done' and 'Details'
;; (clicking 'Done' -> popover closes)
;; (clicking 'Details' -> Examine screen)

;; this tile needs to be like 'image'-component in Examine screen

;; instead of 'show-max-image?: Bool',
;; have a 'max-image-url: String'

;; can no longer be a popover, bc no longer tied to specific anchor?
;; (or can 'whole page' be an anchor?)
;; ... has to be modal instead?

;; could add arrow button on modal bottom,
;; to scroll through all images
;; (a nice slideshow)

;; how best to develop this?
;;

;; not a good idea to do local state of need to swap between paintings;
;; you'll no longer be focused/centered on a single painting



;; in db, would KEEP bool :show-max-image? and
;; ADD url-string :current-max-image

;; :show-max? is turned on when ANY image is clicked

;; move through slideshow -> set :current-max-image


(defn tile [painting]
  [utils/responsive-image
     (:jpg painting)
     utils/widths->vw
     #(dispatch [::events/painting-tile-clicked painting])])

(defn columns [paintings show-max?]
  (let [current-painting (subscribe [::subs/current-painting])
        n-columns (/ (count paintings) 4)
        images (map tile paintings)]
    [rc/v-box
       :children [[utils/image-table images n-columns]
                  (when show-max?
                    [utils/slideshow-modal-image @current-painting])]]))



(defn paintings-found [n]
  (let [x (if (= n 1) "PAINTING" "PAINTINGS")]
    [rc/title :label (clojure.string/join " " [n x "FOUND"])]))


(defn group-name []
  (let [name (subscribe [::subs/group-name])]
    (when-not (empty? @name)
     [rc/title :label (str "Examining " "'"@name"'")])))

;; it's fine to just show 50 or 100 paintings
;; this app is primarily for exploration,
;; not correcting wrong classifications etc.
(defn preview [paintings show-max?]
  [rc/v-box
   :align :center
   :children [[group-name]
              [paintings-found (count paintings)]
              [columns (take 50 paintings) show-max?]]])


;; OLD CODE:

;; a tile
;; one per painting,
;; when clicked, update :current-painting

;; then rows of tiles e.g. 5 long each
;; re-use code from examine.cljs;

; (defn tile [painting]
;   [rc/box
;    :size "auto" ;;
;    :width "100%"
;    :height "auto"
;    :child (utils/responsive-image
;              (:jpg painting)
;              utils/widths->vw
;              #(dispatch [::events/painting-tile-clicked painting]))])
;
; (defn tiles [paintings]
;   (let [painting-tiles (partition-all 3 (map tile paintings))
;         ->ui-row (fn [xs] [rc/h-box :gap "8px" :justify :between :children (into [] xs)])]
;     [rc/v-box
;       :gap "8px"
;       ; :justify :center
;       :children (mapv ->ui-row painting-tiles)]))
;
; (defn paintings-found [n]
;   (let [x (if (= n 1) "PAINTING" "PAINTINGS")]
;    [rc/box
;     :size "auto"
;     :child [rc/title
;              :label (clojure.string/join " " [n x "FOUND"])
;              :level :level2]]))
;
; (defn group-name []
;   (let [current-group-name (subscribe [::subs/group-name])]
;     (fn []
;      [rc/title :label (str "Examining " "'"@current-group-name"'")
;                :level :level1])))
;
; (defn preview [paintings]
;   [rc/v-box
;    :align :center
;    ; :gap "4px"
;    :size "auto"
;    :children [[group-name]
;               [paintings-found (count paintings)]
;               (when (> (count paintings) 0)
;                 [graph/frequencies-chart paintings])
;               [tiles (take 50 paintings)]]])
