(ns landschaften.helpers
  (:require [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]))

(defn sort-by-author [paintings]
  {:pre [(s/valid? ::specs/paintings paintings)]}
  {:post [(s/valid? ::specs/paintings paintings)]}
  (sort :author paintings))