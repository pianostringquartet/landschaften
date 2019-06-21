(ns landschaften.screens.compare
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.specs :as specs]
            [landschaften.events :as events]
            [landschaften.views.chart :as chart]
            [landschaften.views.utils :as utils]
            [landschaften.semantic-ui :as semantic-ui]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]
            [cljs.spec.alpha :as s]))


;; ------------------------------------------------------
;; Comparing groups' (dis)similarity
;; - statistically via variance
;; - visualization via radar chart
;; ------------------------------------------------------


(>defn clear-button! []
  [=> vector?]
  [:> semantic-ui/button
   {:on-click #(dispatch [::events/comparisons-cleared])
    :color    "red"
    :compact  true}
   "CLEAR"])

;; give heavier alpha ('a'), i.e. less transparent
(def color-1 "rgba(255, 99, 132, 0.7)")
(def color-2 "rgba(54, 162, 235, 0.7)")

(>defn compare-group-button! [group-name compared-group-names]
  [string? set? => vector?]
  (let [being-compared? (contains? compared-group-names group-name)
        button-color (if being-compared?
                       (if (= group-name (first compared-group-names))
                         color-1
                         color-2)
                       "grey")]
    [:> semantic-ui/button
     {:on-click (if being-compared?
                  #(dispatch [::events/remove-compare-group-name group-name])
                  #(dispatch [::events/add-compare-group-name group-name]))
      :style    {:color "#fff" ; white
                 :background-color button-color
                 :border-radius "30px"
                 :padding "4px"}}
     group-name]))


(defn compare-group-buttons [saved-groups compared-group-names]
  (when-not (empty? saved-groups)
    [utils/bubble-table
     (map #(compare-group-button! % (into #{} compared-group-names))
          (keys saved-groups))
     3]))


(>defn similarity-measurement
  "Progress bar displaying error-rate between two datasets as"
  [variance max-variance]
  [double? double? => vector?]
  (let [as-percent    (* 100 (/ variance max-variance))
        as-similarity (- 100 as-percent)]
    [:> semantic-ui/progress {:success  "true"
                              :percent  (goog.string/format "%.1f" as-similarity)
                              :progress "percent"}]))


(>defn labeled-variance [variance max-variance]
  [double? double? => vector?]
  [:> semantic-ui/slist {:relaxed true}
   [:> semantic-ui/slist-item
    [rc/label :label "How similar these two groups of paintings are:"]]
   [:> semantic-ui/slist-item
    [similarity-measurement variance max-variance]]])


;; a duplicate of what you have in utils
(>defn concept-frequency-table [group n-many certainty-above]
  [::specs/group int? float? => vector?]
  [utils/concept-frequency-table (:paintings group) n-many certainty-above])


;(defn desktop-compare-screen [groups]
;  (when-not (empty? groups)
;    [:> semantic-ui/slist {:horizontal true :relaxed true}
;     (for [group groups]
;       ^{:key (:group-name group)}
;       [utils/table-with-header (:group-name group) (:paintings group)])]))


;(defn variance-display-mobile [variance max-variance compared-group-1 compared-group-2 n-many certainty-above]
;  [labeled-variance])


;(defn mobile-compare-screen [groups]
;  (when-not (empty? groups)
;    (let [->accordion-panel
;          (fn [group]
;            {:key     (:group-name group)
;             :title   {:content (:group-name group)}
;             ;:content {:content (r/as-component [utils/concept-frequency-table (:paintings group) 15 0.85])}})]
;             :content {:content (r/as-component [concept-frequency-table group 15 0.85])}})]
;      [:> semantic-ui/accordion
;       {:panels (mapv ->accordion-panel groups)}])))





(>defn compare-screen-buttons [saved-groups compared-groups]
  [map? (s/coll-of ::specs/group) => vector?]
  [:> semantic-ui/slist
   [:> semantic-ui/slist-item
    [compare-group-buttons saved-groups (map :group-name compared-groups)]]
   [:> semantic-ui/slist-item [clear-button!]]])


;; was old desktop-compare-screen
;(defn desktop-frequency-charts [groups]
;  (when-not (empty? groups)
;    [:> semantic-ui/slist {:horizontal true :relaxed true}
;     (for [group groups]
;       ^{:key (:group-name group)}
;       [utils/table-with-header (:group-name group) (:paintings group)])]))


(defn compare-sidebar [variance max-variance saved-groups compared-groups]
  [:> semantic-ui/slist
   [:> semantic-ui/slist-item
    [compare-screen-buttons saved-groups compared-groups]]
   [:> semantic-ui/slist-item
    (when variance
      [labeled-variance variance max-variance])]])


(defn radar-chart [compared-groups]
   [:> semantic-ui/slist-item
    ^{:key (rand-int 999)} ;; Workaround: force Chart.js to re-render, don't use React lifecycle methods
    [chart/radar-chart
     (chart/compared-groups->radar-chart-data!
       (first compared-groups) (second compared-groups) 15 0.85)]])

(defn accordion-frequency-tables [groups]
  ;(when-not (empty? groups)
    (let [->accordion-panel
          (fn [group]
            {:key     (:group-name group)
             :title   {:content (:group-name group)}
             :content {:content (r/as-component [concept-frequency-table group 15 0.85])}})]
      [:> semantic-ui/accordion
       {:panels (mapv ->accordion-panel groups)}]))


;; for mobile, show, in order:
;;
;; buttons first,
(defn mobile-compare-screen [variance max-variance saved-groups compared-groups]
  ;[:> semantic-ui/grid {:stackable true}
  ; [:> semantic-ui/grid-column
  ;[:> semantic-ui/grid {:columns 1 :centered true :relaxed true :padded true}
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
  [:> semantic-ui/grid {:columns 1 :centered true :relaxed true :padded true}
   [:> semantic-ui/slist {:relaxed true}
    [:> semantic-ui/slist-item
     (when variance
       [radar-chart compared-groups])]
    [:> semantic-ui/slist-item
     [:> semantic-ui/grid {:columns 2}
      [:> semantic-ui/grid-column
       (if (empty? compared-groups)
         [rc/label :label "Select some saved searches to start comparing."]
         [:> semantic-ui/slist {:horizontal true :relaxed true}
          (for [group compared-groups]
            ^{:key (:group-name group)}
            [utils/table-with-header (:group-name group) (:paintings group)])])]
      [:> semantic-ui/grid-column
       [compare-sidebar variance max-variance saved-groups compared-groups]]]]]])


(defn compare-screen []
  (let [
        variance             (subscribe [::subs/variance])
        max-variance         (subscribe [::subs/max-variance])

        compared-groups               (subscribe [::subs/compared-groups])

        saved-groups         (subscribe [::subs/saved-groups])]

    [:> semantic-ui/slist
      [:> semantic-ui/responsive {:max-width 799}
       [mobile-compare-screen @variance @max-variance @saved-groups @compared-groups]]
      [:> semantic-ui/responsive {:min-width 800}
       [desktop-compare-screen @variance @max-variance @saved-groups @compared-groups]]]))


;(defn compare-screen []
;  (let [
;        variance             (subscribe [::subs/variance])
;        max-variance         (subscribe [::subs/max-variance])
;
;        groups               (subscribe [::subs/compared-groups])
;
;        saved-groups         (subscribe [::subs/saved-groups])]
;
;    [:> semantic-ui/slist
;     [:> semantic-ui/grid {:columns 1 :centered true :relaxed true :padded true}
;      [:> semantic-ui/responsive {:max-width 799} [mobile-compare-screen @variance @max-variance @saved-groups @groups]]
;      [:> semantic-ui/responsive {:min-width 800} [desktop-compare-screen @variance @max-variance @saved-groups @groups]]]]))




;(defn compare-screen []
;  (let [variance             (subscribe [::subs/variance])
;        max-variance         (subscribe [::subs/max-variance])
;        groups               (subscribe [::subs/compared-groups])
;        saved-groups         (subscribe [::subs/saved-groups])]
;    ;compared-groups      (subscribe [::subs/compared-groups])]
;    [:> semantic-ui/slist
;     [:> semantic-ui/slist-item [clear-button!]]
;     [:> semantic-ui/slist-item [compare-group-buttons @saved-groups (map :group-name @groups)]]
;     [:> semantic-ui/grid {:columns 1 :centered true :relaxed true :padded true}
;      (when @variance
;        [:> semantic-ui/slist-item [variance-label @variance @max-variance]]
;
;        ^{:key (rand-int 999)} ;; Workaround: force Chart.js to re-render, don't use React lifecycle methods
;        [chart/radar-chart
;         (chart/compared-groups->radar-chart-data! (first @groups) (second @groups) 15 0.85)])
;      [:> semantic-ui/responsive {:max-width 799} [mobile-compare-screen @groups]]
;      [:> semantic-ui/responsive {:min-width 800} [desktop-compare-screen @groups]]]]))



;(check)