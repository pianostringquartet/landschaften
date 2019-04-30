(ns landschaften.views.compare
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.events :as events]
            [landschaften.views.graph :as graph]
            [landschaften.views.stats :as stats]
            [landschaften.views.utils :as utils]
            [landschaften.specs :as specs]
            [clojure.spec.alpha :as s]
            [landschaften.semantic-ui :as semantic-ui]))


(defn group-button [name color on-click]
  [:> semantic-ui/button
   {:color    color
    :style    {:border-radius "30px" :padding "8px"}        ; curvier
    :on-click on-click}
   name])


(defn selected-button [group-name compared-group-names]
  {:pre [(string? group-name)
         (set? compared-group-names)]}
  (let [being-compared? (contains? compared-group-names group-name) ;(some #{group-name} compared-group-names)
        on-click        (if being-compared?
                          #(dispatch [::events/remove-compare-group-name group-name])
                          #(dispatch [::events/add-compare-group-name group-name]))
        color           (if being-compared?
                          "orange"
                          "grey")]
    (do
      (utils/log "selected-button called")
      (utils/log "compared-group-names: " compared-group-names)

      [group-button group-name color on-click])))



(defn saved-group-buttons [saved-groups compared-group-names]
  (when-not (empty? saved-groups)
    [utils/table
     (map #(selected-button % (into #{} compared-group-names))
          (keys saved-groups))
     2]))

(defn table [paintings]
  (let [n-chartpoints     (subscribe [::subs/show-n-chart-points])
        concept-certainty (subscribe [::subs/concept-certainty-above])]
    [graph/frequencies-chart
     "Table"
     ;; this can't be made a sub,
     ;; because an essential component (paintings)
     ;; is dynamically determined
     ;; ... could be made a sub, but
     (graph/paintings->percentage-chart-data paintings @n-chartpoints @concept-certainty)
     "Concepts' Frequencies"
     ["Concepts" "Frequencies (%)"]]))


(defn labeled-table [name paintings]
  [rc/v-box
   :gap "8px"
   :children [[rc/title :label name :level :level3]
              [table paintings]]])


(defn clear-button []
  [:> semantic-ui/button
   {:on-click #(dispatch [::events/comparisons-cleared])
    :color    "red"
    :compact  true}
   "CLEAR"])


(defn accordion-tables [groups]
  (let [->accordion-panel
        (fn [group]
          {:key (:group-name group)
           :title {:content (:group-name group)}
           :content {:content (r/as-component [table (:paintings group)])}})]
    ;(fn []
      [:> semantic-ui/accordion
       {:panels (mapv ->accordion-panel groups)}]))


(defn labeled-tables [groups]
  (mapv
    (fn [group] [labeled-table (:group-name group) (:paintings group)])
    groups))


(defn error-ready-data [group]
  (graph/paintings->error-data
    (:paintings group)
    20
    0.94))


;; want to do a progress bar etc. for error rate;
;; some kind of visual that makes it intelligible to user;
;; but error rate isn't a percent...
;; could you try e.g. (actual error rate / maximum error rate)
;; 'max error rate' would be different for each group

(defn error-rate-label [error max-error]
  (let [formatter #(goog.string/format "%.4f" %)]
    [rc/v-box
     :children
     [[rc/p
       {:style {:color "lightGrey"}}
       "Error measures similarity of two groups of paintings."]
      [rc/p
       {:style {:color "lightGrey"}}
       "Smaller error -> greater similarity"]
      [rc/label
       :label (str "Error rate: "
                   (formatter error))]
      [rc/label
       :label (str "Max Error rate: "
                   (formatter max-error))]
      [:> semantic-ui/progress {
                                ;:value (formatter error)
                                :success "true"
                                :percent (goog.string/format "%.1f" (* 100 (/ error max-error)))
                                ;:total (formatter max-error)
                                :progress "percent"}]]]))


(defn mobile-compare-panel [groups]
  (when-not (empty? groups)
   [accordion-tables groups]))

(defn desktop-compare-panel [groups]
  (when-not (empty? groups)
    [rc/h-box
     :gap "16px"
     ;; needs to be vector?
     :children (labeled-tables groups)]))


(defn compare-panel []
  (let [error-rate (subscribe [::subs/error-rate])
        max-error-rate (subscribe [::subs/max-error-rate])
        groups (subscribe [::subs/compared-groups])
        saved-groups         (subscribe [::subs/saved-groups])
        compared-group-names (subscribe [::subs/compared-group-names])]
    [:> semantic-ui/slist
     [:> semantic-ui/slist-item [clear-button]]
     (do
       (utils/log "compare-panel: @saved-groups: " @saved-groups)
       (utils/log "compare-panel: @compared-group-names: " @compared-group-names)
       [:> semantic-ui/slist-item [saved-group-buttons @saved-groups @compared-group-names]])
     (when @error-rate
       [:> semantic-ui/slist-item [error-rate-label @error-rate @max-error-rate]])
     [:> semantic-ui/responsive {:max-width 799}
      [mobile-compare-panel @groups]]
     [:> semantic-ui/responsive {:min-width 800}
      [desktop-compare-panel @groups]]]))
