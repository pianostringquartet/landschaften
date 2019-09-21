(ns landschaften.test-utils
  (:require [landschaften.db :as db]
            [re-frame.core :as rf]))

(rf/reg-event-db
  ::setup-db
  (fn setup-db [state [_ app-db]]
    app-db))