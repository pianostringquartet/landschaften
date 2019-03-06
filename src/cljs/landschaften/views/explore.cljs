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
     :label "Search for some paintings to get started."
     :level :level3])


(defn preview-paintings [paintings]
  [preview/preview paintings])


(defn explore
 "Find paintings satisfying constraints"
 [paintings]
 (let [prompt-or-preview (if (empty? paintings)
                            [no-paintings]
                            [preview/preview paintings])]
   [rc/h-box
    :children [prompt-or-preview
               [sidebar/sidebar]]])) ; constraints and groups


;; want to look at current-group;
;; explore-panel uses a current-group
(defn explore-panel []
  (let [paintings (subscribe [::subs/paintings])]
    [explore @paintings]))

;; cases: arrived at explore panel because:

;; first use of app, so no current group -> USE default group

;; you have a current group or not;
;; the notion of current group + ui is split across the subs...

;; this is TERRIBLE design. it's terribly implicit.


;; user clicks SAVE GROUP

;; either

;; what needs to be

