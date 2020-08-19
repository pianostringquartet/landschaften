(ns landschaften.compare.compare
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.specs :as specs]
            [landschaften.compare.compare-subs :as compare-subs]
            [landschaften.compare.compare-events :as compare-events]
            [landschaften.compare.chart :as chart]
            [landschaften.view-utils :as utils]
            [landschaften.semantic-ui :as semantic-ui]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]
            [cljs.spec.alpha :as s]))


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


;; heavier alpha ('a'), i.e. less transparent
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


(>defn compare-group-buttons! [saved-groups-names compared-group-names]
  [(s/coll-of string?) (s/coll-of string?) => vector?]
  (when-not (empty? saved-groups-names)
    [utils/bubble-table
     (map #(compare-group-button! % (into #{} compared-group-names))
          saved-groups-names)
     3]))

(>defn similarity-measurement
  "Progress bar displaying error-rate between two datasets as a percentage."
  [similarity]
  [double? => vector?]
  [:> semantic-ui/container {:style {:padding-right "35%"}} ; garbage UI hack
   [:> semantic-ui/progress {:success  "true"
                             :percent  (goog.string/format "%.1f" similarity)
                             :progress "percent"}]])


(>defn labeled-variance [similarity]
  [double? => vector?]
  [:> semantic-ui/slist {:relaxed true}
   [:> semantic-ui/slist-item
    [rc/label :label "How similar these two groups of paintings are: "]]
   [:> semantic-ui/slist-item
    [similarity-measurement similarity]]])


(>defn compare-screen-buttons [saved-groups-names compared-groups-names]
  [(s/coll-of string?) (s/coll-of string?) => vector?]
  [:> semantic-ui/slist
   [:> semantic-ui/slist-item
    [compare-group-buttons! saved-groups-names compared-groups-names]]
   [:> semantic-ui/slist-item [clear-button!]]])


(>defn compare-sidebar [similarity saved-groups-names compared-groups-names]
  [(s/nilable float?) (s/coll-of string?) (s/coll-of string?) => vector?]
  [:> semantic-ui/slist
   [:> semantic-ui/slist-item [compare-screen-buttons saved-groups-names compared-groups-names]]
   [:> semantic-ui/slist-item (when similarity [labeled-variance similarity])]])


(defn radar-chart [compared-groups]
  [:> semantic-ui/slist-item
   ;; Workaround: force Chart.js to re-render, don't use React lifecycle methods
   (when (<= 2 (count compared-groups))
     ^{:key (rand-int 999)}
     [chart/radar-chart
        (chart/compared-groups->radar-chart-data (first compared-groups) (second compared-groups))])])


(>defn accordion-frequency-tables [groups]
  [(s/coll-of ::specs/group) => vector?]
  (let [->accordion-panel
        (fn [group]
          {:key     (:group-name group)
           :title   {:content (:group-name group)}
           ;; need to feed this just (:frequencies result-set)
           :content {:content (r/as-component [utils/concept-frequency-table (:concept-frequencies group)])}})] ;(:paintings group)])}})]
    [:> semantic-ui/accordion
     {:panels (mapv ->accordion-panel groups)}]))


(>defn mobile-compare-screen [similarity saved-group-names compared-group-names compared-groups]
  [(s/nilable float?) (s/coll-of string?) (s/coll-of string?) (s/coll-of ::specs/group) => vector?]
  [:> semantic-ui/slist
   [:> semantic-ui/slist-item [clear-button!]]
   [:> semantic-ui/slist-item [compare-group-buttons! saved-group-names compared-group-names]]
   [:> semantic-ui/slist-item (when similarity [radar-chart compared-groups])]
   [:> semantic-ui/slist-item (when similarity [labeled-variance similarity])]
   [:> semantic-ui/slist-item
    (if (empty? compared-group-names)
      [rc/label :label "Select some saved searches to start comparing."]
      [accordion-frequency-tables compared-groups])]])


(>defn desktop-compare-screen [similarity saved-groups-names compared-groups-names compared-groups]
  [(s/nilable float?) (s/coll-of string?) (s/coll-of string?) (s/coll-of ::specs/group) => vector?]
  [:> semantic-ui/grid {:columns 2 :centered true}
   [:> semantic-ui/grid-column {:width 10}
    (when similarity [radar-chart compared-groups])
    (when (empty? compared-groups)
      [:> semantic-ui/grid {:centered true :padded true :relaxed true :columns 1}
       [:> semantic-ui/grid-column
        [rc/label :label "Select some saved searches to start comparing."]]])]
   [:> semantic-ui/grid-column {:width 6}
    [compare-sidebar similarity saved-groups-names compared-groups-names]
    [:> semantic-ui/slist {:horizontal true :relaxed true :padded true}
     (for [group compared-groups]
       ^{:key (:group-name group)}                          ; needed
       [utils/table-with-header (:group-name group) (:concept-frequencies group)])]]])


(defn compare-screen []
  (let [similarity           (subscribe [::compare-subs/similarity])
        compared-group-names (subscribe [::compare-subs/compared-group-names])
        compared-groups      (subscribe [::compare-subs/compared-groups])
        saved-group-names    (subscribe [::subs/saved-groups-names])]
    [:> semantic-ui/slist
     [:> semantic-ui/responsive {:max-width 799}
      [mobile-compare-screen @similarity @saved-group-names @compared-group-names @compared-groups]]
     [:> semantic-ui/responsive {:min-width 800}
      [desktop-compare-screen @similarity
       @saved-group-names
       @compared-group-names
       @compared-groups]]]))

;(check) ;; Ghostwheel