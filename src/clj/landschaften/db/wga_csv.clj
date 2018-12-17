(ns landschaften.db.wga-csv
  (:require [clarifai-clj.core :as clarifai]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [landschaften.db.core :refer [*db*]]
            [clojure.java.jdbc :as jdbc]))


;; PREPARE AND INSERT 'WEB GALLERY OF ART' (WGA) CSV-DB ROWS
;; INTO MYSQL DATABASE

; (load-file "src/clj/landschaften/db/wga_csv.clj")
; (mount.core/start *db*)


;; ------------------------------------
;; PREPARING CSV-DB ROWS
;; ------------------------------------

; (defn as-jpg
;  "Convert a WGA painting's webpage url to just its JPG url.
;    Example:
;      (as-jpg \"https://www.wga.hu/html/p/pontormo/1/00leda.html\")
;      => \"https://www.wga.hu/art/p/pontormo/1/00leda.jpg\"
;   "
;   [url-str]
;   (-> url-str
;     (clojure.string/replace "/html" "/art")
;     (clojure.string/replace ".html" ".jpg")))


(defn to-sql-column [a-str]
  (-> a-str
    (clojure.string/replace "-" "_")
    (clojure.string/lower-case)
    (keyword)))

(defn add-jpg [a-map]
  ; (let [url (:url a-map)
  (let [jpg (-> (:url a-map)
                (clojure.string/replace "/html" "/art")
                (clojure.string/replace ".html" ".jpg"))]
    ; (assoc a-map :jpg (as-jpg url))))
    (assoc a-map :jpg jpg url)))

(defn as-wga-csv-row [headers a-csv-row]
  (add-jpg
    (zipmap
      (map to-sql-column headers)
      a-csv-row)))


;; ------------------------
;; INSERT ROWS
;; ------------------------

;; not used anywhere?
(defn insert-wga-csv-row! [row-map]
  (jdbc/insert! *db* :wga_csv_rows row-map))

(defn insert-wga-csv-rows! [headers rows]
  (let [sql-ready-rows (map #(as-wga-csv-row headers %) rows)]
    (jdbc/insert-multi! *db* :wga_csv_rows sql-ready-rows)))

;; ------------------------
;; RETRIEVE ROWS
;; ------------------------

;; not used anywhere?
(defn retrieve-wga-csv-rows []
  (jdbc/query *db* ["select * from wga_csv_rows"]))

(defn retrieve-n-random-painting-rows [n]
  (jdbc/query *db*
    ["select * from wga_csv_rows where form = \"painting\" order by rand() limit ?" n]))

(defn retrieve-n-random-painting-rows-of-type [n painting-type]
  (jdbc/query *db*
    ["select * from wga_csv_rows where form = \"painting\" and `type` = ? order by rand() limit ?" painting-type n]))

;  works:
; (retrieve-n-random-painting-rows-of-type 10 "genre")


;; ------------------------
;; IMPORT CSV INTO DATABASE
;; ------------------------

(defn import-csv
 "Lazily read a 'WGA' CSV into a database.

 Example uses:
  (import-csv \"pontormo.csv\" 5)
  (import-csv \"wga_catalog.csv\" 500)
"
 [filename partition-size]
 (with-open [reader (io/reader filename)]
   (let [csv (csv/read-csv reader)
         headers (first csv)
         empty-row? #(= "" (first %))
         rows (remove empty-row? (rest csv))]
     (doall ;; force evaluation
      (map
        #(insert-wga-csv-rows! headers %)
        (partition-all partition-size rows))))))
