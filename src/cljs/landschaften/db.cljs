(ns landschaften.db
  (:require [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]))

(def excluded-timeframes
  #{"0801-0850" "1051-1100" "1101-1150" "1151-1200" "1201-1250" "1251-1300" "1301-1350" "1351-1400"})

(def excluded-schools
  #{"Bohemian" "Catalan" "Finnish" "Greek" "Irish" "Norwegian" "Other" "Polish" "Portuguese" "Swedish" "Hungarian" "Scottish" "Swiss" "Danish" "Austrian"})

;; use if no groups yet
(def empty-group
  {:group-name ""
   :paintings #{}
   :type-constraints #{}
   :school-constraints #{}
   :timeframe-constraints #{}
   :concept-constraints #{}
   :artist-constraints #{}})

(def default-db
 {;; HIGH LEVEL
  :current-mode            :explore

  ;; EXAMINE
  :current-painting        nil
  :show-max?               false

  ;; EXPLORE
  :query-loading           false
  :all-types               (disj specs/PAINTING-TYPES "Other")
  :all-schools             (apply disj specs/SCHOOLS excluded-schools)
  :all-timeframes          (apply disj specs/TIMEFRAMES excluded-timeframes)
  :all-concepts            #{} ; retrieve from backend
  :all-artists             #{} ; retrieve from backend

  ;; EXPLORE & COMPARE
  :current-group           empty-group ; :current-group sample/sample-group
  :show-group-name-prompt? false

  ;; COMPARE
  ;; map of {:group-name ::group}
  ;:saved-groups     {(:group-name sample/sample-group-2) sample/sample-group-2}})
  :saved-groups            {}})



;; PATHS: abstractions over db-locations
(def path:current-group [:current-group])

(def path:current-paintings (conj path:current-group :paintings))
(def path:current-group-name (conj path:current-group :group-name))

;; Paths to current group's constraints
(def path:type-constraints (conj path:current-group :type-constraints))
(def path:school-constraints (conj path:current-group :school-constraints))
(def path:timeframe-constraints (conj path:current-group :timeframe-constraints))
(def path:concept-constraints (conj path:current-group :concept-constraints))
(def path:artist-constraints (conj path:current-group :artist-constraints))
