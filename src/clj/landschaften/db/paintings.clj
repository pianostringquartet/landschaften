(ns landschaften.db.paintings
  (:require [landschaften.db.core :refer [*db*]]
           [clojure.java.jdbc :as jdbc]
           [landschaften.clarifai :as clarifai]
           [clojure.data.json :as json]
           [clojure.test :refer [is]]
           [clojure.spec.alpha :as s]
           [clojure.spec.test.alpha :as st]
           [expound.alpha :as exp]
           [clojure.spec.gen.alpha :as gen]
           [landschaften.entity :as entity]
           [hugsql.core :as hugsql]))



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

; (def PAINTING-TYPES (into #{} (keys PAINTING-TYPE->SAMPLE-SIZE)))

;; keep the :id and :jpg
(def PAINTING-COLUMNS
  #{:id :author :title :date :form :type :school :timeframe :jpg :concepts})

(def PAINTINGS-TABLE "paintings")

(def CSV-TABLE "wga_csv_rows")

;; ----------------------------------------
;; RETRIEVE PAINTING-ROWS IN FRIENDLY FORMAT
;; ----------------------------------------

; (def RETURNS-STRING {:post [(is #(string? %))]})
; (def r-str {:post [(is #(string? %))]})

;; A STUPID FN to replace the MUCH BETTER sql 'value in (...)'!
(defn column-includes [column-name desired-values]
  (clojure.string/join
    " or "
    (map #(str "`" column-name "` = '" % "'") desired-values)))

;; this is terrible -- you hardcoded a name here
;; you should instead take a map
; (defn query-constraints [types timeframes schools]

;; when this is called incorrectly, e.g.
;; the maps are do not have the right keys?
;; when and where do we provide information about how something is
;; expected to be called? where do we have explosions vs. fallbacks?
(defn query-constraints [constraints]
   {:pre [(is (every? map? constraints))]
    :post [(is (string? %))]}
  (->> constraints
    (map #(column-includes (:column %) (:values %)))
    (remove empty?)
    (map #(str "(" % ")")) ; scope a column's clause
    (clojure.string/join " and ")
    (#(if (empty? %) "" (str " where " %)))))


(defn general-model-concepts
  "Concepts from Clarifai general model only."
 [{json :concepts :as painting}]
 (assoc painting :concepts (-> (json/read-str json :key-fn keyword)
                               (get-in [:general :concepts])
                               (set))))


(defn retrieve-paintings
 "Can query database by columns but not concepts.
                                 Not optimized."
 [db constraints]
 (let [query (str "select * from " PAINTINGS-TABLE
                 (query-constraints constraints))
       result (jdbc/query db [query])]
    (map general-model-concepts result)))

; ;; success, good
; (exp/expound ::entity/painting
;   (first (take 3 (retrieve-paintings *db* #{{} {:column "timeframe" :values ["1501-1550", "1551-1600"]}}))))

; ;; fails, good:
; (exp/expound ::entity/painting
;   (take 3 (retrieve-paintings *db* #{{} {:column "timeframe" :values ["1501-1550", "1551-1600"]}})))




;;; TESTS:
; (= ""
;  (query-constraints [] [] []))

; (= ""
;  (query-constraints {}))

; (= ""
;  (query-constraints #{}))

;; true
; (= " where (`timeframe` = '1501-1550' or `timeframe` = '1551-1600')"
;  (query-constraints {} {:column "timeframe" :values ["1501-1550", "1551-1600"]}))

;; true :-)
; (=
;   " where (`type` = 'landscape' or `type` = 'mythological') and (`timeframe` = '1501-1550' or `timeframe` = '1551-1600') and (`school` = 'Italian' or `school` = 'Flemish')"
;   (query-constraints ["landscape", "mythological"]["1501-1550", "1551-1600"] ["Italian" "Flemish"]))

; (=
;   " where (`type` = 'landscape' or `type` = 'mythological') and (`timeframe` = '1501-1550' or `timeframe` = '1551-1600') and (`school` = 'Italian' or `school` = 'Flemish')"
;   (query-constraints {:column "type" :values ["landscape", "mythological"]} {:column "timeframe" :values ["1501-1550", "1551-1600"]} {:column "school" :values ["Italian" "Flemish"]}))



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
  ; (jdbc/insert-multi! *db* :wga_concepts row-maps))
  (jdbc/insert-multi! *db* PAINTINGS-TABLE row-maps))

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


; (def select-paintings
;   (str "select * from " CSV-TABLE " where `form` = \"painting\" "))


; retrieve n random csv-rows where type = 'painting'

; (defn retrieve-n-random-csv-rows [db n]
;   (let [constraints (query-constraints
;                       [{:column "form" :values ["painting"]}])
;         limit (str " limit " n)
;         query (str "select * from " CSV-TABLE " " constraints "")]))






(defn retrieve-n-random-painting-rows [n]
  (jdbc/query *db*
    ["select * from wga_csv_rows where form = \"painting\" order by rand() limit ?" n]))


; (retrieve-n-random-painting-rows 2)


(defn retrieve-n-random-painting-rows-of-type [n painting-type]
  (jdbc/query *db*
    ["select * from wga_csv_rows where form = \"painting\" and `type` = ? order by rand() limit ?" painting-type n]))

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
