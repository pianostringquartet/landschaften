(ns landschaften.screens.compare
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.events :as events]
            [landschaften.views.chart :as graph]
            [landschaften.variance :as stats]
            [landschaften.views.utils :as utils]
            [landschaften.specs :as specs]
            [clojure.spec.alpha :as s]
            [landschaften.semantic-ui :as semantic-ui]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]))


(>defn clear-button! []
  [ => vector?]
  [:> semantic-ui/button
   {:on-click #(dispatch [::events/comparisons-cleared])
    :color    "red"
    :compact  true}
   "CLEAR"])


(>defn saved-search-button! [group-name compared-group-names]
  [string? set? => vector?]
  (let [being-compared? (contains? compared-group-names group-name)] ;(some #{group-name} compared-group-names)
    [:> semantic-ui/button
     {:color    (if being-compared? "orange" "grey")
      :on-click (if being-compared?
                  #(dispatch [::events/remove-compare-group-name group-name])
                  #(dispatch [::events/add-compare-group-name group-name]))
      :style    {:border-radius "30px" :padding "8px"}}
     group-name]))



(defn saved-search-buttons [saved-groups compared-group-names]
  (when-not (empty? saved-groups)
    [utils/table
     (map #(saved-search-button! % (into #{} compared-group-names))
          (keys saved-groups))
     2]))


(defn table-chart [paintings]
  (let [n-chartpoints     (subscribe [::subs/show-n-chart-points])
        concept-certainty (subscribe [::subs/concept-certainty-above])]
    [graph/frequencies-chart
     "Table"
     (graph/paintings->percentage-chart-data paintings @n-chartpoints @concept-certainty)
     "Concepts' Frequencies"
     ["Concepts" "Frequencies (%)"]]))


(>defn table-with-headers [group]
  [::specs/group => vector?]
  [:> semantic-ui/slist-item
   {:header  (:group-name group)
    :content (r/as-component ^{:key (:group-name group)} [table-chart (:paintings group)])}])


(>defn accordion-tables [groups]
  [(s/coll-of ::specs/group) => vector?]
  (let [->accordion-panel
        (fn [group]
          {:key     (:group-name group)
           :title   {:content (:group-name group)}
           :content {:content (r/as-component [table-chart (:paintings group)])}})]
    [:> semantic-ui/accordion
     {:panels (mapv ->accordion-panel groups)}]))


(defn error-rate-label [error max-error]
  (let [formatter #(goog.string/format "%.4f" %)
        note (fn [s] [rc/p {:style {:color "lightGrey"}} s])
        label (fn [s] [rc/label :label s])
        components [[note "Error measures similarity of two groups of paintings."]
                    [note "Smaller error -> greater similarity"]
                    [label (str "Error rate: " (formatter error))]
                    [label (str "Max Error rate: " (formatter max-error))]]]
    [:> semantic-ui/slist {:relaxed true}
     (utils/as-semantic-ui-list-items components)
     [:> semantic-ui/progress {:success  "true"
                               :percent  (goog.string/format "%.1f" (* 100 (/ error max-error)))
                               :progress "percent"}]]))


(defn mobile-compare-panel [groups]
  (when-not (empty? groups)
    [accordion-tables groups]))


(defn desktop-compare-panel [groups]
  (when-not (empty? groups)
    [:> semantic-ui/slist
     {:horizontal true :relaxed true}
     (for [group groups]
       ^{:key (:group-name group)}
       [table-with-headers group])]))


(defn compare-panel []
  (let [error-rate           (subscribe [::subs/error-rate])
        max-error-rate       (subscribe [::subs/max-error-rate])
        groups               (subscribe [::subs/compared-groups])
        saved-groups         (subscribe [::subs/saved-groups])
        compared-group-names (subscribe [::subs/compared-group-names])]
    [:> semantic-ui/slist
     [:> semantic-ui/slist-item [clear-button!]]
     [:> semantic-ui/slist-item [saved-search-buttons @saved-groups @compared-group-names]]
     (when @error-rate
       [:> semantic-ui/slist-item [error-rate-label @error-rate @max-error-rate]])
     [:> semantic-ui/responsive {:max-width 799}
      [mobile-compare-panel @groups]]
     [:> semantic-ui/responsive {:min-width 800}
      [desktop-compare-panel @groups]]]))
