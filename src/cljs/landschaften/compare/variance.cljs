(ns landschaften.compare.variance
  (:require [clojure.spec.alpha :as s]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]))

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
  "Get the variance (measure of (dis)similarity) between two datasets.

  Lower variance means higher similarity.
  Higher variance means less similarity.

  data-1, data-2: map"
  [data-1 data-2]
  [::dataset ::dataset => ::variance]
  (let [features (into #{} (concat (keys data-1) (keys data-2)))
        normalized-data-1 (normalize data-1)
        normalized-data-2 (normalize data-2)
        adjustments (map
                      #(adjustment % normalized-data-1 normalized-data-2)
                      features)]
    (reduce + 0 adjustments)))

;(check)