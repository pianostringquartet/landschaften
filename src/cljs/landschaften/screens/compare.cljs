(ns landschaften.screens.compare
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.events :as events]
            [landschaften.views.chart :as chart]
            [landschaften.views.utils :as utils]
            [landschaften.specs :as specs]
            [clojure.spec.alpha :as s]
            [landschaften.semantic-ui :as semantic-ui]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]))


(>defn clear-button! []
  [=> vector?]
  [:> semantic-ui/button
   {:on-click #(dispatch [::events/comparisons-cleared])
    :color    "red"
    :compact  true}
   "CLEAR"])


(>defn saved-search-button! [group-name compared-group-names]
  [string? set? => vector?]
  (let [being-compared? (contains? compared-group-names group-name)]
    [:> semantic-ui/button
     {:color    (if being-compared? "orange" "grey")
      :on-click (if being-compared?
                  #(dispatch [::events/remove-compare-group-name group-name])
                  #(dispatch [::events/add-compare-group-name group-name]))
      :style    {:border-radius "30px" :padding "8px"}}
     group-name]))


(defn saved-search-buttons [saved-groups compared-group-names]
  (when-not (empty? saved-groups)
    [utils/bubble-table
     (map #(saved-search-button! % (into #{} compared-group-names))
          (keys saved-groups))
     2]))


(>defn similarity-measurement
  "Progress bar displaying error-rate between two datasets as"
  [error max-error]
  [double? double? => vector?]
  (let [as-percent    (* 100 (/ error max-error))
        as-similarity (- 100 as-percent)]
    [:> semantic-ui/progress {:success  "true"
                              :percent  (goog.string/format "%.1f" as-similarity)
                              :progress "percent"}]))


(>defn error-rate-label [error max-error]
  [double? double? => vector?]
  [:> semantic-ui/slist {:relaxed true}
   [:> semantic-ui/slist-item
    [rc/label :label "How similar the two groups of paintings are:"]]
   [:> semantic-ui/slist-item
    [similarity-measurement error max-error]]])


;(defn concept-frequency-table [paintings n-many certainty-above]
;  [utils/sem-table (chart/paintings->percentage-chart-data paintings n-many certainty-above)])
;
;
;(>defn table-with-header [header paintings]
;  [string? ::specs/paintings => vector?]
;  [:> semantic-ui/slist-item
;   {:header  header
;    :content {:content (r/as-component ^{:key header} [concept-frequency-table paintings 15 0.85])}}])


(defn desktop-compare-screen [groups]
  (when-not (empty? groups)
    [:> semantic-ui/slist {:horizontal true :relaxed true}
     (for [group groups]
       ^{:key (:group-name group)}
       [utils/table-with-header (:group-name group) (:paintings group)])]))


(defn mobile-compare-screen [groups]
  (when-not (empty? groups)
    (let [->accordion-panel
          (fn [group]
            {:key     (:group-name group)
             :title   {:content (:group-name group)}
             :content {:content (r/as-component [utils/concept-frequency-table (:paintings group) 15 0.85])}})]
      [:> semantic-ui/accordion
       {:panels (mapv ->accordion-panel groups)}])))


(defn compare-screen []
  (let [error-rate           (subscribe [::subs/variance])
        max-error-rate       (subscribe [::subs/max-variance])
        groups               (subscribe [::subs/compared-groups])
        saved-groups         (subscribe [::subs/saved-groups])
        compared-group-names (subscribe [::subs/compared-group-names])
        compared-groups (subscribe [::subs/compared-groups])]
    [:> semantic-ui/slist
     [:> semantic-ui/slist-item [clear-button!]]
     [:> semantic-ui/slist-item [saved-search-buttons @saved-groups @compared-group-names]]
     (when @error-rate
       [:> semantic-ui/slist-item [error-rate-label @error-rate @max-error-rate]])
     (when @error-rate
       ;; Workaround: force Chart.js to re-render, don't use React lifecycle methods
       ^{:key (rand-int 999)}
       [chart/radar-chart-component
          (chart/compared-groups->radar-chart-data! (first @compared-groups) (second @compared-groups) 15 0.85)])
     [:> semantic-ui/responsive {:max-width 799} [mobile-compare-screen @groups]]
     [:> semantic-ui/responsive {:min-width 800} [desktop-compare-screen @groups]]]))



(check)