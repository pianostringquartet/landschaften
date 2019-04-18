(ns landschaften.views.mui
  (:require [reagent-material-ui.core :as ui]
            [reagent.core :as reagent]
            [landschaften.subs :as subs]
            [landschaften.events :as events]
            [landschaften.views.examine :as examine]
            [landschaften.views.utils :as utils]
            [re-frame.core :as rf :refer [subscribe dispatch]]
            [day8.re-frame-10x.utils.re-com :as rc]
            [landschaften.views.sidebar :as sidebar]
            [landschaften.semantic-ui :as semantic-ui]))
            ;[landschaften.mui-items :as mui-items]))


; example how to use M-UI in app

;; some helpers
(def el reagent/as-element)
(defn icon [nme] [ui/FontIcon {:className "material-icons"} nme])
(defn color [nme] (aget ui/colors nme))

;; create a new theme based on the dark theme from Material UI
(defonce theme-defaults {:muiTheme (ui/getMuiTheme
                                     (-> ui/darkBaseTheme
                                         (js->clj :keywordize-keys true)
                                         (update :palette merge {:primary1Color (color "amber500")
                                                                 :primary2Color (color "amber700")})
                                         clj->js))})

(defn simple-nav []
  (let [is-open? (reagent/atom false)
        close #(reset! is-open? false)]
    (fn []
      [:div
       [ui/AppBar {:title "yipgo" :onLeftIconButtonTouchTap #(reset! is-open? true)}]
       [ui/Drawer {:open @is-open? :docked false}
        [ui/List
         [ui/ListItem {:leftIcon (el [:i.material-icons "home"])
                       :on-click (fn []
                                   (js/console.log "ListItem was clicked, dog")
                                   (close))}
          "Home"]
         [ui/Divider]]]])))



;; official mui site uses GridListItem:
;; https://material-ui.com/demos/grid-list/

;; but cljs mui lib has only GridTile in macros listed?
;;
;(defn grid [paintings show-max?]
;  [ui/GridList {:cellHeight 160 :cols 3}
;   (for [painting paintings]
;     ^{:key (:jpg painting)}
;     [ui/GridTile
;      {:key (:jpg painting)}
;      [:img {:src (:jpg painting)
;             :on-click #(re-frame.core/dispatch [::events/painting-tile-clicked painting])}]])])


(defn grid-tile [painting]
  ;
   [ui/GridTile
  ;[mui-items/GridTile
    {:key (:jpg painting)}
    [:img {:src (:jpg painting)
           :on-click #(re-frame.core/dispatch [::events/painting-tile-clicked painting])}]])

#_(defn grid-tile [painting]
    ;
    [ui/GridTile
     ;[mui-items/GridTile
     {:key (:jpg painting)}
     [:> semantic-ui/image
      {:fluid true
       :src (:jpg painting)
       :style {:padding "8px"}
       :on-click #(re-frame.core/dispatch [::events/painting-tile-clicked painting])}]])


;(defn grid [paintings show-max? current-painting]
;  [:div
;   (when show-max? [examine/details-slideshow-modal-image current-painting show-max?])
;   [ui/GridList {:cellHeight 160 :cols 3}
;   ;[mui-items/GridList {:cellHeight 160 :cols 3}
;    (for [painting paintings]
;      ^{:key (:jpg painting)}
;      [grid-tile painting])]])


(defn grid [paintings show-max? current-painting n-columns]
  ;[:div
  {:pre [(pos? n-columns)]}
  (if show-max?
    [examine/details-slideshow-modal-image current-painting show-max?]
    [ui/GridList {:cellHeight 160 :cols n-columns}
     ;[mui-items/GridList {:cellHeight 160 :cols 3}
     (for [painting paintings]
       ^{:key (:jpg painting)}
       [grid-tile painting])]))


(defn mui-grid [paintings show-max? n-columns]
  (let [current-painting (re-frame.core/subscribe [::subs/current-painting])]
    [ui/MuiThemeProvider theme-defaults
     [grid paintings show-max? @current-painting n-columns]]))


(defn material-grid-tile [key datum]
  [ui/GridTile
   {:key key}
   datum])


(defn material-grid [data n-cols]
  [ui/MuiThemeProvider theme-defaults
   [ui/GridList ;{:cols n-cols}
    (map-indexed material-grid-tile data)]])



(defn mui-explore [paintings show-slideshow? loading? mobile?]
 [:div "love"])
 ;[mui-items/Grid "container" {:spacing 24}
 ;  [mui-items/Grid "item" {:xs 12}
 ;                         [mui-items/Paper "x=12"]]
 ;  [mui-items/Grid "item" {:xs 6}
 ;                         [mui-items/Paper "x=6"]]
 ;  [mui-items/Grid "item" {:xs 6}
 ;                         [mui-items/Paper "x=6"]]])


;; must nest MUI comps for MuiThemeProvider?; or can reuse MUiTheme provider?
(defn mui-explore-panel []
  (let [paintings (subscribe [::subs/paintings])
        show-slideshow? (subscribe [::subs/show-slideshow?])
        loading? (subscribe [::subs/query-loading?])
        mobile? (subscribe [::subs/mobile?])]
    [ui/MuiThemeProvider theme-defaults
     [mui-explore @paintings @show-slideshow? @loading? @mobile?]]))

    ;[rc/h-box
    ; :justify :between
    ; :gap "16px"
    ; :margin "16px"
    ; :style {:padding-left "8px" :padding-right "8px"}
    ; :children [[explore @paintings @show-slideshow?]
    ;            [sidebar/sidebar]
    ;            (when @loading? [loading-modal])]]))



;(defn home-page []
;  [ui/MuiThemeProvider theme-defaults
;    [re-com.core/v-box :children [[simple-nav]
;                                  [re-com.core/label :label "Welcome to a simple, example application."]
;                                  [:h4 "Welcome to a simple, example application."]]]])



;; this divs etc. won't play well with re-com flex...
;(defn home-page []
;  [ui/MuiThemeProvider theme-defaults
;   [:div
;    [simple-nav]
;    [:div
;     [:h2 "Welcome to a simple, example application."]]]])