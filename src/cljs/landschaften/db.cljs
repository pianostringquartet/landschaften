(ns landschaften.db
  (:require [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.sample :as sample]))

;; too few paintings to be interesting
(def excluded-timeframes
  #{"0801-0850" "1051-1100" "1101-1150" "1151-1200" "1201-1250" "1251-1300" "1301-1350" "1351-1400"})

(def excluded-schools
  #{"Bohemian" "Catalan" "Finnish" "Greek" "Irish" "Norwegian" "Other" "Polish" "Portuguese" "Swedish" "Hungarian" "Scottish" "Swiss" "Danish" "Austrian"})

;; use if no groups yet
(def empty-group
  {:group-name ""
   :paintings #{}
   :types #{}
   :schools #{}
   :timeframes #{}
   :concepts #{}
   :artists #{}})

(def default-db
 {;; HIGH LEVEL
  :current-mode     :explore

  ;; EXAMINE
  :current-painting nil
  :show-max?        false

  ;; EXPLORE
  :query-loading    false
  :all-types        (disj specs/PAINTING-TYPES "Other")
  :all-schools      (apply disj specs/SCHOOLS excluded-schools)
  :all-timeframes   (apply disj specs/TIMEFRAMES excluded-timeframes)
  :all-concepts     #{} ; retrieve from backend
  :all-artists      #{} ; retrieve from backend

  ;; EXPLORE & COMPARE
  :current-group    empty-group ; :current-group sample/sample-group

  ;; COMPARE
  ;; map of {:group-name ::group}
  ;:saved-groups     {(:group-name sample/sample-group-2) sample/sample-group-2}})
  :saved-groups     {}})
