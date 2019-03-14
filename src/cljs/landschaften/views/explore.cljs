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
            [landschaften.views.sidebar :as sidebar]
            [landschaften.views.examine :as examine]))


(defn no-paintings []
  [rc/title
     :label "Search for some paintings to get started."
     :level :level3])


(defn preview-paintings [paintings show-max?]
  [preview/preview paintings show-max?])


(defn explore
 "Find paintings satisfying constraints"
 [paintings show-max?]
 (if (empty? paintings)
    [no-paintings]
    [preview/preview paintings show-max?]))

(defn explore-or-examine [paintings current-painting]
  (if current-painting
    [examine/examine-painting current-painting]
    [explore paintings]))


;; non-nil current-painting no longer takes us to Examine screen
;; instead, have explicit toggle :examining?

;; examining?
;; exploring?
;;

(defn explore-panel []
  (let [paintings (subscribe [::subs/paintings])
        current-painting (subscribe [::subs/current-painting])
        examining? (subscribe [::subs/examining?])
        show-max? (subscribe [::subs/show-max?])]
      ;[explore @paintings]))
      [rc/h-box
         :children [(if @examining?
                      [examine/examine-painting @current-painting @show-max?]
                      [explore @paintings @show-max?])
                    [sidebar/sidebar]]]))
;[explore-or-examine @paintings @current-painting]