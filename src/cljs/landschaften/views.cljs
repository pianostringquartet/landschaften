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
            [landschaften.views.examine :as examine]
            [landschaften.views.mui :as mui]
            [landschaften.views.sidebar :as sidebar]))


;(def modes
;  {:explore explore/explore-panel
;   :compare compare/compare-panel
;   :search  sidebar/sidebar})

(defn modes [mobile?]
  (let [panels {:explore explore/explore-panel
                :compare compare/compare-panel}]
    (if mobile?
      (assoc panels :search sidebar/sidebar)
      panels)))


(defn mode-tabs [current-mode-id modes]
  {:pre [(s/valid? ::ui-specs/mode current-mode-id)]}
  (let [->tab (fn [panel-id]
               {:id panel-id
                :label (clojure.string/upper-case (name panel-id))})]
   [rc/horizontal-tabs
    :model current-mode-id
    :tabs (mapv ->tab (keys modes))
    :on-change #(dispatch [::events/mode-changed %])]))


; if window smaller than 768,
; use 3 tabs: EXPLORE, SEARCH, COMPARE
; where 'searching' on SEARCH tab brings you back to EXPLORE tab
(js/console.log "js/window.innerWidth: " js/window.innerWidth)


(defn hello-world []
 (let [current-mode-id (subscribe [::subs/current-mode])
       mobile? (subscribe [::subs/mobile?])
       modes (modes @mobile?)]
   ;[(@current-mode-id modes)]))
   [rc/v-box
     :gap "8px"
     :children [[mode-tabs @current-mode-id modes]
                [(@current-mode-id modes)]]]))

