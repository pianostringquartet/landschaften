(ns landschaften.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures run-tests]]
            [pjstadig.humane-test-output]
            [landschaften.compare.variance :refer [variance]]
            [re-frame.core :as rf]
            [landschaften.test-utils :as test-utils]
            [landschaften.explore.explore-events :as explore-events]
            [landschaften.compare.compare-events :as compare-events]
            [landschaften.compare.compare-subs :as compare-subs]
            [landschaften.explore.explore-subs :as explore-subs]
            [day8.re-frame.test :as rf-test]
            [landschaften.db :as db]
            [landschaften.sample.cezanne :as cezanne]))


;; ------------------------------------------------------
;; Variance
;; ------------------------------------------------------

(def colors-dataset-1 {"red" 2 "blue" 3 "black" 1})
(def colors-dataset-2 {"red" 10 "blue" 30 "white" 50})
(def colors-dataset-3 {"k" 10 "b" 30 "w" 50})

(deftest test-variance
  (is (= (variance colors-dataset-2 colors-dataset-1)
         0.4135802469135803)))

(deftest test-variance-complete-overlap
  (is (= (variance colors-dataset-1 colors-dataset-1)
         0.0)))

(deftest test-variance-no-overlap
  (is (= (variance colors-dataset-1 colors-dataset-3)
         0.8209876543209877)))

(deftest test-addition
  (is (= 0 (+ 0 0))))


;; ------------------------------------------------------
;; Re-frame logic: event handlers, subscriptions
;; ------------------------------------------------------

;; TODO: include saved-groups,
;; to confirm that removed-group also removes relevant saved-group
(deftest test-add-and-remove-compare-groups
  (rf-test/run-test-sync
    (let [compared-group-names (rf/subscribe [::compare-subs/compared-group-names])
          impressionism        "Impressionism"
          mannerism            "Mannerism"]

      ;; Initialize database
      (do (rf/dispatch [::test-utils/setup-db db/fresh-db]))

      (is (empty? @compared-group-names)) ; Start without any compared groups

      ;; Add compare-group-names
      (doseq [name-to-add [impressionism mannerism]]
        (rf/dispatch [::compare-events/add-compare-group-name name-to-add]))

      ;; Compare-group-names are concat'd to front of list
      (is (= impressionism (second @compared-group-names)))
      (is (= mannerism (first @compared-group-names)))

      ;; Remove a group name
      (do (rf/dispatch [::explore-events/remove-group impressionism]))

      ;; Confirm that group name was removed:
      (is (empty? (filter #(= impressionism %) @compared-group-names)))
      ;; ... and that other group name remains:
      (is (not-empty (filter #(= % mannerism) @compared-group-names)))

      ;; Remove last group name
      (do (rf/dispatch [::explore-events/remove-group mannerism]))

      ;; Confirm last name is removed
      (is (empty? (filter #(= mannerism %) @compared-group-names))))))


(defn cezanne? [paintings types schools timeframes concepts artists]
  (and
    (= (into #{} paintings) cezanne/cezanne-sample-paintings)
    (= types cezanne/cezanne-type-constraints)
    (= schools cezanne/cezanne-school-constraints)
    (= timeframes cezanne/cezanne-timeframe-constraints)
    (= concepts cezanne/cezanne-concept-constraints)
    (= artists cezanne/cezanne-artist-constraints)))


(deftest test-switch-group
  (rf-test/run-test-sync
    (let [paintings           (rf/subscribe [::explore-subs/paintings])
          selected-types      (rf/subscribe [::explore-subs/types])
          selected-schools    (rf/subscribe [::explore-subs/school-constraints])
          selected-timeframes (rf/subscribe [::explore-subs/timeframe-constraints])
          selected-concepts   (rf/subscribe [::explore-subs/concept-constraints])
          selected-artists    (rf/subscribe [::explore-subs/artist-constraints])]

      ;; demo db contains Manet and Cezanne sample groups; starts with Cezanne
      (do (rf/dispatch [::test-utils/setup-db db/demo-db]))

      (is (= true (not (cezanne? @paintings
                                 @selected-types
                                 @selected-schools
                                 @selected-timeframes
                                 @selected-concepts
                                 @selected-artists))))

      ;; Switch to Cezanne group
      (do (rf/dispatch [::explore-events/switch-current-group cezanne/cezanne-people-group-name]))

      (is (= true (cezanne? @paintings
                            @selected-types
                            @selected-schools
                            @selected-timeframes
                            @selected-concepts
                            @selected-artists))))))


(deftest test-save-group)

(deftest test-removing-saved-group)




