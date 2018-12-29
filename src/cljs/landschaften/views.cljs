(ns landschaften.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.db :as db]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.views.examine :as examine]
            [landschaften.views.sidebar :as sidebar]))

(defn hello-world []
 [rc/h-box
  ; :size "auto"
  ; :justify :center
  :children [ [examine/examine-painting]
              [sidebar/sidebar]]])
             ; [rc/box :child [rc/label :label "MY WORLD"]]
             ; [rc/box :child [rc/label :label "THEIR WORLD"]]
              ; [rc/box :child [rc/label :label "YOUR WORLD"]]]])

; (hello-world)

;; from recom demo site
;; all flat -- h-box not working locally
;; no errors in console
;; solved: needed to add public/assets and retrieve data in home.html
; (defn hello-world []
;   [rc/v-box
;    :children [[rc/box :child "Header"]
;               [rc/h-box
;                :height "100px"
;                :children [[rc/box :size "70px" :child "Nav"]
;                           [rc/box :size "1" :child "Content"]]]
;               [rc/box :child "Footer"]]])
