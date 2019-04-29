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


;; make group-buttons a table like others

;; make specific selected groups as accordions

;;


(defn group-button [name color on-click]
  [:> semantic-ui/button
   {:color    color
    :style    {:border-radius "30px" :padding "8px"}        ; curvier
    :on-click on-click}
   name])

(defn selected-button [group-name compared-group-names]
  {:pre [(string? group-name)]}
  (let [being-compared? (some #{group-name} compared-group-names)
        on-click        (if being-compared?
                          #(dispatch [::events/remove-compare-group-name group-name])
                          #(dispatch [::events/add-compare-group-name group-name]))
        color           (if being-compared?
                          "orange"
                          "grey")]
    [group-button group-name color on-click]))


(defn saved-groups []
  (let [saved-groups         (subscribe [::subs/saved-groups])
        compared-group-names (subscribe [::subs/compared-group-names])]
    [rc/v-box
     :gap "8px"
     :children [[utils/button-table
                 (keys @saved-groups)
                 2
                 #(selected-button % @compared-group-names)]]]))


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


(defn accordion-title [title active-index index on-click]
  [:> semantic-ui/accordion-title
   {:active   (= active-index index)
    :index    index
    :on-click on-click}
   [:> semantic-ui/icon {:name "dropdown"}]
   title])

(defn accordion-content [content active-index index]
  [:> semantic-ui/accordion-content
   {:active (= active-index index)}
   content])

#_(defn mobile-labeled-tables [[group-1 group-2]]
    (let [active-index (r/atom 0)
          on-click     (fn [event props]
                         (do
                           (utils/log "accordion on-click called")
                           (utils/log "props: " props)
                           (utils/log "event: " event)
                           (let [index     (.-index props)
                                 new-index (if (= index @active-index)
                                             -1
                                             index)]
                             (reset! active-index new-index))))]
        (fn []
          [:> semantic-ui/accordion
           [accordion-title
            (:group-name group-1) @active-index 0 on-click]
           [accordion-content
             [table (:paintings group-1)] @active-index 0]
           [accordion-title
            (:group-name group-2) @active-index 1 on-click]
           [accordion-content
            [table (:paintings group-2)] @active-index 1]])))


(defn mobile-labeled-tables [groups]
  (let [->accordion-panel
        (fn [group]
          {:key (:group-name group)
           :title {:content (:group-name group)}
           :content {:content (r/as-component [table (:paintings group)])}})]
    (fn []
      [:> semantic-ui/accordion
       {:defaultActiveIndex 0
        :panels (mapv ->accordion-panel groups)}])))


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

(defn error-rate-label [error]
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
                 ;   doesn't have to be "times 100"
                 ; error rate isn't actually a percent,
                 ; but the extremely long decimals were hard to read
                 ;(goog.string/format "%.3f" (* error 100)))]]])
                 (goog.string/format "%.4f" error))]]])


(defn mobile-compare-panel [groups]
  (when-not (empty? groups)
   [mobile-labeled-tables groups]))


(defn desktop-compare-panel [groups]
  (when-not (empty? groups)
    [rc/h-box
     :gap "16px"
     :children (labeled-tables groups)]))


(defn compare-panel []
  (let [error-rate (subscribe [::subs/error-rate])
        groups (subscribe [::subs/compared-groups])]
    [:> semantic-ui/slist
     [:> semantic-ui/slist-item [clear-button]]
     [:> semantic-ui/slist-item [saved-groups]]
     (when @error-rate
       [:> semantic-ui/slist-item [error-rate-label @error-rate]])
     [:> semantic-ui/responsive {:max-width 799}
      [mobile-compare-panel @groups]]
     [:> semantic-ui/responsive {:min-width 800}
      [desktop-compare-panel @groups]]]))


#_(defn compare-panel []
    (let [error-rate (subscribe [::subs/error-rate])]
      [:> semantic-ui/slist {:relaxed true}
       ;[:> semantic-ui/slist-item [clear-button]]
       ;[:> semantic-ui/slist-item [saved-groups]]
       ;(when @error-rate
       ;  [:> semantic-ui/slist-item [error-rate-label @error-rate]])
       [:> semantic-ui/slist-item [display-data]]]))
