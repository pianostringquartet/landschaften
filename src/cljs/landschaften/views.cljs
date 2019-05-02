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


(def search-tab {:id       :search
                 :menuItem "SEARCH"
                 :render   #(r/as-component [sidebar/desktop-sidebar])})

(def tabs [{:id       :explore
            :menuItem "EXPLORE"
            :render   #(r/as-component [explore-pane])}
           {:id       :compare
            :menuItem "COMPARE"
            :render   #(r/as-component [compare-pane])}])

;; how to show/hide 3rd tab based on screen size?
(defn mode-tabs [current-tab-id]
  {:pre [(s/valid? ::ui-specs/mode current-tab-id)]}
  (let [panes     [{:id       :explore
                    :menuItem "EXPLORE"
                    :render   #(r/as-component [explore-pane])}
                   {:id       :compare
                    :menuItem "COMPARE"
                    :render   #(r/as-component [compare-pane])}]
        ; Semantic UI uses indices; Clojure uses names (keywords)
        id->tab   (fn [id] (first (filter #(= (:id %) id) panes)))
        index->id (fn [index] (:id (nth panes index)))
        default-index #(if (neg? %) 0 %)]
    [:> semantic-ui/tab
     {:active-index  (default-index
                       (.indexOf (to-array panes) (id->tab current-tab-id)))
      :on-tab-change #(dispatch [::events/mode-changed (index->id (goog.object/get %2 "activeIndex"))])
      :panes         panes}]))

(defn hello-world []
  (let [current-mode-id (subscribe [::subs/current-mode])]
    [:> semantic-ui/container {:fluid true}
     [mode-tabs @current-mode-id]]))


