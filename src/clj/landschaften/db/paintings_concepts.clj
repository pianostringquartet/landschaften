(ns landschaften.db.paintings-concepts
  (:require [landschaften.db.core :refer [*db*]]
           [clojure.java.jdbc :as jdbc]
           [landschaften.clarifai :as clarifai]
           [clojure.data.json :as json]
           [clojure.test :refer [is]]
           [clojure.spec.alpha :as s]
           [clojure.spec.test.alpha :as st]
           [expound.alpha :as exp]
           [clojure.spec.gen.alpha :as gen]
           [landschaften.db.paintings :as p]
           [hugsql.core :as hugsql]))

;; denormalize a `paintings` row's concepts

(def PAINTINGS-CONCEPTS-TABLE "paintings_concepts")

(s/def ::painting_id int?)
(s/def ::name string?)
(s/def ::value double?)
(s/def ::painting_concept (s/keys :req-un [::painting_id ::name ::value]))

(defn p-row->pc-rows [{:keys [id concepts]}]
  (map #(merge {:painting_id id} %) concepts))

(defn insert-paintings-concepts-rows!
 "Example usage:
    (insert-paintings-concepts-rows! *db* (p/retrieve-paintings *db* #{}))"
 [db p-rows]
 (let [pc-rows (mapcat p-row->pc-rows p-rows)]
  (jdbc/insert-multi! db PAINTINGS-CONCEPTS-TABLE pc-rows)))
