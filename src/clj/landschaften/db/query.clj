(ns landschaften.db.query
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.spec.alpha :as s]
            [landschaften.entity :as entity]))


;; Overall a bad approach:
;; - constructing sqlvec-style queries requires deeper and clearer abstractions
;;    e.g. placement of spaces is overgenerous and meaningless
;; - sacrified on scope: e.g. no "concept must have certainty above 0.96"
;; - not reusable in another context


;; ----------------------------
;; BUILD QUERY
;; ----------------------------

;; instead of an 'if' branch,
;; can you do a rule?
;; e.g. add as many ? as there are elems,
;; then interpose "," as needed
(defn snippet:column-in [table-alias column n]
  (let [param-placeholders
         (if (= 1 n) "?" (clojure.string/join ", " (repeat n "?")))]
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


;; ----------------------------
;; RUN QUERY
;; ----------------------------

(s/fdef run-query
 :args (s/cat :db some? :query ::entity/sqlvec)
 :ret (s/coll-of ::entity/painting))

(defn run-query [db query]
  (jdbc/query db query))
