(ns landschaften.db
  (:require [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.sample.manet :as manet]
            [landschaften.sample.cezanne :as cezanne]))

;; BUG?: when imported from namespace, not evaluated at compile-time;
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
  #{"Russian" "Bohemian" "Catalan" "Finnish" "Greek" "Irish" "Norwegian" "Other" "Polish"
    "Portuguese" "Swedish" "Hungarian" "Scottish" "Swiss" "Danish" "Austrian"})



;; The db a first-time, non-demo user encounters.
(def fresh-db
  {:current-mode                      :explore
   :examining?                        false
   :mobile-search?                    true
   :constraints-updated-since-search? false

   ;; EXAMINE
   :current-painting                  nil
   :paintings                         #{}
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
   :selected-genres                    #{}
   :selected-schools                  #{}
   :selected-timeframes               #{}
   :selected-concepts                 #{}
   :selected-artists                  #{}

   ;; EXPLORE & COMPARE
   :current-group-name                nil
   :show-group-name-prompt?           false
   :saved-groups                      {}
   ;; COMPARE
   :compared-group-names              '()

   ;; CHARTS
   :show-n-chart-points               SHOW-N-CHARTPOINTS
   :concept-certainty-above           CONCEPT-CERTAINTY-ABOVE})


;; A db for demos.
(def demo-db
  (-> fresh-db
      (assoc :paintings                         manet/manet-sample-paintings)
      (assoc :selected-genres                    manet/manet-type-constraints)
      (assoc :selected-schools                  manet/manet-school-constraints)
      (assoc :selected-timeframes               manet/manet-timeframe-constraints)
      (assoc :selected-concepts                 manet/manet-concept-constraints)
      (assoc :selected-artists                  manet/manet-artist-constraints)
      (assoc :current-group-name                manet/manet-people-group-name)
      (assoc :saved-groups                      {manet-people-group-name   manet/manet-example-group
                                                 cezanne-people-group-name cezanne/cezanne-example-group})
      (assoc :compared-group-names              [manet-people-group-name cezanne-people-group-name])))
