(ns landschaften.api
  (:require [landschaften.db.core :refer [*db*]]
            [landschaften.db.paintings :as paintings]
            [proto-repl-charts.charts :as charts]
            [mount.core :as mount]
            [clojure.test :refer [is]]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [expound.alpha :as exp]
            [landschaften.db.query :as query]
            [landschaften.entity :as entity]
            [clojure.spec.gen.alpha :as gen]))


;; ----------------------------
;; MAIN API
;; ----------------------------

(s/fdef paintings-satisfying
 :args (s/cat :constraints (s/coll-of ::entity/constraint))
 :ret (s/* ::entity/painting))

; (defn paintings-satisfying [constraints]
;   (take 3 (paintings/retrieve-paintings *db* constraints)))

(defn paintings-satisfying [db constraints]
  (query/run-query db (query/build-query constraints)))


;; ----------------------------
;; TESTING
;; ----------------------------
; ;; turn on the spec for the fn in this namespace
; (st/instrument `paintings-satisfying)
;
; ;; should fail:
; (paintings-satisfying malformed-constraint)
; (paintings-satisfying [malformed-constraint])
;
; ; ;; should succeed:
; (paintings-satisfying #{type-constraint})
;
; (paintings-satisfying #{})
