(ns landschaften.db.core
  (:require
    [clj-time.jdbc]
    [clojure.tools.logging :as log]
    [conman.core :as conman]
    [landschaften.config :refer [env]]
    [mount.core :refer [defstate]])
  (:import [java.sql
            BatchUpdateException
            PreparedStatement]))

(defstate ^:dynamic *db*
  :start (if-let [jdbc-url (env :database-url)]
           (do
            (conman/connect! {:jdbc-url jdbc-url}))
           (do
             (log/warn "database connection URL was not found, please set :database-url in your config, e.g: dev-config.edn")
             *db*))
  :stop (conman/disconnect! *db*))
