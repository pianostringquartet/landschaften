(ns landschaften.events.explore-events
  (:require [re-frame.core :refer [reg-cofx inject-cofx after dispatch reg-event-db reg-sub reg-event-fx reg-fx]]
            [landschaften.db :as db]
            [ajax.core :refer [POST GET]]
            [landschaften.events.core-events :as core-events]
            [landschaften.ui-specs :as ui-specs]
            [landschaften.specs :as specs]
            [landschaften.helpers :as helpers]
            [cljs.spec.alpha :as s]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]))


;; ------------------------------------------------------
;; Querying server for paintings
;; ------------------------------------------------------


;(defn ->query-constraints
;  "Put group's constraints in backend API's expected format."
;  [db]
;  (remove
;    #(empty? (:values %))
;    #{{:column "type" :values (into [] (get-in db db/path:type-constraints))}
;      {:column "school" :values (into [] (get-in db db/path:school-constraints))}
;      {:column "timeframe" :values (into [] (get-in db db/path:timeframe-constraints))}
;      {:column "author" :values (into [] (get-in db db/path:artist-constraints))}
;      {:column "name" :values (into [] (get-in db db/path:concept-constraints))}}))

(defn ->query-constraints
  "Put group's constraints in backend API's expected format."
  [db]
  (remove
    #(empty? (:values %))
    #{{:column "type" :values (into [] (db :selected-types))}
      {:column "school" :values (into [] (db :selected-schools))}
      {:column "timeframe" :values (into [] (db :selected-timeframes))}
      {:column "author" :values (into [] (db :selected-artists))}
      {:column "name" :values (into [] (db :selected-concepts))}}))


;(>defn on-query-started [db]
;  [::specs/app-db => ::specs/app-db]
;  (-> db
;      (assoc :query-loading? true)
;      (assoc :show-group-name-prompt? false)))


;(reg-event-fx
;  ::query-started
;  (fn query [cofx [_ group-name]]
;    (let [db (:db cofx)]
;      {:db (on-query-started db)
;       :post-request
;           {:uri     "/query"
;            :params  {:constraints (->query-constraints db)}
;            :handler #(dispatch [::query-succeeded % group-name])}})))

(reg-event-fx
  ::query-started
  (fn query [cofx _]
    (let [db (:db cofx)]
      {:db  (assoc db :query-loading? true) ;(on-query-started db)
       :post-request {:uri     "/query"
                      :params  {:constraints (->query-constraints db)}
                      :handler #(dispatch [::query-succeeded %])}})))




(reg-event-fx
  ::add-default-group
  (fn add-default-group [cofx [_ default-group]]
    (let [db (:db cofx)]
      (if-not (:current-group db)
        {:db       (assoc db :current-group default-group)
         :dispatch [::query-started (:group-name default-group)]}
        db))))


(declare toggle-save-group-popover-showing save-current-group)


;(defn on-query-succeeded [db paintings]
(>defn on-query-succeeded [db paintings]
  [::specs/app-db ::specs/paintings => ::specs/app-db]
  (-> db
      (assoc :paintings paintings)
      (assoc :query-loading? false)
      ;(assoc-in db/path:current-paintings paintings)
      (assoc :constraints-updated-since-search? false)
      (assoc :mobile-search? false) ; show resulting paintings on mobile
      (assoc :examining? false)))

;;
;(defn on-query-succeeded [db paintings]
;  (let [db-with-query-results
;        (-> db
;            (assoc :query-loading? false)
;            (assoc-in db/path:current-paintings paintings)
;            (assoc :constraints-updated-since-search? false)
;            (assoc :mobile-search? false)                   ; switch back to paintings
;            (assoc :examining? false))]
;    (if group-name
;      (-> db-with-query-results
;          (toggle-save-group-popover-showing false)
;          (save-current-group group-name))
;      db-with-query-results)))

;(reg-event-fx
;  ::query-succeeded
;  core-events/interceptors
;  (fn query-succeeded [cofx [_ paintings group-name]]
;    (let [db (on-query-succeeded (:db cofx) paintings group-name)]
;      {:persist-state db
;       :db            db})))


(reg-event-fx
  ::query-succeeded
  core-events/interceptors
  (fn query-succeeded [cofx [_ paintings]]
    (let [db (on-query-succeeded (:db cofx) paintings)]
      {:persist-state db
       :db            db})))


;; ------------------------------------------------------
;; Updating constraints
;; ------------------------------------------------------


(defn constraints-updated-since-search [db]
  (assoc db :constraints-updated-since-search? true))

(reg-event-db
  ::update-selected-types
  core-events/interceptors
  (fn update-selected-types [db [_ selected-types]]
    (constraints-updated-since-search (assoc db :selected-types selected-types))))


(reg-event-db
  ::update-selected-schools
  core-events/interceptors
  (fn update-selected-schools [db [_ selected-schools]]
    (constraints-updated-since-search (assoc db :selected-schools selected-schools))))


(reg-event-db
  ::update-selected-timeframes
  core-events/interceptors
  (fn update-selected-timeframes [db [_ selected-timeframes]]
    (constraints-updated-since-search (assoc db :selected-timeframes selected-timeframes))))


(>defn update-selected-concepts [db selected-concept]
  [::specs/app-db string? => ::specs/app-db]
  (update db :selected-concepts conj selected-concept))


(reg-event-db
  ::update-selected-concepts
  core-events/interceptors
  (fn [db [_ selected-concept]]
    (constraints-updated-since-search (update-selected-concepts db selected-concept))))


(defn remove-selected-concept [db selected-concept]
  (update db :selected-concepts disj selected-concept))

(reg-event-db
  ::remove-selected-concept
  core-events/interceptors
  (fn [db [_ selected-concept]]
    (constraints-updated-since-search (remove-selected-concept db selected-concept))))


(reg-event-db
  ::toggle-concept-selection
  core-events/interceptors
  (fn toggle-concept-selection [db [_ concept]]
    (constraints-updated-since-search
      (let [currently-selected-concepts (db :selected-concepts)]
        (if (contains? currently-selected-concepts concept)
          (remove-selected-concept db concept)
          (update-selected-concepts db concept))))))


(reg-event-db
  ::update-selected-artists
  core-events/interceptors
  (fn update-selected-artists [db [_ selected-artist]]
    (constraints-updated-since-search (update db :selected-artists conj selected-artist))))


(reg-event-db
  ::remove-selected-artist
  core-events/interceptors
  (fn remove-selected-artist [db [_ selected-artist]]
    (constraints-updated-since-search (update db :selected-artists disj selected-artist))))


(reg-event-db
  ::selections-cleared
  core-events/interceptors
  (fn selections-cleared [db _]
    (-> db
        (assoc :selected-types #{})
        (assoc :selected-schools #{})
        (assoc :selected-timeframes #{})
        (assoc :selected-concepts #{})
        (assoc :selected-artists #{})
        (constraints-updated-since-search))))


;; ------------------------------------------------------
;; Updating groups
;; ------------------------------------------------------


(defn toggle-save-group-popover-showing [db showing?]
  (assoc db :show-group-name-prompt? showing?))


(reg-event-db
  ::hide-save-group-popover
  core-events/interceptors
  (fn hide-save-group-popover [db _]
    (toggle-save-group-popover-showing db false)))


(reg-event-db
  ::show-save-group-popover
  core-events/interceptors
  (fn show-save-group-popover [db _]
    (toggle-save-group-popover-showing db true)))


(defn save-current-group [db group-name]
  {:pre [(string? group-name)]}
  (let [current-group (:current-group db)
        updated-group (assoc current-group :group-name group-name)]
    (-> db
        (assoc-in [:saved-groups group-name] updated-group)
        (assoc :current-group updated-group))))


(>defn bring-in-group [db group-name]
  [::specs/app-db string? => ::specs/app-db]
  (let [new-current-group (get (:saved-groups db) group-name)]
    (assoc db :current-group new-current-group)))


(reg-event-db
  ::switch-groups
  core-events/interceptors
  (fn switch-groups [db [_ destination-group-name]]
    (bring-in-group db destination-group-name)))


(>defn remove-compare-group-name [db group-name]
  [::specs/app-db string? => ::specs/app-db]
  (assoc db :compared-group-names (remove #{group-name} (:compared-group-names db))))


(>defn remove-group [db group-name]
  [::specs/app-db string? => ::specs/app-db]
  (let [old-saved-groups     (:saved-groups db)
        updated-saved-groups (dissoc old-saved-groups group-name)]
    (assoc db :saved-groups updated-saved-groups)))


(reg-event-fx
  ::remove-group
  core-events/interceptors
  (fn remove-group-handler [cofx [_ group-name]]
    (let [db         (:db cofx)
          updated-db (remove-compare-group-name
                       (remove-group db group-name)
                       group-name)]
      {:db            updated-db
       :persist-state updated-db})))


;; ------------------------------------------------------
;; Examining a single painting
;; ------------------------------------------------------


(reg-event-db
  ::done-button-clicked
  core-events/interceptors
  (fn done-button-clicked [db _]
    (-> db
        (assoc :examining? false)
        (assoc :show-painting-modal? false))))


(reg-event-db
  ::painting-tile-clicked
  core-events/interceptors
  (fn painting-tile-clicked [db [_ painting]]
    (-> db
        (assoc :current-painting painting)
        (assoc :show-painting-modal? true))))


(reg-event-db
  ::toggle-painting-modal
  core-events/interceptors
  (fn toggle-painting-modal [db _]
    (update db :show-painting-modal? not)))


(reg-event-db
  ::toggle-image-zoomed
  core-events/interceptors
  (fn toggle-image-zoomed [db _]
    (update db :image-zoomed? not)))


;; ------------------------------------------------------
;; Navigating through paintings' details (modals)
;; ------------------------------------------------------


(>defn previous-painting [db]
  [::specs/app-db => ::specs/app-db]
  (let [paintings        (helpers/sort-by-author (:paintings db))
        current-painting (:current-painting db)
        prev-slide       (or (last (take-while #(not= % current-painting) paintings))
                             (last paintings))]
    (assoc db :current-painting prev-slide)))


(reg-event-db
  ::go-to-previous-painting
  (fn [db _] (previous-painting db)))


(>defn next-painting [db]
  [::specs/app-db => ::specs/app-db]
  (let [paintings        (helpers/sort-by-author (:paintings db))
        current-painting (:current-painting db)
        next-slide       (or (second (drop-while #(not= % current-painting) paintings))
                             (first paintings))]
    (assoc db :current-painting next-slide)))


(reg-event-db
  ::go-to-next-painting
  core-events/interceptors
  (fn [db _] (next-painting db)))


;; TODO: Update functions to satisfy Ghostwheel's check
;(check)