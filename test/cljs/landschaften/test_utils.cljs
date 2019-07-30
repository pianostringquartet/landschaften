(ns landschaften.test-utils
  (:require [landschaften.db :as db]
            [re-frame.core :as rf]))

(rf/reg-event-db
  ::initialize-test-db
  (fn initialize-test-db [state _]
    db/fresh-db))