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


(defn no-paintings []
  [rc/title
     :label "Search for some paintings get started."
     :level :level3])

(defn preview-paintings [paintings]
  [preview/preview paintings])

(defn explore-paintings
 "Find paintings satisfying constraints"
 [paintings]
 ; {:pre [(s/valid? (s/coll-of ::specs/painting) paintings)]}
 (let [prompt-or-preview (if (empty? paintings)
                            [no-paintings]
                            [preview/preview paintings])]
   [rc/h-box
    :children [prompt-or-preview
               ; [preview-paintings paintings
               [sidebar/sidebar]]])) ; constraints and groups)


(defn explore-panel []
  (let [paintings (subscribe [::subs/paintings])]
  ; (let [paintings paintings-data]
    (fn []
      ; (if (empty? @paintings)
        ; [no-paintings]
        [explore-paintings @paintings])))
