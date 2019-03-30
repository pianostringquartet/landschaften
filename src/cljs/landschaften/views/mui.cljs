(ns landschaften.views.mui
  (:require [reagent-material-ui.core :as ui]
            [reagent.core :as reagent]))


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

(defn home-page []
  [ui/MuiThemeProvider theme-defaults
   [:div
    [simple-nav]
    [:div
     [:h2 "Welcome to a simple, example application."]]]])