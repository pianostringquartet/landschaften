(ns landschaften.db.core
  (:require
    [clj-time.jdbc]
    [clojure.java.jdbc :as jdbc]
    [clojure.tools.logging :as log]
    [conman.core :as conman]
    [landschaften.config :refer [env]]
    [mount.core :refer [defstate]])
  (:import [java.sql
            BatchUpdateException
            PreparedStatement]))



;; To get the actual env-map you might have to start up the app
;; (see -main method in non-db core)
;; (env-map is itself a mount-state)
;; Alternatively, for REPL play you might as well provide
;; a harcoded text?

;; you had assumed that `env` referred to some static variable,
;; whereas it's actually a prod vs dev vs test mode environment variable,
;; and prod vs dev etc is decided when the app is started up.

;; You were able to see that SOMETHING with probably wrong with *db*,
;; so you should have gone
;
(defstate ^:dynamic *db*
  :start (if-let [jdbc-url (env :database-url)]
           (do
            (prn "there was a database url!")
            (conman/connect! {:jdbc-url jdbc-url}))
           (do
            ; where is this getting printed / logged?!
             (log/warn "database connection URL was not found, please set :database-url in your config, e.g: dev-config.edn")
             (prn "defstate for db called")
             *db*))
  :stop (conman/disconnect! *db*))

; (conman/bind-connection *db* "sql/queries.sql")
