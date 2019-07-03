(defproject landschaften "0.1.0-SNAPSHOT"
  :description "Analyzing paintings"
  :url "https://github.com/pianostringquartet/landschaften"
  :dependencies [[clj-time "0.14.4"]
                 [cljs-ajax "0.7.3"]
                 [com.cognitect/transit-java "0.8.332"]
                 [conman "0.8.1"]
                 [cprop "0.1.11"]
                 [funcool/struct "1.3.0"]
                 [luminus-immutant "0.2.4"]
                 [luminus-migrations "0.5.0"]
                 [luminus-nrepl "0.1.4"]
                 [luminus/ring-ttl-session "0.3.2"]
                 [markdown-clj "1.0.2"]
                 [metosin/muuntaja "0.5.0"]
                 [metosin/reitit "0.1.2"]
                 [metosin/ring-http-response "0.9.0"]
                 [mount "0.1.12"]
                 [mysql/mysql-connector-java "6.0.5"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.339" :scope "provided"]
                 [org.clojure/tools.cli "0.3.7"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.webjars.bower/tether "1.4.4"]
                 [org.webjars/bootstrap "4.1.1"]
                 [org.webjars/font-awesome "5.1.0"]
                 [org.webjars/webjars-locator "0.34"]
                 [re-frame "0.10.5"]
                 [reagent "0.8.1"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-defaults "0.3.2"]
                 [secretary "1.2.3"]
                 [selmer "1.11.7"]
                 [org.clojure/test.check "0.10.0-alpha3"]
                 [org.clojure/data.json "0.2.6"]
                 [clarifai-clj "1.0.0"]
                 [org.clojure/java.data "0.1.1"]
                 [org.clojure/data.csv "0.1.3"]
                 [proto-repl "0.3.1"]
                 [expound "0.7.1"]
                 [re-com "2.4.0"]
                 [day8.re-frame/tracing-stubs "0.5.1"]
                 [gnl/ghostwheel "0.3.8"]
                 [gnl/ghostwheel.tracer "0.3.8"]
                 [day8.re-frame/test "0.1.5"]
                 [figwheel-sidecar "0.5.18"]
                 [org.clojure/core.async "0.4.474"]
                 [reagent-material-ui "0.2.5"]
                 [cljsjs/chartjs "2.8.0-0"]
                 [cljsjs/semantic-ui-react "0.84.0-0"]]


  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs" "src/cljc" "src/script"] ;; added script for FW
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot landschaften.core
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]
            "build-dev" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]}
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-immutant "2.1.0"]]
  :clean-targets ^{:protect false}
                  [:target-path [:cljsbuild :builds :app :compiler :output-dir]
                                [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
    {:http-server-root "public"
     :nrepl-port 7002
     :css-dirs ["resources/public/css"]
     :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :profiles
    {:uberjar {:omit-source true
               :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
               :cljsbuild
               {:builds
                {:min
                 {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                  :compiler
                  {:output-dir "target/cljsbuild/public/js"
                   :output-to "target/cljsbuild/public/js/app.js"
                   :source-map "target/cljsbuild/public/js/app.js.map"
                   :optimizations :advanced
                   :pretty-print false
                   :closure-warnings
                   {:externs-validation :off :non-standard-jsdoc :off}
                   :externs ["react/externs/react.js"]}}}}
               :aot :all
               :uberjar-name "landschaften.jar"
               :source-paths ["env/prod/clj"]
               :resource-paths ["env/prod/resources"]}
     :dev    [:project/dev :profiles/dev]

     ;; Why multiple
     :test          [:project/dev :project/test :profiles/test]
     :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                    :dependencies [[binaryage/devtools "0.9.10"]
                                   [com.cemerick/piggieback "0.2.2"]
                                   [day8.re-frame/re-frame-10x "0.3.7-react16"]
                                   [doo "0.1.10"]
                                   [expound "0.7.1"]
                                   [figwheel-sidecar "0.5.16"]
                                   [pjstadig/humane-test-output "0.8.3"]
                                   [prone "1.6.0"]
                                   [ring/ring-devel "1.6.3"]
                                   [ring/ring-mock "0.3.2"]
                                   [day8.re-frame/tracing "0.5.1"]]
                    :plugins      [[com.jakemccrary/lein-test-refresh "0.19.0"]
                                   [lein-doo "0.1.10"]
                                   [lein-cljsbuild "1.1.7"]
                                   [org.clojure/clojurescript "1.10.339"]]
                    :cljsbuild
                    {:builds
                     {:app
                      {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                       :figwheel {:on-jsload "landschaften.core/mount-components"}
                       :compiler
                       {:main "landschaften.app"
                        :asset-path "/js/out"
                        :output-to "target/cljsbuild/public/js/app.js"
                        :output-dir "target/cljsbuild/public/js/out"
                        :source-map true
                        :optimizations :none
                        :pretty-print true
                        :external-config {:ghostwheel {:check     true
                                                       :outstrument true
                                                       :num-tests 10}}
                        :closure-defines
                              {"re_frame.trace.trace_enabled_QMARK_" true
                               "day8.re_frame.tracing.trace_enabled_QMARK_"  true}
                        :preloads [day8.re-frame-10x.preload]}}}}
                    :doo {:build "test"}
                    :source-paths ["env/dev/clj"]
                    :resource-paths ["env/dev/resources"]
                    :repl-options {:init-ns user}
                    :injections [(require 'pjstadig.humane-test-output)
                                 (pjstadig.humane-test-output/activate!)]}
     :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                    :resource-paths ["env/test/resources"]
                     :cljsbuild
                     {:builds
                      {:test
                       {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                        :compiler
                        {:output-to "target/test.js"
                         :main "landschaften.doo-runner"
                         :optimizations :whitespace
                         :verbose false
                         :pretty-print true}}}}}
     :profiles/dev {}
     :profiles/test {}})
