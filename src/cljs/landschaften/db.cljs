(ns landschaften.db
  (:require [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.sample :as sample]))

;; (declare x) is not respected;
;; cljs will be compiled with x as nil
(def default-db
 {:paintings sample/sample-paintings
  :types specs/PAINTING-TYPES
  :schools specs/SCHOOLS
  :timeframes specs/TIMEFRAMES
  :concepts sample/sample-concepts
  :selected-types #{}
  :selected-schools #{}
  :selected-timeframes #{}
  :selected-concepts #{}})
