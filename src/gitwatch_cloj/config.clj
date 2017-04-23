(ns gitwatch-cloj.config
    (:require [clojure.java.io :refer [as-file]]
              [clojure.string :refer [ends-with?]]
              [cli-app-frmwk.io :refer [prompt]]))

(def FILE_PATH
    (str (System/getProperty "user.home")
         "/.gitwatch-clojure.clj"))

(defn load-config-string []
    (slurp (str FILE_PATH)))

(defn load-config []
    (read-string (load-config-string)))

(defn save-config [newconf]
    (spit FILE_PATH (.toString newconf)))

(def empty-config
    {:repos    {}
     :git-tool {:command "x-terminal-emulator" :method "cwd"}
     :version  2})

(defn init-config-file
    "writes an empty config file"
    []
    (when (not (.exists (as-file FILE_PATH)))
          (spit FILE_PATH (with-out-str (print empty-config)))))

(defn add-repo
    [name path]
    (let [gitdir   (str path "/.git")
          conf     (load-config)
          repos    (:repos conf)
          newrepos (merge repos {(keyword name) gitdir})
          newconf  (assoc conf :repos newrepos)]
        (save-config newconf)))

(defn add-resursive-repos [path]
    (->> (file-seq (as-file path))
         (filter
             #(and (ends-with? (str %) "/.git")
               (.isDirectory %)))
         (map #(hash-map :path % :name (prompt (str "Name for " %))))
         (filter #(not-empty? (:name %)))))

(defn find-repo-path [name]
    (let [conf   (load-config)
          kw-name (keyword name)]
        (kw-name (:repos conf))))
