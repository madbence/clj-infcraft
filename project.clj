(defproject infcraft "0.0.1-SNAPSHOT"
  :description "Very serious application"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.jogamp.jogl/jogl-all-main "2.3.2"]
                 [org.jogamp.gluegen/gluegen-rt-main "2.3.2"]]
  :main ^:skip-aot infcraft.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
