(ns landschaften.db.paintings
  (:require [landschaften.db.core :refer [*db*]]
           [clojure.java.jdbc :as jdbc]
           [landschaften.clarifai :as clarifai]
           [clojure.data.json :as json]))


;;; NEEDS REFACTORING:

;; LOGIC FOR PAINTING-ROWS:
;; - TRANSFORM CSV-DB-ROWS (DB ROW REPRESENTATIONS OF CSV ROWS)
;;   INTO PAINTING-ROWS
;; - EASY-TO-WORK-WITH FORM OF PAINTING-ROWS

;; Overly large sample sizes
(def PAINTING-TYPE->SAMPLE-SIZE
 {"mythological" 500
  "genre" 500
  "portrait" 500
  "landscape" 500
  "religious" 500
  "other" 400
  "historical" 400
  "interior" 400
  "still-life" 500
  "study" 78}) ; only 78 total

(def PAINTING-COLUMNS
  #{:id :author :title :date :form :type :school :timeframe :jpg :concepts})

(def PAINTINGS-TABLE "paintings")

(def CLARIFAI-API-LIMIT 128)

;; ----------------------------------------
;; TRANSFORM CSV-DB-ROWS TO PAINTING-ROWS
;; ----------------------------------------

(defn add-concepts-to-row [row concepts model]
  (assoc row :concepts (json/write-str {model concepts})))

(defn as-painting-row [almost-row]
  (let [row-keyset (into #{} (keys almost-row))]
    (apply dissoc
     almost-row
     (clojure.set/difference row-keyset PAINTING-COLUMNS))))

;; ----------------------------------------
;; INSERT PAINTING-ROWS
;; ----------------------------------------

(defn insert-wga-concept-rows! [row-maps]
  (jdbc/insert-multi! *db* PAINTINGS-TABLE row-maps))

;; WAY TOO BIG
;; creates paintings-rows from csv-rows
(defn insert-concepts!
 "rows: wga-csv-rows
  model: keyword, e.g. :general"
 [rows model]
 (let [concepts
         (clarifai/get-concepts-for-images model (map #(:jpg %) rows))
       concepts-as-rows ; way too much happening here -- nested maps :o
         (map as-painting-row
          (map #(add-concepts-to-row %1 %2 model) rows concepts))]
   (insert-wga-concept-rows! concepts-as-rows)))

(defn retrieve-n-random-painting-rows-of-type [n painting-type]
  (jdbc/query *db*
    ["select * from wga_csv_rows where form = \"painting\" and `type` = ? order by rand() limit ?" painting-type n]))

(defn insert-n-concepts-of-type! [n model painting-type]
 (let [rows (retrieve-n-random-painting-rows-of-type n painting-type)]
   (map
     #(insert-concepts! % model)
     (partition-all CLARIFAI-API-LIMIT rows))))

(defn insert-model-concepts-for-painting-type
 "For a given painting-type pt, insert pt's sample-size many model-concepts.

 Example use:
 (for [painting-type painting-types]
   (insert-model-concepts-for-painting-type :general painting-type))"
 [model painting-type]
 (let [sample-size (get PAINTING-TYPE->SAMPLE-SIZE painting-type)]
      (insert-n-concepts-of-type! sample-size model painting-type)))
