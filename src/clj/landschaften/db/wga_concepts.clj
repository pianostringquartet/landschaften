(ns landschaften.db.wga-concepts
  (:require [landschaften.db.core :refer [*db*]]
           [clojure.java.jdbc :as jdbc]
           [landschaften.clarifai :as clarifai]
           [clojure.data.json :as json]
           [clojure.test :refer [is]]
           [clojure.spec.alpha :as s]
           [clojure.spec.test.alpha :as st]
           [expound.alpha :as exp]
           [clojure.spec.gen.alpha :as gen]))

;; LOGIC FOR PAINTING-ROWS:
;; - TRANSFORM CSV-DB-ROWS (DB ROW REPRESENTATIONS OF CSV ROWS)
;;   INTO PAINTING-ROWS
;; - EASY-TO-WORK-WITH FORM OF PAINTING-ROWS

;; TODO:
;; Rename 'wga-concepts' table and code to e.g. "paintings"
;; Have spec-check somewhere (eg in a 'test' file) that you can run, to guarantee preserved behavior


;; Sample sizes based on external calculations
(def PAINTING-TYPE->SAMPLE-SIZE
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

(def PAINTING-TYPES (into #{} (keys PAINTING-TYPE->SAMPLE-SIZE)))

;; keep the :id and :jpg
(def PAINTING-COLUMNS
  #{:id :author :title :date :form :type :school :timeframe :jpg :concepts})

(def PAINTINGS-TABLE "wga_concepts")

(def CSV-TABLE "wga_csv_rows")

;; ----------------------------------------
;; RETRIEVE PAINTING-ROWS IN FRIENDLY FORMAT
;; ----------------------------------------

; (def RETURNS-STRING {:post [(is #(string? %))]})
; (def r-str {:post [(is #(string? %))]})

(defn column-includes [column-name desired-values]
  (clojure.string/join
    " or "
    (map #(str "`" column-name "` = '" % "'") desired-values)))

(defn query-constraints [types timeframes schools]
  {:post [(is (string? %))]}
  (->> [(column-includes "type" types)
        (column-includes "timeframe" timeframes)
        (column-includes "school" schools)]
    (remove empty?)
    (map #(str "(" % ")")) ; scope a column's clause
    (clojure.string/join " and ")
    (#(if (empty? %) "" (str " where " %)))))


(defn general-model-concepts
  "Concepts from Clarifai general model only."
 [{json :concepts :as painting}]
 (assoc painting :concepts (-> (json/read-str json :key-fn keyword)
                               (get-in [:general :concepts]))))


;; from the database you can query by any of the columns,
;; but cannot query by concepts
;; (column's values are a disjoint ie mutually exclusive)
;; you want a spec for :painting here
;; you'll return a SET of ::painting

(defn retrieve-paintings
  "Can query database by columns but not concepts.
  Not optimized."
  [db {:keys [types timeframes schools]
       :or {types [] timeframes [] schools []}}]
  (let [constraints (query-constraints types timeframes schools)
        query (str "select * from " PAINTINGS-TABLE constraints)
        result (jdbc/query db [query])]
       result
    (map general-model-concepts result)))

;; work :)
(take 1 (retrieve-paintings *db* {}))
(take 2 (retrieve-paintings *db* {}))



;;; TESTS:

;; true :-)
(=
  "select * from wga_concepts where (`type` = 'landscape' or `type` = 'mythological') and (`timeframe` = '1501-1550' or `timeframe` = '1551-1600') and (`school` = 'Italian' or `school` = 'Flemish')"
  (build-query ["landscape", "mythological"]["1501-1550", "1551-1600"] ["Italian" "Flemish"]))

;; true
(= ""
 (query-constraints [] [] []))

;; true
(= " where (`timeframe` = '1501-1550' or `timeframe` = '1551-1600')"
  (query-constraints [] ["1501-1550", "1551-1600"] []))




;;; NEEDS REFACTORING:


;; ----------------------------------------
;; TRANSFORM CSV-DB-ROWS TO PAINTING-ROWS
;; ----------------------------------------

(defn add-concepts-to-row [row concepts model]
  (assoc row :concepts (json/write-str {model concepts})))

(defn as-painting-row [almost-row]
  (let [PAINTING-COLUMNS
          #{:id :author :title :date :form :type :school :timeframe :jpg :concepts}
        row-keyset (into #{} (keys almost-row))]
    (apply dissoc
     almost-row
     (clojure.set/difference row-keyset PAINTING-COLUMNS))))

;; ----------------------------------------
;; INSERT PAINTING-ROWS
;; ----------------------------------------

(defn insert-wga-concept-rows! [row-maps]
  (jdbc/insert-multi! *db* :wga_concepts row-maps))

;; WAY TOO BIG
;; create paintings-rows
(defn insert-concepts!
 "rows: wga-csv-rows
  model: keyword, e.g. :general"
 [rows model]
 (let [concepts
         (clarifai/get-concepts-for-images model (map #(:jpg %) rows))
       concepts-as-rows ; way too much happening here -- nested maps BLECH
         (map as-painting-row
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
 (let [sample-size (get PAINTING-TYPE->SAMPLE-SIZE painting-type)]
      (insert-n-concepts-of-type! sample-size model painting-type)))

; (insert-model-concepts-for-painting-type :general "study")












;;;; DEPRECATED

;; DEPRECATED
; (defn with-model-concepts
;  "Set wga-concept-row's :concepts to just those belonging to model"
;  ([concept-row]
;   (with-model-concepts concept-row :general))
;  ([concept-row model]
;   {:pre [(is (map? concept-row))]}
;   (as-> concept-row row
;      (assoc row :concepts (json/read-str (:concepts row) :key-fn keyword))
;      (assoc row :concepts (get-in row [:concepts model :concepts])))))

; (defn retrieve-wga-concepts [db]
;   (jdbc/query db [(str "select * from " PAINTINGS-TABLE)]))
;
; ; (take 5 (retrieve-wga-concepts *db*))
;
; ;; Retrieve paintings by column `type` = ...
; (defn retrieve-paintings-type [db painting-type]
;     (jdbc/query db [(str "select * from " PAINTINGS-TABLE " where `type`=?" painting-type)]))
