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

(defn tile [painting]
   [:img {:src (:jpg painting)
          :style {:max-width 150
                  :max-height 150}
          :on-click #(dispatch [::events/painting-tile-clicked painting])}])

(defn tiles [paintings]
  (let [painting-tiles (partition-all 3 (map tile paintings))
        ->ui-row (fn [xs] [rc/h-box :gap "8px" :width "500px" :children (into [] xs)])]
    [rc/v-box
      :gap "8px"
      :justify :center
      :children (mapv ->ui-row painting-tiles)]))

(defn paintings-found [n]
  (let [x (if (= n 1) "PAINTING" "PAINTINGS")]
   [rc/label
      :label (clojure.string/join " " [n x "(150 limit)"])
      :class "h1"]))

(defn preview [paintings]
  [rc/v-box
   :align :center
   :children [[paintings-found (count paintings)]
              [tiles paintings]]])

; (partition 3 )
