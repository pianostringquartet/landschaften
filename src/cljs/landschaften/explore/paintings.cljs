(ns landschaften.explore.paintings
  (:require [reagent-material-ui.core :as ui]
            [landschaften.explore.explore-events :as explore-events]
            [landschaften.explore.painting :as examine]
            [re-frame.core :refer [dispatch]]
            [landschaften.specs :as specs]
            [clojure.spec.alpha :as s]
            [re-com.core :as rc]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]
            [landschaften.semantic-ui :as semantic-ui]
            [landschaften.db :as db]))



(defn tile! [painting]
  [ui/GridTile
   {:key (:jpg painting)}
   [:img {:src (:jpg painting)
          :on-click #(dispatch [::explore-events/painting-tile-clicked painting])}]])


(defn grid [current-painting paintings show-max? n-columns]
  {:pre [(pos? n-columns)
         (boolean? show-max?)]}
  [:div
   (when show-max?
     [examine/painting-modal current-painting show-max?])
   [ui/MuiThemeProvider
    [ui/GridList {:cellHeight 160 :cols n-columns}
     (for [painting paintings] ^{:key (:jpg painting)} [tile! painting])]]])


(>defn paintings-found [n]
  [int? => string?]
  (clojure.string/join " " [n (if (= n 1) "PAINTING" "PAINTINGS") "FOUND"]))


(>defn paintings-grid [current-painting paintings paintings-count show-max? n-columns]
  [(s/nilable ::specs/painting) ::specs/paintings int? boolean? int? => vector?]
  [:> semantic-ui/slist {:relaxed true}
   [:> semantic-ui/slist-item
    [rc/title :label (paintings-found paintings-count)]]
   [:> semantic-ui/slist-item
    [grid current-painting (take db/DISPLAY-MAX-N-PAINTINGS paintings) show-max? n-columns]]])


;(check)