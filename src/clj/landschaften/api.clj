(ns landschaften.api
  (:require [landschaften.db.core :refer [*db*]]
            [landschaften.db.paintings :as paintings]
            [mount.core :as mount]
            [clojure.test :refer [is]]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [expound.alpha :as exp]
            [clojure.data.json :as json]
            [landschaften.db.query :as query]
            [landschaften.entity :as entity]
            [clojure.spec.gen.alpha :as gen]
            [clojure.java.jdbc :as jdbc]))

(defn is? [some-spec some-value]
  (or (s/valid? some-spec some-value)
      (s/explain some-spec some-value)))

;; ----------------------------
;; API
;; ----------------------------

;; get all artist's names
(defn artists-names [db]
  (jdbc/query db ["select distinct `author` from paintings"]))

;; get all concepts (names only)
(defn concepts [db]
  (jdbc/query db ["select distinct `name` from paintings_concepts"]))

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
  {:pre [(is? (s/coll-of ::entity/constraint) constraints)]}
  (map general-model-concepts
   (query/run-query db (query/build-query constraints))))




;; fails :)
; (def botticelli #{{:column "author" :values "BOTTICELLI, Sandro"}})

;; succeeds :)
; (def botticelli #{{:column "author" :values ["BOTTICELLI, Sandro"]}
;                   {:column "type" :values ["portrait"]}
;                   {:column "title" :values ["Portrait of a Young Man"]}})

; (take 5 (paintings-satisfying *db* botticelli))

;; TESTS
; (take 3 (paintings-satisfying *db* no-constraints))
; (take 3 (paintings-satisfying *db* concept-constraints))
; (take 3 (paintings-satisfying *db* painting-and-concept-constraints))
; (take 3 (paintings-satisfying *db* painting-constraints))
