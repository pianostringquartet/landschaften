(ns landschaften.ui-specs
  (:require [cljs.spec.alpha :as s]))

;; UI-Specs:
;; - represent UI-related constraints
;; - do not represent a coherent problem-domain concept


;; Major 'modes' app can be in
(def MODES
  #{:explore :compare})

(s/def ::mode #(contains? MODES %))





