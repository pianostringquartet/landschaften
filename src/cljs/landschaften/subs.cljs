(ns landschaften.subs
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub]]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]
            [cljs.spec.alpha :as s]))



;; ------------------------------------------------------
;; Subscriptions
;; - node graph for sourcing from db
;; - should source directly (not change db data etc.)
;; ------------------------------------------------------


;; ------------------------------------------------------
;; High level subs
;; ------------------------------------------------------

(reg-sub
  ::current-mode
  (fn current-mode [db _]
    (:current-mode db)))


;; ------------------------------------------------------
;; Groups (selected constraints, retrieved paintings)
;; ------------------------------------------------------


;; used by both compare and explore screens
(reg-sub
  ::saved-groups
  (fn saved-groups [db _]
    {:post [(s/valid? map? %)]}
    (:saved-groups db)))


(reg-sub
  ::saved-groups-names
  (fn saved-groups-names [db _]
    (keys (:saved-groups db))))


;; temporary: eventually each group / query-result
;; will have its own server-side-calculated concept-frequency-data
