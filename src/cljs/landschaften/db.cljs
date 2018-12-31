(ns landschaften.db
  (:require [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.sample :as sample]))

;; too few paintings to be interesting
(def excluded-timeframes
  #{"0801-0850" "1051-1100" "1101-1150" "1151-1200" "1201-1250" "1251-1300" "1301-1350" "1351-1400"})

(def excluded-schools
  #{"Bohemian" "Catalan" "Finnish" "Greek" "Irish" "Norwegian" "Other" "Polish" "Portuguese" "Swedish"})

(def default-db
 {:paintings sample/sample-paintings
  :default-painting (first sample/sample-paintings)
  :current-painting nil
  :types (disj specs/PAINTING-TYPES "Other")
  :schools (apply disj specs/SCHOOLS excluded-schools)
  :timeframes (apply disj specs/TIMEFRAMES excluded-timeframes)
  :concepts sample/sample-concepts
  :selected-types #{}
  :selected-schools #{}
  :selected-timeframes #{}
  :selected-concepts #{}
  :query-loading false})
