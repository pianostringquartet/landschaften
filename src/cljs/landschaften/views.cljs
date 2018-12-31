(ns landschaften.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe]]
            [re-com.core :as rc]
            [landschaften.subs :as subs]
            [landschaften.db :as db]
            [clojure.spec.alpha :as s]
            [landschaften.specs :as specs]
            [landschaften.views.examine :as examine]
            [landschaften.views.preview :as preview]
            [landschaften.views.sidebar :as sidebar]))

(defn hello-world []
 (let [current-painting (subscribe [::subs/current-painting])
       paintings (subscribe [::subs/paintings])]
  [rc/h-box
   :children
     [(if @current-painting
        [examine/examine-painting @current-painting]
        ; [preview/preview @paintings])
        [preview/preview (take 150 @paintings)])
      [sidebar/sidebar]]]))
