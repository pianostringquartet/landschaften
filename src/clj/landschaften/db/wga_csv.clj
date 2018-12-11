(ns landschaften.db.wga-csv
  (:require [clarifai-clj.core :as clarifai]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [landschaften.db.core :refer [*db*]]
            [clojure.java.jdbc :as jdbc]))

; (load-file "src/clj/landschaften/db/wga_csv.clj")
; (mount.core/start *db*)


;; ------------------------------------
;; Updating CSV with image urls
;; ------------------------------------


(defn as-jpg
 "Convert a WGA painting's webpage url to just its JPG url.

   Example:
     (as-jpg \"https://www.wga.hu/html/p/pontormo/1/00leda.html\")
     => \"https://www.wga.hu/art/p/pontormo/1/00leda.jpg\"
    "
  [url-str]
  (-> url-str
    (clojure.string/replace "/html" "/art")
    (clojure.string/replace ".html" ".jpg")))


(defn to-sql-column [a-str]
  (-> a-str
    (clojure.string/replace "-" "_")
    (clojure.string/lower-case)
    (keyword)))


(defn add-jpg [a-map]
  (let [url (:url a-map)]
    (assoc a-map :jpg (as-jpg url))))


(defn as-wga-csv-row [headers a-csv-row]
  (add-jpg
    (zipmap
      (map to-sql-column headers)
      a-csv-row)))


(defn insert-wga-csv-row! [row-map]
  (jdbc/insert! *db* :wga_csv_rows row-map))


(defn retrieve-wga-csv-rows []
  (jdbc/query *db* ["select * from wga_csv_rows"]))


(defn insert-wga-csv-rows! [headers rows]
  (let [sql-ready-rows (map #(as-wga-csv-row headers %) rows)]
    (jdbc/insert-multi! *db* :wga_csv_rows sql-ready-rows)))


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
