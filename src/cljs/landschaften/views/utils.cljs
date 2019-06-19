(ns landschaften.views.utils
  (:require [reagent.core :as r]
            [re-com.core :as rc]
            [landschaften.semantic-ui :as semantic-ui]      ; disable when loading repl
            [landschaften.specs :as specs]
            [clojure.spec.alpha :as s]
            [re-frame.core :refer [subscribe dispatch]]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]
            [landschaften.helpers :as helpers]))

;; ------------------------------------------------------
;; Utility functions and components
;; ------------------------------------------------------

;(def log js/console.log)

;;; ------------------------------------------------------
;;; String manipulation
;;; ------------------------------------------------------
;
;(def special-chars
;  (let [lower-case "ąàáäâãåæăćčĉęèéëêĝĥìíïîĵłľńňòóöőôõðøśșşšŝťțţŭùúüűûñÿýçżźž"]
;    (clojure.string/join [lower-case (clojure.string/upper-case lower-case)])))
;
;
;(def normal-chars
;  (let [lower-case "aaaaaaaaaccceeeeeghiiiijllnnoooooooossssstttuuuuuunyyczzz"]
;    (clojure.string/join [lower-case (clojure.string/upper-case lower-case)])))
;
;
;(def special->normal-char
;  (into {}
;    (map
;      (fn [special normal] {(str special) (str normal)})
;      special-chars
;      normal-chars)))
;
;
;(defn replace-special-chars [word]
;  (clojure.string/join
;    (map
;      #(if (clojure.string/includes? special-chars (str %))
;         (get special->normal-char (str %))
;         (str %))
;      word)))


;(defn search-suggestions [user-input options suggestion-count]
;  (let [matches? #(some?
;                    (re-find (re-pattern (str "(?i)" user-input)) (helpers/replace-special-chars %)))]
;    (->> options
;         (filter matches?)
;         (take suggestion-count)
;         (into []))))

;; ------------------------------------------------------
;; Data massaging
;; ------------------------------------------------------


(defn concepts-above [painting n]
  (filter
    #(> (:value %) n)
    (:concepts painting)))

;; better -- get frequencies of all concepts
;; then filter by whether given concept has certainty above X
;; then just grab n-many

(>defn frequencies-of-concepts-with-certainty-above [paintings n]
  [::specs/paintings float? => map?]
  (->> paintings
       (mapcat #(concepts-above % n))
       (map :name)
       (frequencies)))

(>defn paintings->concepts-frequencies
  "Return the n-many concepts' frequencies,
  where each concept's certainty is above some level."
  [paintings n-many certainty-above]
  [::specs/paintings int? float? => (s/coll-of vector?)]
  (->> (frequencies-of-concepts-with-certainty-above paintings certainty-above)
       (sort-by second)
       (reverse)
       (take n-many)))

(defn ->percent [frequency total]
  (->> (/ frequency total)
       (double)
       (* 100)
       (goog.string/format "%.1f")
       (js/parseFloat)))


(>defn paintings->frequency-percent-data [paintings n-many certainty-above]
  [::specs/paintings int? float? => (s/coll-of vector?)]
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


(check)