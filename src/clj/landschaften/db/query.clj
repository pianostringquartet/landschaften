(ns landschaften.db.query
  (:require
    [clojure.java.jdbc :as jdbc]
    [landschaften.db.core :refer [*db*]]
    [clojure.test :refer [is]]
    [clojure.spec.alpha :as s]
    [clojure.spec.test.alpha :as st]
    [landschaften.entity :as entity]
    [clojure.string :refer [join]]))

;; ----------------------------
;; SAMPLE DATA
;; ----------------------------

;; painting constraints
(def type-constraint {:column "type" :values ["landschaften" "study"]})
(def timeframe-constraint {:column "timeframe" :values ["1501-1500"]})
;; concept constraint
(def concept-name-constraint {:column "name" :values ["no person"]})
(def malformed-constraint {:column "malformed column" :values ["no person"]})

(def no-constraints #{})
(def painting-constraints #{type-constraint timeframe-constraint})
(def concept-constraints #{concept-name-constraint})
(def painting-and-concept-constraints
  #{type-constraint timeframe-constraint concept-name-constraint})
(def malformed-constraints #{malformed-constraint})


(def QUERY-BASE
  {:no-constraints
    (str "select * from paintings")
   :painting-contraints
    (str "select t.* from paintings t where ")
   :concept-constraints
    (str "select t.* from paintings t, paintings_concepts t2 where t.id = t2.painting_id ")
   :painting-and-concept-constraints
    (str "select t.* from paintings t, paintings_concepts t2 where t.id = t2.painting_id ")})


(s/fdef build-query
 :args (s/cat :constraints (s/coll-of ::entity/constraint))
 :ret ::entity/sqlvec)

;; dispatches based # of constraints and :column value:
;;  empty constraints -> no constraints query
;;  one or more maps have :column in (name, value), is concept-query
;;  one or more maps have :column in (ptable column list), is painting-query
(defn build-query [constraints])


(st/instrument `build-query)



(s/fdef run-query
 :args (s/cat :db some? :query ::entity/sqlvec)
 :ret (s/coll-of ::entity/painting))

(defn run-query [db query]
  (jdbc/query db query))

; (st/instrument `run-query)

(defn snippet:column-in [table-alias column n]
  (let [param-placeholders (if (= 1 n) "?" (join ", " (repeat n "?")))]
    (str table-alias "." column " in (" param-placeholders ") ")))

;; this is returning a map that will later be used as a sqlvec
(defn column-in-values [table-alias column values]
  {:snippet (snippet:column-in table-alias column (count values))
   :params values})

(defn combine-snippets [snippets]
  (reduce str (interpose " and " snippets)))

;; returns sql-vec
(defn painting-constraints [constraint-maps]
  (let [base (:painting-contraints QUERY-BASE)
        constraints (combine-snippets (map :snippet constraint-maps))
        params (mapcat :params constraint-maps)]
    (into
     [(str base " " constraints)]
     params)))

(defn concept-constraints-query [db constraint-maps]
  (let [base (:concept-constraints QUERY-BASE)
        constraints (combine-snippets (map :snippet))]))

;; constraint-maps: set of {:column }
; (defn painting-constraints-query [db constraints]
;   (map column-in-values constraints
;     (jdbc/query db (painting-constraints constraint-maps))))

(defn no-constraints-query [db]
  (jdbc/query db (:no-constraints QUERY-BASE)))

; (defn painting-and-concept-constraints-query [db constraints])

; (def c1 (column-in-values p-alias "type" ["landscape" "study"]))
; (def c2 (column-in-values p-alias "timeframe" ["1501-1550"]))
; (def cs [c1 c2])
;
; (painting-constraints cs)
;
; ;; works
; (take 3 (jdbc/query *db* (painting-constraints cs)))
; ;; works:
; (take 3 (painting-constraints-query *db* cs))
;
; (take 2 (no-constraints-query *db*))
; (take 2 (no-constraints-query *db*))


;; working in the repl can be really bad:
;; state overhead; what's defined in repl vs. not
;; let's you write incoherent relationships based on an ephemeral part (hosted in repl)
;; gives the false sense of productivity just bc evaluating candy in the repl

;; compiler helps:
;; write good code even when tired or lazy,
;; offloads some mental space to the computer,
;; forces you to think through important relationships,
;; shows you logic errors in rest of codebase when refactoring

;; why on earth would someone NOT use static typing?
;; is it just that it's the pain of learning it?
;; even if static typing makes the mistake RH discusses in 'Maybe Not',
;; that's a small price to pay for the benefits.


; (=
;  (reduce str (interpose " and "(map :snippet cs)))
;  "t.type in (?, ?)  and t.timeframe in (?) ")

(s/def ::snippet string?)
(s/def ::params seq?)
(s/def ::constraint (s/keys :req-un [::snippet ::params]))

; (s/def ::sqlvec (s/))




; (= "t.type in (?, ?) "
;    (snippet p-alias "type" 2))
;
; (=
;   (column-in-values p-alias "type" ["landscape" "study"])
;   {:snippet "t.type in (?, ?) " :params ["landscape" "study"]})



  ;;; TESTS:
  ; (= ""
  ;  (query-constraints [] [] []))
  ;
  ; (= ""
  ;  (query-constraints {}))
  ;
  ; (= ""
  ;  (query-constraints #{}))
  ;
  ; ; true
  ; (= " where (`timeframe` = '1501-1550' or `timeframe` = '1551-1600')"
  ;  (query-constraints {} {:column "timeframe" :values ["1501-1550", "1551-1600"]}))
  ;
  ; ;; true :-)
  ; (=
  ;   " where (`type` = 'landscape' or `type` = 'mythological') and (`timeframe` = '1501-1550' or `timeframe` = '1551-1600') and (`school` = 'Italian' or `school` = 'Flemish')"
  ;   (query-constraints ["landscape", "mythological"]["1501-1550", "1551-1600"] ["Italian" "Flemish"])))

  ; (=
  ;   " where (`type` = 'landscape' or `type` = 'mythological') and (`timeframe` = '1501-1550' or `timeframe` = '1551-1600') and (`school` = 'Italian' or `school` = 'Flemish')"
  ;   (query-constraints {:column "type" :values ["landscape", "mythological"]} {:column "timeframe" :values ["1501-1550", "1551-1600"]} {:column "school" :values ["Italian" "Flemish"]}))
