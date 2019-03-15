(ns landschaften.events
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub reg-event-fx reg-fx dispatch]]
            [landschaften.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [ajax.core :refer [POST GET]]
            [landschaften.ui-specs :as ui-specs]
            [landschaften.helpers :as helpers]
            [cljs.spec.alpha :as s]))


(def log js/console.log)

;; ------------------------------------------------------
;; High level events
;; ------------------------------------------------------


(reg-event-db
 ::initialize-db
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



;; ------------------------------------------------------
;; Communicating with server
;; ------------------------------------------------------


;; taken from cardy
(defn default-error-handler [response]
  (js/console.log "Encountered unexpected error: " response))


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
  (fn concepts-retrieved [db [_ artists]]
    (assoc db :all-concepts (into #{} artists))))


(reg-event-db
  ::artists-names-retrieved
  (fn artists-names-retrieved [db [_ artists]]
    (assoc db :all-artists (into #{} artists))))


(defn ->query-constraints
  "Put group's constraints in backend API's expected format."
  [db]
  ;{:pre [(s/valid? ::specs/group group)]}
  (remove
   #(empty? (:values %))
   #{{:column "type" :values (into [] (get-in db db/path:type-constraints))}
     {:column "school" :values (into [] (get-in db db/path:school-constraints))}
     {:column "timeframe" :values (into [] (get-in db db/path:timeframe-constraints))}
     {:column "author" :values (into [] (get-in db db/path:artist-constraints))}
     {:column "name" :values (into [] (get-in db db/path:concept-constraints))}}))


;; will sometimes be used when saving a group


;; "saving a group" =
;; 1. updating current-group's paintings
;; 2. adding updated current-group to saved-groups
(reg-event-fx
  ::query-started
  (fn query [cofx [_ group-name]]
    (let [db (:db cofx)]
          ;constraints (if-let [group (:current-group db)]
          ;              (->query-constraints group)
          ;              #{})] ;; if no group, then no constraints
      {:db (assoc db :query-loading true)
       :post-request
        {:uri "/query"
         :params {:constraints (->query-constraints db)}
         ;:params {:constraints constraints}
         :handler #(dispatch [::query-succeeded % group-name])}})))


(declare toggle-save-group-popover-showing)
(declare save-current-group)

(reg-event-db
  ::query-succeeded
  ;(fn query-succeeded [db [_ paintings]]
  (fn query-succeeded [db [_ paintings group-name]]
    (let [db-with-query-results (-> db
                                    (assoc :query-loading false)
                                    (assoc-in db/path:current-paintings paintings)
                                    (assoc :current-painting nil)
                                    (assoc ::db/slideshow-paintings paintings))]

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
 (fn-traced update-selected-types [db [_ selected-types]]
   (assoc-in db db/path:type-constraints selected-types)))


(reg-event-db
 ::update-selected-schools
 (fn update-selected-schools [db [_ selected-schools]]
   (assoc-in db db/path:school-constraints selected-schools)))


(reg-event-db
 ::update-selected-timeframes
 (fn update-selected-timeframes [db [_ selected-timeframes]]
   (assoc-in db db/path:timeframe-constraints selected-timeframes)))


(reg-event-db
 ::update-selected-concepts
 (fn update-selected-concepts [db [_ selected-concept]]
   (update-in db db/path:concept-constraints conj selected-concept)))


(reg-event-db
 ::remove-selected-concept
 (fn remove-selected-concept [db [_ selected-concept]]
   (update-in db db/path:concept-constraints disj selected-concept)))


(reg-event-db
 ::update-selected-artists
 (fn update-selected-artists [db [_ selected-artist]]
   (update-in db db/path:artist-constraints conj selected-artist)))


(reg-event-db
 ::remove-selected-artist
 (fn remove-selected-artist [db [_ selected-artist]]
   (update-in db db/path:artist-constraints disj selected-artist)))


(reg-event-db
 ::selections-cleared
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
  (fn hide-save-group-popover [db _]
    (toggle-save-group-popover-showing db false)))

(reg-event-db
  ::show-save-group-popover
  (fn show-save-group-popover [db _]
    (toggle-save-group-popover-showing db true)))


;; override :current-group's name with the provided group-name
;; when we 'save the current group',
;; we use the user-provided name;
(defn save-current-group [db group-name]
  {:pre [(string? group-name)]} ;; group-name's should always be strings

  (let [current-group (:current-group db)
        updated-group (assoc current-group :group-name group-name)
        ;x (assoc-in db [:saved-groups group-name] updated-group)]
        x (-> db
            (assoc-in [:saved-groups group-name] updated-group)
            (assoc :current-group updated-group))]
    (do
      (log "save-current-group: returning x: " x)
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
;     (log "::group-saved group-name: " group-name)
;     (-> db
;       (toggle-save-group-popover-showing false) ;; hide the popover
;       (save-current-group group-name)))))


(defn bring-in-group [db group-name]
  {:pre [(string? group-name)]}
  (let [new-current-group (get (:saved-groups db) group-name)
        new-db (assoc db :current-group new-current-group)]

    (do
     (js/console.log "bring-in-group group-name: " group-name)
     (js/console.log "bring-in-group new-db: " new-db)
     (js/console.log "bring-in-group new-current-group: " new-current-group)
     new-db)))


;; when we switch groups,
;; prompt user (via dialogue) for name of group;
;; (if group already had name, then prefill the input slot with that name)


;; CUT BACK SCOPE: when switching to g2, throwaway current group
;; (can later add modal dialogue, "Do you want to save current group?" etc.)
(reg-event-db
 ::switch-groups
 (fn switch-groups [db [_ destination-group-name]]
   (do
    (js/console.log "destination-group-name: " destination-group-name)
    (-> db
       ;(save-current-group) ;; add current group to group-history i.e. :other-groups
       ; then take destination group and make current group
       (bring-in-group destination-group-name)))))



;; ------------------------------------------------------
;; Comparing groups
;; ------------------------------------------------------


(reg-event-db
  ::add-compare-group-name
  (fn add-compare-group [db [_ group-name]]
    {:pre [(string? group-name)]}
    (let [y (:compared-group-names db)
          x (update db :compared-group-names conj group-name)]
      (do
        (log "add-compare-group :compared-group-names was " y)
        (log "add-compare-group :compared-group-names is now " (:compared-group-names x))
        x))))

(reg-event-db
  ::remove-compare-group-name
  (fn remove-compare-group-name [db [_ group-name]]
    (do
      (log "remove-compare-group-name called")
      (update db :compared-group-names disj group-name))))

(reg-event-db
  ::comparisons-cleared
  (fn comparisons-cleared [db _]
    (do
      (log "comparisons-cleared called")
      (assoc db :compared-group-names #{}))))


;; ------------------------------------------------------
;; Examining a single painting
;; ------------------------------------------------------


;; when examine's done button is clicked,
;; we no longer have a 'current painting' that we're examining
(reg-event-db
  ::done-button-clicked
  (fn done-button-clicked [db _]
    (-> db
        ;(assoc :current-painting nil))))
      (assoc :examining? false)
      (assoc :show-max? false))))
        ;(assoc :))))


;; now, when painting tile clicked,
(reg-event-db
  ::painting-tile-clicked
  (fn painting-tile-clicked [db [_ painting]]
    (do
      (log "painting-tile-clicked handler called")
      (log "painting-tile-clicked handler painting: " painting)
      (-> db
        (assoc :current-painting painting)
        (assoc :show-max? true)))))


(reg-event-db
  ::show-max-image
  (fn show-max-image [db _]
    (assoc db :show-max? true)))


(reg-event-db
  ::hide-max-image
  (fn hide-max-image [db _]
    (assoc db :show-max? false)))


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

(reg-event-db
  ::go-to-previous-slide
  (fn previous-slide [db]
    (let [paintings (helpers/sort-by-author
                      (get-in db db/path:current-paintings))
          current-painting (:current-painting db)
          prev-slide (last (take-while #(not= % current-painting) paintings))]
      (do
        (log "prev-slide: " prev-slide)
        (assoc db :current-painting prev-slide)))))

(reg-event-db
  ::go-to-next-slide
  (fn next-slide [db]
    (let [paintings (helpers/sort-by-author
                      (get-in db db/path:current-paintings))
          current-painting (:current-painting db)
          next-slide (second (drop-while #(not= % current-painting) paintings))]
      (do
        (log "next-slide: " next-slide)
        (assoc db :current-painting next-slide)))))

(reg-event-db
  ::go-to-details
  (fn go-to-details [db [_ painting]]
    (-> db
        (assoc :current-painting painting)
        (assoc :examining? true)
        (assoc :show-max? false))))

;; slideshow
;(reg-event-db)