(ns landschaften.db.wga-concepts
  (:require [landschaften.db.core :refer [*db*]]
           [clojure.java.jdbc :as jdbc]
           [landschaften.clarifai :as clarifai]
           [clj-http.client :as client]
           [clojure.data.json :as json]
           [proto-repl-charts.charts :as charts]
           [mount.core :as mount]
           [clojure.test :refer [is]]
           [clojure.spec.alpha :as s]
           [clojure.spec.test.alpha :as st]
           [expound.alpha :as exp]
           [clojure.spec.gen.alpha :as gen]))

;; TODO:
;; Rename 'wga-concepts' table and code to e.g. "paintings"
;; Have spec-check somewhere (eg in a 'test' file) that you can run, to guarantee preserved behavior
;; When refactoring: how general should the functions be? should they be able to stand separately, as e.g. 1/3rd of an implementation of a library?


;; keep the :id and :jpg
(def wga-concepts-row-columns
  #{:id :author :title :date :form :type :school :timeframe :jpg :concepts})

(defn add-concepts-to-row [row concepts model]
  (assoc row :concepts (json/write-str {model concepts})))


(defn as-wga-concepts-row [almost-row]
  (let [wga-concepts-row-columns
          #{:id :author :title :date :form :type :school :timeframe :jpg :concepts}
        row-keyset (into #{} (keys almost-row))]
    (apply dissoc
     almost-row
     (clojure.set/difference row-keyset wga-concepts-row-columns))))


(defn insert-wga-concept-rows! [row-maps]
  (jdbc/insert-multi! *db* :wga_concepts row-maps))


(defn retrieve-n-random-painting-rows [n]
  (jdbc/query *db*
    ["select * from wga_csv_rows where form = \"painting\" order by rand() limit ?" n]))

(defn retrieve-n-random-painting-rows-of-type [n painting-type]
  (jdbc/query *db*
    ["select * from wga_csv_rows where form = \"painting\" and `type` = ? order by rand() limit ?" painting-type n]))

;  works:
; (retrieve-n-random-painting-rows-of-type 10 "genre")

(defn insert-concepts!
 "rows: wga-csv-rows
  model: keyword, e.g. :general"
 [rows model]
 (let [concepts
         (clarifai/get-concepts-for-images model (map #(:jpg %) rows))
       concepts-as-rows
         (map as-wga-concepts-row
          (map #(add-concepts-to-row %1 %2 model) rows concepts))]
   (insert-wga-concept-rows! concepts-as-rows)))


(defn insert-n-concepts! [n model]
  (map
    #(insert-concepts! % model)
    (partition-all 128 (retrieve-n-random-painting-rows n))))


;; 128 is clarifai api limit
(defn insert-n-concepts-of-type! [n model painting-type]
  (map
    #(insert-concepts! % model)
    (partition-all 128 (retrieve-n-random-painting-rows-of-type n painting-type))))


(def painting-types
  #{"mythological" "genre" "portrait" "landscape" "religious" "other" "historical" "interior" "still-life" "study"})

; Based on external calculations
(def painting-type->sample-size
 {"mythological" 341
  "genre" 336
  "portrait" 355
  "landscape" 351
  "religious" 373
  "other" 263
  "historical" 269
  "interior" 236
  "still-life" 301
  "study" 65})



;  this is doing way too many things
;  or is it? part of designing eg function arities is thinking about
; "which part is the entry part? what's the interface?"
; when you stated writing this fn... you just started updating your fns
; to eg take a painting type or a sample size...
(defn insert-model-concepts-for-painting-type
 "For a given painting-type pt, insert pt's sample-size many model-concepts.

 Example use:
 (for [painting-type painting-types]
   (insert-model-concepts-for-painting-type :general painting-type))"
 [model painting-type]
 (let [sample-size (get painting-type->sample-size painting-type)]
      (insert-n-concepts-of-type! sample-size model painting-type)))


; (insert-model-concepts-for-painting-type :general "study")






;; ------------------------------------------------
;; Extracting already-written rows
;; ------------------------------------------------


(defn with-model-concepts
 "Set wga-concept-row's :concepts to just those belonging to model"
 ([concept-row]
  (with-model-concepts concept-row :general))
 ([concept-row model]
  {:pre [(is (map? concept-row))]}
  (as-> concept-row row
     (assoc row :concepts (json/read-str (:concepts row) :key-fn keyword))
     (assoc row :concepts (get-in row [:concepts model :concepts])))))


(defn retrieve-wga-concepts []
  (jdbc/query *db* ["select * from wga_concepts"]))

(defn retrieve-paintings-type [painting-type]
  (jdbc/query
   *db*
   ["select * from wga_concepts where `type`=?" painting-type]))
