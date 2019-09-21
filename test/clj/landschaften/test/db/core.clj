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


