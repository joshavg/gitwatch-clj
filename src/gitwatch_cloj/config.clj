(ns gitwatch-cloj.config
    (:use [clojure.java.io :only (as-file)]))

(def FILE_PATH
    (str (System/getProperty "user.home")
         "/.gitwatch-clojure.clj"))

(defn load-config-string []
    (slurp (str FILE_PATH)))

(defn load-config []
    (read-string (load-config-string)))

(defn save-config [newconf]
    (spit FILE_PATH (.toString newconf)))

(defn init-config-file
    "writes an empty config file"
    []
    (when (not (.exists (as-file FILE_PATH)))
          (spit FILE_PATH (with-out-str (print {:repos {}})))))

(defn add-repo
    [name path]
    (let [gitdir   (str path "/.git")
          conf     (load-config)
          repos    (:repos conf)
          newrepos (merge repos {(keyword name) gitdir})
          newconf  (assoc conf :repos newrepos)]
        (save-config newconf)))

(defn find-repo-path [name]
    (let [conf   (load-config)
          kwname (keyword name)]
        (kwname (:repos conf))))
