(ns landschaften.semantic-ui
  (:require [cljsjs.semantic-ui-react]
            [goog.object]))

;; From cljsjs/semantic-ui-react:
;; https://github.com/cljsjs/packages/tree/master/semantic-ui-react

;; Top-level extern
(def semantic-ui js/semanticUIReact)

(defn component
  "Get a component from sematic-ui-react:
    (component \"Button\")
    (component \"Menu\" \"Item\")"
  [k & ks]
  (if (seq ks)
    (apply goog.object/getValueByKeys semantic-ui k ks)
    (goog.object/get semantic-ui k)))

(def container (component "Container"))
(def button (component "Button"))
(def segment (component "Segment"))
(def dimmer (component "Dimmer"))
(def loader (component "Loader"))
(def modal (component "Modal"))
(def modal-actions (component "Modal" "Actions"))
(def modal-content (component "Modal" "Content"))
(def modal-description (component "Modal" "Description"))
(def message (component "Message"))
(def message-header (component "Message" "Header"))
(def grid (component "Grid"))
(def grid-column (component "Grid" "Column"))
(def grid-row (component "Grid" "Row"))
(def image (component "Image"))
(def item (component "Item"))
(def item-image (component "Item" "Image"))
(def item-content (component "Item" "Content"))

;; Reagent usage:
(defn view []
  [:> button {:onClick #(println "Hello world")} "Press Me"])