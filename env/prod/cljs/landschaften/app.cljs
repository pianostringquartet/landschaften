(ns landschaften.app
  (:require [landschaften.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
