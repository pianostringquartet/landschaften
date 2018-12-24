(ns landschaften.db.query
  (:require
    [clojure.java.jdbc :as jdbc]
    [landschaften.db.core :refer [*db*]]
    [clojure.test :refer [is]]
    [clojure.spec.alpha :as s]
    [clojure.spec.test.alpha :as st]
    [landschaften.entity :as entity]
    [clojure.string :refer [join]]))


;; Overall a bad approach:
;; - constructing sqlvec-style queries requires deeper and clearer abstractions
;;    e.g. placement of spaces is overgenerous and meaningless
;; - sacrified on scope: e.g. no "concept must have certainty above 0.96"
;; - not reusable in any other context


;; ----------------------------
;; SAMPLE DATA
;; ----------------------------

;; painting constraints
(def type-constraint {:column "type" :values ["landschaften" "study"]})
(def timeframe-constraint {:column "timeframe" :values ["1501-1550"]})
;; concept constraint
(def concept-name-constraint {:column "name" :values ["no person"]})
(def malformed-constraint {:column "malformed column" :values ["no person"]})

(def no-constraints #{})
(def painting-constraints #{type-constraint timeframe-constraint})
(def concept-constraints #{concept-name-constraint})
(def painting-and-concept-constraints
  #{type-constraint timeframe-constraint concept-name-constraint})
(def malformed-constraints #{malformed-constraint})

;; ----------------------------
;; BUILD QUERY
;; ----------------------------

(defn snippet:column-in [table-alias column n]
  (let [param-placeholders (if (= 1 n) "?" (join ", " (repeat n "?")))]
    (str " " table-alias "." column " in (" param-placeholders ") ")))

(defn ->painting-snippet [{:keys [column values]}]
  (when (entity/painting-column? column)
    {:snippet (snippet:column-in "t" column (count values))
     :params values}))

(defn ->concept-snippet [{:keys [column values]}]
  (when (entity/concept-column? column)
    {:snippet (snippet:column-in "t2" column (count values))
     :params values}))

(defn ->sqlvec [snippets base]
  (let [query (clojure.string/join " and " (map :snippet snippets))
        params (mapcat :params snippets)]
    (into [(str base query)] params)))

(defn base [constraints]
  (let [column? (fn [pred] (some #(pred (:column %)) constraints))
        painting-constraints? (column? entity/painting-column?)
        concept-constraints? (column? entity/concept-column?)]
    (cond
     concept-constraints? ; may or may not also have painting-constraints
     "select t.* from paintings t, paintings_concepts t2 where t.id = t2.painting_id and "

     (and painting-constraints?
          (not concept-constraints?)) ; i.e. painting-constraints only
     "select t.* from paintings t where "

     :else "select * from paintings ")))

(s/fdef build-query
 :args (s/cat :constraints (s/coll-of ::entity/constraint))
 :ret ::entity/sqlvec)

(defn build-query [constraints]
  (let [->snippets #(not-empty (remove nil? (map % constraints)))
        painting-snippets (->snippets ->painting-snippet)
        concept-snippets (->snippets ->concept-snippet)]
    (->sqlvec
      (into painting-snippets concept-snippets)
      (base constraints))))

; ; (st/instrument `build-query)
; ;
; ;; all as expected :)
; (= (build-query #{}) ["select * from paintings "]) ;; true
; (= (build-query 5) ["select * from paintings "]) ;; fails spec, good!
; (= (build-query painting-constraints) ;; true
;   ["select t.* from paintings t where  t.timeframe in (?)  and  t.type in (?, ?) "
;    "1501-1550"
;    "landschaften"
;    "study"])
; (= (build-query painting-and-concept-constraints) ;; true
;    ["select t.* from paintings t, paintings_concepts t2 where t.id = t2.painting_id and  t2.name in (?)  and  t.timeframe in (?)  and  t.type in (?, ?) "
;     "no person"
;     "1501-1550"
;     "landschaften"
;     "study"])


;; ----------------------------
;; RUN QUERY
;; ----------------------------

(s/fdef run-query
 :args (s/cat :db some? :query ::entity/sqlvec)
 :ret (s/coll-of ::entity/painting))

(defn run-query [db query]
  (jdbc/query db query))
