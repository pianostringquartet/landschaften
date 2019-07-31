 (ns landschaften.test.db.core
   (:require
    ;[landschaften.test.db.fixtures :as fixtures]
    [landschaften.db.query :refer [build-query]]
    [clojure.test :refer [is deftest]]))
    ;[landschaften.db.wga-concepts :refer :all]
    ;[landschaften.api :refer :all]))



 ;; can you use spec?
 ;; i.e. spec the backend fn?
 ;; ... but you don't really have a strong definition of what's valid SQL or not...

 ;; ------------------------------------------------
 ;; Constraints -> SQL statements
 ;; ------------------------------------------------

 ;; want to test landsch.api/paintings-satisfying

 ;; separate out the db dep,
 ;; and want to examine the query that's returned,
 ;; i.e don't want to run the query right away

 ;; will also want to just test against the existing


 ;; TO ALSO LEARN REPL STUFF,
 ;; start prototyping etc. from the console REPl
 ;; then when actually writing test, use the intellij repl

 ;; hmm tehnically the front end shouldn't know about :column, :values...
 ;; should just send the data format it knows/comprehends...


 ;; just want to test "build-query"

 ;; REGULAR CONSTRAINTS




(def test-constraints [{:column "school" :values ["Italian"]}])
;; select * from wga_concepts where school = "Italian"

(deftest test-build-query
 (is
  (build-query test-constraints)
  ["select distinct t.* from paintings t where  t.school in (?) " "Italian"]))


(def test-constraints-2 [{:column "school" :values ["Italian", "German"]}])


(def test-constraints-3 [{:column "school" :values ["Italian", "German"]}
                         {:column "timeframe" :values ["1501-1550"]}])

 ;; ALSO WITH SPECIFIC CONCEPTS and AUTHORS

 ;; concepts alone
(def test-constraints-4 [{:column "concept" :values []}])

 ;; authors alone
(def test-constraints-5 [
                         {:column "author" :values ["MANET, Edouard"]}
                         {:column "concept" :values ["people"]}])

;; concepts and authors
(def test-constraints-6)






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
