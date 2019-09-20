(ns landschaften.db.wga-csv
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [landschaften.db.core :refer [*db*]]
            [clojure.java.jdbc :as jdbc]))

;; TODO:
;; - refactor, using Ghostwheel
;; - make a separate script

;; PREPARE AND INSERT 'WEB GALLERY OF ART' (WGA) CSV-DB ROWS
;; INTO MYSQL DATABASE

; (load-file "src/clj/landschaften/db/wga_csv.clj")
; (mount.core/start *db*)

;; ------------------------------------
;; PREPARING CSV-DB ROWS
;; ------------------------------------


(defn to-sql-column [a-str]
  (-> a-str
    (clojure.string/replace "-" "_")
    (clojure.string/lower-case)
    (keyword)))


(defn url->jpg-url [url]
  (-> url
    (clojure.string/replace "/html" "/art")
    (clojure.string/replace ".html" ".jpg")))


(defn add-jpg [a-map]
  (let [jpg (-> (:url a-map)
                (clojure.string/replace "/html" "/art")
                (clojure.string/replace ".html" ".jpg"))]
    (assoc a-map :jpg jpg)))


(defn as-wga-csv-row [headers a-csv-row]
  (add-jpg
    (zipmap
      (map to-sql-column headers)
      a-csv-row)))



;; ------------------------
;; INSERT ROWS
;; ------------------------


(defn insert-wga-csv-row! [row-map]
  (jdbc/insert! *db* :wga_csv_rows row-map))


(defn insert-wga-csv-rows! [headers rows]
  (let [sql-ready-rows (map #(as-wga-csv-row headers %) rows)]
    (jdbc/insert-multi! *db* :wga_csv_rows sql-ready-rows)))


;; ------------------------
;; RETRIEVE ROWS
;; ------------------------


(defn retrieve-wga-csv-rows []
  (jdbc/query *db* ["select * from wga_csv_rows"]))


;; ------------------------
;; IMPORT CSV INTO DATABASE
;; ------------------------


(defn import-csv
 "Lazily read a 'WGA' CSV into a database.

 Example uses:
  (import-csv \"pontormo.csv\" 5 :separator \\;)
  (import-csv \"wga_catalog.csv\" 500)
"
 [filename partition-size separator]
 (with-open [reader (io/reader filename)]
   (let [csv (csv/read-csv reader :separator (or separator \,))
         headers (first csv)
         empty-row? #(= "" (first %))
         rows (remove empty-row? (rest csv))]
     (doall ;; force evaluation
      (map
        #(insert-wga-csv-rows! headers %)
        (partition-all partition-size rows))))))


;; ------------------------------------------------
;; UPDATING AUTHOR + TITLE IN EXISTING ROWS
;; ------------------------------------------------

(defn update-painting-row [jpg-url author title]
  (jdbc/update!
    *db*
    :paintings
    {:author author :title title}
    ["wga_jpg = ?" jpg-url]))

(defn update-wga-csv-row [jpg-url author title]
  (jdbc/update!
    *db*
    :wga_csv_rows
    {:author author :title title}
    ["jpg = ?" jpg-url]))


(defn update-author+title [jpg-url author title]
  (do
   (update-wga-csv-row jpg-url author title)
   (update-painting-row jpg-url author title)))

;; need to make sure that we do the lazy operation
;; before the reader closes
(defn update-csv
 "Import a CSV and use its rows as args to function."
 [filename separator]
 (with-open [reader (io/reader filename)]
   (let [csv (csv/read-csv reader :separator (or separator \,))
         empty-row? #(= "" (first %))
         rows (remove empty-row? (rest csv))]
     (doall ;; force evaluation
      (map
        (fn [raw-row]
          (let [jpg-url (url->jpg-url (nth raw-row 6))
                author (first raw-row)
                title  (nth raw-row 2)]
            (update-author+title jpg-url author title)))
        rows)))))
