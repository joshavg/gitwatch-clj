(ns gitwatch-cloj.core
    (:gen-class)
    (:require [clojure.string :refer [trim split]]
              [gitwatch-cloj.config :refer [load-config-string
                                            init-config-file
                                            add-repo
                                            load-config
                                            find-repo-path]]
              [gitwatch-cloj.git :refer [status-changed
                                         status-all]]
              [gitwatch-cloj.system :refer [open-repo]]
              [cli-app-frmwk.core :refer [exit-fn
                                          run-lifecycle]]
              [cli-app-frmwk.io :refer [prompt]]))

(defn get-param-1 [cmd]
    (nth (split cmd #"\s+") 1 nil))

(defn -main
    [& args]
    (init-config-file)
    (run-lifecycle
        {:welcome "Welcome to GitWatch"
         :states  [{:test #(empty? %)
                    :task (fn [l] {:out (status-changed (load-config))})}
                   {:test #(= "ls" %)
                    :task (fn [l] {:out (status-all (load-config))})}
                   {:test #(= "ls-conf" %)
                    :task (fn [l] {:out (load-config-string)})}
                   {:test #(= "add" %)
                    :task (fn [l] (add-repo (prompt "Name") (prompt "Path")))}
                   {:test #(.startsWith % "open")
                    :task #(open-repo (find-repo-path (get-param-1 %)) (load-config))}
                   exit-fn
                   {:test (fn [l] true)
                    :task (fn [l] {:out "need help?"})}]}))
