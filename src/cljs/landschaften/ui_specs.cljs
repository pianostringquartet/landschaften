(ns landschaften.ui-specs
  (:require [cljs.spec.alpha :as s]))

;; UI-Specs:
;; - represent UI-related constraints
;; - do not represent a coherent problem-domain concept


;; Major 'modes' app can be in
(def MODES
  #{:explore :compare})

(s/def ::mode #(contains? MODES %))


(s/def ::google-chart-type
  #(contains? #{"LineChart" "PieChart" "ColumnChart" "AreaChart", "BarChart" "Table"} %))


;; add one for bootstrap buttons


