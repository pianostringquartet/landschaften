(ns landschaften.views.sidebar
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.db :as db]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]))

;; sidebar with mostly dropdown selects etc.


;; this component should be responsible for
;; manipulating the data into the way
;; the re-com comp needs

;; types is originally a set

;; there's TONS of internal state here!
(defn select-types [types]
 (let [choices (r/atom (map (fn [v] {v v})  types))
       selections (r/atom types)]
  [rc/selection-list
    :choices choices
    :model selections
    :id-fn identity
    :label-fn first
   ; receives selected items
    ; :on-change #(js/console.log "on-change received: " %)]))
    :on-change #(reset! selections %)]))
    ; want to dispatch action here
    ; i.e. keep an updated list of 'selected types'


;; choice list MUST BE list of maps, need not be reagent atom
;; selections (i.e. selected items) MUST BE set, need not be reagent atom
;; better to maintain state only in app-db -
;; i.e. update selections in app-db, will re-render here

;; just use the silly :id, :label stuff;
;;
(defn selection-list-from-set [some-set]
  {:pre [set? some-set]}
  ; (let [choices (r/atom (map (fn [v] {v v}) some-set))
  (let [choices (map (fn [v] {:id v :label v}) some-set)
        ; selections (r/atom some-set)]
        selections (r/atom #{})]
    [rc/selection-list
      :choices choices ; must be coll of maps
      :model selections
      ; :id-fn identity
      ; :label-fn first
     ; receives selected items
      :on-change #(do
                    (js/console.log "on-change received: " %)
                    ; dispatch
                    (reset! selections %))]))

(defn labeled-selection [label data]
  [rc/v-box :children [[rc/label :label label :class "h4"]
                       [selection-list-from-set data]]])

;; interesting -- it expects ids for the data
(defn sidebar []
  (let [types (rf/subscribe [::subs/types])
        schools (rf/subscribe [::subs/schools])
        timeframes (rf/subscribe [::subs/timeframes])]
    [rc/h-box
      :children [[labeled-selection "genres" (apply sorted-set @types)]
                 [labeled-selection "schools" (apply sorted-set @schools)]
                 [labeled-selection "timeframes" (apply sorted-set @timeframes)]]]))
