(ns landschaften.views.stats
  (:require [clojure.spec.alpha :as s]))

;
;;; for calculating `error` (later, cluster analysis?)
;
;
;;; will want to first just go by concepts, not 'school' etc.
;;; because 'school' and 'timeframe' being different is not interesting...
;
;;; start with one dictionary per group,
;;; dict has one key per feature (here: concept),
;;; key's value is how many times the feature appears
;
;
;;; suppose we have 'german' dict and 'french' dict
;
;;; (Here dict will be a map of concepts, i.e. NOT a ::group)
;
;;; first we normalize the values;
;;; i.e. divide each key's value by the sum(all keys' values)
;;(note: key's value = how many times that concept appears)
;
;
;;; then we do
;
;;error = 0
;;
;;(AFTER NORMALIZING BOTH german and french concept counts:)
;;iterate through german concepts;
;;if the concept is also in french concepts,
;;then error = error +
;;            (french concept's frequency - german concept's frequency)^2
;;else error = error + (german concept's frequency)^2
;
;;; some thoughts:
;;; 1: LOOK UP what 'error 'actually is
;;; 2: reproduce the python code in clojure first
;;; 3: finally, use your custom clojure data
;
;;https://www.wikihow.com/Calculate-Variance
;
;
;;; example data produced by ->chart-data fn
;;; French landscape group
(def gf [["no person" 100] ["travel" 61] ["landscape" 56] ["water" 55] ["outdoors" 52] ["tree" 44] ["people" 28] ["art" 25] ["building" 25] ["architecture" 25] ["painting" 24] ["nature" 23] ["old" 20] ["sky" 16] ["adult" 16] ["sea" 14] ["group" 14] ["vehicle" 13] ["illustration" 13] ["watercraft" 13]])
;;; Spanish religion group
(def gs [["art" 19] ["painting" 19] ["people" 18] ["religion" 17] ["adult" 17] ["man" 13] ["saint" 12] ["woman" 11] ["Renaissance" 8] ["one" 8] ["god" 8] ["group" 8] ["Mary" 7] ["illustration" 7] ["book" 6] ["veil" 5] ["sculpture" 5] ["church" 5] ["aura" 4] ["print" 4]])
;
;;; convert chart-data to a single big Python dict (Clojure map)
(defn chart-data->map [chart-data]
  (apply merge
    (map (fn [[concept frequency]] {concept frequency})
         chart-data)))
;
;;; (converted format:)
(def gfm {"architecture" 25,
          "watercraft" 13,
          "outdoors" 52,
          "illustration" 13,
          "group" 14,
          "adult" 16,
          "travel" 61,
          "water" 55,
          "painting" 24,
          "no person" 100,
          "people" 28,
          "nature" 23,
          "sea" 14,
          "landscape" 56,
          "tree" 44,
          "old" 20,
          "vehicle" 13,
          "building" 25,
          "art" 25,
          "sky" 16})

(def gsm {"book" 6,
          "illustration" 7,
          "group" 8,
          "adult" 17,
          "painting" 19,
          "people" 18,
          "Mary" 7,
          "church" 5,
          "sculpture" 5,
          "god" 8,
          "man" 13,
          "veil" 5,
          "woman" 11,
          "one" 8,
          "saint" 12,
          "print" 4,
          "Renaissance" 8,
          "aura" 4,
          "religion" 17,
          "art" 19})

;
;
;;; normalizing the data
;(defn normalize [m]
;  (let [total (reduce + (vals m))]
;    (->> m
;      (map (fn [[k v]] {k (/ (double v) (double total))}))
;      (apply merge))))

;;; normalized data
(def gfmn {"architecture" 25/637,
           "watercraft" 1/49,
           "outdoors" 4/49,
           "illustration" 1/49,
           "group" 2/91,
           "adult" 16/637,
           "travel" 61/637,
           "water" 55/637,
           "painting" 24/637,
           "no person" 100/637,
           "people" 4/91,
           "nature" 23/637,
           "sea" 2/91,
           "landscape" 8/91,
           "tree" 44/637,
           "old" 20/637,
           "vehicle" 1/49,
           "building" 25/637,
           "art" 25/637,
           "sky" 16/637})
;
(def gsmn {"book" 2/67,
           "illustration" 7/201,
           "group" 8/201,
           "adult" 17/201,
           "painting" 19/201,
           "people" 6/67,
           "Mary" 7/201,
           "church" 5/201,
           "sculpture" 5/201,
           "god" 8/201,
           "man" 13/201,
           "veil" 5/201,
           "woman" 11/201,
           "one" 8/201,
           "saint" 4/67,
           "print" 4/201,
           "Renaissance" 8/201,
           "aura" 4/201,
           "religion" 17/201,
           "art" 19/201})
;
;
;;;
;

(defn normalize [m]
  (let [total (reduce + (vals m))]
    (->> m
         (map (fn [[k v]] {k (/ (double v) (double total))}))
         (apply merge))))

(def german {"red" 2 "blue" 3 "black" 1})
(def french {"red" 10 "blue" 30 "white" 50})
;
(normalize german) ;; matches jupyter
(normalize french) ;; matches jupyter

(def french-concepts (set (keys french)))
(def german-concepts (-> german keys set))



(defn adjustment
  "How much to adjust error rate, for given feature."
  [feature data-1 data-2]
  (let [diff (- (get data-1 feature 0)
                (get data-2 feature 0))]
    (* diff diff)))


(defn error-rate
  "Get the error-rate (measure of (dis)similarity between two datasets.

  Lower error means higher similarity.
  Higher error means less similarity.

  data-1, data-2: map"
  [data-1 data-2]
  {:pre [(map? data-1) (map? data-2)]
   :post}
  (let [features (into #{} (concat (keys data-1) (keys data-2)))
        normalized-data-1 (normalize data-1)
        normalized-data-2 (normalize data-2)
        adjustments (map
                      #(adjustment % normalized-data-1 normalized-data-2)
                      features)]
    (reduce + 0 adjustments)))

(def german {"red" 2 "blue" 3 "black" 1})
(def french {"red" 10 "blue" 30 "white" 50})
(error-rate french german)
0.4135802469135803


;; assuming error starts as 0
;(reduce + 0 adjustments)
;  0.4135802469135803 ... matches jupyter :-) 

