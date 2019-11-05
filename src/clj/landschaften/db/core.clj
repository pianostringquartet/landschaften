(ns landschaften.db.core
  (:require
    [clj-time.jdbc]
    [landschaften.config :refer [env]]
    [mount.core :refer [defstate]]
    [clojure.data.json :as json]
    [clojure.java.jdbc :as jdbc]
    [clj-http.client :as client]))

(def DB-SPEC {:dbtype "postgresql" :dbname "bilder" :user "concerto"})
(def PAINTINGS-TABLE "paintings")

;; ------------------------------------------------------------
;; WGA CSV ROWS -> PAINTING ROWS
;; ------------------------------------------------------------

(def CSV-TABLE "wga_csv_rows")
(def SELECT-CSV-ROWS-QUERY ;; only rows for paintings and whose jpg is not in paintings-table already
  "
  select distinct t.* from wga_csv_rows t where
  t.form = 'painting'
  and not exists (select from paintings t2 where wga_jpg = t.jpg and id = t.id)
  ")

(def CLARIFAI-API-LIMIT 128)
(def API-KEY "ebc31d071d23414ab7e369c003e3c3bf")

(defn- response->json [response]
  (json/read-json (:body response true)))

(defn post-request [urls]
  (let [general-model "aaa03c23b3724a16a56b629203edc62c" ;; clarifai has for the Predict API general model
        api-endpoint (str "https://api.clarifai.com/v2/models/" general-model "/outputs")
        headers {:headers {:authorization (str "Key " API-KEY)
                           :content-type  "Application/JSON"}}
        body {:body (json/write-str {:inputs  (mapv (fn [url] {:data {:image {:url url}}})
                                                    urls)})}]
    (client/post api-endpoint (merge headers body))))

(post-request ["https://www.wga.hu/art/a/aachen/bacchus.jpg"])

(defn image-concepts [output]
  {:url (get-in output [:input :data :image :url])
   :concepts (map #(dissoc % :id :app_id) (get-in output [:data :concepts]))})

(defn images-concepts [urls]
  (->> (post-request urls)
       (response->json)
       (:outputs)
       (map image-concepts)))

(defn as-painting-row [almost-row]
  (merge {:wga_jpg (:jpg almost-row)}
    (select-keys almost-row [:id :author :title :date :form :type :school :timeframe :concepts])))

(defn add-concepts-to-row [row concepts model]
  (assoc row :concepts (json/write-str {model concepts})))

(defn insert-concepts!
  "rows: wga-csv-rows (which have :jpg NOT :wga_jpg key)
  model: keyword, e.g. :general"
  [db-spec rows]
  (let [concepts (images-concepts (map #(:jpg %) rows))
        concepts-as-rows (map as-painting-row
                              (map #(add-concepts-to-row %1 %2 :general) rows concepts))]
     (jdbc/insert-multi! db-spec PAINTINGS-TABLE concepts-as-rows)))

;; FIRST: csv rows -> paintings rows
;; TODO: finish classifying 21k csv-rows
;(let [csv-rows (jdbc/query DB-SPEC SELECT-CSV-ROWS-QUERY)]
;  (map #(insert-concepts! DB-SPEC %)
;       (partition-all CLARIFAI-API-LIMIT csv-rows)))


;; ------------------------------------------------------------
;; PAINTING ROWS -> PAINTING CONCEPTS ROWS
;; ------------------------------------------------------------

(def PAINTINGS-CONCEPTS-TABLE "paintings_concepts")

(def SELECT-PAINTING-ROWS-QUERY ; painting rows whose concepts have not yet been propagated as painting-concepts rows
  "
  select distinct t.* from paintings t where not exists (
    select from paintings_concepts t2
    where painting_id = t.id)
  ")

(defn general-model-concepts
  "Put a painting row's concepts into a Clojure-friendly form.
  Concepts from Clarifai general model only."
  [{json :concepts :as painting}]
  (assoc painting :concepts (-> (json/read-str json :key-fn keyword)
                                (get-in [:general :concepts])
                                (set))))


(defn painting-row->painting-concept-rows [{:keys [id concepts]}]
  (map #(merge {:painting_id id} %) concepts))

(defn insert-paintings-concepts-rows!
  "Takes painting rows (in a Clojure-friendly form)
  and inserts one painting-concept row per concept in the painting."
  [db p-rows]
  (jdbc/insert-multi! db
                      PAINTINGS-CONCEPTS-TABLE
                      (mapcat painting-row->painting-concept-rows p-rows)))


;; THEN: painting rows -> painting_concepts rows
;(let [paintings-to-propagate (map general-model-concepts (jdbc/query DB-SPEC SELECT-PAINTING-ROWS-QUERY))]
;  (do (insert-paintings-concepts-rows! DB-SPEC
;              paintings-to-propagate)
;      (print "\n Done :) \n")))
