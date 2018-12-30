(ns landschaften.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.db :as db]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.views.examine :as examine]
            [landschaften.views.sidebar :as sidebar]))

(defn hello-world []
 [rc/h-box 
  :children [[examine/examine-painting]
             [sidebar/sidebar]]])
