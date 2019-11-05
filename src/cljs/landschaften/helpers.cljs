(ns landschaften.helpers
  (:require [cljs.spec.alpha :as s]
            [landschaften.specs :as specs]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]))


;; ------------------------------------------------------
;; Utility functions and components
;; ------------------------------------------------------

(defn sort-by-author [paintings]
  (sort :author paintings))

(def log js/console.log)

;; ------------------------------------------------------
;; String manipulation
;; ------------------------------------------------------

(def special-chars
  (let [lower-case "ąàáäâãåæăćčĉęèéëêĝĥìíïîĵłľńňòóöőôõðøśșşšŝťțţŭùúüűûñÿýçżźž"]
    (clojure.string/join [lower-case (clojure.string/upper-case lower-case)])))


(def normal-chars
  (let [lower-case "aaaaaaaaaccceeeeeghiiiijllnnoooooooossssstttuuuuuunyyczzz"]
    (clojure.string/join [lower-case (clojure.string/upper-case lower-case)])))


(def special->normal-char
  (into {}
        (map
          (fn [special normal] {(str special) (str normal)})
          special-chars
          normal-chars)))


(defn replace-special-chars [word]
  (clojure.string/join
    (map
      #(if (clojure.string/includes? special-chars (str %))
         (get special->normal-char (str %))
         (str %))
      word)))