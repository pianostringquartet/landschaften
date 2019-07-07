(ns landschaften.db
  (:require [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.sample.manet :as manet]
            [landschaften.sample.cezanne :as cezanne]))

;; BUG: when imported from namespace, not evaluated at compile-time;
;; spec fails with "manet-people-group-name is not a String";
;; TICKET: https://trello.com/c/nNAjcZ4V
(def manet-people-group-name "Manet's people")
(def cezanne-people-group-name "Cezanne's people")

;; TODO: use these consistently across the entire app
;; TICKET: https://trello.com/c/L6WNYBxd
(def SHOW-N-CHARTPOINTS 15)

(def CONCEPT-CERTAINTY-ABOVE 0.85)

(def excluded-timeframes
  #{"0801-0850" "1051-1100" "1101-1150" "1151-1200" "1201-1250" "1251-1300" "1301-1350" "1351-1400"})

(def excluded-schools
  #{"Russian" "Bohemian" "Catalan" "Finnish" "Greek" "Irish" "Norwegian" "Other" "Polish" "Portuguese" "Swedish" "Hungarian" "Scottish" "Swiss" "Danish" "Austrian"})

;; A database that starts off with 'Manet' and 'Cezanne' saved-searches,
;; to demonstrate app capabilities.
(def default-db
  {:current-mode                      :explore
   :examining?                        false
   :mobile-search?                    true
   :constraints-updated-since-search? false

   ;; EXAMINE
   :current-painting                  nil
   :paintings                         manet/manet-sample-paintings
   ::search-result-paintings          #{}
   :show-painting-modal?              false
   :image-zoomed?                     false

   ;; EXPLORE
   :query-loading?                    false
   :all-types                         (disj specs/PAINTING-TYPES "Other")
   :all-schools                       (apply disj specs/SCHOOLS excluded-schools)
   :all-timeframes                    (apply disj specs/TIMEFRAMES excluded-timeframes)
   :all-concepts                      #{}                   ; retrieved from backend during initialization
   :all-artists                       #{}                   ; retrieved from backend during initialization
   :selected-types                    manet/manet-type-constraints
   :selected-schools                  manet/manet-school-constraints
   :selected-timeframes               manet/manet-timeframe-constraints
   :selected-concepts                 manet/manet-concept-constraints
   :selected-artists                  manet/manet-artist-constraints

   ;; EXPLORE & COMPARE
   :current-group-name                manet/manet-people-group-name
   :show-group-name-prompt?           false
   :saved-groups                      {manet-people-group-name   manet/manet-example-group
                                       cezanne-people-group-name cezanne/cezanne-example-group}
   ;; COMPARE
   :compared-group-names              [manet-people-group-name cezanne-people-group-name] ; start as vector to avoid compile-time spec issue

   ;; CHARTS
   :show-n-chart-points               SHOW-N-CHARTPOINTS
   :concept-certainty-above           CONCEPT-CERTAINTY-ABOVE})