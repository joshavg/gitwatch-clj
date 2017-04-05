(ns gitwatch-cloj.core
    (:gen-class)
    (:require [clojure.string :refer [trim]]
              [gitwatch-cloj.config :refer [load-config-string
                                            init-config-file
                                            add-repo
                                            load-config]]
              [gitwatch-cloj.git :refer [status-changed
                                         status-all]]))

(def EXIT_CODE -256)

(defn prompt
    [txt]
    (println txt)
    (read-line))

(defn handle-input
    [input]
    (cond (empty? input)
        [(status-changed (load-config))]
          (= "ls" input)
        [(status-all (load-config))]
          (= "exit" input)
        ["goodbye" EXIT_CODE]
          (= "ls-conf" input)
        [(load-config-string)]
          (= "add" input)
        (add-repo (prompt "Name") (prompt "Path"))
          :else
        ["need help?"]))

(defn mainloop
    []
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
                (println "error occured" rc)))))

(defn -main
    "I don't do a whole lot ... yet."
    [& args]
    (init-config-file)
    (println "Welcome to GitWatch")
    (mainloop))
