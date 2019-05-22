(ns landschaften.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [landschaften.views :as views]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [landschaften.ajax :refer [load-interceptors!]]
            [landschaften.events :as core-events]
            [reitit.core :as reitit]
            [clojure.string :as string]
            [landschaften.db :as db]
            ;; For Clojurescript tracing
            [ghostwheel.tracer])

  (:import goog.History))

(defn nav-link [uri title page]
  [:li.nav-item
   {:class (when (= page @(rf/subscribe [:page])) "active")}
   [:a.nav-link {:href uri} title]])

(defn navbar []
  [:nav.navbar.navbar-dark.bg-primary.navbar-expand-md
   {:role "navigation"}
   [:button.navbar-toggler.hidden-sm-up
    {:type "button"
     :data-toggle "collapse"
     :data-target "#collapsing-navbar"}
    [:span.navbar-toggler-icon]]
   [:a.navbar-brand {:href "#/"} "landschaften"]
   [:div#collapsing-navbar.collapse.navbar-collapse
    [:ul.nav.navbar-nav.mr-auto
     [nav-link "#/" "Home" :home]
     [nav-link "#/about" "About" :about]]]])

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src "/img/warning_clojure.png"}]]]])

(defn home-page []
  [:div.container
   [:div.row>div.col-sm-12
    [:h2.alert.alert-info "Tip: try pressing CTRL+H to open re-frame tracing menu"]]
   (when-let [docs @(rf/subscribe [:docs])]
     [:div.row>div.col-sm-12
      [:div {:dangerouslySetInnerHTML
             {:__html (md->html docs)}}]])])

(def pages
  {:home #'home-page
   :about #'about-page})

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes

(def router
  (reitit/router
    [["/" :home]
     ["/about" :about]]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (let [uri (or (not-empty (string/replace (.-token event) #"^.*#" "")) "/")]
          (rf/dispatch
            [:navigate (reitit/match-by-path router uri)]))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(rf/dispatch [:set-docs %])}))

;; Default Luminus page.
; (defn mount-components []
;   (rf/clear-subscription-cache!)
;   (r/render [#'page] (.getElementById js/document "app")))

;; Your app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [views/hello-world] (.getElementById js/document "app")))

;; called in html file
(defn init! []
  (rf/dispatch-sync [:navigate (reitit/match-by-name router :home)])
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (rf/dispatch-sync [::core-events/initialize-app])
  (rf/dispatch-sync [::core-events/retrieve-artists-names])
  (rf/dispatch-sync [::core-events/retrieve-concepts])
  (rf/dispatch-sync [::core-events/add-default-group? db/example-group])
  (mount-components))



