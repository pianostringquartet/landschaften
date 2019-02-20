(ns landschaften.db
  (:require [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.sample :as sample]))

;; too few paintings to be interesting
(def excluded-timeframes
  #{"0801-0850" "1051-1100" "1101-1150" "1151-1200" "1201-1250" "1251-1300" "1301-1350" "1351-1400"})

(def excluded-schools
  #{"Bohemian" "Catalan" "Finnish" "Greek" "Irish" "Norwegian" "Other" "Polish" "Portuguese" "Swedish" "Hungarian" "Scottish" "Swiss" "Danish" "Austrian"})

;; keep state flat (ie no :current-gtoup), and have only :previous-group <list ::group>?
;; wouldn't need to change logic,
;; if group should be saved, then do so explicitly and force user to give name to group

(def default-db
 {:paintings sample/sample-paintings
  :default-painting (first sample/sample-paintings)
  :current-painting nil
  :current-group sample/sample-group
  :query-loading false
  :show-max? false
  :all-types (disj specs/PAINTING-TYPES "Other")
  :all-schools (apply disj specs/SCHOOLS excluded-schools)
  :all-timeframes (apply disj specs/TIMEFRAMES excluded-timeframes)
  :all-concepts sample/sample-concepts
  :all-artists #{(:author (first sample/sample-paintings))}
  ; :types #{}
  ; :schools #{}
  ; :timeframes #{}
  ; :concepts #{}
  ; :artists #{}
;; collection of other ::groups, indexed by group-name
;; start with fake sample group
  :saved-groups {(:group-name sample/sample-group-2) sample/sample-group-2}})
