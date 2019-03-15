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


(defn hello-world []
 (let [current-mode-id (subscribe [::subs/current-mode])]
   [rc/v-box
     :gap "8px"
     :children [[mode-tabs @current-mode-id modes]
                [(@current-mode-id modes)]]]))

