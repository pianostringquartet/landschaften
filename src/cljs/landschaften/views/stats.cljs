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
;(defn chart-data->map [chart-data]
;  (apply merge
;    (map (fn [[concept frequency]] {concept frequency})
;         chart-data)))

(defn normalize [m]
  {:pre [(map? m)]
   :post [(map? %)]}
  (let [total (reduce + (vals m))]
    (->> m
      (map (fn [[k v]] {k (/ (double v) (double total))}))
      (apply merge))))

;(def german {"red" 2 "blue" 3 "black" 1})
;(def french {"red" 10 "blue" 30 "white" 50})
;;
;(= (normalize german) ;; matches jupyter
;   {"red" 0.3333333333333333, "blue" 0.5, "black" 0.16666666666666666})
;
;(= (normalize french) ;; matches jupyter
;   {"red" 0.1111111111111111, "blue" 0.3333333333333333, "white" 0.5555555555555556})


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
  {:pre [(every? int? (vals data-1))
         (every? int? (vals data-2))]
   :post [(<= 0 % 1)]}
  (let [features (into #{} (concat (keys data-1) (keys data-2)))
        normalized-data-1 (normalize data-1)
        normalized-data-2 (normalize data-2)
        adjustments (map
                      #(adjustment % normalized-data-1 normalized-data-2)
                      features)]
    (reduce + 0 adjustments)))

;(def german {"red" 2 "blue" 3 "black" 1})
;(def french {"red" 10 "blue" 30 "white" 50})
;
;(= (error-rate french german)
;   0.4135802469135803)
;0.4135802469135803
;
;
;;; assuming error starts as 0
;;(reduce + 0 adjustments)
;;  0.4135802469135803 ... matches jupyter :-)


; 'max error rate' i.e. highest error rate
; = no German feature appears in French features; vice-versa
;

; 'min error rate' i.e. lowest error rate
; = German and French features totally overlap

;; min and max cannot be assumed to be 0 vs 1

(defn error-rate2
 "Get the error-rate (measure of (dis)similarity between two datasets.

  Lower error means higher similarity.
  Higher error means less similarity.

  data-1, data-2: map"
 [data-1 data-2 adjustment-fn]
 {:pre [(every? int? (vals data-1))
        (every? int? (vals data-2))]
  :post [(<= 0 % 1)]}
 (let [features (into #{} (concat (keys data-1) (keys data-2)))
       normalized-data-1 (normalize data-1)
       normalized-data-2 (normalize data-2)
       adjustments (map
                     #(adjustment-fn % normalized-data-1 normalized-data-2)
                     features)]
   (reduce + 0 adjustments)))


;; assumes
;; think carefully how to do this, given how you wrote
;; your original fn
;;
;; the adjustment fn approach might not work
;;
;(defn max-error-rate [data-1 data-2]
;  (let [assume-no-overlap ()]))