(ns landschaften.views.utils
  (:require [reagent.core :as r]
            [re-com.core :as rc]
            [landschaften.semantic-ui :as semantic-ui]))

;; ------------------------------------------------------
;; Utility functions and components
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

;; works
(defn replace-special-chars [word]
  (clojure.string/join
    (map
      #(if (clojure.string/includes? special-chars (str %))
         (get special->normal-char (str %))
         (str %))
      word)))


(def log js/console.log)

;; color is a css bootstrap class e.g. "btn btn-warning", "btn btn-info", etc.
(defn ->bubble-button [datum on-button-press color]
  {:pre (string? datum)}
  [rc/button
   :label datum
   :on-click on-button-press
   :class color
   :style {:border-radius "30px"}])


(defn ->table-row [data]
  [rc/h-box :gap "4px" :children (into [] data)]) ;; should already be in a vector?


;; where button-fn is e.g. artist button
(defn button-table [data row-size button-fn]
  {:pre [(int? row-size)]}
  (let [buttons (map button-fn data)
        rows (mapv ->table-row (partition-all row-size buttons))]
    [rc/v-box :gap "4px" :children rows]))

(defn table [data n-per-row]
  {:pre [(int? n-per-row)]}
  (let [rows (partition-all n-per-row data)]
    [:> semantic-ui/slist
     (do
       (log "(count rows): " (count rows))
       (log "rows: " rows)
       (for [row rows]
         ^{:key (first row)}
         [:> semantic-ui/slist {:horizontal true}
          row]))]))

;; create the element first,
;; THEN hand it over to be UI-arranged

(defn ->table-column [data]
  ;(let [boxes (map (fn [datum] [rc/box :size "auto" :child datum]) data)]
  ; [rc/h-box :size "auto" :children (into [] boxes)]) ;; should already be in a vector?
   [rc/v-box :gap "8px" :children (into [] data)]) ;; should already be in a vector?


(defn image-table [data column-size]
  (let [columns (mapv ->table-column (partition-all column-size data))]
    [rc/h-box :gap "8px" :children columns]))


(defn search-suggestions [user-input options suggestion-count]
  (let [matches? #(some?
                    (re-find (re-pattern (str "(?i)" user-input)) (replace-special-chars %)))]
    (->> options
      (filter matches?)
      (take suggestion-count)
      (into []))))


(defn typeahead [placeholder choices on-choose suggestion-count]
  [rc/typeahead
    :data-source #(search-suggestions % choices suggestion-count)
    :placeholder placeholder
    :change-on-blur? true
    :on-change on-choose])
    ; (reset! model ""))]))
    ;; this clears the model everytime you type,
    ;; after initially selecting something


;; sample url:
; (def cu  "https://res.cloudinary.com/dgpqnl8ul/image/upload/gmllxpcdzouaanz0syip.jpg")

(defn src-set-part [cloudinary-url width]
  (-> cloudinary-url
    (clojure.string/replace #"upload/" (str "upload/f_auto,q_70,w_" width "/"))
    (str " " width "w")))

(defn sizes-part [{:keys [width vw]}]
  (str "(min-width: " width "px) " vw "vw"))

;; used for responsive images
(def widths->vw [{:width 256 :vw 20}
                 {:width 512 :vw 40}
                 {:width 768 :vw 50}
                 {:width 1024 :vw 70}
                 {:width 1280 :vw 80}])


(def mid-widths->vw [{:width 256 :vw 30}
                     {:width 512 :vw 50}
                     {:width 768 :vw 50}
                     {:width 1024 :vw 70}
                     {:width 1280 :vw 80}])


(defn responsive-image [image-url widths->vw on-click]
  [:img
    {:on-click on-click
     :sizes (clojure.string/join ", " (map sizes-part widths->vw))
     :src-set (clojure.string/join ", "
                (map
                  #(src-set-part image-url (:width %))
                  widths->vw))
     :src image-url}])


(defn max-responsive-image [image-url widths->vw on-click]
  [:img
   {:on-click on-click
    :style {:max-height "600px"}
    :sizes (clojure.string/join ", " (map sizes-part widths->vw))
    :src-set (clojure.string/join ", "
                                 (map
                                   #(src-set-part image-url (:width %))
                                   widths->vw))
    :src image-url}])
