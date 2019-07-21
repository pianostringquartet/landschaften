(ns landschaften.view-specs
  (:require [cljs.spec.alpha :as s]))

;; UI-Specs:
;; - represent UI-related constraints
;; - do not represent a problem-domain concept per se

(def MODES
  #{:explore :compare})

(s/def ::mode #(contains? MODES %))

(def ACCORDION-CONSTRAINTS
  #{"genre constraints" "timeframe constraints" "school constraints"})

(s/def ::accordion-constraints #(contains? ACCORDION-CONSTRAINTS %))
