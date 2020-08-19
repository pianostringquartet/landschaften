(ns landschaften.view-utils
  (:require [reagent.core :as r]
            [landschaften.semantic-ui :as semantic-ui]
            [landschaften.specs :as specs]
            [clojure.spec.alpha :as s]
            [re-frame.core :refer [subscribe dispatch]]
            [landschaften.helpers :as helpers]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]
            [landschaften.db :as db]))


(defn count->percent [[concept-name concept-count]]
  {:post [vector? (string? (first %))]}
  [concept-name (->> concept-count
                     (goog.string/format "%.1f")
                     (js/parseFloat))])


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


(>defn concept-frequency-table [concept-frequencies]
  [::specs/concept-frequencies => vector?]
  [sem-table (take db/SHOW-N-CHARTPOINTS concept-frequencies)])


(>defn table-with-header [header concept-frequencies]
  [string? ::specs/concept-frequencies => vector?]
  [:> semantic-ui/slist-item
   {:header  header
    :content {:content (r/as-component ^{:key header}
                                       [concept-frequency-table concept-frequencies])}}])

;(check)