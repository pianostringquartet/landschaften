(ns landschaften.views.preview
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.events :as events]
            [landschaften.subs :as subs]
            [landschaften.views.utils :as utils]
            [landschaften.views.examine :as examine]
            [landschaften.views.mui :as mui]))


(defn tile [painting]
  [utils/responsive-image
     (:jpg painting)
     utils/widths->vw
     #(dispatch [::events/painting-tile-clicked painting])])


(defn columns [paintings show-max?]
  (let [current-painting (subscribe [::subs/current-painting])
        n-columns (/ (count paintings) 3)
        images (map tile paintings)]
    [rc/v-box
     :children [[utils/image-table images n-columns]
                (when show-max?
                  [examine/details-slideshow-modal-image
                     @current-painting
                     show-max?])]]))


(defn paintings-found [n]
  (let [x (if (= n 1) "PAINTING" "PAINTINGS")]
    [rc/title :label (clojure.string/join " " [n x "FOUND"])]))

;; it's fine to just show 50 or 100 paintings
;; this app is primarily for exploration,
;; not correcting wrong classifications etc.

;; better to call this "the painting grid" ...
;(defn preview [paintings show-max?]
;  [rc/v-box
;   :align :center
;   :children [[paintings-found (count paintings)]
;              [columns (take 50 paintings) show-max?]]])


(defn preview [paintings show-max?]
  [:div
   [paintings-found (count paintings)]
   [mui/mui-grid (take 50 paintings) show-max?]])
