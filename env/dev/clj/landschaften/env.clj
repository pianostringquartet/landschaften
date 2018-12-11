(ns landschaften.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [landschaften.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[landschaften started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[landschaften has shut down successfully]=-"))
   :middleware wrap-dev})
