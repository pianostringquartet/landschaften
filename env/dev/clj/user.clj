(ns user
  (:require [landschaften.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [landschaften.figwheel :refer [start-fw stop-fw cljs]]
            [landschaften.core :refer [start-app]]
            [landschaften.db.core]
            [conman.core :as conman]
            [luminus-migrations.core :as migrations]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'landschaften.core/repl-server))

(defn stop []
  (mount/stop-except #'landschaften.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn restart-db []
  (mount/stop #'landschaften.db.core/*db*)
  (mount/start #'landschaften.db.core/*db*)
  (binding [*ns* 'landschaften.db.core]
    (conman/bind-connection landschaften.db.core/*db* "sql/queries.sql")))

(defn reset-db []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))


