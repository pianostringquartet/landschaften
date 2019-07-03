(ns landschaften.events.compare-events
  (:require [landschaften.events.core-events :as core-events]
            [re-frame.core :refer [reg-event-db reg-sub reg-event-fx]]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [ajax.core :refer [POST GET]]
            [landschaften.specs :as specs]
            [landschaften.events.explore-events :as explore-events]
            [cljs.spec.alpha :as s]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]))


;; ------------------------------------------------------
;; Comparing groups
;; ------------------------------------------------------


(defn add-compare-group-name [group-names group-name]
  {:pre  [(string? group-name)]
   :post [(s/valid? (s/coll-of string?) %)
          (>= 2 (count %))]}
  (let [already-comparing? (boolean (some #{group-name} group-names))
        already-full?      (boolean (= 2 (count group-names)))]
    (cond
      already-comparing? group-names
      already-full? (conj (drop-last group-names) group-name)
      :else (conj group-names group-name))))


(reg-event-db
  ::add-compare-group-name
  core-events/interceptors
  (fn add-compare-group [db [_ group-name]]
    {:pre [(string? group-name)]}
    (let [group-names (:compared-group-names db)]
      (assoc db :compared-group-names (add-compare-group-name group-names group-name)))))


(reg-event-db
  ::remove-compare-group-name
  core-events/interceptors
  (fn remove-compare-group-name-handler [db [_ group-name]]
    (explore-events/remove-compare-group-name db group-name)))


(reg-event-db
  ::comparisons-cleared
  core-events/interceptors
  (fn comparisons-cleared [db _]
    (assoc db :compared-group-names '())))

