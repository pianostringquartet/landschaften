(ns landschaften.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures run-tests]]
            [pjstadig.humane-test-output]
            [landschaften.variance :refer [variance]]
            [re-frame.core :as rf]
            [landschaften.events :as events]
            [landschaften.subs :as subs]
            [day8.re-frame.test :as rf-test]))


(deftest test-love
  (is (= 2 2)))


;; VARIANCE

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


;; test that:
;; - when group removed, it's removed from compared-groups and saved-groups
;; ignore


;; karma doesn't refresh the state each time it reloads the tests

;;
(deftest test-update-group
  (rf-test/run-test-sync
    (let [compared-group-names (rf/subscribe [::subs/compared-group-names])
          impressionism        "Impressionism"
          mannerism            "Mannerism"]

      (do
        (rf/dispatch [::events/initialize-app]))

      ;; Start without any compared groups
      (is (empty? @compared-group-names))

      ;; Add compare-group-names
      (doseq [name-to-add [impressionism mannerism]]
        (rf/dispatch [::events/add-compare-group-name name-to-add]))

      ;; Compare-group-names are concat'd to front of list
      (is (= impressionism (second @compared-group-names)))
      (is (= mannerism (first @compared-group-names)))

      ;; Remove a group name
      ;(doseq [name-to-remove [impressionism]]
      ;  (rf/dispatch [::events/remove-group name-to-remove]))
      (do
        (rf/dispatch [::events/remove-group impressionism]))

      ;; Confirm that group name was removed:
      (is (empty? (filter #(= impressionism %) @compared-group-names)))
      ;; ... and that other group name remains:
      (is (not-empty (filter #(= % mannerism) @compared-group-names)))

      ;; Remove last group name
      ;(doseq [name-to-remove [mannerism]]
      (do (rf/dispatch [::events/remove-group mannerism]))

      ;; Confirm last name is removed
      (is (empty? (filter #(= mannerism %) @compared-group-names))))))
