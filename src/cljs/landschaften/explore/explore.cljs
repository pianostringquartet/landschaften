(ns landschaften.explore.explore
  (:require [re-frame.core :refer [subscribe dispatch]]
            [re-com.core :as rc]
            [landschaften.explore.explore-subs :as explore-subs]
            [landschaften.events :as core-events]
            [landschaften.specs :as specs]
            [landschaften.explore.paintings :as paintings]
            [landschaften.explore.sidebar :as sidebar]
            [landschaften.semantic-ui :as semantic-ui]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]
            [cljs.spec.alpha :as s]))

;; ------------------------------------------------------
;; Explore paintings
;; - specify constraints and search for paintings
;; ------------------------------------------------------

(defn no-paintings-found []
  [rc/title
   :label "No paintings matching criteria found."
   :level :level3])


(>defn explore
  "Find paintings satisfying constraints"
  [current-painting paintings paintings-count show-max? n-columns]
  [(s/nilable ::specs/painting) ::specs/paintings int? boolean? int? => vector?]
  (if (empty? paintings)
    [no-paintings-found]
    [paintings/paintings-grid current-painting paintings paintings-count show-max? n-columns]))


(defn loading-modal [loading?]
  [:> semantic-ui/modal {:open loading?}
   [:> semantic-ui/loader {:size "big"} "Loading..."]])


(>defn search-or-results-button! [search?]
  [boolean? => vector?]
  (let [toggle #(dispatch [::core-events/toggle-mobile-search])]
    [:> semantic-ui/button-group
     [:> semantic-ui/button {:compact true :positive search? :on-click toggle}
      "search"]
     [:> semantic-ui/button-or]
     [:> semantic-ui/button {:compact true :positive (not search?) :on-click toggle}
      "paintings"]]))


(defn mobile-explore-panel [current-painting paintings paintings-count show-slideshow? search?]
  [:> semantic-ui/grid {:columns 1 :centered true :padded true}
   [:> semantic-ui/slist {:relaxed true}
    [:> semantic-ui/slist-item
     [search-or-results-button! search?]]
    (if search?
      [:> semantic-ui/slist-item [sidebar/mobile-sidebar]]
      [:> semantic-ui/slist-item [explore current-painting paintings paintings-count show-slideshow? 2]])]])


;(>defn desktop-explore-panel [current-painting paintings paintings-count show-slideshow? concept-frequencies]
;  [(s/nilable ::specs/current-painting) ::specs/paintings int? boolean? ::specs/concept-frequencies => vector?]
;  [:> semantic-ui/grid {:columns 2}
;   [:> semantic-ui/grid-column [explore current-painting paintings paintings-count show-slideshow? 3]]
;   ;; sidebar only needs concept-frequencies
;   [:> semantic-ui/grid-column [sidebar/desktop-sidebar paintings concept-frequencies]]])

(>defn desktop-explore-panel [current-painting paintings paintings-count show-slideshow? concept-frequencies]
  [(s/nilable ::specs/current-painting) ::specs/paintings int? boolean? ::specs/concept-frequencies => vector?]
  [:> semantic-ui/grid {:columns 2}
   [:> semantic-ui/grid-column [explore current-painting paintings paintings-count show-slideshow? 3]]
   ;; sidebar only needs concept-frequencies
   [:> semantic-ui/grid-column [sidebar/desktop-sidebar paintings concept-frequencies]]])

(def log js/console.log)

(defn explore-panel []
  (let [paintings               (subscribe [::explore-subs/paintings])
        current-painting        (subscribe [::explore-subs/current-painting])
        concept-frequencies     (subscribe [::explore-subs/concept-frequencies])
        painting-ids            (subscribe [::explore-subs/painting-ids])
        show-painting-modal?    (subscribe [::explore-subs/show-painting-modal?])
        loading?                (subscribe [::explore-subs/query-loading?])
        mobile-search?          (subscribe [::explore-subs/mobile-search?])
        current-painting-window (subscribe [::explore-subs/current-painting-window])]
      [:> semantic-ui/slist
       [loading-modal @loading?]
       [:> semantic-ui/responsive {:max-width 799}
        [mobile-explore-panel @current-painting @paintings (count @painting-ids) @show-painting-modal? @mobile-search?]]
       [:> semantic-ui/responsive {:min-width 800}
        [desktop-explore-panel
         @current-painting
         (or @current-painting-window @paintings) ;; default to all paintings if windows not relevant
         (count @painting-ids)
         @show-painting-modal?
         @concept-frequencies]]]))
