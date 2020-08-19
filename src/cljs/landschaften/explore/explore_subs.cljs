(ns landschaften.explore.explore-subs
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub]]
            [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]
            [landschaften.helpers :as helpers]))

; TODO: Allow user to choose window size
(def WINDOW-SIZE 15)

(reg-sub
  ::mobile-search?
  (fn mobile-search? [db] ; show 'search' controls vs 'results' paintings on mobile
    (:mobile-search? db)))

(reg-sub
  ::active-accordion-constraint
  (fn active-accordion-constraint [db]
    (:active-accordion-constraint db)))


;; ------------------------------------------------------
;; Constraint choices
;; ------------------------------------------------------


(reg-sub
  ::all-types
  (fn all-types [db _]
    (:all-types db)))


(reg-sub
  ::all-schools
  (fn all-schools [db _]
    (:all-schools db)))


(reg-sub
  ::all-timeframes
  (fn all-timeframes [db _]
    (:all-timeframes db)))


(reg-sub
  ::all-concepts
  (fn all-concepts [db _]
    (:all-concepts db)))


(reg-sub
  ::all-artists
  (fn all-artists [db _]
    (:all-artists db)))


;; ------------------------------------------------------
;; Currently selected constraints
;; ------------------------------------------------------


(>defn selected-genres-sub [db _]
  [any? any? => ::specs/genre-constraints]
  (:selected-genres db))

(reg-sub ::types selected-genres-sub)

(>defn selected-schools-sub [db _]
  [any? any? => ::specs/school-constraints]
  (:selected-schools db))

(reg-sub ::school-constraints selected-schools-sub)

(>defn selected-timeframes-sub [db _]
  [any? any? => ::specs/timeframe-constraints]
  (:selected-timeframes db))

(reg-sub ::timeframe-constraints selected-timeframes-sub)

(reg-sub
  ::concept-constraints
  (fn concepts [db _]
    {:post [(s/valid? ::specs/concept-constraints %)]}
    (:selected-concepts db)))


(reg-sub
  ::artist-constraints
  (fn artists [db _]
    {:post [(s/valid? ::specs/artist-constraints %)]}
    (:selected-artists db)))


;;; ------------------------------------------------------
;;; Groups (selected constraints, retrieved paintings)
;;; ------------------------------------------------------

(reg-sub
  ::save-group-popover-showing?
  (fn save-group-popover-showing? [db _]
    (:show-group-name-prompt? db)))


(reg-sub
  ::group-name
  (fn group-name [db _]
    (:current-group-name db)))


;; ALL paintings
(reg-sub
  ::paintings
  (fn paintings [db _]
    ;; use if-let instead?
    (let [current-paintings (:paintings db)]
      (if current-paintings
        (helpers/sort-by-author current-paintings)
        []))))


(defn get-painting-windows [db]
  (let [paintings (:paintings db)]
    (when (some? paintings)
      (partition-all WINDOW-SIZE paintings))))


(>defn get-current-painting-window [paintings current-window-index]
  [(s/nilable ::specs/paintings) int? => (s/nilable ::specs/paintings)]
  (when (some? paintings)
    (nth (partition-all WINDOW-SIZE paintings)
         current-window-index)))


(reg-sub
  ::current-painting-window
  (fn current-painting-window-sub-handler [db]
    (get-current-painting-window (:paintings db) (get db :current-painting-window-index 0))))


;; e.g. "Showing paintings 100-125 of 500 total paintings"
(>defn current-painting-window-shows [db]
  [any? => string?]
  (let [current-window-index     (get db :current-painting-window-index 0)
        current-window-count     (count (get-current-painting-window (:paintings db) current-window-index))
        previous-paintings-count (* WINDOW-SIZE current-window-index)
        window-starts-at         (inc previous-paintings-count)
        window-ends-at           (+ current-window-count previous-paintings-count)]
    (str window-starts-at "-" window-ends-at)))


(reg-sub
  ::current-painting-window-shows
  (fn [db _]
    (current-painting-window-shows db)))


;; ONLY FOR DECIDING TO SHOW UI BUTTONS
(>defn use-painting-windows? [db]
  [any? => boolean?]
  (<= 2 (count (get-painting-windows db))))


(reg-sub
  ::show-painting-windows?
  use-painting-windows?)


(reg-sub
  ::painting-ids
  (fn paintings [db _]
    (let [current-painting-ids (:painting-ids db)]
      (if current-painting-ids
        current-painting-ids
        []))))


(>defn concept-frequencies-sub-handler [db]
  [any? => ::specs/concept-frequencies]
  (:concept-frequencies db))


(reg-sub
  ::concept-frequencies
  (fn concept-frequencies [db _]
    (concept-frequencies-sub-handler db)))


(reg-sub
  ::query-loading?
  (fn query-loading? [db _]
    (:query-loading? db)))


;; ------------------------------------------------------
;; Examine
;; ------------------------------------------------------

(reg-sub
  ::current-painting
  (fn current-painting [db _]
    {:post [(s/valid? (s/nilable ::specs/painting) %)]}
    (:current-painting db)))


(reg-sub
  ::show-painting-modal?
  (fn show-slideshow? [db _]
    (:show-painting-modal? db)))


(reg-sub
  ::image-zoomed?
  (fn image-zoomed? [db _]
    (:image-zoomed? db)))