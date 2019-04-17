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


(defn loading-modal [loading?]
  [:> semantic-ui/modal {:open loading?}
   [:> semantic-ui/loader {:size "big"} "Loading..."]])


(defn b []
  [sidebar/mobile-sidebar])
  ;[:> semantic-ui/button "Hello"])


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
      [loading-modal @loading?]
      [:> semantic-ui/responsive {:max-width 799} [mobile-explore-panel @paintings @show-slideshow?]]
      [:> semantic-ui/responsive {:min-width 800} [desktop-explore-panel @paintings @show-slideshow?]]]))
