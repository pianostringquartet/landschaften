(ns landschaften.clarifai
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [landschaften.config :refer [env]]))


;; TODO: clean this up

;; ---------------------------------------
;; Image processing only (no video!; default to page 1)
;; ---------------------------------------

(def api-key (:api-key env))


(def models
  {:apparel "e0be3b9d6a454f0493ac3a30784001ff"
   :celebrity "e466caa0619f444ab97497640cefc4dc"
   :color "eeed0b6733a644cea07cf4c60f87ebb7"
   :demographics "c0c0ac362b03416da06ab3fa36fb58e3"
   :general "aaa03c23b3724a16a56b629203edc62c"
   :general-embedding "bbb5f41425b8468d9b7a554ff10f8581"
   :logo "c443119bf2ed4da98487520d01a0b1e3"
   :portrait-quality "de9bd05cfdbf4534af151beb2a5d0953"
   :travel "eee28c313d69466f836ab83287a54ed9"
   :moderation "d16f390eb32cad478c7ae150069bd2c6"
   :nsfw "e9576d86d2004ed1a38ba0cf39ecb4b1"
   :food "bd367be194cf45149e75f01d59f77ba7"
   :face-detection "a403429f2ddf4b49b307e318f00e528b-detection"
   :face-embedding "d02b4508df58432fbb84e800597b8959"
   :landscape-quality "bec14810deb94c40a05f1f0eb3c91403"
   :wedding "c386b7a870114f4a87477c0824499348"
   :focus "c2cf7cecd8a6427da375b9f35fcd2381"
   :textures-patterns "fbefb47f9fdb410e8ce14f24f54b47ff"})


(defn api-endpoint [model]
  (str "https://api.clarifai.com/v2/models/" (model models) "/outputs"))


(defn- request-headers [api-key]
  {:headers {:authorization (str "Key " api-key)
             :content-type "Application/JSON"}})


(defn request-body [urls]
  {:body (json/write-str {:inputs
                          (mapv
                            (fn [url] {:data {:image {:url url}}})
                            urls)})})


(defn post-request [api-key model urls]
  (client/post
      (api-endpoint model)
      (merge (request-headers api-key)
             (request-body urls))))


(defn- response->json [response]
  (json/read-json (:body response true)))


(defn get-concepts-for-image [output]
 {:url (get-in output [:input :data :image :url])
  :concepts (map #(dissoc % :id :app_id) (get-in output [:data :concepts]))})


(defn get-concepts-for-images [model urls]
 (->> (post-request api-key model urls)
   (response->json)
   (:outputs)
   (map get-concepts-for-image)))
