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
            [landschaften.views.sidebar :as sidebar]
            [landschaften.semantic-ui :as semantic-ui]
            [ghostwheel.core
             :as g
             :refer [check >defn >defn- >fdef => | <- ?]]
            [landschaften.views.utils :as utils]))


(defn explore-pane []
  [:> semantic-ui/tab-pane [explore/explore-panel]])

(defn compare-pane []
  [:> semantic-ui/tab-pane [compare/compare-panel]])

;; how to show/hide 3rd tab based on screen size?
(defn mode-tabs [current-tab-id]
  {:pre [(s/valid? ::ui-specs/mode current-tab-id)]}
  (let [tabs      [{:id       :explore
                    :menuItem "EXPLORE"
                    :render   #(r/as-component [explore-pane])}
                   {:id       :compare
                    :menuItem "COMPARE"
                    :render   #(r/as-component [compare-pane])}
                   {:id       :search
                    :menuItem "SEARCH"
                    :render   #(r/as-component [sidebar/sidebar])}]
        ; Semantic UI uses indices; Clojure uses names (keywords)
        id->tab   (fn [id] (first (filter #(= (:id %) id) tabs)))
        index->id (fn [index] (:id (nth tabs index)))]
    [:> semantic-ui/tab
     {:active-index (.indexOf (to-array tabs) (id->tab current-tab-id))
      :on-tab-change #(dispatch [::events/mode-changed (index->id (goog.object/get %2 "activeIndex"))])
      :panes       tabs}]))


; if window smaller than 768,
; use 3 tabs: EXPLORE, SEARCH, COMPARE
; where 'searching' on SEARCH tab brings you back to EXPLORE tab
(js/console.log "js/window.innerWidth: " js/window.innerWidth)


(defn hello-world []
  (let [current-mode-id (subscribe [::subs/current-mode])
        mobile?         (subscribe [::subs/mobile?])]
    [rc/v-box
     :gap "8px"
     :children [[mode-tabs @current-mode-id]]]))

