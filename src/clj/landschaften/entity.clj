(ns landschaften.entity
  (:require [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]))


;; TODO:
;; - create versions (e.g. of ::painting) compatible with front-end use cases,
;;   and reuse via .cljc files


;; ----------------------------
;; CONSTRAINT (DATABASE-VERSIONS)
;; ----------------------------

(def PAINTINGS-COLUMNS
  #{"id" "author" "title" "date" "form" "type" "school" "timeframe" "jpg" "concepts"})

(def PAINTINGS-CONCEPTS-COLUMNS
  #{"painting_id" "name" "value"})

(def painting-column? (partial contains? PAINTINGS-COLUMNS))
(def concept-column? (partial contains? PAINTINGS-CONCEPTS-COLUMNS))

(s/def ::column
  (s/or :painting-constraint painting-column?
        :concept-constraint concept-column?))
(s/def ::values (s/* string?))
(s/def ::constraint (s/keys :req-un [::column ::values]))



; ----------------------------
; SQLVEC
; ----------------------------

;; SQLVec is not SQL,
;; but rather a format jdbc turns into SQL.
;; SQLVec allows parameter escaping to avoid SQL-injection attacks.
(s/def ::sqlvec
  (fn [x]
    (when (vector? x)
      (let [[query & params] x
            param-placeholders (count (re-seq #"\?" query))]
        (= param-placeholders (count params))))))


;; ----------------------------
;; PAINTING
;; ----------------------------

(def SAMPLE-JPEGS
  #{"https://www.wga.hu/art/b/bruegel/pieter_e/01/04icarus.jpg", "https://www.wga.hu/art/n/napoleta/navalbat.jpg", "https://www.wga.hu/art/b/bril/paul/staghunt.jpg", "https://www.wga.hu/art/b/bison/milancat.jpg", "https://www.wga.hu/art/v/velde/willem/calm_sea.jpg"})

(s/def ::date string?)
(s/def ::school #(contains? specs/SCHOOLS %))
(s/def ::type #(contains? specs/PAINTING-TYPES %))
(s/def ::title string?)
(s/def ::author string?)
(s/def ::form #{"painting"})
(s/def ::timeframe string?)

(s/def ::wga-jpg                                            ; Web Gallery of Art jpg url
  (s/with-gen
    (s/and #(clojure.string/includes? % "https://www.wga.hu/art/")
           #(clojure.string/includes? % ".jpg"))
    (s/gen SAMPLE-JPEGS)))

(s/def ::name string?)
(s/def ::value #(<= 0.0 % 1.0))
(s/def ::concept (s/keys :req-un [::name ::value]))
(s/def ::concepts (s/coll-of ::concept))

(s/def ::painting (s/keys :req-un [::date ::school ::type ::title ::form ::author ::timeframe ::wga-jpg ::concepts]))
