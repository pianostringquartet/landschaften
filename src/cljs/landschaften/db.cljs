(ns landschaften.db
  (:require [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.sample :as sample]))

(def default-db
 {:paintings sample/sample-paintings
  :default-painting (first sample/sample-paintings)
  :current-painting nil
  :types (disj specs/PAINTING-TYPES "Other")
  :schools (disj specs/SCHOOLS "Other")
  :timeframes (disj specs/TIMEFRAMES "0801-0850" "1051-1100")
  :concepts sample/sample-concepts
  :selected-types #{}
  :selected-schools #{}
  :selected-timeframes #{}
  :selected-concepts #{}
  :query-loading false})
