(ns landschaften.views.explore
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.views.preview :as preview]
            [landschaften.views.sidebar :as sidebar]
            [landschaften.views.examine :as examine]))


(defn no-paintings-found []
  [rc/title
     :label "No paintings matching criteria found."
     :level :level3])


(defn preview-paintings [paintings show-max?]
  [preview/preview paintings show-max?])


(defn explore
 "Find paintings satisfying constraints"
 [paintings show-max?]
 (if (empty? paintings)
    [no-paintings-found]
    [preview/preview paintings show-max?]))



;; PROBLEM: when 4+ pting columns, sidebar partially pushed out of view
;; OPTIONS:
;; - limit to 3 pting columns only
;; - move sidebar to a collapsible/modal space
;; - constrain the size of the pting grid
(defn explore-panel []
  (let [paintings (subscribe [::subs/paintings])
        current-painting (subscribe [::subs/current-painting])
        examining? (subscribe [::subs/examining?])
        show-slideshow? (subscribe [::subs/show-slideshow?])]
      [rc/h-box
         :justify :between
         :gap "16px"
         :margin "16px"
         :style {:padding-left "16px" :padding-right "16px"}
         :children [(if @examining?
                      [examine/examine-painting @current-painting @show-slideshow?]
                      [explore @paintings @show-slideshow?])
                    [sidebar/sidebar]]]))