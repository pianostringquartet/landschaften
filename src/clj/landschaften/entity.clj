(ns landschaften.entity
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [expound.alpha :as exp]
            [clojure.spec.gen.alpha :as gen]))




;; painting constraints
(def type-constraint {:column "type" :values ["landschaften" "study"]})
(def timeframe-constraint {:column "timeframe" :values ["1501-1500"]})
;; concept constraint
(def concept-name-constraint {:column "name" :values ["no person"]})

(def malformed-constraint {:column "malformed column" :values ["no person"]})

(def no-constraints #{})
(def painting-constraints #{type-constraint timeframe-constraint})
(def concept-constraints #{concept-name-constraint})
(def painting-and-concept-constraints
  #{type-constraint timeframe-constraint concept-name-constraint})
(def malformed-constraints #{malformed-constraint})


;; ----------------------------
;; SPEC HELPERS
;; ----------------------------

(defn coerce [some-spec some-data]
  (let [coerced-data (s/conform some-spec some-data)]
    (if (= coerced-data :clojure.spec.alpha/invalid)
      nil
      coerced-data)))


;; ----------------------------
;; CONSTRAINT
;; ----------------------------

(def PAINTINGS-COLUMNS
  #{"id" "author" "title" "date" "form" "type" "school" "timeframe" "jpg" "concepts"})

(def PAINTINGS-CONCEPTS-COLUMNS
  #{"painting_id" "name" "value"})

;; this is a problem for compiler?
(def painting-column? (partial contains? PAINTINGS-COLUMNS))
(def concept-column? (partial contains? PAINTINGS-CONCEPTS-COLUMNS))

(s/def ::column
 (s/or :painting-constraint painting-column?
       :concept-constraint  concept-colum?))
(s/def ::values (s/* string?))
(s/def ::constraint (s/keys :req-un [::column ::values]))


;; ----------------------------
;; SQLVEC
;; ----------------------------

(s/def ::sqlvec
  (fn [[query & params]]
    (let [param-placeholders (count (re-seq #"\?" query))]
      (= param-placeholders (count params)))))


;; ----------------------------
;; PAINTING
;; ----------------------------

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
;; is set okay, or needs to be #(contains? ...) ?
(s/def ::school #(contains? SCHOOLS %))
(s/def ::type #(contains? PAINTING-TYPES %))
(s/def ::title string?)
(s/def ::author string?)
(s/def ::form #{"painting"})
(s/def ::timeframe string?)

(s/def ::jpg
 (s/with-gen
  (s/and #(clojure.string/includes? % "https://www.wga.hu/art/")
         #(clojure.string/includes? % ".jpg"))
  (s/gen SAMPLE-JPEGS)))

(s/def ::name string?)
(s/def ::value #(<= 0.0 % 1.0))
(s/def ::concept (s/keys :req-un [::name ::value]))
(s/def ::concepts (s/coll-of ::concept))

(s/def ::painting (s/keys :req-un [::date ::school ::type ::title ::form  ::author ::timeframe ::jpg ::concepts]))


;; ----------------------------
;; TESTS
;; ----------------------------

; ; turn on the spec for the fn in this namespace
; (st/instrument `paintings-satisfying)
; ;
; ; ; should fail:
; (paintings-satisfying malformed-constraint)
; (paintings-satisfying [malformed-constraint])
; ; ;
; ; ; ;; should succeed:
; (paintings-satisfying #{type-constraint})
;
; (paintings-satisfying #{})

; ;; success, good
; (exp/expound ::entity/painting
;   (first (take 3 (retrieve-paintings *db* #{{} {:column "timeframe" :values ["1501-1550", "1551-1600"]}}))))

; ;; fails, good:
; (exp/expound ::entity/painting
;   (take 3 (retrieve-paintings *db* #{{} {:column "timeframe" :values ["1501-1550", "1551-1600"]}})))
