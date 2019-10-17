(ns landschaften.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [landschaften.root-view :as views]
            [ajax.core :refer [GET POST]]
            [landschaften.ajax :refer [load-interceptors!]]
            [landschaften.events :as core-events]
            [ghostwheel.tracer]) ; for Clojurescript tracing
  (:import goog.History))


(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [views/root-component] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (rf/dispatch-sync [::core-events/initialize-app])
  (mount-components))
