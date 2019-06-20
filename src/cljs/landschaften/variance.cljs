(ns landschaften.variance
  (:require [clojure.spec.alpha :as s]
            [ghostwheel.core :as g :refer [check >defn >defn- >fdef => | <- ?]]))

; https://www.wikihow.com/Calculate-Variance

(s/def ::dataset
  (s/map-of string? number?))

(s/def ::variance
  #(<= 0 % 1))

(>defn normalize [m]
  [::dataset => ::dataset]
  (let [total (reduce + (vals m))]
    (->> m
      (map (fn [[k v]] {k (/ (double v) (double total))}))
      (apply merge))))

(>defn adjustment
  "How much to adjust error rate, for given feature."
  [feature data-1 data-2]
  [string? ::dataset ::dataset => number?]
  (let [diff (- (get data-1 feature 0)
                (get data-2 feature 0))]
    (* diff diff)))

(>defn variance
  "Get the variance (measure of (dis)similarity between two datasets.

  Lower variance means higher similarity.
  Higher variance means less similarity.

  data-1, data-2: map"
  [data-1 data-2]
  [::dataset ::dataset => ::variance]
  ;{:pre [(every? int? (vals data-1))
  ;       (every? int? (vals data-2))]
  ; :post [(<= 0 % 1)]} ; variance falls within 0, 1
  (let [features (into #{} (concat (keys data-1) (keys data-2)))
        normalized-data-1 (normalize data-1)
        normalized-data-2 (normalize data-2)
        adjustments (map
                      #(adjustment % normalized-data-1 normalized-data-2)
                      features)]
    (reduce + 0 adjustments)))


;;; TESTS

(def german {"red" 2 "blue" 3 "black" 1})
(def french {"red" 10 "blue" 30 "white" 50})
;; this is exactly what Thiago had :)
(= (variance french german)
   0.4135802469135803)

;;; SOME OVERLAP: all features same, but values differ
;(def some-overlap-german {"red" 2 "blue" 3 "black" 1})
;(def some-overlap-french {"red" 10 "blue" 30 "black" 50})
;(= (error-rate some-overlap-french some-overlap-german)
;   0.22839506172839513)
;
;
;;; PERFECT OVERLAP: even the values are the same
;(def all-overlap-german {"red" 2 "blue" 3 "black" 1})
;(def all-overlap-french {"red" 2 "blue" 3 "black" 1})
;(= (error-rate all-overlap-french all-overlap-german)
;   0.0)
;;; ^^ as expected when datasets are IDENTICAL
;
;(def no-overlap-german  {"red" 2 "blue" 3 "black" 1})
;(def no-overlap-french {"k" 10 "b" 30 "w" 50})
;(= (error-rate no-overlap-french no-overlap-german)
;   0.8209876543209877)
;;; ^^ same as what Thiago get
;
;
;;(def german {"red" 2 "blue" 3 "black" 1})
;;(def french {"red" 10 "blue" 30 "white" 50})
;;;
;;(= (normalize german) ;; matches jupyter
;;   {"red" 0.3333333333333333, "blue" 0.5, "black" 0.16666666666666666})
;;
;;(= (normalize french) ;; matches jupyter
;;   {"red" 0.1111111111111111, "blue" 0.3333333333333333, "white" 0.5555555555555556})

(check)