(ns landschaften.explore.explore-events
  (:require [re-frame.core :refer [reg-cofx inject-cofx after dispatch reg-event-db reg-sub reg-event-fx reg-fx]]
            [ajax.core :refer [POST GET]]
            [landschaften.events :as core-events]
            [landschaften.specs :as specs]
            [landschaften.view-specs :as view-specs]
            [landschaften.helpers :as helpers]
            [cljs.spec.alpha :as s]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]))



;; ------------------------------------------------------
;; High level 'app states'
;; ------------------------------------------------------


;; Explore-ready-state:
;; Put the app into an explore-ready state,
;; i.e. no prompts, loading modal, paintings examined etc.
(defn explore-ready-state [db]
  (-> db
      (assoc :current-painting nil)
      (assoc :query-loading? false)
      (assoc :mobile-search? false)
      (assoc :examining? false)
      (assoc :show-group-name-prompt? false)))

;; A query has just succeeded;
;; i.e. want to set 'constraints updated since search?' false
;; and put app in explore-ready-state
(defn query-succeeded-state [db]
  (-> db
      (assoc :constraints-updated-since-search? false)
      (assoc :current-group-name nil) ; i.e. we're no longer looking at any group specifically
      (explore-ready-state)))

;; We are currently waiting for server's response
;; i.e. show loading modal but not group name prompt etc.
(defn waiting-for-server-response-state [db]
  (-> db
      (assoc :query-loading? true)
      (assoc :examining? false)
      (assoc :show-group-name-prompt? false)))



;; ------------------------------------------------------
;; Querying server for paintings
;; ------------------------------------------------------

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


(reg-event-fx
  ::query-started
  (fn query [cofx _]
    (let [db (:db cofx)]
      {:db  (waiting-for-server-response-state db) ;(assoc db :query-loading? true) ;(on-query-started db)
       :post-request {:uri     "/query"
                      :params  {:constraints (->query-constraints db)}
                      :handler #(dispatch [::query-succeeded %])}})))


(>defn on-query-succeeded [db paintings]
 [::specs/app-db ::specs/paintings => ::specs/app-db]
 (-> db
     (assoc :paintings paintings)
     (query-succeeded-state)))


(reg-event-db
  ::query-succeeded
  core-events/check-and-persist-interceptors
  (fn query-succeeded [db [_ paintings]]
    (on-query-succeeded db paintings)))


;; ------------------------------------------------------
;; Updating constraints
;; ------------------------------------------------------


(defn constraints-updated-since-search [db]
  (assoc db :constraints-updated-since-search? true))

(reg-event-db
  ::update-selected-types
  core-events/check-and-persist-interceptors
  (fn update-selected-types [db [_ selected-types]]
    (constraints-updated-since-search (assoc db :selected-types selected-types))))


(reg-event-db
  ::update-selected-schools
  core-events/check-and-persist-interceptors
  (fn update-selected-schools [db [_ selected-schools]]
    (constraints-updated-since-search (assoc db :selected-schools selected-schools))))


(reg-event-db
  ::update-selected-timeframes
  core-events/check-and-persist-interceptors
  (fn update-selected-timeframes [db [_ selected-timeframes]]
    (constraints-updated-since-search (assoc db :selected-timeframes selected-timeframes))))


(>defn update-selected-concepts [db selected-concept]
  [::specs/app-db string? => ::specs/app-db]
  (update db :selected-concepts conj selected-concept))


(reg-event-db
  ::update-selected-concepts
  core-events/check-and-persist-interceptors
  (fn [db [_ selected-concept]]
    (constraints-updated-since-search (update-selected-concepts db selected-concept))))


(defn remove-selected-concept [db selected-concept]
  (update db :selected-concepts disj selected-concept))

(reg-event-db
  ::remove-selected-concept
  core-events/check-and-persist-interceptors
  (fn [db [_ selected-concept]]
    (constraints-updated-since-search (remove-selected-concept db selected-concept))))


(reg-event-db
  ::toggle-concept-selection
  core-events/check-and-persist-interceptors
  (fn toggle-concept-selection [db [_ concept]]
    (constraints-updated-since-search
      (let [currently-selected-concepts (db :selected-concepts)]
        (if (contains? currently-selected-concepts concept)
          (remove-selected-concept db concept)
          (update-selected-concepts db concept))))))


(reg-event-db
  ::update-selected-artists
  core-events/check-and-persist-interceptors
  (fn update-selected-artists [db [_ selected-artist]]
    (constraints-updated-since-search (update db :selected-artists conj selected-artist))))


(reg-event-db
  ::remove-selected-artist
  core-events/check-and-persist-interceptors
  (fn remove-selected-artist [db [_ selected-artist]]
    (constraints-updated-since-search (update db :selected-artists disj selected-artist))))


(reg-event-db
  ::selections-cleared
  core-events/check-and-persist-interceptors
  (fn selections-cleared [db _]
    (-> db
        (assoc :selected-types #{})
        (assoc :selected-schools #{})
        (assoc :selected-timeframes #{})
        (assoc :selected-concepts #{})
        (assoc :selected-artists #{})
        (constraints-updated-since-search))))



(>defn active-accordion-constraint-updated [db new-active-accordion]
  [::specs/app-db (s/nilable ::view-specs/accordion-constraints) => ::specs/app-db]
  (assoc db :active-accordion-constraint new-active-accordion))

(reg-event-db
  ::active-accordion-constraint-updated
  (fn active-accordion-constraint-updated-handler [db [_ new-active-accordion]]
    (active-accordion-constraint-updated db new-active-accordion)))



;; ------------------------------------------------------
;; Updating groups
;; ------------------------------------------------------


(defn toggle-save-group-popover-showing [db showing?]
  (assoc db :show-group-name-prompt? showing?))


(reg-event-db
  ::hide-save-group-popover
  core-events/check-and-persist-interceptors
  (fn hide-save-group-popover [db _]
    (toggle-save-group-popover-showing db false)))


(reg-event-db
  ::show-save-group-popover
  core-events/check-and-persist-interceptors
  (fn show-save-group-popover [db _]
    (toggle-save-group-popover-showing db true)))


(defn save-current-group [db group-name]
  {:pre [(string? group-name)]}
  (let [current-group (:current-group db)
        updated-group (assoc current-group :group-name group-name)]
    (-> db
        (assoc-in [:saved-groups group-name] updated-group)
        (assoc :current-group updated-group))))

;; Adds group to :saved-groups
(>defn save-group [db group]
  [::specs/app-db ::specs/group => ::specs/app-db]
  (assoc-in db [:saved-groups (:group-name group)] group))

;; Make a group's constraints the top-level constraints,
;; its paintings the top-level paintings, etc.
(>defn set-current-group [db group]
  [::specs/app-db ::specs/group => ::specs/app-db]
  (-> db
      (assoc :paintings (:paintings group))
      (assoc :selected-types (:type-constraints group))
      (assoc :selected-schools (:school-constraints group))
      (assoc :selected-timeframes (:timeframe-constraints group))
      (assoc :selected-concepts (:concept-constraints group))
      (assoc :selected-artists (:artist-constraints group))
      (assoc :current-group-name (:group-name group))))


(>defn save-search-no-query-required [db new-group]
  [::specs/app-db ::specs/group => ::specs/app-db]
  (-> db
      (explore-ready-state)
      (save-group new-group)
      (set-current-group new-group)))


(reg-event-db
  ::save-search-query-succeeded
  core-events/check-and-persist-interceptors
  (fn save-search-query-succeeded-handler [db [_ new-group]]
    (-> db
        (query-succeeded-state)
        ;; Once we've retrieved paintings from backend for new group,
        ;; the process is same as if hadn't had to make query.
        (save-search-no-query-required new-group))))



(>defn start-save-search! [db new-group-name]
  [::specs/app-db string? => map?]
  (let [;; Want selected constraints etc. from time of save-search query's start
        create-group (fn [paintings] {:group-name new-group-name
                                      :paintings paintings
                                      :type-constraints (:selected-types db)
                                      :school-constraints (:selected-schools db)
                                      :timeframe-constraints (:selected-timeframes db)
                                      :concept-constraints (:selected-concepts db)
                                      :artist-constraints (:selected-artists db)})]
    (if (:constraints-updated-since-search? db)
      {:db (waiting-for-server-response-state db)
       :post-request {:uri "/query"
                      :params {:constraints (->query-constraints db)}
                      :handler #(dispatch [::save-search-query-succeeded (create-group %)])}}
      ;; If don't need to search, then just immediately save group etc.
      ;; :loading?, :constraints-updated-since-search? etc. should all already be false.
      {:db (save-search-no-query-required (explore-ready-state db)
                                          (create-group (:paintings db)))})))



(reg-event-fx
  ::save-search
  (fn start-save-search-handler [cofx [_ new-group-name]]
   (start-save-search! (:db cofx) new-group-name)))


;; Retrieve the group from saved-groups,
;; then set as current group.
(>defn switch-group! [db group]
  [::specs/app-db ::specs/group => ::specs/app-db]
  (set-current-group db group))


(reg-event-db
  ::switch-current-group
  core-events/check-and-persist-interceptors
  (fn switch-groups-handler [db [_ group-name]]
    (let [group (get-in db [:saved-groups group-name])]
      ;; Don't switch to the group if group has already been deleted.
      ;; Workaround for bug where UI dispatches first remove-group, then switch-current-group events.
      (if (nil? group)
        db
        (switch-group! db group)))))


(>defn remove-compare-group-name [db group-name]
  [::specs/app-db string? => ::specs/app-db]
  (assoc db :compared-group-names (remove #{group-name} (:compared-group-names db))))


;; Remove the group from the app;
;; i.e. remove from saved-groups, current-group-name, compared-group-names etc.
;; Do not modify top-level paintings, constraints etc.
;; If user deleted the current-group,
;; then app will be in same state as if had made a search and not yet saved anything.
(>defn remove-group! [db group-name]
  [::specs/app-db string? => ::specs/app-db]
  (let [updated-saved-groups (dissoc (:saved-groups db) group-name)
        maybe-remove-current-group-name (fn [db] (if (= group-name (:current-group-name db))
                                                   (assoc db :current-group-name nil)
                                                   db))]
    (-> db
        (assoc :saved-groups updated-saved-groups)
        (remove-compare-group-name group-name)
        (maybe-remove-current-group-name))))


(reg-event-db
  ::remove-group
  core-events/check-and-persist-interceptors
  (fn remove-group-handler [db [_ group-name]]
    (remove-group! db group-name)))


;; ------------------------------------------------------
;; Examining a single painting
;; ------------------------------------------------------


(reg-event-db
  ::done-button-clicked
  core-events/check-and-persist-interceptors
  (fn done-button-clicked [db _]
    (-> db
        (assoc :examining? false)
        (assoc :show-painting-modal? false))))


(reg-event-db
  ::painting-tile-clicked
  core-events/check-and-persist-interceptors
  (fn painting-tile-clicked [db [_ painting]]
    (-> db
        (assoc :current-painting painting)
        (assoc :show-painting-modal? true))))


(reg-event-db
  ::toggle-painting-modal
  core-events/check-and-persist-interceptors
  (fn toggle-painting-modal [db _]
    (update db :show-painting-modal? not)))


(reg-event-db
  ::toggle-image-zoomed
  core-events/check-and-persist-interceptors
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
  core-events/check-and-persist-interceptors
  (fn [db _] (next-painting db)))


;; TODO: Update functions to satisfy Ghostwheel's check
;(check)