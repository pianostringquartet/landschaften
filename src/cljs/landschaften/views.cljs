(ns landschaften.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe, dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.events :as events]
            [landschaften.db :as db]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.ui-specs :as ui-specs]
            [landschaften.views.examine :as examine]
            [landschaften.views.preview :as preview]
            [landschaften.views.sidebar :as sidebar]
            [landschaften.views.explore :as explore]
            [landschaften.views.compare :as compare]))

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

(defn hello-world []
 (let [current-mode-id (subscribe [::subs/current-tab])]
   [rc/v-box
     :children [[mode-tabs @current-mode-id modes]
                (@current-mode-id modes)]]))


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
