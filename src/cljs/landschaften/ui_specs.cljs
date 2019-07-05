(ns landschaften.ui-specs
  (:require [cljs.spec.alpha :as s]))

;; UI-Specs:
;; - represent UI-related constraints
;; - do not represent a problem-domain concept per se

(def MODES
  #{:explore :compare})

(s/def ::mode #(contains? MODES %))

