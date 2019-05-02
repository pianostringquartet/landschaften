(ns landschaften.views.utils
  (:require [reagent.core :as r]
            [re-com.core :as rc]
            [landschaften.semantic-ui :as semantic-ui]
            [clojure.spec.alpha :as s]))

;; ------------------------------------------------------
;; Utility functions and components
;; ------------------------------------------------------

(def log js/console.log)

(defn valid? [spec data]
  (or (s/valid? spec data)
      (s/explain spec data)))


(def special-chars
  (let [lower-case "ąàáäâãåæăćčĉęèéëêĝĥìíïîĵłľńňòóöőôõðøśșşšŝťțţŭùúüűûñÿýçżźž"]
    (clojure.string/join [lower-case (clojure.string/upper-case lower-case)])))


(def normal-chars
  (let [lower-case "aaaaaaaaaccceeeeeghiiiijllnnoooooooossssstttuuuuuunyyczzz"]
    (clojure.string/join [lower-case (clojure.string/upper-case lower-case)])))


(def enumerate (partial map-indexed
                        (fn [index item] (list index item))))

(defn as-semantic-ui-list-items
  "Assumes components is list of Hiccup forms,
  i.e. don't wrap in brackets again."
  [components]
  (for [[i component] (enumerate components)]
    ^{:key i} [:> semantic-ui/slist-item component]))


(def special->normal-char
  (into {}
    (map
      (fn [special normal] {(str special) (str normal)})
      special-chars
      normal-chars)))


;; works
(defn replace-special-chars [word]
  (clojure.string/join
    (map
      #(if (clojure.string/includes? special-chars (str %))
         (get special->normal-char (str %))
         (str %))
      word)))


;; assumes data are react-components
(defn table [data n-per-row]
  {:pre [(int? n-per-row)]}
  (let [rows (partition-all n-per-row data)]
    [:> semantic-ui/slist
       (for [[index datum] (map-indexed (fn [i r] [i r]) rows)]
          ^{:key index}
          [:> semantic-ui/slist {:horizontal true} datum])]))


(defn search-suggestions [user-input options suggestion-count]
  (let [matches? #(some?
                    (re-find (re-pattern (str "(?i)" user-input)) (replace-special-chars %)))]
    (->> options
      (filter matches?)
      (take suggestion-count)
      (into []))))
