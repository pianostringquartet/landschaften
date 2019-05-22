(ns landschaften.views.paintings
  (:require [reagent-material-ui.core :as ui]
            [landschaften.events :as events]
            [landschaften.specs :as specs]
            [landschaften.views.painting :as examine]
            [re-frame.core :as rf :refer [subscribe dispatch]]
            [clojure.spec.alpha :as s]
            [re-com.core :as rc]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]))


(>defn tile! [painting]
  [::specs/painting => vector?]
  [ui/GridTile
   {:key (:jpg painting)}
   [:img {:src (:jpg painting)
          :on-click #(dispatch [::events/painting-tile-clicked painting])}]])


(defn grid [current-painting paintings show-max? n-columns]
  {:pre [(pos? n-columns)
         (s/nilable (s/valid? ::specs/painting current-painting))
         (boolean? show-max?)]}
  [:div
   (when show-max?
     [examine/painting-modal! current-painting show-max?])
   [ui/MuiThemeProvider
    [ui/GridList {:cellHeight 160 :cols n-columns}
     (for [painting paintings] ^{:key (:jpg painting)} [tile! painting])]]])


(>defn paintings-found [n]
  [int? => string?]
  (clojure.string/join " " [n (if (= n 1) "PAINTING" "PAINTINGS") "FOUND"]))


(>defn paintings-grid [current-painting paintings show-max? n-columns]
  [(s/nilable ::specs/painting) ::specs/paintings boolean? int? => vector?]
  [:div
   [rc/title :label (paintings-found (count paintings))]
   [grid current-painting (take 50 paintings) show-max? n-columns]])


;(check)