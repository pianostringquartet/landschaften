(ns landschaften.api
  (:require [landschaften.db.core :refer [*db*]]
            [landschaften.db.paintings :as paintings]
            [proto-repl-charts.charts :as charts]
            [mount.core :as mount]
            [clojure.test :refer [is]]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [expound.alpha :as exp]
            [clojure.data.json :as json]
            [landschaften.db.query :as query]
            [landschaften.entity :as entity]
            [clojure.spec.gen.alpha :as gen]))

;; ----------------------------
;; API
;; ----------------------------

(defn general-model-concepts
  "Concepts from Clarifai general model only."
 [{json :concepts :as painting}]
 (assoc painting :concepts (-> (json/read-str json :key-fn keyword)
                               (get-in [:general :concepts])
                               (set))))

(s/fdef paintings-satisfying
 :args (s/cat :constraints (s/coll-of ::entity/constraint))
 :ret (s/* ::entity/painting))

(defn paintings-satisfying [db constraints]
  (map general-model-concepts
   (query/run-query db (query/build-query constraints))))


;; TESTS
; (take 3 (paintings-satisfying *db* no-constraints))
; (take 3 (paintings-satisfying *db* concept-constraints))
; (take 3 (paintings-satisfying *db* painting-and-concept-constraints))
; (take 3 (paintings-satisfying *db* painting-constraints))
