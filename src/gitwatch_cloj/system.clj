(ns gitwatch-cloj.system
    (:require [clojure.java.shell :refer [sh]]
              [clojure.string :refer [split]]
              [gitwatch-cloj.config :refer [find-repo-path]]))

(defn open-emulator [path]
    (sh "x-terminal-emulator" :dir path))

(defn open-repo [cmd]
    (let [splits (split cmd #"\s+")
          name   (nth splits 1 nil)
          path   (find-repo-path name)]
        (if (nil? path)
            ["unknown repo" 10]
            (do
                (open-emulator path)
                ["" 0]))))
