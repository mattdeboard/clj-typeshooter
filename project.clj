(defproject clj-typeshooter "0.1.0-SNAPSHOT"
  :plugins [[lein-cljsbuild "0.3.3"]]
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main clj-typeshooter.core
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.222.0-83d0c2-alpha"]
                 [prismatic/dommy "0.1.1"]
                 [com.cemerick/piggieback "0.1.0"]]

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :profiles {:uberjar {:aot :all}}

  :cljsbuild {:builds {:prod
                       {:source-paths ["src/cljs"]
                        :compiler
                        {:output-to "resources/js/typeshooter.js"
                         :pretty-print true
                         :foreign-libs [{:file "resources/js/simpleGame.js"
                                         :provides ["simplegame"]}]}}}}

  :hooks [leiningen.cljsbuild])
