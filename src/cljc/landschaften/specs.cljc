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

(def SAMPLE-JPEGS
  #{"https://www.wga.hu/art/b/bruegel/pieter_e/01/04icarus.jpg", "https://www.wga.hu/art/n/napoleta/navalbat.jpg", "https://www.wga.hu/art/b/bril/paul/staghunt.jpg", "https://www.wga.hu/art/b/bison/milancat.jpg", "https://www.wga.hu/art/v/velde/willem/calm_sea.jpg"})

(s/def ::date string?)
(s/def ::school #(contains? SCHOOLS %))
(s/def ::type #(contains? PAINTING-TYPES %))
(s/def ::title string?)
(s/def ::author string?)
(s/def ::form #{"painting"})
(s/def ::timeframe string?)

(s/def ::jpg
 ; (s/with-gen
  (s/and #(clojure.string/includes? % "https://www.wga.hu/art/")
         #(clojure.string/includes? % ".jpg")))
  ; (s/gen SAMPLE-JPEGS))

(s/def ::name string?)
(s/def ::value #(<= 0.0 % 1.0))
(s/def ::concept (s/keys :req-un [::name ::value]))
(s/def ::concepts (s/coll-of ::concept))

(s/def ::painting (s/keys :req-un [::date ::school ::type ::title ::form  ::author ::timeframe ::jpg ::concepts]))
