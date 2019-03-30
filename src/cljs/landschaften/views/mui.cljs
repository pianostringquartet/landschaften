(ns landschaften.views.mui
  (:require [reagent-material-ui.core :as ui]
            [reagent.core :as reagent]
            [landschaften.subs :as subs]))


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
(defn grid [jpg-urls]
  (fn []
    [:div
     [ui/GridList {:cellHeight "160" :cols "3"}
      (for [jpg-url jpg-urls]
        ^{:key jpg-url} [ui/GridTile {:key jpg-url :cols "1"}
                                     [:img {:src jpg-url}]])]]))

(defn home-page []
  (let [paintings (re-frame.core/subscribe [::subs/paintings])]
    [ui/MuiThemeProvider theme-defaults
     [grid (map :jpg (take 50 @paintings))]]))


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