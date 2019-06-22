(ns landschaften.views.utils
  (:require [reagent.core :as r]
            [re-com.core :as rc]
            [landschaften.semantic-ui :as semantic-ui]
            [landschaften.specs :as specs]
            [clojure.spec.alpha :as s]
            [re-frame.core :refer [subscribe dispatch]]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]
            [landschaften.helpers :as helpers]))


;; ------------------------------------------------------
;; Getting frequencies of concepts
;; ------------------------------------------------------

(>defn frequencies-of-concepts-with-certainty-above [paintings certainty-above]
  [::specs/paintings float? => map?]
  (let [high-certainty-concepts (fn [{:keys [concepts]}]
                                  (filter #(> (:value %) certainty-above) concepts))]
    (->> paintings
         (mapcat high-certainty-concepts)
         (map :name)
         (frequencies))))


(>defn paintings->concepts-frequencies
  "Return the n-many concepts' frequencies,
  where each concept's certainty is above certainty-above."
  [paintings n-many certainty-above]
  [::specs/paintings int? float? => (s/coll-of vector?)]
  (->> (frequencies-of-concepts-with-certainty-above paintings certainty-above)
       (sort-by second)
       (reverse)
       (take n-many)))


(defn count->percent [[concept-name concept-count] total]
  {:post [vector? (string? (first %))]}
  [concept-name (->> (/ concept-count total)
                     (double)
                     (* 100)
                     (goog.string/format "%.1f")
                     (js/parseFloat))])


(>defn paintings->frequency-percent-data [paintings n-many certainty-above]
  [::specs/paintings int? float? => (s/coll-of vector?)]
  (->> (paintings->concepts-frequencies paintings n-many certainty-above)
       (mapv #(count->percent % (count paintings)))))


;; ------------------------------------------------------
;; UI helpers
;; ------------------------------------------------------

(def enumerate (partial map-indexed
                        (fn [index item] (list index item))))

(defn as-semantic-ui-list-items
  "Assumes components is list of Hiccup forms,
  i.e. don't wrap in brackets again."
  [components]
  (for [[i component] (enumerate components)]
    ^{:key i} [:> semantic-ui/slist-item component]))


;; Assumes data are React.js components
(defn bubble-table
  "A 'table' that arranges its data in rows n-length."
  [data n-per-row]
  {:pre [(int? n-per-row)]}
  (let [rows (partition-all n-per-row data)]
    ^{:key (str (first data))}
    [:> semantic-ui/slist
       (for [[index datum] (map-indexed (fn [i r] [i r]) rows)]
          ^{:key index}
          [:> semantic-ui/slist {:horizontal true} datum])]))


(>defn sem-table-row [name value]
  [string? float? => vector?]
  ^{:key name}
  [:> semantic-ui/table-row
   [:> semantic-ui/table-cell {:on-click #(helpers/log "on click called")} name]
   [:> semantic-ui/table-cell (goog.string/format "%.2f" value)]])


(defn sem-table [frequency-data]
  [:> semantic-ui/table {:selectable true :compact "very" :size "small"}
   [:> semantic-ui/table-body
    (for [datum frequency-data]
      ^{:key (first datum)} (sem-table-row (first datum) (second datum)))]])


(defn concept-frequency-table [paintings n-many certainty-above]
  [sem-table (paintings->frequency-percent-data paintings n-many certainty-above)])


(>defn table-with-header [header paintings]
  [string? ::specs/paintings => vector?]
  [:> semantic-ui/slist-item
   {:header  header
    :content {:content (r/as-component ^{:key header} [concept-frequency-table paintings 15 0.85])}}])


;(check)