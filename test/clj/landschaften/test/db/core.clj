 (ns landschaften.test.db.core
   (:require [landschaften.db.query :refer [build-query]]
             [clojure.test :refer [is deftest testing]]))


 ;; ------------------------------------------------
 ;; Constraints -> SQL statements
 ;; ------------------------------------------------

(deftest test-build-query
 (testing "building escaped SQL query with one `school` constraint"
   (is
    (= (build-query [{:column "school" :values ["Italian"]}])
       ["select distinct t.* from paintings t where  t.school in (?) " "Italian"])))

 (testing "building escaped SQL query with multiple `school` constraints"
  (is
   (= (build-query [{:column "school" :values ["Italian", "German"]}])
      ["select distinct t.* from paintings t where  t.school in (?, ?) " "Italian" "German"])))

 (testing "building escaped SQL query with multiple `school` and `timeframe` constraints"
  (is
   (= (build-query [{:column "school" :values ["Italian", "German"]}
                    {:column "timeframe" :values ["1501-1550"]}])
      ["select distinct t.* from paintings t where  t.school in (?, ?)  and  t.timeframe in (?) "
       "Italian"
       "German"
       "1501-1550"])))

 (testing "building escaped SQL query with `name` (concept) constraint"
   (is
    (= (build-query [{:column "name" :values ["people"]}])
     ["select distinct t.* from paintings t, paintings_concepts t2 where t.id = t2.painting_id and  t2.name in (?) "
      "people"])))

 (testing "building escaped SQL query with `name` (concept) constraints"
  (is
   (= (build-query [{:column "name" :values ["wild" "no person"]}])
      ["select distinct t.* from paintings t, paintings_concepts t2 where t.id = t2.painting_id and  t2.name in (?, ?) "
         "wild"
         "no person"])))

 (testing "building escaped SQL query with `author` and `name` (concept) constraints"
   (is
    (= (build-query [{:column "author" :values ["MANET, Edouard"]}
                     {:column "name" :values ["people"]}])
       ["select distinct t.* from paintings t, paintings_concepts t2 where t.id = t2.painting_id and  t2.name in (?)  and  t.author in (?) "
        "people"
        "MANET, Edouard"]))))






;
;
; (def row fixtures/a-wga-concept-row-with-general-concepts)
;
; (def rows fixtures/wga-concept-rows-with-general-concepts)
; 
; (deftest test-has-certainty-above
;   (is (has-certainty-above {:name "cool" :value 0.8} 0.7)))
;
; (deftest test-has-certainty-above-1
;   (is (not (has-certainty-above {:name "cool" :value 0.8} 0.9))))
;
; (deftest test-is-concept
;   (is (is-concept {:name "cool" :value 0.8} "cool")))
;
; (deftest test-is-concept-1
;   (is (not (is-concept {:name "cool" :value 0.8} "hotdog"))))
;
; (deftest test-has-concepts-satisfying
;   (is (has-concept-satisfying
;        row
;        [#(is-concept % "no person")
;         #(has-certainty-above % 0.95)])))
;
; (deftest test-has-concepts-satisfying-1
;   (is (not (has-concept-satisfying
;             row
;             [#(is-concept % "no person")
;              #(has-certainty-above % 0.99)]))))
;
; (deftest test-has-concepts-satisfying-2
;   (is (not (has-concept-satisfying
;             row
;             [#(is-concept % "beaux-arts")
;              #(has-certainty-above % 0.95)]))))
;
;
;
;
; ; (clojure.test/is)
; ; (deftest test-rows-with-concepts-satisfying
; ;  (let [preds [#(is-concept % "architecture") #(has-certainty-above % 0.80)]]
; ;   (is
; ;      (=
; ;       (some)
; ;       (rows-with-concepts-satisfying rows preds)))))
;
;
;
;
; ;
; ; (deftest test-users
; ;   (jdbc/with-db-transaction [t-conn *db*]
; ;     (jdbc/db-set-rollback-only! t-conn)
; ;     (is (= 1 (db/create-user!
; ;                t-conn
; ;                {:id         "1"
; ;                 :first_name "Sam"
; ;                 :last_name  "Smith"
; ;                 :email      "sam.smith@example.com"
; ;                 :pass       "pass"})))
; ;     (is (= {:id         "1"
; ;             :first_name "Sam"
; ;             :last_name  "Smith"
; ;             :email      "sam.smith@example.com"
; ;             :pass       "pass"
; ;             :admin      nil
; ;             :last_login nil
; ;             :is_active  nil}
; ;            (db/get-user t-conn {:id "1"})))))
