(ns landschaften.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe, dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.events :as events]
            [clojure.spec.alpha :as s]
            [landschaften.ui-specs :as ui-specs]
            [landschaften.screens.explore :as explore]
            [landschaften.screens.compare :as compare]
            [landschaften.semantic-ui :as semantic-ui]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]))


(>defn mode-tabs! [current-tab-id]
  [::ui-specs/mode => vector?]
  (let [panes         [{:key      :explore
                        :id       :explore
                        :menuItem "EXPLORE"
                        :render   #(r/as-component [:> semantic-ui/tab-pane [explore/explore-panel]])}
                       {:key      :compare
                        :id       :compare
                        :menuItem "COMPARE"
                        :render   #(r/as-component [:> semantic-ui/tab-pane [compare/compare-screen]])}]
        ; Semantic UI uses indices; Clojure uses names (keywords)
        id->tab       (fn [id] (first (filter #(= (:id %) id) panes)))
        index->id     (fn [index] (:id (nth panes index)))
        default-index #(if (neg? %) 0 %)]
    [:> semantic-ui/tab
     {:active-index  (->> current-tab-id (id->tab) (.indexOf (to-array panes)) (default-index))
      :on-tab-change #(dispatch [::events/mode-changed (index->id (goog.object/get %2 "activeIndex"))])
      :panes         panes}]))


(defn hello-world []
  (let [current-mode-id (subscribe [::subs/current-mode])]
    [:> semantic-ui/container {:fluid true}
     [mode-tabs! (or @current-mode-id :explore)]]))


(check)