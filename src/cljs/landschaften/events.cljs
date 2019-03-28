(ns landschaften.events
  (:require [re-frame.core :refer [after dispatch reg-event-db reg-sub reg-event-fx reg-fx]]
            [landschaften.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [ajax.core :refer [POST GET]]
            [landschaften.ui-specs :as ui-specs]
            [landschaften.specs :as specs]
            [landschaften.helpers :as helpers]
            [cljs.spec.alpha :as s]
            [landschaften.views.utils :as utils]
            [re-frame.core :as rf]
            [cljs.spec.test.alpha :as st]
            [ghostwheel.core
              :as g
              :refer [check >defn >defn- >fdef => | <- ?]]))


;; ------------------------------------------------------
;; Interceptors
;; ------------------------------------------------------


;; persist via local storage
;(def ->local-store (after todos->local-store))

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


(reg-event-db
 ::initialize-db
 interceptors
 (fn initialize-db [_ _]
   db/default-db))


(reg-event-db
  ::mode-changed
  (fn mode-changed [db [_ new-mode]]
    {:pre [(s/valid? ::ui-specs/mode new-mode)]}
    (assoc db :current-mode new-mode)))


(reg-event-db
  :navigate
  (fn [db [_ route]]
    (assoc db :route route)))


(reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))


;; putting in and pulling from local storage
;;

;(def ls-auth-key "session-info")
;
;
;(rf/reg-fx
;  :create-session
;  (fn login-localStorage [email]
;    (.setItem js/localStorage ls-auth-key (str email))))
;
;(rf/reg-cofx
;  :user-session
;  (fn user-session [cofx _]
;    (assoc cofx :user-session (some->> (.getItem js/localStorage ls-auth-key)))))
;
;
;;; NOTE:
;;; Since we use email-address strings in localStorage to
;;; indicate an active session, and '@' is not valid Clojure,
;;; we don't use (cljs.reader/read-string <localStorage content>).
;(defn ls->cljs [a-str]
;  (if (= "false" a-str) false a-str))
;
;(reg-event-fx
;  ::retrieve-user-session
;  [(rf/inject-cofx :user-session)]
;  (fn retrieve-user-session [cofx [_ _]]
;    (let [db (:db cofx)
;          session (:user-session cofx)]
;       {:db (assoc db :session (ls->cljs session))})))
;
;
;(rf/reg-event-fx
;  ::resume-session
;  (fn resume-session [cofx [_ session-email]]
;    {:db (-> (:db cofx))
;             ;(assoc :logged-in? true :email session-email))
;             ;(goto :home))
;     :dispatch [::pull-decks]}))
;
;
;(rf/reg-fx
;  :end-session
;  (fn end-session [_]
;    (.setItem js/localStorage ls-auth-key false)))

;;

;(defn logout-app-db [db]
;  (assoc db :session false :logged-in? false))

;(rf/reg-event-fx
;  ::logout
;  (fn logout [cofx [_]]
;    {:db (-> (:db cofx)
;             (logout-app-db)
;             (goto :auth))
;     :end-session nil}))

;; ------------------------------------------------------
;; Communicating with server
;; ------------------------------------------------------


;; not real error handling...
(defn default-error-handler [response]
  (utils/log "Encountered unexpected error: " response))


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


(reg-event-fx
  ::query-started
  (fn query [cofx [_ group-name]]
    (let [db (:db cofx)]
      {:db (assoc db :query-loading? true)
       :post-request
        {:uri "/query"
         :params {:constraints (->query-constraints db)}
         :handler #(dispatch [::query-succeeded % group-name])}})))


(declare toggle-save-group-popover-showing)
(declare save-current-group)


(reg-event-db
  ::query-succeeded
  interceptors
  (fn query-succeeded [db [_ paintings group-name]]
    (let [db-with-query-results (-> db
                                    (assoc :query-loading? false)
                                    (assoc-in db/path:current-paintings paintings)
                                    (assoc :examining? false))]
      (if group-name
        (-> db-with-query-results
          (toggle-save-group-popover-showing false) ;; hide the popover
          (save-current-group group-name))
        db-with-query-results))))


;; ------------------------------------------------------
;; Updating constraints
;; ------------------------------------------------------


;; handler's inner fns need to be separated out to produce a generic
;; 'works on any group' version

(reg-event-db
 ::update-selected-types
 interceptors
 (fn update-selected-types [db [_ selected-types]]
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


(reg-event-db
 ::update-selected-concepts
 interceptors
 (fn update-selected-concepts [db [_ selected-concept]]
   (update-in db db/path:concept-constraints conj selected-concept)))


(reg-event-db
 ::remove-selected-concept
 interceptors
 (fn remove-selected-concept [db [_ selected-concept]]
   (update-in db db/path:concept-constraints disj selected-concept)))


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
      (utils/log "save-current-group: returning x: " x)
      x)))

;; how to test this whole flow?
;; this entire thing is basically test free -- ugh.

;;

;; someone wants to save group g

;; either g already exists and so g is being edited
;; or g is new and so can be
;; ;;
;; any time g is added to saved groups,
;; we must query with current constraints,
;; so that g in gs contains updated paintings


;; CUT BACK SCOPE: always query when group is saved
;; (can later check, "have constraints changed since we queried?")
;; so, this should dispatch (::query-started
;(reg-event-db
; ::group-saved
; (fn-traced group-saved [db [_ group-name]]
;   (do
;     (utils/log "::group-saved group-name: " group-name)
;     (-> db
;       (toggle-save-group-popover-showing false) ;; hide the popover
;       (save-current-group group-name)))))


(defn bring-in-group [db group-name]
  {:pre [(string? group-name)]}
  (let [new-current-group (get (:saved-groups db) group-name)
        new-db (assoc db :current-group new-current-group)]

    (do
     (utils/log "bring-in-group group-name: " group-name)
     (utils/log "bring-in-group new-db: " new-db)
     (utils/log "bring-in-group new-current-group: " new-current-group)
     new-db)))


;; when we switch groups,
;; prompt user (via dialogue) for name of group;
;; (if group already had name, then prefill the input slot with that name)


;; CUT BACK SCOPE: when switching to g2, throwaway current group
;; (can later add modal dialogue, "Do you want to save current group?" etc.)
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
    (cond
      already-comparing? group-names
      ;; group-names MUST BE A LIST, not a vector,
      ;; we want to prepend the group-name
      already-full? (conj (drop-last group-names) group-name)
      :else (conj group-names group-name))))


(reg-event-db
  ::add-compare-group-name
  interceptors
  (fn add-compare-group [db [_ group-name]]
    {:pre [(string? group-name)]}
    (let [group-names (:compared-group-names db)]
      (assoc
        db
        :compared-group-names
        (add-compare-group-name group-names group-name)))))


(reg-event-db
  ::remove-compare-group-name
  interceptors
  (fn remove-compare-group-name [db [_ group-name]]
    (assoc
      db
      :compared-group-names
      (remove #{group-name} (:compared-group-names db)))))


(reg-event-db
  ::comparisons-cleared
  interceptors
  (fn comparisons-cleared [db _]
     (assoc db :compared-group-names '())))


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


;(reg-event-db
;  ::show-slideshow
;  interceptors
;  (fn show-max-image [db _]
;    (assoc db :show-slideshow? true)))

;(reg-event-db
;  ::hide-slideshow
;  interceptors
;  (fn hide-max-image [db _]
;    (assoc db :show-slideshow? false)))


(reg-event-db
  ::toggle-slideshow
  interceptors
  (fn toggle-slidehow [db _]
    (update db :show-slideshow? not)))


(reg-event-db
  ::toggle-image-zoomed
  interceptors
  (fn toggle-image-zoomed [db _]
    (update db ::db/image-zoomed? not)))

;; ------------------------------------------------------
;; Slidesow
;; ------------------------------------------------------


;; prev vs next slide should look at current painting
;; and move forward or backward in the list

;(defn painting-index [painting]
;  {:pre [(s/valid? ::specs/painting painting)]
;   :post [(int)]})

;; (take-while (not= x current-painting) xs)
;; ^^^ will take paintings up until we encounter the current paintng

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



;(defn good-db? [db]
;  (s/valid? ::specs/app-db db))

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
;          (utils/log "next-slide: " next-slide)
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