(ns landschaften.views.explore
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.views.preview :as preview]
            [landschaften.views.sidebar :as sidebar]))


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


(defn loading-modal []
  [rc/modal-panel
   :backdrop-on-click #(js/console.log "please wait ...")
   :child [rc/v-box
            :align :center
            :children [[rc/throbber :size :large]
                       [rc/label :label "Loading..."]]]])


(defn explore-panel []
  (let [paintings (subscribe [::subs/paintings])
        show-slideshow? (subscribe [::subs/show-slideshow?])
        loading? (subscribe [::subs/query-loading?])]
      [rc/h-box
         :justify :between
         :gap "16px"
         :margin "16px"
         :style {:padding-left "16px" :padding-right "16px"}
         :children [[explore @paintings @show-slideshow?]
                    [sidebar/sidebar]
                    (when @loading? [loading-modal])]]))