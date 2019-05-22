(ns landschaften.screens.explore
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.events :as events]
            [landschaften.views.paintings :as paintings]
            [landschaften.views.sidebar :as sidebar]
            [landschaften.semantic-ui :as semantic-ui]))


(defn no-paintings-found []
  [rc/title
   :label "No paintings matching criteria found."
   :level :level3])


(defn explore
  "Find paintings satisfying constraints"
  [current-painting paintings show-max? n-columns]
  (if (empty? paintings)
    [no-paintings-found]
    [paintings/paintings-grid current-painting paintings show-max? n-columns]))


(defn loading-modal [loading?]
  [:> semantic-ui/modal {:open loading?}
   [:> semantic-ui/loader {:size "big"} "Loading..."]])


(defn search-or-results [search?]
  (let [toggle #(dispatch [::events/toggle-mobile-search])]
    [:> semantic-ui/button-group
     [:> semantic-ui/button {:compact true
                             :positive search?
                             :on-click toggle}
                            "search"]
     [:> semantic-ui/button-or]
     [:> semantic-ui/button {:compact true
                             :positive (not search?)
                             :on-click toggle}
                            "paintings"]]))


(defn mobile-explore-panel [current-painting paintings show-slideshow? search?]
  [:> semantic-ui/slist
   [:> semantic-ui/slist-item [search-or-results search?]]
   (if search?
     [:> semantic-ui/slist-item [sidebar/mobile-sidebar]]
     [:> semantic-ui/slist-item [explore current-painting paintings show-slideshow? 2]])])


(defn desktop-explore-panel [current-painting paintings show-slideshow?]
  [:> semantic-ui/grid {:columns 2}
   [:> semantic-ui/grid-column [explore current-painting paintings show-slideshow? 3]]
   [:> semantic-ui/grid-column [sidebar/desktop-sidebar]]])


(defn explore-panel []
  (let [paintings       (subscribe [::subs/paintings])
        current-painting (subscribe [::subs/current-painting])
        show-slideshow? (subscribe [::subs/show-slideshow?])
        loading?        (subscribe [::subs/query-loading?])
        mobile-search?  (subscribe [::subs/mobile-search?])]
    [:> semantic-ui/slist
     [loading-modal @loading?]
     [:> semantic-ui/responsive
      {:max-width 799}
      [mobile-explore-panel @current-painting @paintings @show-slideshow? @mobile-search?]]
     [:> semantic-ui/responsive
      {:min-width 800}
      [desktop-explore-panel @current-painting @paintings @show-slideshow?]]]))
