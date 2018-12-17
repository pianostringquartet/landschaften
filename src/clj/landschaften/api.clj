(ns landschaften.api
  (:require [landschaften.db.core :refer [*db* love]]
            [landschaften.db.wga-concepts :as wga-concepts]
            [proto-repl-charts.charts :as charts]
            [mount.core :as mount]
            [clojure.test :refer [is]]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [expound.alpha :as exp]
            [clojure.spec.gen.alpha :as gen]))


;; QUERY CONCEPTS ETC. OF PAINTING-ROWS


;;
;;; REPL PLAY:


;; *db* won't be good until we've started the app
; (mount/start *db*)

; (prn love)



;; ":as some-symbol" -- this binds some-symbol to the ENTIRE, NON-destructed param
;; "& some-symbol" -- binds the REMAINING, NON-destructed param part to some-symbol

;; use Expound's printer
(set! s/*explain-out* exp/printer)

;; start with a rough idea of the data you're gonna be using
(def sample-concept {:name "my string" :value 0.98765})

;; then write some specs:
(s/def ::name string?)
(s/def ::value float?)
(s/def ::concept (s/keys :req-un [::name ::value]))

; (exp/expound ::concept sample-concept) ;; Success!

;; we generate some sample data like this:
; ([{:name "", :value 0.5} {:name "", :value 0.5}]
;  [{:name "G", :value -0.5} {:name "G", :value -0.5}]
;  [{:name "G", :value 1.5} {:name "G", :value 1.5}]
;  [{:name "Gb", :value 3} {:name "Gb", :value 3}] ...)
; (s/exercise ::concept)

(s/fdef has-certainty-above
  :args (s/cat :concept ::concept :certainty float?)
  :ret boolean?
  :fn #(= (:ret %)
          (< (:certainty (:args %))
             (:value (:concept (:args %))))))

(defn has-certainty-above [{value :value} certainty]
  (< certainty value))


;; -----------------------------------
;; specs for row
;; -----------------------------------

(s/def ::school #{"Italian", "Other", "Dutch", "French", "Spanish", "American", "Flemish", "English", "Netherlandish", "German", "Hungarian", "Swiss", "Bohemian", "Danish", "Austrian", "Belgian"})
(s/def ::type #{"landscape"})
(s/def ::form #{"painting"})
(s/def ::title string?)
(s/def ::author string?)
(s/def ::timeframe string?)

(defn jpg-gen []
  (s/gen #{"https://www.wga.hu/art/b/bruegel/pieter_e/01/04icarus.jpg", "https://www.wga.hu/art/n/napoleta/navalbat.jpg", "https://www.wga.hu/art/b/bril/paul/staghunt.jpg", "https://www.wga.hu/art/b/bison/milancat.jpg", "https://www.wga.hu/art/v/velde/willem/calm_sea.jpg"}))

(s/def ::jpg
 (s/with-gen
  (s/and
    string?
    #(clojure.string/includes? % "https://www.wga.hu/art/") #(clojure.string/includes? % ".jpg"))
  jpg-gen))

(s/def ::concepts (s/* ::concept))

(s/def ::row (s/keys :req-un [::school ::type ::form ::title ::author ::timeframe ::jpg ::concepts]))

;; -----------------------------------

(s/fdef is-concept
 :args (s/cat :concept-map ::concept :desired-concept string?)
 :ret boolean?)

(defn is-concept [{name :name} concept]
  (= name concept))

; (exp/explain-results (st/check `is-concept))

(defn pred1 [{name :name}] (< 10 (count name)))
(defn pred2 [{value :value}] (< 0.5 value))
(defn pred-gen [] (s/gen #{pred1 pred2}))
(s/def ::pred
 (s/with-gen
   (s/fspec
     :args (s/cat :concept ::concept)
     :ret boolean?)
   pred-gen))

;; is the name longer than 10 chars?
; (exp/expound ::pred (fn [{name :name}] (< 10 (count name))))
; ;; is the value higher than 0.5?
; (exp/expound ::pred (fn [{value :value}] (< 0.5 value)))
; ;; ought to fail bc does not return bool
; (exp/expound ::pred (fn [{value :value}] (+ 0.5 value)))
; ;; ought to fail bc does not accept row
; (exp/expound ::pred (fn [value] (+ 0.5 value)))
; (exp/expound ::pred (fn [{value :x}] (+ 0.5 value)))

(s/fdef has-concept-satisfying
  :args (s/cat :row ::row :preds (s/coll-of ::pred))
  :ret boolean?)

(defn has-concept-satisfying
 "Does this row have a concept satisfying all preds?"
 [{concepts :concepts} preds]
 (if (empty? preds)
   false
   (boolean (some (apply every-pred preds) concepts))))


(let [{concepts :concepts} preds]
  (if (empty? preds)
    false
    (boolean (some (apply every-pred preds) concepts))))



;; (st/check `x) ... check whether the implementation of x satisfies x's spec
(exp/explain-results (st/check `has-concept-satisfying))

;; generates some sample args :-)
; (s/exercise (:args (s/get-spec `has-concept-satisfying)))

(s/fdef rows-with-concepts-satisfying
  :args (s/cat :rows (s/coll-of ::row) :preds (s/coll-of ::pred))
  :ret (s/coll-of ::row)
  :fn #(clojure.set/subset?
         (set (:ret %))
         (set (:rows (:args %)))))

(defn rows-with-concepts-satisfying [rows preds]
  (filter #(has-concept-satisfying % preds) rows))

;; (st/instrument `f) ... check :args part of f's spec whenver f is called
;; f must be re-instrumented if its spec or body are redefined
; (st/instrument `rows-with-concepts-satisfying)

; (wga-concepts/)

;; How to specify, "A set of strings"?
;; not working?:
;;    (s/coll-of string? :kind set)
;;    (s/and set? (s/coll-of string?)))

(s/fdef has-no-concept-like
 :args (s/cat :row ::row :unwanted set?)
 :ret boolean?)

(defn has-no-concept-like [{row-concepts :concepts} unwanted]
  (let [concepts (into #{} (map :name row-concepts))]
    (not-any? #(contains? concepts %) unwanted)))

; (st/check `has-no-concept-like)
; (exp/explain-results (st/check `has-no-concept-like))

; (has-no-concept-like {:concepts [{:name "sea"} {:name "dog"}]} #{"sea" "beach"}) ;; true
; (has-no-concept-like {:concepts [{:name "flower"} {:name "dog"}]} #{"sea" "beach"}) ;; true
; (has-no-concept-like {:concepts [{:name "flower"} {:name "dog"}]} []) ;; fails pre-op
;

;; ------------------------------------------------
;; exploring the data
;; ------------------------------------------------

; #{"mythological" "genre" "portrait" "landscape" "religious" "other" "historical" "interior" "still-life" "study"}

; (def rows
;  (map wga-concepts/with-model-concepts
;    (wga-concepts/retrieve-paintings-type "study")))
;
; (def high-certainty-rows
;   (rows-with-concepts-satisfying rows [#(has-certainty-above % 0.98)]))
;
; ;; add a shortcut that copy+pastes the selected line
; ;; one line below itself; for iterative development.
; (first high-certainty-rows)
; (first high-certainty-rows)



;; PROBLEM: this fn is totally ambiguous;
;; concepts, not rows, have certainty,
;; and this fn is returning any row that
;; has AT LEAST ONE CONCEPT with certainty higher than `certainty`

;; returns list of rows
(defn type-rows-above-certainty [painting-type certainty]
  (let [rows (map wga-concepts/with-model-concepts
               (wga-concepts/retrieve-paintings-type painting-type))]
    (rows-with-concepts-satisfying
     rows
     [#(has-certainty-above % certainty)])))



;; ultimately you're not working with anything super nested:
;; 1 painting per row
;; first level attrs like :school, :timeframe, :type,
;; a list of concepts

;; NOTES:
;; - 'form' is irrelevant bc your db only has form='painting')
;; - the list of concepts might as well be a set -- you're never going to use the concepts' ORDER




;; if you take :concepts for a row, you are taking a list;
;; but we want a flat list in the end,
;; so we do flatmap ie mapcat
(def rs
 (mapcat :concepts
  (type-rows-above-certainty "mythological" 0.98)))


; (take 10 rs)

;; {:concept-name <how-many-times-it-appears> ...}
; (frequencies (map :name rs))

;; the 10 most frequent concepts and how often they appear
(let [genre "mythological"
      cs (mapcat :concepts
           (type-rows-above-certainty genre 0.98))
      n 10]
  (take-last n
    (sort-by #(second %)
        (frequencies (map :name cs)))))


(defn n-most-frequent-high-certainty-concepts-of-genre
  [n certainty genre]
  (let [concepts (mapcat :concepts
                   (type-rows-above-certainty genre 0.98))]
    (take-last n
      (sort-by #(second %)
          (frequencies (map :name concepts))))))

; (for [genre wga-concepts/painting-types]
;   (let [frequent-concepts
;           (n-most-frequent-high-certainty-concepts-of-genre
;             10 0.98 genre)]
;     (do
;      (print "\nGENRE: " genre "\n")
;      (clojure.pprint/pprint frequent-concepts))))
;


; ;; where concepts is a list of maps
;; wtf is the signifcance of 'second' here? what does that mean?!
(defn concepts-appearing-more-than [concepts n]
  (filter #(< n (second %)) (frequencies (map :name concepts))))
;
; (concepts-appearing-more-than rs 20)
; ;
; (let [name "myth50"
;       n 50
;       rs rs]
;   (charts/bar-chart
;     name
;     {"More than n times" (mapv second (concepts-appearing-more-than rs n))}
;     {:labels (mapv first (concepts-appearing-more-than rs n))}))



;;
;
; ;; use a lower threshold
; (charts/bar-chart
;   "more concepts"
;   {"More than 10 times" (mapv second (concepts-appearing-more-than cs 10))}
;   {:labels (mapv first (concepts-appearing-more-than cs 10))})




;; Exploring landscapes:

(def unwanted-concepts #{"sea" "watercraft" "ship" "rowboat" "warship" "shipwreck", "ocean", "boat", "harbor", "seashore"})


; (let [preds  #{ ;;(fn [{x :name}] (= x "transportation system"))
;                (fn [{x :value}] (> x 0.98))}
;       rows (map with-model-concepts (retrieve-wga-concepts))
;       res (rows-with-concepts-satisfying rows preds)
;       res2 (filter #(has-no-concept-like % unwanted-concepts) res)]
;       ; res2 (filter
;       ;        (fn [{concepts :concepts}]
;       ;         (let [xs (into #{} (map :name concepts))]
;       ;            ; (not (contains? xs "ship"))))
;       ;            (empty? (clojure.set/union xs #{"ship"}))))
;       ;        res)]
;   (do
;    res2
;    (print (count res2))
;    (print "\n")
;    ; (clojure.pprint/pprint (take 3 res2))))
;    (print (map :jpg res))))


;; a lot of these images are of boats etc.;
;; you want to see non-maritime images,
;; but even when excluding any image marked with 'ship',
;; there are still too many maritime images.

;; so you wrote a fn like 'has-no-concept-like' to
;; be able to exclude a broader set of concepts.

;; now establish a baseline, and test against that

;; NO MARITIME; 98 certainty
;; https://www.wga.hu/art/m/monet/03/argent13.jpg

;; MARITIME ALLOWED; 98 certainty
; https://www.wga.hu/art/f/friedric/4/407fried.jpg https://www.wga.hu/art/g/goeneutt/harbour.jpg https://www.wga.hu/art/g/guardi/francesc/2/guard212.jpg https://www.wga.hu/art/m/melbye/calmwate.jpg https://www.wga.hu/art/m/minderho/handelsk.jpg https://www.wga.hu/art/m/monet/03/argent13.jpg https://www.wga.hu/art/s/scott/shipping.jpg https://www.wga.hu/art/v/velde/willem/calm_sea.jpg

;; Some additional non-maritime 0.98 certainty images
; https://www.wga.hu/art/a/asselyn/italiana.jpg
; https://www.wga.hu/art/a/andreesc/winter.jpg
; https://www.wga.hu/art/p/pissarro/camille/5/13figure.jpg https://www.wga.hu/art/p/poelenbu/ruinrome.jpg https://www.wga.hu/art/p/porcelli/jan/shipwrec.jpg
; https://www.wga.hu/art/v/valencie/viewrome.jpg https://www.wga.hu/art/v/velazque/07/0709vela.jpg https://www.wga.hu/art/v/velde/peter/souda.jpg https://www.wga.hu/art/v/velde/willem/calm_sea.jpg
