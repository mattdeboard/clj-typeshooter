(defproject clj-typeshooter "0.1.0-SNAPSHOT"
  :plugins [[lein-cljsbuild "0.3.3"]
            [com.cemerick/austin "0.1.1"]]
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main clj-typeshooter.core
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1889"]
                 [domina "1.0.0"]
                 [org.clojure/google-closure-library-third-party "0.0-2029"]
                 [com.cemerick/piggieback "0.1.0"]]

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :profiles {:uberjar {:aot :all}}
  :eval-in-leiningen true

  :cljsbuild {:builds {:prod
                       {:source-paths ["src/cljs"]
                        :compiler {:output-to "resources/js/typeshooter.js"
                                   :optimizations :advanced
                                   :pretty-print false}}}}

  :hooks [leiningen.cljsbuild])
