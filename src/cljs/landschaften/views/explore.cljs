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

;; just


;; if pass down Group,
;; then components will access fields of Group,
;; and so components know about structure of Group

;; if don't pass down Group,
;; UI doesn't reflect a fundamental concept e.g. that "Explore" UI
;; is "ABOUT" a group

;; wow... wow... types are so important...
;; i don't know how to solve the issue of "fake access into a db"
;; ... and this problem arises ANYTIME a data structure's structure changes...
;; i can enforce that the data has a certain structure,
;; ... but that doesn't (programmatically) update the "outdated accessors"

;; in python, you could do a class, and the class had enforced relations.
;; clojure has defrecord, but no control over / guidance for access

;; ... ugh, you're totally mentally typed based now...
