(ns landschaften.specs
  (:require [clojure.spec.alpha :as s]))


;; -------------------------
;; PAINT SPEC
;; -------------------------

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

(def SCHOOLS #{"German" "Italian" "Danish" "Flemish" "Dutch" "Netherlandish" "Swiss" "Other" "Russian" "English" "Austrian" "Scottish" "Bohemian" "French" "Spanish" "Belgian" "Hungarian" "American" "Polish" "Norwegian" "Swedish" "Irish" "Finnish" "Portuguese" "Greek" "Catalan"})

(def TIMEFRAMES
 #{"0801-0850" "1051-1100" "1101-1150" "1151-1200" "1201-1250" "1251-1300" "1301-1350" "1351-1400" "1401-1450" "1451-1500" "1501-1550" "1551-1600" "1601-1650" "1651-1700" "1701-1750" "1751-1800" "1801-1850" "1851-1900"})

(def SAMPLE-JPEGS
  #{"https://www.wga.hu/art/b/bruegel/pieter_e/01/04icarus.jpg", "https://www.wga.hu/art/n/napoleta/navalbat.jpg", "https://www.wga.hu/art/b/bril/paul/staghunt.jpg", "https://www.wga.hu/art/b/bison/milancat.jpg", "https://www.wga.hu/art/v/velde/willem/calm_sea.jpg"})

(s/def ::date string?)

(s/def ::school
  (s/with-gen
    #(contains? SCHOOLS %)
    #(s/gen SCHOOLS)))

;(s/def ::type #(contains? PAINTING-TYPES %))

(s/def ::type
  (s/with-gen
    #(contains? PAINTING-TYPES %) ; the sepc
    #(s/gen PAINTING-TYPES))) ; the gen


(s/def ::title string?)
(s/def ::author string?)
(s/def ::form #{"painting"})

(s/def ::timeframe
  (s/with-gen
    #(contains? TIMEFRAMES %)
    #(s/gen TIMEFRAMES)))

(s/def ::wga-jpg
 ; (s/with-gen ; doesn't work with cljs?
  (s/and #(clojure.string/includes? % "https://www.wga.hu/art/")
         #(clojure.string/includes? % ".jpg")))
  ; (s/gen SAMPLE-JPEGS))

; (s/def ::jpg ; Cloudinary 'secure [jpg] url'
;   (s/nilable
;     (s/and
;       #(clojure.string/includes? % "https://res.cloudinary.com/")
;       #(clojure.string/includes? % "/image/upload/"))))

;; too complicated now
;; ... needs to be nillable because some
(s/def ::jpg (s/nilable string?))

; (s/def ::jpg ; Cloudinary 'secure [jpg] url'
;   (s/nilable
;     (or
;       (s/and
;         #(clojure.string/includes? % "https://res.cloudinary.com/")
;         #(clojure.string/includes? % "/image/upload/"))
;       (s/and #(clojure.string/includes? % "https://www.wga.hu/art/")
;             #(clojure.string/includes? % ".jpg")))))

(s/def ::name string?)

;; works
(s/def ::value
  (s/with-gen
    #(<= 0.0 % 1.0)
    #(s/gen (s/and float?
                   (fn [x] (<= 0.0 x 1.0))))))

(s/def ::concept (s/keys :req-un [::name ::value]))

(s/def ::concepts (s/coll-of ::concept))

(s/def ::painting (s/keys :req-un [::date
                                   ::school
                                   ::type
                                   ::title
                                   ::form
                                   ::author
                                   ::timeframe
                                   ::jpg
                                   ::concepts]))



;; -------------------------
;; GROUP SPEC
;; -------------------------


(s/def ::group-name string?)
(s/def ::paintings (s/coll-of ::painting))
(s/def ::type-constraints (s/coll-of ::type))
(s/def ::school-constraints (s/coll-of ::school))
(s/def ::timeframe-constraints (s/coll-of ::timeframe))
(s/def ::concept-constraints (s/coll-of string?)) ;; not needed?
(s/def ::artist-constraints (s/coll-of string?))
(s/def ::type-constraints (s/coll-of ::type))

(s/def ::group (s/keys :req-un [::group-name
                                ::paintings
                                ::type-constraints
                                ::school-constraints
                                ::timeframe-constraints
                                ::concept-constraints
                                ::artist-constraints]))

;; -------------------------
;; DB SPEC
;; -------------------------


;; spec-gen is generating nonsense data here;
;; e.g. -1, weird strings etc.
;; can you not simply reuse a spec, assigned to another key name?
;;
(s/def ::current-painting (s/nilable ::painting))
;(s/def ::current-painting #(s/valid? (s/nilable ::painting) %))

(s/def ::examining? boolean?)
(s/def ::show-slideshow? boolean?)
(s/def ::image-zoomed? boolean?)
(s/def ::query-loading? boolean?)

(s/def ::all-types (s/coll-of ::type))
(s/def ::all-schools (s/coll-of ::school))
(s/def ::all-timeframes (s/coll-of ::timeframe))

;; string, not ::concept, because ::concept is {:name :value}
(s/def ::all-concepts (s/coll-of string?))
(s/def ::all-artists (s/coll-of string?))

(s/def ::current-group ::group)
(s/def ::show-group-name-prompt? boolean?)

;; currently not very specific
(s/def ::saved-groups map?)

(s/def ::compared-group-names (s/and (s/coll-of string?)
                                     #(>= 2 (count %))))

;(s/def ::compared-group-names (s/coll-of string?))

(s/def ::app-db
  (s/keys :req-un [::current-painting
                   ::examining?
                   ::show-slideshow?
                   ::image-zoomed?
                   ::query-loading?
                   ::all-types
                   ::all-schools
                   ::all-timeframes
                   ::all-concepts
                   ::all-artists
                   ::current-group
                   ::show-group-name-prompt?
                   ::saved-groups
                   ::compared-group-names]))

