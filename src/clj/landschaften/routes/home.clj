(ns landschaften.routes.home
  (:require [landschaften.layout :as layout]
            [landschaften.db.core :as db]
            [clojure.java.io :as io]
            [landschaften.middleware :as middleware]
            [ring.util.http-response :as response]
            [landschaften.api :as api]))

(defn home-page [_]
  (layout/render "home.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-base
                 middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/docs" {:get (fn [_]
                    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]
   ["/query" {:post (fn [{:keys [params]}]
                      (response/ok
                         (api/paintings-satisfying
                           db/*db*
                           (:constraints params))))}]])
