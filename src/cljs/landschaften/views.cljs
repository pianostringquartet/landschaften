(ns landschaften.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe, dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.events :as events]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.ui-specs :as ui-specs]
            [landschaften.views.explore :as explore]
            [landschaften.views.compare :as compare]
            [landschaften.views.examine :as examine]))


(def modes
  {:explore explore/explore-panel
   :compare compare/compare-panel})

(defn mode-tabs [current-mode-id modes]
  {:pre [(s/valid? ::ui-specs/mode current-mode-id)]}
  (let [->tab (fn [panel-id]
               {:id panel-id
                :label (clojure.string/upper-case (name panel-id))})]
   [rc/horizontal-tabs
    :model current-mode-id
    :tabs (mapv ->tab (keys modes))
    :on-change #(dispatch [::events/mode-changed %])]))


;; when there's a current-painting,
;; don't even show the tabs?

;; CUT BACK ON SCOPE: when current painting, let examine-modal take whole screen
;; (can later make examine modal a genuine modal)



(defn hello-world []
 (let [current-mode-id (subscribe [::subs/current-mode])]
       ;current-painting (subscribe [::subs/current-painting])]
   [rc/v-box
     :children ;(if @current-painting
                 ;[[examine/examine-painting @current-painting]]
                 [[mode-tabs @current-mode-id modes]
                  [(@current-mode-id modes)]]]))


;; OLD:

; (defn hello-world []
;  (let [current-painting (subscribe [::subs/current-painting])
;        paintings (subscribe [::subs/paintings])]
;   [rc/h-box
;    :gap "4px"
;    :style {:margin-left "24px" :margin-right "24px" :margin-bottom "24px"}
;    :justify :between
;    :children
;      [(if @current-painting
;         [examine/examine-painting @current-painting]
;         [preview/preview @paintings])
;       [sidebar/sidebar]]]))
