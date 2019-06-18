(ns landschaften.events
  (:require [re-frame.core :refer [reg-cofx inject-cofx after dispatch reg-event-db reg-sub reg-event-fx reg-fx]]
            [landschaften.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [ajax.core :refer [POST GET]]
            [landschaften.ui-specs :as ui-specs]
            [landschaften.specs :as specs]
            [landschaften.helpers :as helpers]
            [cljs.spec.alpha :as s]
            ;[landschaften.views.utils :as utils]
            [re-frame.core :as rf]
            [cljs.spec.test.alpha :as st]
            [ghostwheel.core
              :as g
              :refer [check >defn >defn- >fdef => | <- ?]]))

;; ------------------------------------------------------
;; Interceptors
;; ------------------------------------------------------

(def log js/console.log)

;; persist via local storage
;(def ->local-store (after todos->local-store))

(def ls-auth-key "session-info")


(>defn ->localstore! [state]
  [::specs/app-db => nil?]
  (do
    (log "->localstore! called")
    (log "(:current-group state): " (:current-group state))
    (.setItem js/localStorage ls-auth-key state)))


(reg-fx
  :persist-state
  ->localstore!)


;; rename to ':persisted-data'?
(reg-cofx
  :user-session
  (fn user-session [cofx _]
    (let [data-from-local-storage (cljs.reader/read-string
                                    (some->> (.getItem js/localStorage ls-auth-key)))]
        (assoc cofx :user-session data-from-local-storage))))


;; spec check
(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

;; now we create an interceptor using `after`
;(def check-spec-interceptor (after (partial check-and-throw :todomvc.db/db)))
(def spec? (after (partial check-and-throw ::specs/app-db)))

(def interceptors [spec?])



;; ------------------------------------------------------
;; High level events
;; ------------------------------------------------------



;; if persisted data, use that
;; else do init-db
;; (NOTE: currently persisting ENTIRE db)
(reg-event-fx
  ::initialize-app
  ;interceptors
  [(inject-cofx :user-session)] ;; an interceptor
  (fn initialize-app [cofx _]
    (let [persisted-db (:user-session cofx)]
      (do
        (log "(keys (:saved-groups persisted-db): " (keys (:saved-groups persisted-db)))
        (log ":type-constraints of (:saved-groups persisted-db): " (map #(-> % second :type-constraints) (:saved-groups persisted-db)))
        (if (s/valid? ::specs/app-db persisted-db)
          {:db persisted-db}
          {:db db/default-db})))))


(reg-event-db
  ::mode-changed
  interceptors
  (fn mode-changed [db [_ new-mode]]
    {:pre [(s/valid? ::ui-specs/mode new-mode)]}
    (do
      (log "::mode-changed called")
      (assoc db :current-mode new-mode))))


(reg-event-db
  ::toggle-mobile-search
  interceptors
  (fn mobile-search-toggled [db]
    (update db :mobile-search? not)))


;; not really using :navigate here, so don't include in app
(reg-event-db
  :navigate
  (fn [db [_ route]]
    db))
    ; (assoc db :route route)))


(reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))


;; ------------------------------------------------------
;; Communicating with server
;; ------------------------------------------------------


;; not real error handling...
(defn default-error-handler [response]
  (log "Encountered unexpected error: " response))


(reg-fx
  :post-request
  (fn post-request-handler
    [{uri :uri params :params handler :handler error-handler :error-handler
      :or {error-handler default-error-handler}}]
    (POST uri {:params params :handler handler :error-handler error-handler})))


(reg-fx
  :get-request
  (fn get-request-handler
    [{uri :uri handler :handler error-handler :error-handler
      :or {error-handler default-error-handler}}]
    (GET uri {:handler handler :error-handler error-handler})))


;; need failure handlers...
(reg-event-fx
  ::retrieve-artists-names
  (fn query [cofx _]
    {:get-request {:uri "/artists"
                   :handler #(dispatch [::artists-names-retrieved %])}}))


(reg-event-fx
  ::retrieve-concepts
  (fn query [cofx _]
    {:get-request {:uri "/concepts"
                   :handler #(dispatch [::concepts-retrieved %])}}))


(reg-event-db
  ::concepts-retrieved
  interceptors
  (fn concepts-retrieved [db [_ artists]]
    (assoc db :all-concepts (into #{} artists))))


(reg-event-db
  ::artists-names-retrieved
  interceptors
  (fn artists-names-retrieved [db [_ artists]]
    (assoc db :all-artists (into #{} artists))))


(defn ->query-constraints
  "Put group's constraints in backend API's expected format."
  [db]
  (remove
   #(empty? (:values %))
   #{{:column "type" :values (into [] (get-in db db/path:type-constraints))}
     {:column "school" :values (into [] (get-in db db/path:school-constraints))}
     {:column "timeframe" :values (into [] (get-in db db/path:timeframe-constraints))}
     {:column "author" :values (into [] (get-in db db/path:artist-constraints))}
     {:column "name" :values (into [] (get-in db db/path:concept-constraints))}}))

;; rather than drawing 'hidden params' from the db within the handler,
;; would be better to pass in everything needed for action explicitly,
;; within the action itself

;; but, from the UI itself, will you really have access to the constraints?

;; you could instead optionally add another action,
;; which would be triggered


;; this is impure! the desired logic of the fn
;; actually depends on db being a certain way

;; querying alone is fine
;;
;;;; but when group-name is provided, we're implicitly depending on db having
;;;; certain constraints!
(reg-event-fx
  ::query-started
  (fn query [cofx [_ group-name]]
    (let [db (:db cofx)]
      (do
        (log "::query-started called")
        (log "::query-started constraints: " (->query-constraints db))
        {:db (assoc db :query-loading? true)
         :post-request
          {:uri "/query"
           ;; grabs the persisted constraints from the database?
           :params {:constraints (->query-constraints db)}
           :handler #(dispatch [::query-succeeded % group-name])}}))))


(reg-event-fx
  ::add-default-group?
  (fn add-default-group? [cofx [_ default-group]]
    (let [db (:db cofx)]
      (if-not (:current-group db)
        {:db (assoc db :current-group default-group)
         :dispatch [::query-started (:group-name default-group)]}
        db))))

;; the action of 'adding a new group' depends on a NAME and a set of CONSTRAINTS
;; so the "function" that does this needs to explicitly receive a NAME and CONSTRAINTS

;; EITHER:
;; [dispatch :add-group] (and then handler pulls NEW-NAME and CONSTRAINTS from db)
;; OR:
;; [dispatch :add-group new-name constraints] (and handler just does request)
;; WOULD BE FINE.


;(reg-event-fx
;  ::add-group
;  (fn [db [_ group-name constraints]]))


(declare toggle-save-group-popover-showing)

(declare save-current-group)


;; big picture:

;; when a query has succeeded,
;; we're receiving a new set of paintings
;; and MAYBE a new-group-name

;; new paintings should always be


;; maybe save the current-group first?
;; =



;; persist-state receives the db returned from this fn
;; assume we're calling it with a
(defn on-query-succeeded [db paintings group-name]
  (let [db-with-query-results
          (-> db (assoc :query-loading? false)
                 (assoc-in db/path:current-paintings paintings)
                 (assoc :mobile-search? false) ; switch back to paintings
                 (assoc :examining? false))]

    (do
      (log "on-query-succeeded: group-name: " group-name)
      (if group-name
        (-> db-with-query-results
            (toggle-save-group-popover-showing false) ;; hide the popover
            (save-current-group group-name))
        db-with-query-results))))


;; you persist the data whenever a query succeeds

(reg-event-fx
  ::query-succeeded
  interceptors
  (fn query-succeeded [cofx [_ paintings group-name]]
    (let [db (on-query-succeeded (:db cofx) paintings group-name)]
        {:persist-state db
         :db db})))

;; ------------------------------------------------------
;; Updating constraints
;; ------------------------------------------------------


;; handler's inner fns need to be separated out to produce a generic
;; 'works on any group' version

(reg-event-db
 ::update-selected-types
 interceptors
 (fn update-selected-types [db [_ selected-types]]
   ;(update-in db db/path:type-constraints conj selected-types)
   (assoc-in db db/path:type-constraints selected-types)))


(reg-event-db
 ::update-selected-schools
 interceptors
 (fn update-selected-schools [db [_ selected-schools]]
   (assoc-in db db/path:school-constraints selected-schools)))


(reg-event-db
 ::update-selected-timeframes
 interceptors
 (fn update-selected-timeframes [db [_ selected-timeframes]]
   (assoc-in db db/path:timeframe-constraints selected-timeframes)))


(defn update-selected-concepts [db selected-concept]
  (update-in db db/path:concept-constraints conj selected-concept))


(reg-event-db
 ::update-selected-concepts
 interceptors
 (fn [db [_ selected-concept]]
   (update-selected-concepts db selected-concept)))

(defn remove-selected-concept [db selected-concept]
  (update-in db db/path:concept-constraints disj selected-concept))

(reg-event-db
 ::remove-selected-concept
 interceptors
 (fn [db [_ selected-concept]]
   (remove-selected-concept db selected-concept)))

(reg-event-db
  ::toggle-concept-selection
  interceptors
  (fn toggle-concept-selection [db [_ concept]]
    (let [currently-selected-concepts (get-in db db/path:concept-constraints)]
      (if (contains? currently-selected-concepts concept)
        (remove-selected-concept db concept)
        (update-selected-concepts db concept)))))


(reg-event-db
 ::update-selected-artists
 interceptors
 (fn update-selected-artists [db [_ selected-artist]]
   (update-in db db/path:artist-constraints conj selected-artist)))


(reg-event-db
 ::remove-selected-artist
 interceptors
 (fn remove-selected-artist [db [_ selected-artist]]
   (update-in db db/path:artist-constraints disj selected-artist)))


(reg-event-db
 ::selections-cleared
 interceptors
 (fn selections-cleared [db _]
  (-> db
     (assoc-in db/path:type-constraints #{})
     (assoc-in db/path:school-constraints #{})
     (assoc-in db/path:timeframe-constraints #{})
     (assoc-in db/path:concept-constraints #{})
     (assoc-in db/path:artist-constraints #{}))))


;; ------------------------------------------------------
;; Updating groups
;; ------------------------------------------------------

(defn toggle-save-group-popover-showing [db showing?]
  (assoc db :show-group-name-prompt? showing?))

(reg-event-db
  ::hide-save-group-popover
  interceptors
  (fn hide-save-group-popover [db _]
    (toggle-save-group-popover-showing db false)))

(reg-event-db
  ::show-save-group-popover
  interceptors
  (fn show-save-group-popover [db _]
    (toggle-save-group-popover-showing db true)))


;; override :current-group's name with the provided group-name
;; when we 'save the current group',
;; we use the user-provided name;
(defn save-current-group [db group-name]
  {:pre [(string? group-name)]} ;; group-name's should always be strings

  (let [current-group (:current-group db)
        updated-group (assoc current-group :group-name group-name)
        x (-> db
            (assoc-in [:saved-groups group-name] updated-group)
            (assoc :current-group updated-group))]
    (do
      (log "save-current-group CALLED")
      (log "(keys (:saved-groups db): " (keys (:saved-groups x)))
      (log ":type-constraints of (:saved-groups db): " (map #(-> % second :type-constraints) (:saved-groups x)))
      x)))


;; if we delete current group, then switch to some other group, what happens?
;; ah, we just bring in the new group, we don't make any changes etc.
(defn bring-in-group [db group-name]
  {:pre [(string? group-name)]}
  (let [new-current-group (get (:saved-groups db) group-name)
        new-db (assoc db :current-group new-current-group)]

    (do
     (log "bring-in-group CALLED: " group-name)
     (log "bring-in-group group-name: " group-name)
     (log "(keys (:saved-groups db): " (keys (:saved-groups new-db)))
     (log ":type-constraints of (:saved-groups db): " (map #(-> % second :type-constraints) (:saved-groups new-db)))
     (log "bring-in-group new-current-group: " new-current-group)
     new-db)))


(reg-event-db
 ::switch-groups
 interceptors
 (fn switch-groups [db [_ destination-group-name]]
   (-> db
     (bring-in-group destination-group-name))))

;; ------------------------------------------------------
;; Comparing groups
;; ------------------------------------------------------


;; just returns (potentially updated) vector of names
(defn add-compare-group-name [group-names group-name]
  {:pre [(string? group-name)]
         ;(list? group-names)] ;; group-names is a list, why is this failing?!
   :post [(s/valid? (s/coll-of string?) %)
          (>= 2 (count %))]}
  (let [already-comparing? (boolean (some #{group-name} group-names))
        already-full? (boolean (= 2 (count group-names)))]

    (do
      (log "already-comparing?: " already-comparing?)
      (log "already-full?: " already-full?)
      (cond
        already-comparing? group-names
        ;; group-names MUST BE A LIST, not a vector,
        ;; we want to prepend the group-name
        already-full? (conj (drop-last group-names) group-name)
        :else (conj group-names group-name)))))


(reg-event-db
  ::add-compare-group-name
  interceptors
  (fn add-compare-group [db [_ group-name]]
    {:pre [(string? group-name)]}
    (let [group-names (:compared-group-names db)]
      (do
        (log "add-compare-group-name called")
        (assoc
          db
          :compared-group-names
          (add-compare-group-name group-names group-name))))))


(>defn remove-compare-group-name [db group-name]
  [::specs/app-db string? => ::specs/app-db]
  (assoc
    db
    :compared-group-names
    (remove #{group-name} (:compared-group-names db))))

(reg-event-db
  ::remove-compare-group-name
  interceptors
  (fn remove-compare-group-name-handler [db [_ group-name]]
    (do
      (log "remove-compare-group-name-handler called; group-name: " group-name)
      (remove-compare-group-name db group-name))))
      ;(assoc
      ;  db
      ;  :compared-group-names
      ;  (remove #{group-name} (:compared-group-names db))))))


(reg-event-db
  ::comparisons-cleared
  interceptors
  (fn comparisons-cleared [db _]
     (assoc db :compared-group-names '())))


;; given a db and group-name,
;; returns a db with that group removed

;; also remove it from compared-group-names
(>defn remove-group! [db group-name]
  [::specs/app-db string? => ::specs/app-db]
  (let [old-saved-groups (:saved-groups db)
        updated-saved-groups (dissoc old-saved-groups group-name)]
    (do
      (log "remove-group!: group-name: " group-name)
      (log "remove-group!: old-saved-groups: " old-saved-groups)
      (log "remove-group!: updated-saved-groups: " updated-saved-groups)
      (assoc db :saved-groups updated-saved-groups))))


(reg-event-fx
  ::remove-group
  interceptors
  (fn remove-group-handler [cofx [_ group-name]]
    (let [db (:db cofx)
          updated-db (remove-compare-group-name
                       (remove-group! db group-name)
                       group-name)]
      {:db updated-db
       :persist-state updated-db}))) ;;



;(reg-event-fx
;  ::query-succeeded
;  interceptors
;  (fn query-succeeded [cofx [_ paintings group-name]]
;    (let [db (on-query-succeeded (:db cofx) paintings group-name)]
;      {:persist-state db
;       :db db})))

;; ------------------------------------------------------
;; Examining a single painting
;; ------------------------------------------------------

(reg-event-db
  ::done-button-clicked
  interceptors
  (fn done-button-clicked [db _]
    (-> db
      (assoc :examining? false)
      (assoc :show-slideshow? false))))


(reg-event-db
  ::painting-tile-clicked
  interceptors
  (fn painting-tile-clicked [db [_ painting]]
    (-> db
      (assoc :current-painting painting)
      (assoc :show-slideshow? true))))

(reg-event-db
  ::toggle-slideshow
  interceptors
  (fn toggle-slidehow [db _]
    (update db :show-slideshow? not)))


(reg-event-db
  ::toggle-image-zoomed
  interceptors
  (fn toggle-image-zoomed [db _]
    (update db :image-zoomed? not)))

;; ------------------------------------------------------
;; Slidesow
;; ------------------------------------------------------

(>defn previous-slide [db]
  [::specs/app-db => ::specs/app-db]
  (let [paintings (helpers/sort-by-author
                    (get-in db db/path:current-paintings))
        current-painting (:current-painting db)
        prev-slide (or (last (take-while #(not= % current-painting) paintings))
                     (last paintings))]
    (assoc db :current-painting prev-slide)))

(reg-event-db
  ::go-to-previous-slide
  (fn [db _] (previous-slide db)))


(>defn next-slide [db]
  [::specs/app-db => ::specs/app-db]
  (let [paintings (helpers/sort-by-author
                    (get-in db db/path:current-paintings))
        current-painting (:current-painting db)
        next-slide (or (second (drop-while #(not= % current-painting) paintings))
                     (first paintings))]
    (assoc db :current-painting next-slide)))

(reg-event-db
  ::go-to-next-slide
  interceptors
  (fn [db _] (next-slide db)))

;#_(reg-event-db
;    ::go-to-next-slide
;    (fn next-slide [db]
;      (let [paintings (helpers/sort-by-author
;                        (get-in db db/path:current-paintings))
;            current-painting (:current-painting db)
;            next-slide (or (second (drop-while #(not= % current-painting) paintings))
;                         (first paintings))]
;        (do
;          (log "next-slide: " next-slide)
;          (assoc db :current-painting next-slide)))))


;; don't need interceptors per se
;; just gw-spec an event handler,
;; then turn on g/check in the namespace


;; fails, and can see why in js console :-)
;(>defn addition [a b]
;  [pos-int? pos-int? => int? | #(> % a) #(> % b)]
;  (- a b))


;; passes and can see in js console :-)
;(>defn addition [a b]
;  [pos-int? pos-int? => int? | #(> % a) #(> % b)]
;  (+ a b))

;; make sure you have the following in cljs :compiler options
;:external-config {:ghostwheel {:check     true
;                               :outstrument true
;                               :num-tests 10}}
;(g/check)
(check)