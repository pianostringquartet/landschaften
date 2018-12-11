(ns landschaften.test.db.core
  (:require
   [landschaften.test.db.fixtures :as fixtures]
   [clojure.test :refer [is deftest]]
   [landschaften.db.wga-concepts :refer :all]
   [landschaften.api :refer :all]))


(def row fixtures/a-wga-concept-row-with-general-concepts)

(def rows fixtures/wga-concept-rows-with-general-concepts)

(deftest test-has-certainty-above
  (is (has-certainty-above {:name "cool" :value 0.8} 0.7)))

(deftest test-has-certainty-above-1
  (is (not (has-certainty-above {:name "cool" :value 0.8} 0.9))))

(deftest test-is-concept
  (is (is-concept {:name "cool" :value 0.8} "cool")))

(deftest test-is-concept-1
  (is (not (is-concept {:name "cool" :value 0.8} "hotdog"))))

(deftest test-has-concepts-satisfying
  (is (has-concept-satisfying
       row
       [#(is-concept % "no person")
        #(has-certainty-above % 0.95)])))

(deftest test-has-concepts-satisfying-1
  (is (not (has-concept-satisfying
            row
            [#(is-concept % "no person")
             #(has-certainty-above % 0.99)]))))

(deftest test-has-concepts-satisfying-2
  (is (not (has-concept-satisfying
            row
            [#(is-concept % "beaux-arts")
             #(has-certainty-above % 0.95)]))))




; (clojure.test/is)
; (deftest test-rows-with-concepts-satisfying
;  (let [preds [#(is-concept % "architecture") #(has-certainty-above % 0.80)]]
;   (is
;      (=
;       (some)
;       (rows-with-concepts-satisfying rows preds)))))







;
; (deftest test-users
;   (jdbc/with-db-transaction [t-conn *db*]
;     (jdbc/db-set-rollback-only! t-conn)
;     (is (= 1 (db/create-user!
;                t-conn
;                {:id         "1"
;                 :first_name "Sam"
;                 :last_name  "Smith"
;                 :email      "sam.smith@example.com"
;                 :pass       "pass"})))
;     (is (= {:id         "1"
;             :first_name "Sam"
;             :last_name  "Smith"
;             :email      "sam.smith@example.com"
;             :pass       "pass"
;             :admin      nil
;             :last_login nil
;             :is_active  nil}
;            (db/get-user t-conn {:id "1"})))))
