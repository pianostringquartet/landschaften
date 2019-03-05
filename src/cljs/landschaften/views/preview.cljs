(ns landschaften.views.preview
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [clojure.spec.alpha :as s]
            [landschaften.events :as events]
            [landschaften.subs :as subs]
            [landschaften.specs :as specs]
            [landschaften.views.utils :as utils]
            [landschaften.views.graph :as graph]))


(defn tile [painting]
  [utils/responsive-image
    (:jpg painting)
    utils/widths->vw
    #(dispatch [::events/painting-tile-clicked painting])])

#_(defn tiles [paintings]
    (let [painting-tiles (partition-all 3 (map tile paintings))
          ->ui-row (fn [xs] [rc/h-box :gap "8px" :justify :between :children (into [] xs)])]
      [rc/v-box
        :children (mapv ->ui-row painting-tiles)]))

;; need to pass in full paintings?
(defn tiles [paintings]
  [utils/button-table paintings 3 tile])


(defn paintings-found [n]
  (let [x (if (= n 1) "PAINTING" "PAINTINGS")]
   [rc/title
     :label (clojure.string/join " " [n x "FOUND"])]))

(defn group-name []
  (let [current-group-name (subscribe [::subs/group-name])]
    (fn []
     [rc/title :label (str "Examining " "'"@current-group-name"'")])))


(defn preview [paintings]
  [rc/v-box
   :align :center
   :children [[group-name]
              [paintings-found (count paintings)]
              (when (> (count paintings) 0)
                [graph/frequencies-chart paintings])
              [tiles (take 50 paintings)]]])



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
