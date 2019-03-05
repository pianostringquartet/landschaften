(ns landschaften.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            [landschaften.core :as rc]))

(deftest test-home
  (is (= true true)))


;; should GENERATE THESE via spec
;; otherwise have to maintain these instances...
;(def group-without-constraints)
;(def group-with-constraints)
;(deftest test->query-constraints)


