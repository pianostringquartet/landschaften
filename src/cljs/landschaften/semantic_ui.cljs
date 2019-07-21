(ns landschaften.semantic-ui
  (:require [cljsjs.semantic-ui-react]
            [goog.object]))

(def semantic-ui js/semanticUIReact) ; top-level extern

(defn component
  "Get a component from sematic-ui-react:
    (component \"Button\")
    (component \"Menu\" \"Item\")"
  [k & ks]
  (if (seq ks)
    (apply goog.object/getValueByKeys semantic-ui k ks)
    (goog.object/get semantic-ui k)))

(def accordion (component "Accordion"))
(def accordion-title (component "Accordion" "Title"))
(def accordion-content (component "Accordion" "Content"))

(def button (component "Button"))
(def button-group (component "Button" "Group"))
(def button-or (component "Button" "Or"))

(def container (component "Container"))

(def grid (component "Grid"))
(def grid-column (component "Grid" "Column"))
(def grid-row (component "Grid" "Row"))

(def icon (component "Icon"))
(def image (component "Image"))
(def input (component "Input"))
(def item (component "Item"))
(def item-image (component "Item" "Image"))

(def loader (component "Loader"))

(def modal (component "Modal"))
(def modal-content (component "Modal" "Content"))

(def popup (component "Popup"))
(def progress (component "Progress"))

(def responsive (component "Responsive"))

(def search (component "Search"))
(def segment (component "Segment"))
(def slist (component "List"))
(def slist-item (component "List" "Item"))

(def tab (component "Tab"))
(def tab-pane (component "Tab" "Pane"))
(def table (component "Table"))
(def table-body (component "Table" "Body"))
(def table-cell (component "Table" "Cell"))
(def table-row (component "Table" "Row"))
