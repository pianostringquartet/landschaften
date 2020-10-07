(ns user
  (:require [landschaften.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [landschaften.figwheel :refer [start-fw stop-fw cljs]]
            [landschaften.core :refer [start-app]]))
            ;[landschaften.db.core]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'landschaften.core/repl-server))

(defn stop []
  (mount/stop-except #'landschaften.core/repl-server))
