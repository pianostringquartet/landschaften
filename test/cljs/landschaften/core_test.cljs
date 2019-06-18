(ns landschaften.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures run-tests]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            [landschaften.core :as rc]
            [day8.re-frame.test :as rf-test]))


;(enable-console-print!)

(deftest test-home
  (is (= true true)))


;; should GENERATE THESE via spec
;; otherwise have to maintain these instances...
;(def group-without-constraints)
;(def group-with-constraints)
;(deftest test->query-constraints)

(deftest test-addition
  (is (= 2 (+ 1 1))))


(deftest test-love
  (is (= 2 9)))

;(run-tests)
