(ns landschaften.routes.home
  (:require [landschaften.layout :as layout]
            [landschaften.middleware :as middleware]))

(defn home-page [_]
  (layout/render "home.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-base
                 middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]])
