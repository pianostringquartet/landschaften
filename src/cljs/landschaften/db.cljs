(ns landschaften.db
  (:require [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]))


(def excluded-timeframes
  #{"0801-0850" "1051-1100" "1101-1150" "1151-1200" "1201-1250" "1251-1300" "1301-1350" "1351-1400"})


(def excluded-schools
  #{"Russian" "Bohemian" "Catalan" "Finnish" "Greek" "Irish" "Norwegian" "Other" "Polish" "Portuguese" "Swedish" "Hungarian" "Scottish" "Swiss" "Danish" "Austrian"})


(def example-group
  {:group-name "Michelangelo's saints"
   :paintings #{}
   :type-constraints #{}
   :school-constraints #{"Italian"}
   :timeframe-constraints #{"1451-1500", "1501-1550", "1551-1600"}
   :concept-constraints #{"saint"}
   :artist-constraints #{"MICHELANGELO Buonarroti"}})


(def french-landscapes
  {:group-name "French landscapes"
   :paintings #{} ;; normally not possible
   :type-constraints #{"landscape"}
   :school-constraints #{"French"}
   :timeframe-constraints #{}
   :artist-constraints #{}
   :concept-constraints #{}})


(def spanish-religious
  {:group-name "Spanish religious"
   :paintings #{} ;; normally not possible
   :type-constraints #{"religious"}
   :school-constraints #{"Spanish"}
   :timeframe-constraints #{}
   :artist-constraints #{}
   :concept-constraints #{"religion"}})


(def default-db
 {:current-mode            :explore
  :examining?              false


  ;; EXAMINE
  :current-painting        nil
  :show-slideshow?         false ;; i.e. slideshow
  ::image-zoomed?          false

  ;; EXPLORE
  :query-loading?          false
  :all-types               (disj specs/PAINTING-TYPES "Other")
  :all-schools             (apply disj specs/SCHOOLS excluded-schools)
  :all-timeframes          (apply disj specs/TIMEFRAMES excluded-timeframes)
  :all-concepts            #{} ; retrieve from backend
  :all-artists             #{} ; retrieve from backend

  ;; EXPLORE & COMPARE
  :current-group           example-group ; :current-group sample/sample-group
  :show-group-name-prompt? false
  :saved-groups            {} ;{(:group-name french-landscapes) french-landscapes}
  ;                          (:group-name spanish-religious) spanish-religious}
  ;; COMPARE

  ;; set of group-names (strings): the groups that are being compared
  :compared-group-names    '()

  ;; CHARTS
  ;; how many data points in a bar chart or table to show
  :show-n-chart-points     20
  ;; only interested in concepts with certainty above ...
  :concept-certainty-above 0.94})

  ;; ah, can i use the same namespaced keyword?
  ;; e.g. could I use the default db as the 'source of truth'?
  ;; e.g. access landschacften.db/slideshow-paintings
  ;; in the subs and events?
  ;::show-slideshow? false})



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
