(ns landschaften.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures run-tests]]
            [pjstadig.humane-test-output]
            [landschaften.variance :refer [variance]]
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


;;; VARIANCE

;
;;; Sample data
;(def german {"red" 2 "blue" 3 "black" 1})
;(def french {"red" 10 "blue" 30 "white" 50})
;
;(= (variance/error-rate french german)
;   0.4135802469135803)
;
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

;(def german {"red" 2 "blue" 3 "black" 1})
;(def french {"red" 10 "blue" 30 "white" 50})
;;
;(= (normalize german) ;; matches jupyter
;   {"red" 0.3333333333333333, "blue" 0.5, "black" 0.16666666666666666})
;
;(= (normalize french) ;; matches jupyter
;   {"red" 0.1111111111111111, "blue" 0.3333333333333333, "white" 0.5555555555555556})