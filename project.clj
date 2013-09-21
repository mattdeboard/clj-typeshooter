(defproject clj-typeshooter "0.1.0-SNAPSHOT"
  :plugins [[lein-cljsbuild "0.3.3"]]
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main clj-typeshooter.core
  :profiles {:uberjar {:aot :all}}
  :eval-in-leiningen true
  :cljsbuild {:builds {:prod
                       {:source-paths ["src/cljs"]
                        :compiler {:output-to "resources/js/typeshooter.js"
                                   :optimizations :advanced
                                   :pretty-print false}}}}
  :hooks [leiningen.cljsbuild])
