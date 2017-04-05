(defproject gitwatch-cloj "0.1.0"
    :description "FIXME: write description"
    :url "https://github.com/joshavg/gitwatch-clj"
    :license
    {:name "GNU GPL v3"
     :url  "https://www.gnu.org/licenses/gpl-3.0.html"}
    :dependencies [[org.clojure/clojure "1.8.0"]
                   [org.eclipse.jgit/org.eclipse.jgit "4.4.1.201607150455-r"]]
    :main ^:skip-aot gitwatch-cloj.core
    :target-path "target/%s"
    :profiles {:uberjar {:aot :all}})
