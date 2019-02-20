(ns landschaften.views.stats
 (:require [reagent.core :as r]
           [re-frame.core :refer [subscribe dispatch]]
           [re-com.core :as rc]
           [clojure.spec.alpha :as s]
           [landschaften.events :as events]
           [landschaften.specs :as specs]
           [landschaften.views.utils :as utils]
           [landschaften.views.graph :as graph]))


;; for calculating `error` (later, cluster analysis?)
