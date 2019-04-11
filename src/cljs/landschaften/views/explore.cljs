(ns landschaften.views.explore
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.views.preview :as preview]
            [landschaften.views.sidebar :as sidebar]
            [landschaften.views.mui :as mui]
            [landschaften.semantic-ui :as semantic-ui]))


(defn no-paintings-found []
  [rc/title
     :label "No paintings matching criteria found."
     :level :level3])


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
            ;:align :center
            :children [[rc/throbber :size :large]
                       [rc/label :label "Loading..."]]]])


;(defn explore-panel []
;  (let [paintings (subscribe [::subs/paintings])
;        show-slideshow? (subscribe [::subs/show-slideshow?])
;        loading? (subscribe [::subs/query-loading?])
;        mobile? (subscribe [::subs/mobile?])]
;    [rc/h-box
;       :justify :between
;       :gap "16px"
;       :margin "16px"
;       :style {:padding-left "8px" :padding-right "8px"}
;       :children [[explore @paintings @show-slideshow?]
;                  (when-not @mobile? [sidebar/sidebar])
;                  (when @loading? [loading-modal])]]))


(defn explore-panel []
  (let [paintings (subscribe [::subs/paintings])
        show-slideshow? (subscribe [::subs/show-slideshow?])
        loading? (subscribe [::subs/query-loading?])
        mobile? (subscribe [::subs/mobile?])]
    [:> semantic-ui/grid {:columns 2} ;{:padding "32px"}
     [:> semantic-ui/grid-column
      [explore @paintings @show-slideshow?]]
     [:> semantic-ui/grid-column
      [sidebar/sidebar]
      (when @loading? [loading-modal])]]))



#_(defn explore-panel []
    (let [paintings (subscribe [::subs/paintings])
          show-slideshow? (subscribe [::subs/show-slideshow?])
          loading? (subscribe [::subs/query-loading?])
          mobile? (subscribe [::subs/mobile?])]
        [rc/h-box
           :justify :between
           :gap "16px"
           :margin "16px"
           :style {:padding-left "8px" :padding-right "8px"}
           :children [[explore @paintings @show-slideshow?]
                      (when-not @mobile? [sidebar/sidebar])
                      (when @loading? [loading-modal])]]))

     ;:children [[explore @paintings @show-slideshow?]
      ;           [sidebar/sidebar]
      ;           (when @loading? [loading-modal])]]))