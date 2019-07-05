(ns landschaften.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [landschaften.views :as views]
            [ajax.core :refer [GET POST]]
            [landschaften.ajax :refer [load-interceptors!]]
            [landschaften.events.core-events :as core-events]
            [landschaften.events.explore-events :as explore-events]
            [landschaften.db :as db]
            [ghostwheel.tracer]) ; for Clojurescript tracing
  (:import goog.History))


(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [views/root-component] (.getElementById js/document "app")))


(defn init! []
  (load-interceptors!)
  (rf/dispatch-sync [::core-events/initialize-app])
  (mount-components))
