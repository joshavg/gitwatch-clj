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
              [gitwatch-cloj.system :refer [open-repo]]))

(def EXIT_CODE -256)

(defn prompt [txt]
    (println txt)
    (read-line))

(defn get-param-1 [cmd]
    (nth (split cmd #"\s+") 1 nil))

(defn handle-input
    [input]
    (cond (empty? input)
        [(status-changed (load-config))]
          (= "ls" input)
        [(status-all (load-config))]
          (= "ls-conf" input)
        [(load-config-string)]
          (= "add" input)
        (add-repo (prompt "Name") (prompt "Path"))
          (.startsWith input "open ")
        (open-repo (find-repo-path (get-param-1 input)) (load-config))
          (= "exit" input)
        ["goodbye" EXIT_CODE]
          :else
        ["need help?"]))

(defn mainloop []
    (loop [input (read-line)]
        (let [[output return-code]
                 (handle-input (trim input))
              rc (if (nil? return-code) 0 return-code)]
            (cond (= rc 0)
                (do (println (if (nil? output) "" output))
                    (recur (read-line)))
                  (= rc EXIT_CODE)
                (println output)
                  :else
                (do
                    (println (format "error occured: %s, error code %d" output rc))
                    (recur (read-line)))))))

(defn -main
    [& args]
    (init-config-file)
    (println "Welcome to GitWatch")
    (mainloop))
