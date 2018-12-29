(ns landschaften.specs
  (:require [clojure.spec.alpha :as s]))


;; -------------------------
;; PAINT SPEC
;; -------------------------
;; ^^ later: in cljc folder, to share

(def PAINTING-TYPES
 #{"mythological"
   "interior"
   "landscape"
   "study"
   "genre"
   "religious"
   "other"
   "still-life"
   "historical"
   "portrait"})

(def SCHOOLS
 #{"Italian", "Other", "Dutch", "French", "Spanish", "American", "Flemish", "English", "Netherlandish", "German", "Hungarian", "Swiss", "Bohemian", "Danish", "Austrian", "Belgian"})

(def TIMEFRAMES
 #{"0801-0850" "1051-1100" "1101-1150" "1151-1200" "1201-1250" "1251-1300" "1301-1350" "1351-1400" "1401-1450" "1451-1500" "1501-1550" "1551-1600" "1601-1650" "1651-1700" "1701-1750" "1751-1800" "1801-1850" "1851-1900"})

(def SAMPLE-JPEGS
  #{"https://www.wga.hu/art/b/bruegel/pieter_e/01/04icarus.jpg", "https://www.wga.hu/art/n/napoleta/navalbat.jpg", "https://www.wga.hu/art/b/bril/paul/staghunt.jpg", "https://www.wga.hu/art/b/bison/milancat.jpg", "https://www.wga.hu/art/v/velde/willem/calm_sea.jpg"})

(s/def ::date string?)
(s/def ::school #(contains? SCHOOLS %))
(s/def ::type #(contains? PAINTING-TYPES %))
(s/def ::title string?)
(s/def ::author string?)
(s/def ::form #{"painting"})
; (s/def ::timeframe string?)
(s/def ::timeframe #(contains? TIMEFRAMES %))

(s/def ::jpg
 ; (s/with-gen ; doesn't work with cljs?
  (s/and #(clojure.string/includes? % "https://www.wga.hu/art/")
         #(clojure.string/includes? % ".jpg")))
  ; (s/gen SAMPLE-JPEGS))

(s/def ::name string?)
(s/def ::value #(<= 0.0 % 1.0))
(s/def ::concept (s/keys :req-un [::name ::value]))
(s/def ::concepts (s/coll-of ::concept))

(s/def ::painting (s/keys :req-un [::date ::school ::type ::title ::form  ::author ::timeframe ::jpg ::concepts]))
