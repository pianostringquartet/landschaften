(ns landschaften.db
  (:require [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]))


(def excluded-timeframes
  #{"0801-0850" "1051-1100" "1101-1150" "1151-1200" "1201-1250" "1251-1300" "1301-1350" "1351-1400"})


(def excluded-schools
  #{"Russian" "Bohemian" "Catalan" "Finnish" "Greek" "Irish" "Norwegian" "Other" "Polish" "Portuguese" "Swedish" "Hungarian" "Scottish" "Swiss" "Danish" "Austrian"})


(def example-group
  {:group-name            "Michelangelo's saints"
   :paintings             #{}
   :type-constraints      #{}
   :school-constraints    #{"Italian"}
   :timeframe-constraints #{"1451-1500", "1501-1550", "1551-1600"}
   :concept-constraints   #{"saint"}
   :artist-constraints    #{"MICHELANGELO Buonarroti"}})


(def default-db
  {:current-mode            :explore
   :examining?              false
   :mobile-search?          true

   ;; EXAMINE
   :current-painting        nil
   ;:show-slideshow?         false                           ;; i.e. slideshow
   :show-painting-modal?    false                           ;; i.e. slideshow
   :image-zoomed?           false

   ;; EXPLORE
   :query-loading?          false
   :all-types               (disj specs/PAINTING-TYPES "Other")
   :all-schools             (apply disj specs/SCHOOLS excluded-schools)
   :all-timeframes          (apply disj specs/TIMEFRAMES excluded-timeframes)
   :all-concepts            #{}                             ; retrieve from backend
   :all-artists             #{}                             ; retrieve from backend

   ;; EXPLORE & COMPARE
   :current-group           nil
   :show-group-name-prompt? false
   :saved-groups            {}

   ;; COMPARE
   :compared-group-names    '()

   ;; CHARTS
   :show-n-chart-points     20
   :concept-certainty-above 0.94})


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
