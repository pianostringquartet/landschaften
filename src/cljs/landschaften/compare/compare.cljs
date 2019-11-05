(ns landschaften.compare.compare
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.compare.compare-subs :as compare-subs]
            [landschaften.specs :as specs]
            [landschaften.compare.compare-events :as compare-events]
            [landschaften.compare.chart :as chart]
            [landschaften.view-utils :as utils]
            [landschaften.semantic-ui :as semantic-ui]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]
            [cljs.spec.alpha :as s]
            [landschaften.db :as db]))


;; ------------------------------------------------------
;; Comparing groups' (dis)similarity
;; - statistically via variance
;; - visualization via radar chart
;; ------------------------------------------------------


(defn clear-button! []
  {:post [(vector? %)]}
  [:> semantic-ui/button
   {:on-click #(dispatch [::compare-events/comparisons-cleared])
    :color    "red"
    :compact  true}
   "CLEAR"])


;; give heavier alpha ('a'), i.e. less transparent
(def color-1 "rgba(255, 99, 132, 0.7)")
(def color-2 "rgba(54, 162, 235, 0.7)")


(>defn compare-group-button! [group-name compared-group-names]
  [string? set? => vector?]
  (let [being-compared? (contains? compared-group-names group-name)
        button-color    (if being-compared?
                          (if (= group-name (first compared-group-names))
                            color-1
                            color-2)
                          "grey")]
    [:> semantic-ui/button
     {:on-click (if being-compared?
                  #(dispatch [::compare-events/remove-compare-group-name group-name])
                  #(dispatch [::compare-events/add-compare-group-name group-name]))
      :style    {:color            "#fff"                   ; white
                 :background-color button-color
                 :border-radius    "30px"
                 :padding          "4px"}}
     group-name]))


(defn compare-group-buttons [saved-groups compared-group-names]
  (when-not (empty? saved-groups)
    [utils/bubble-table
     (map #(compare-group-button! % (into #{} compared-group-names))
          (keys saved-groups))
     3]))


;; CALCULATIONS SHOULD BE DONE IN CALC-SUB,
;; AND POSSIBLY EVEN ON SERVER
(>defn similarity-measurement
  "Progress bar displaying error-rate between two datasets as a percentage."
  [variance max-variance]
  [double? double? => vector?]
  (let [as-percent    (* 100 (/ variance max-variance))
        as-similarity (- 100 as-percent)]
    [:> semantic-ui/container {:style {:padding-right "35%"}} ; garbage UI hack
     [:> semantic-ui/progress {:success  "true"
                               :percent  (goog.string/format "%.1f" as-similarity)
                               :progress "percent"}]]))


(>defn labeled-variance [variance max-variance]
  [double? double? => vector?]
  [:> semantic-ui/slist {:relaxed true}
   [:> semantic-ui/slist-item
    [rc/label :label "How similar these two groups of paintings are: "]]
   [:> semantic-ui/slist-item
    [similarity-measurement variance max-variance]]])


(>defn compare-screen-buttons [saved-groups compared-groups]
  [map? (s/coll-of ::specs/group) => vector?]
  [:> semantic-ui/slist
   [:> semantic-ui/slist-item
    [compare-group-buttons saved-groups (map :group-name compared-groups)]]
   [:> semantic-ui/slist-item [clear-button!]]])


(defn compare-sidebar [variance max-variance saved-groups compared-groups]
  [:> semantic-ui/slist
   [:> semantic-ui/slist-item
    [compare-screen-buttons saved-groups compared-groups]]
   [:> semantic-ui/slist-item
    (when variance
      [labeled-variance variance max-variance])]])


;; radar-chart should JUST RECEIVE A SUB,
;; WHOSE VALUE HAS ALREADY BEEN CALCULATED ON THE SERVER
(defn radar-chart [compared-groups]
  [:> semantic-ui/slist-item
   ;; Workaround: force Chart.js to re-render, don't use React lifecycle methods
   ^{:key (rand-int 999)}
   [chart/radar-chart
    (chart/compared-groups->radar-chart-data!
      (first compared-groups) (second compared-groups) db/SHOW-N-CHARTPOINTS db/CONCEPT-CERTAINTY-ABOVE)]])


(defn accordion-frequency-tables [groups]
  (let [->accordion-panel
        (fn [group]
          {:key     (:group-name group)
           :title   {:content (:group-name group)}
           :content {:content (r/as-component [utils/concept-frequency-table (:paintings group)
                                               db/SHOW-N-CHARTPOINTS db/CONCEPT-CERTAINTY-ABOVE])}})]
    [:> semantic-ui/accordion
     {:panels (mapv ->accordion-panel groups)}]))


(defn mobile-compare-screen [variance max-variance saved-groups compared-groups]
  [:> semantic-ui/slist
   [:> semantic-ui/slist-item
    [clear-button!]]
   [:> semantic-ui/slist-item
    [compare-group-buttons saved-groups (map :group-name compared-groups)]]
   [:> semantic-ui/slist-item
    (when variance [radar-chart compared-groups])]
   [:> semantic-ui/slist-item
    (when variance [labeled-variance variance max-variance])]
   [:> semantic-ui/slist-item
    (if (empty? compared-groups)
      [rc/label :label "Select some saved searches to start comparing."]
      [accordion-frequency-tables compared-groups])]])


(defn desktop-compare-screen [variance max-variance saved-groups compared-groups]
  [:> semantic-ui/grid {:columns 2 :centered true}
   [:> semantic-ui/grid-column {:width 10}
    (when variance [radar-chart compared-groups])
    (when (empty? compared-groups)
      [:> semantic-ui/grid {:centered true :padded true :relaxed true :columns 1}
       [:> semantic-ui/grid-column
        [rc/label :label "Select some saved searches to start comparing."]]])]
   [:> semantic-ui/grid-column {:width 6}
     [compare-sidebar variance max-variance saved-groups compared-groups]
     [:> semantic-ui/slist {:horizontal true :relaxed true :padded true}
      (for [group compared-groups]
        [utils/table-with-header (:group-name group) (:paintings group)])]]])


(defn compare-screen []
  (let [variance        (subscribe [::compare-subs/variance])
        max-variance    (subscribe [::compare-subs/max-variance])
        compared-groups (subscribe [::compare-subs/compared-groups])
        saved-groups    (subscribe [::subs/saved-groups])]
    [:> semantic-ui/slist
     [:> semantic-ui/responsive {:max-width 799}
      [mobile-compare-screen @variance @max-variance @saved-groups @compared-groups]]
     [:> semantic-ui/responsive {:min-width 800}
      [desktop-compare-screen @variance @max-variance @saved-groups @compared-groups]]]))


;(check)