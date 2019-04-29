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

(def accordion (component "Accordion"))
(def accordion-panel (component "Accordion" "Panel"))
(def accordion-content (component "Accordion" "Content"))
(def accordion-title (component "Accordion" "Title"))

(def container (component "Container"))
(def button (component "Button"))
(def button-group (component "Button" "Group"))
(def button-or (component "Button" "Or"))
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
(def icon (component "Icon"))
(def input (component "Input"))
(def item (component "Item"))
(def item-image (component "Item" "Image"))
(def item-content (component "Item" "Content"))
(def progress (component "Progress"))
(def popup (component "Popup"))
(def popup-content (component "Popup" "Content"))
(def table (component "Table"))
(def table-body (component "Table" "Body"))
(def table-cell (component "Table" "Cell"))
(def table-row (component "Table" "Row"))
(def table-header (component "Table" "Header"))
(def table-header-cell (component "Table" "HeaderCell"))

(def dropdown (component "Dropdown"))
(def search (component "Search"))
(def slist (component "List"))
(def slist-content (component "List" "Content"))
(def slist-item (component "List" "Item"))
(def tab (component "Tab"))
(def tab-pane (component "Tab" "Pane"))

(def responsive (component "Responsive"))


;; Reagent usage:
(defn view []
  [:> button {:onClick #(println "Hello world")} "Press Me"])