(ns landschaften.views.utils
  (:require [reagent.core :as r]
            [re-com.core :as rc]
            [landschaften.semantic-ui :as semantic-ui] ; disable when loading repl
            [landschaften.specs :as specs]
            [clojure.spec.alpha :as s]
            [re-frame.core :refer [subscribe dispatch]]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]))

;; ------------------------------------------------------
;; Utility functions and components
;; ------------------------------------------------------

(def log js/console.log)

(defn valid? [spec data]
  (or (s/valid? spec data)
      (s/explain spec data)))


;; ------------------------------------------------------
;; String manipulation
;; ------------------------------------------------------

(def special-chars
  (let [lower-case "ąàáäâãåæăćčĉęèéëêĝĥìíïîĵłľńňòóöőôõðøśșşšŝťțţŭùúüűûñÿýçżźž"]
    (clojure.string/join [lower-case (clojure.string/upper-case lower-case)])))


(def normal-chars
  (let [lower-case "aaaaaaaaaccceeeeeghiiiijllnnoooooooossssstttuuuuuunyyczzz"]
    (clojure.string/join [lower-case (clojure.string/upper-case lower-case)])))


(def special->normal-char
  (into {}
    (map
      (fn [special normal] {(str special) (str normal)})
      special-chars
      normal-chars)))


(defn replace-special-chars [word]
  (clojure.string/join
    (map
      #(if (clojure.string/includes? special-chars (str %))
         (get special->normal-char (str %))
         (str %))
      word)))


(defn search-suggestions [user-input options suggestion-count]
  (let [matches? #(some?
                    (re-find (re-pattern (str "(?i)" user-input)) (replace-special-chars %)))]
    (->> options
         (filter matches?)
         (take suggestion-count)
         (into []))))

;; ------------------------------------------------------
;; Data massaging
;; ------------------------------------------------------


; TODO: tests, example data

(defn concepts-above [painting n]
  (filter
    #(> (:value %) n)
    (:concepts painting)))

;(def xs [1 2 3])
;
;(map inc xs)


;; takes paintings, and returns
;(defn frequencies-of-concepts-with-certainty-above [paintings n]

;; takes paintings,
;; returns map of {"concept" number-of-times-concept-appears in paintings}
;; excludes concepts whose certainty was equal to or below n
(>defn frequencies-of-concepts-with-certainty-above [paintings n]
  [::specs/paintings float? => map?]
  (->> paintings
       (mapcat #(concepts-above % n))
       (map :name)
       (frequencies)))





(defn paintings->concepts-frequencies
  "Return the n-many concepts' frequencies, where each concept's certainty is above some level."
  [paintings n-many certainty-above]
  {:pre [(valid? ::specs/paintings paintings)
         (int? n-many)
         (float? certainty-above)]
   :post [(s/coll-of vector?) %]}
  (do
    (js/console.log "paintings->concepts-frequencies will return: " (->> (frequencies-of-concepts-with-certainty-above paintings certainty-above)
                                                                         (sort-by second)                                     ; meaningless
                                                                         (reverse)
                                                                         (take n-many)))
    (->> (frequencies-of-concepts-with-certainty-above paintings certainty-above)
         (sort-by second)                                     ; meaningless
         (reverse)
         (take n-many))))

;(frequencies-of-concepts-with-certainty-above
;  sample-paintings 0.80)

;; returns highest frequency first?
;(defn paintings->concepts-frequencies
;  "Return the n-many concepts' frequencies, where each concept's certainty is above some level."
;  [paintings n-many certainty-above]
;  {:pre [(valid? ::specs/paintings paintings)
;         (int? n-many)
;         (float? certainty-above)]
;   :post [(s/coll-of vector?) %]}
;  (do
;    (js/console.log "paintings->concepts-frequencies will return: " (->> (frequencies-of-concepts-with-certainty-above paintings certainty-above)
;                                                                         (sort-by second)                                     ; meaningless
;                                                                         (reverse)
;                                                                         (take n-many)))
;    (->> (frequencies-of-concepts-with-certainty-above paintings certainty-above)
;         (sort-by second)                                     ; meaningless
;         (reverse)
;         (take n-many))))


;paintings->concepts-frequencies

(defn ->percent [frequency total]
  (->> (/ frequency total)
       (double)
       (* 100)
       (goog.string/format "%.1f")
       (js/parseFloat)))


(defn paintings->percentage-chart-data [paintings n-many certainty-above]
  (let [total (count paintings)]
    (->> (paintings->concepts-frequencies paintings n-many certainty-above)
         (mapv
           (fn [[concept frequency]]
             [concept (->percent frequency total)])))))


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


;; this table is not a real table -- it's just rows arranged
;; Assumes data are React.js components
(defn bubble-table
  "A table that arranges its data in rows n-length.

  Not a real table, but rather a 'forced arrangement'."
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
   [:> semantic-ui/table-cell {:on-click #(log "on click called")} name]
   [:> semantic-ui/table-cell (goog.string/format "%.2f" value)]])


(defn sem-table [frequency-data]
  [:> semantic-ui/table {:selectable true :compact "very" :size "small"}
   [:> semantic-ui/table-body
    (for [datum frequency-data]
      ^{:key (first datum)} (sem-table-row (first datum) (second datum)))]])


(defn concept-frequency-table [paintings n-many certainty-above]
  [sem-table (paintings->percentage-chart-data paintings n-many certainty-above)])


(>defn table-with-header [header paintings]
  [string? ::specs/paintings => vector?]
  [:> semantic-ui/slist-item
   {:header  header
    :content {:content (r/as-component ^{:key header} [concept-frequency-table paintings 15 0.85])}}])