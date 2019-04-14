(ns landschaften.views.explore
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.views.preview :as preview]
            [landschaften.views.sidebar :as sidebar]
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
           :children [[rc/throbber :size :large]
                      [rc/label :label "Loading..."]]]])

;(defn love-modal []
;  [rc/modal-panel
;   :backdrop-on-click #(js/console.log "you love me...")
;   :child [rc/v-box
;           :children [[rc/throbber :size :large]
;                      [rc/label :label "Love..."]]]])


;; responsive components are counted

(defn b []
  [:> semantic-ui/button "Hello"])

;; need to set a max width on the items in a list
;; otherwise, when one-item is too long, the row becomes stacked

#_(defn explore-panel []
    (let [paintings       (subscribe [::subs/paintings])
          show-slideshow? (subscribe [::subs/show-slideshow?])]
      [:> semantic-ui/slist {:horizontal true}
       [:> semantic-ui/slist-item
        [:> semantic-ui/slist-content
         [:> semantic-ui/responsive {:max-width 799} [b]]]]
       [:> semantic-ui/slist-item
        [:> semantic-ui/slist-content {:floated "left"}
         [:> semantic-ui/container {:fluid false}
          (clojure.string/join (repeat 20 "love"))]]]
       ;[explore @paintings @show-slideshow?]]]

       [:> semantic-ui/slist-item
        [:> semantic-ui/slist-content {:floated "right"}
         [:> semantic-ui/responsive {:min-width 800} [sidebar/sidebar]]]]]))
;(when @loading? [loading-modal])]))


;; two different panels;
;; show mobile, hide desktop when mobile
;; and vice-versa


;; re-com loading modal doesn't play nicely here...
;; try semantic-ui; may need to put in different position too
#_(when @loading? [loading-modal])


(defn mobile-explore-panel [paintings show-slideshow?]
  [:> semantic-ui/slist
   [:> semantic-ui/slist-item [b]]
   [:> semantic-ui/slist-item [explore paintings show-slideshow?]]])


(defn desktop-explore-panel [paintings show-slideshow?]
  [:> semantic-ui/grid {:columns 2}
   [:> semantic-ui/grid-column [explore paintings show-slideshow?]]
   [:> semantic-ui/grid-column [sidebar/sidebar]]])


(defn explore-panel []
  (let [paintings       (subscribe [::subs/paintings])
        show-slideshow? (subscribe [::subs/show-slideshow?])
        loading?        (subscribe [::subs/query-loading?])]
    [:> semantic-ui/slist
      [:> semantic-ui/responsive {:max-width 799} [mobile-explore-panel @paintings @show-slideshow?]]
      [:> semantic-ui/responsive {:min-width 800} [desktop-explore-panel @paintings @show-slideshow?]]]))
