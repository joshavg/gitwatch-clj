(ns gitwatch-cloj.system
    (:require [clojure.java.shell :refer [sh]]))

(defn open-git-tool [path tool-cfg]
    (sh (:command tool-cfg) :dir path))

(defn open-repo [path config]
    (if (nil? path)
        ["unknown repo" 10]
        (do
            (open-git-tool (str path "/..") (:git-tool config))
            ["" 0])))
