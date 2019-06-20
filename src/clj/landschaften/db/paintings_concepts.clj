(ns landschaften.db.paintings-concepts
  (:require [landschaften.db.core :refer [*db*]]
            [clojure.spec.alpha :as s]
            [clojure.java.jdbc :as jdbc]))


;; TODO: Make a separate script

(def PAINTINGS-CONCEPTS-TABLE "paintings_concepts")

(s/def ::painting_id int?)
(s/def ::name string?)
(s/def ::value double?)
(s/def ::painting_concept (s/keys :req-un [::painting_id ::name ::value]))

(defn painting-row->painting-concept-rows [{:keys [id concepts]}]
  (map #(merge {:painting_id id} %) concepts))

(defn insert-paintings-concepts-rows!
 "Example usage:
    (insert-paintings-concepts-rows! *db* (retrieve-paintings *db* #{}))"
 [db p-rows]
 (let [pc-rows (mapcat painting-row->painting-concept-rows p-rows)]
  (jdbc/insert-multi! db PAINTINGS-CONCEPTS-TABLE pc-rows)))
