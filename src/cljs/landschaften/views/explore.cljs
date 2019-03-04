(ns landschaften.views.explore
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.events :as events]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.ui-specs :as ui-specs]
            [landschaften.views.preview :as preview]
            [landschaften.views.sidebar :as sidebar]))

;; will include sidebar, preview

; (def explore-panel [:div "explore me"])

(def paintings-data #{"love" "pain"})

(defn no-data []
  [rc/title
     :label "No data yet. Search for paintings get started."
     :level :level3])

(defn paintings-found [n]
  (let [x (if (= n 1) "PAINTING" "PAINTINGS")]
   [rc/title
     :label (clojure.string/join " " [n x "FOUND"])]))

;; paintings should satisfy: s/valid? s/coll-of ::painting
; (defn data [paintings]
;   [paintings-found (count paintings)])

; (defn explore-data [paintings]
;  {:pre [(s/valid? (s/coll-of ::specs/painting) paintings)]}
;  [paintings-found (count paintings)])



(defn preview-data [paintings]
  [paintings-found (count paintings)])


(defn explore-data [paintings]
 {:pre [(s/valid? (s/coll-of ::specs/painting) paintings)]}
 [rc/h-box
   :children [[preview-data paintings]
              [sidebar/sidebar]]])


(def explore-panel
  (let [paintings (subscribe [::subs/paintings])]
  ; (let [paintings paintings-data]
    (if (empty? @paintings)
      [no-data]
      [explore-data @paintings])))
