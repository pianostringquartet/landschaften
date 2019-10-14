(ns landschaften.db.query
  ;#:ghostwheel.core{:check     true
  ;                  :outstrument true
  ;                  :num-tests 10}
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.spec.alpha :as s]
            [landschaften.entity :as entity]
            [ghostwheel.core
             :as g
             :refer [>defn >defn- >fdef => | <- ?]]))



;; Problems with current approach:
;; - constructing sqlvec-style queries requires deeper and clearer abstractions
;;    e.g. placement of spaces is overgenerous and meaningless
;; - sacrified on scope: e.g. no "concept must have certainty above 0.96"
;; - not reusable in another context
;; - can't currently support "Painting has concepts X AND Y"
;; - not declarative (compared to a parameterized SQL file itself)


;; ----------------------------
;; BUILD QUERY
;; ----------------------------


(defn snippet:column-in [table-alias column n]
  (let [param-placeholders
        (if (= 1 n) "?" (clojure.string/join ", " (repeat n "?")))]
    (str " " table-alias "." column " in (" param-placeholders ") ")))


(defn ->painting-snippet [{:keys [column values]}]
  (when (entity/painting-column? column)
    {:snippet (snippet:column-in "t" column (count values))
     :params  values}))

;; concept snippets need to be MUTUALLY EXCLUSIVE
(defn ->concept-snippet [{:keys [column values]}]
  (when (entity/concept-column? column)
    {:snippet (snippet:column-in "t2" column (count values))
     :params  values}))


(defn ->sqlvec [snippets base]
  (let [query  (clojure.string/join " and " (map :snippet snippets))
        params (mapcat :params snippets)]
    (into [(str base query)] params)))


;; at a high level, just saying:
;; "case: concept-constraints vs. paintings-constraints only vs. no constraints at all"
;; then adding the snippets

(defn base [constraints]
  (let [column?               (fn [pred] (some #(pred (:column %)) constraints))
        painting-constraints? (column? entity/painting-column?)
        concept-constraints?  (column? entity/concept-column?)]
    (cond
      concept-constraints?                                  ; may or may not also have painting-constraints
      "select distinct t.* from paintings t, paintings_concepts t2 where t.id = t2.painting_id and "

      (and painting-constraints?
           (not concept-constraints?))                      ; i.e. painting-constraints only
      "select distinct t.* from paintings t where "

      :else "select distinct * from paintings ")))


(s/fdef build-query
        :args (s/cat :constraints (s/coll-of ::entity/constraint))
        :ret ::entity/sqlvec)


(defn build-query [constraints]
  (let [->snippets        #(not-empty (remove nil? (map % constraints)))
        painting-snippets (->snippets ->painting-snippet)
        concept-snippets  (->snippets ->concept-snippet)]
    (->sqlvec
      (into painting-snippets concept-snippets)
      (base constraints))))


(>defn build-query-2 [constraints]
  [(s/coll-of ::entity/constraint) => ::entity/sqlvec]
  (let [->snippets        #(not-empty (remove nil? (map % constraints)))
        painting-snippets (->snippets ->painting-snippet)
        concept-snippets  (->snippets ->concept-snippet)]
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
