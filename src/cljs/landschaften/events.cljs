(ns landschaften.events
  (:require [re-frame.core :refer [reg-cofx inject-cofx after dispatch reg-event-db reg-sub reg-event-fx reg-fx]]
            [landschaften.db :as db]
            [ajax.core :refer [POST GET]]
            [landschaften.view-specs :as ui-specs]
            [landschaften.specs :as specs]
            [landschaften.helpers :as helpers]
            [cljs.spec.alpha :as s]
            [clojure.walk :refer [keywordize-keys]]
            [ghostwheel.core :refer [check >defn >defn- >fdef => | <- ?]]
            [landschaften.config :refer [service-url]]))


(def ARTISTS-ENDPOINT (str service-url "/artists"))
(def CONCEPTS-ENDPOINT (str service-url "/concepts"))


;; ------------------------------------------------------
;; Checking data
;; ------------------------------------------------------

(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))


;; ------------------------------------------------------
;; Persisting data
;; ------------------------------------------------------

(def ls-auth-key "landschaften-session-data")

;; BUG?: When fn used in interceptor chain,
;; Ghostwheel thinks `state` is [event-id, event-handler's received argument]
;; -- but printing `state` shows `state` is app-db as expected.
(defn ->localstore! [state]
  (.setItem js/localStorage ls-auth-key state))


(reg-cofx
  :user-session
  (fn user-session [cofx _]
    (let [data-from-local-storage (cljs.reader/read-string
                                    (some->> (.getItem js/localStorage ls-auth-key)))]
      (assoc cofx :user-session data-from-local-storage))))


;; ------------------------------------------------------
;; Interceptors
;; ------------------------------------------------------

(def spec? (after (partial check-and-throw ::specs/app-db)))

(def persist (after ->localstore!))

(def check-and-persist-interceptors [spec? persist])


;; ------------------------------------------------------
;; HTTP Requests
;; ------------------------------------------------------


;; TODO: Handle subset of errors; log rest to external logs
(defn default-error-handler [response]
  (helpers/log "Encountered unexpected error: " response))


(reg-fx
  :post-request
  (fn post-request-handler
    [{uri :uri params :params handler :handler error-handler :error-handler
      :or {error-handler default-error-handler}}]
    (POST uri {:params params
               :handler handler
               :error-handler error-handler
               :format :text
               :response-format :json})))


(reg-fx
  :get-request
  (fn get-request-handler
    [{uri :uri handler :handler error-handler :error-handler
      :or {error-handler default-error-handler}}]
    (GET uri {:handler handler :error-handler error-handler})))


;; ------------------------------------------------------
;; Initializing the app
;; ------------------------------------------------------


(reg-event-fx
  ::initialize-app
  [(inject-cofx :user-session)] ; an interceptor
  (fn initialize-app [cofx _]
    (let [persisted-db (:user-session cofx)]
      (if (s/valid? ::specs/app-db persisted-db)
        {:db persisted-db}
       {:db         db/demo-db
        :dispatch-n (list [::retrieve-artists-names]
                          [::retrieve-concepts])}))))

(reg-event-fx
  ::retrieve-artists-names
  (fn query [cofx _]
    {:get-request {:uri ARTISTS-ENDPOINT
                   :handler #(dispatch [::artists-names-retrieved (:artists (keywordize-keys %))])}}))

(reg-event-fx
  ::retrieve-concepts
  (fn query [cofx _]
      {:get-request {:uri CONCEPTS-ENDPOINT
                     :handler #(dispatch [::concepts-retrieved (:conceptNames (keywordize-keys %))])}}))


(reg-event-db
  ::concepts-retrieved
  check-and-persist-interceptors
  (fn concepts-retrieved [db [_ artists]]
    (assoc db :all-concepts (into #{} artists))))


(reg-event-db
  ::artists-names-retrieved
  check-and-persist-interceptors
  (fn artists-names-retrieved [db [_ artists]]
    (assoc db :all-artists (into #{} artists))))


;; ------------------------------------------------------
;; UI-related events
;; ------------------------------------------------------

(reg-event-db
  ::mode-changed
  check-and-persist-interceptors
  (fn mode-changed [db [_ new-mode]]
    {:pre [(s/valid? ::ui-specs/mode new-mode)]}
    (assoc db :current-mode new-mode)))


(reg-event-db
  ::toggle-mobile-search
  check-and-persist-interceptors
  (fn mobile-search-toggled [db]
    (update db :mobile-search? not)))