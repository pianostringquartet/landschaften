(ns landschaften.mui-items)
  ;(:require [cljsjs.material-ui]
            ;[material-ui]
            ;[reagent.core :refer [adapt-react-class]])

;; does this work for nested retrieval?
;; compare to semantic-ui-react component retrieval in cardy...
;(defn tag->component [tag-name]
;  (adapt-react-class (aget js/MaterialUI (name tag-name))))

;(defn tag->component [tag-name]
;  (adapt-react-class (aget js/MaterialUI (name tag-name))))

;; this works:
;(def GridList (tag->component "GridList"))
;(def GridTile (tag->component "GridTile"))

;; this fails ("adapt-react-class: comp must not be nil")
;(def Grid (tag->component "Grid"))

;(def Grid (tag->component "Grid Container"))

;(def Grid (tag->component 'Grid))
;(defn Grid (tag->component 'Grid))

;(def Paper (tag->component "Paper"))
;(def Paper (tag->component 'Paper))

