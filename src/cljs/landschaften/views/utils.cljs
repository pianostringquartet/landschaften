(ns landschaften.views.utils
  (:require [reagent.core :as r]
            [re-com.core :as rc]
            [landschaften.semantic-ui :as semantic-ui] ; disable when loading repl
            [landschaften.specs :as specs]
            [clojure.spec.alpha :as s]
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


;paintings->concepts-frequencies




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
(defn table [data n-per-row]
  {:pre [(int? n-per-row)]}
  (let [rows (partition-all n-per-row data)]
    ^{:key (str (first data))}
    [:> semantic-ui/slist
       (for [[index datum] (map-indexed (fn [i r] [i r]) rows)]
          ^{:key index}
          [:> semantic-ui/slist {:horizontal true} datum])]))


(def sample-paintings
  #{{:date "c. 1469",
     :school "Italian",
     :type "portrait",
     :title "Portrait of a Young Man",
     :author "BOTTICELLI, Sandro",
     :concepts #{{:name "gown (clothing)", :value 0.90708596}
                 {:name "one", :value 0.986664}
                 {:name "cape", :value 0.87464726}
                 {:name "adult", :value 0.98579407}
                 {:name "side view", :value 0.8062773}
                 {:name "religion", :value 0.93637943}
                 {:name "sculpture", :value 0.86673677}
                 {:name "lid", :value 0.9411217}
                 {:name "people", :value 0.9946501}
                 {:name "painting", :value 0.9754119}
                 {:name "wear", :value 0.95125747}
                 {:name "portrait", :value 0.9801239}
                 {:name "facial expression", :value 0.8723508}
                 {:name "man", :value 0.9584564}
                 {:name "veil", :value 0.96336377}
                 {:name "facial hair", :value 0.8060329}
                 {:name "woman", :value 0.874543}
                 {:name "illustration", :value 0.8150852}
                 {:name "art", :value 0.96110016}
                 {:name "leader", :value 0.8733945}},
     :id 5623,
     :timeframe "1451-1500",
     :form "painting",
     :jpg "https://www.wga.hu/art/b/botticel/7portrai/01youngm.jpg"}
    {:date "c. 1483",
     :school "Italian",
     :type "portrait",
     :title "Portrait of a Young Man",
     :author "BOTTICELLI, Sandro",
     :concepts #{{:name "one", :value 0.99197435}
                 {:name "adult", :value 0.98972064}
                 {:name "side view", :value 0.86024535}
                 {:name "religion", :value 0.7984845}
                 {:name "jewelry", :value 0.8666209}
                 {:name "print", :value 0.8635602}
                 {:name "lid", :value 0.94501436}
                 {:name "outerwear", :value 0.9058312}
                 {:name "necklace", :value 0.92569244}
                 {:name "people", :value 0.9990963}
                 {:name "painting", :value 0.94927025}
                 {:name "wear", :value 0.9762198}
                 {:name "portrait", :value 0.9976093}
                 {:name "man", :value 0.94694376}
                 {:name "veil", :value 0.96435404}
                 {:name "profile", :value 0.8191407}
                 {:name "facial hair", :value 0.88426876}
                 {:name "jacket", :value 0.86635554}
                 {:name "art", :value 0.9661212}
                 {:name "leader", :value 0.96634877}},
     :id 5632,
     :timeframe "1451-1500",
     :form "painting",
     :jpg "https://www.wga.hu/art/b/botticel/7portrai/10youngm.jpg"}})

;; should satisfy ::group spec...
(def sample-group
  {:group-name "a sample group"
   :paintings sample-paintings
   :type-constraints #{}
   :school-constraints #{}
   :timeframe-constraints #{}
   :concept-constraints #{}
   :artist-constraints #{}})

(def sample-group-2
  {:group-name "spanish-religious"
   :paintings sample-paintings
   :type-constraints #{"religious"}
   :school-constraints #{"Spanish"}
   :concept-constraints #{"religion"}})
