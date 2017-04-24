(ns gitwatch-cloj.config
    (:require [clojure.java.io :refer [as-file]]
              [clojure.string :refer [ends-with?]]
              [cli-app-frmwk.io :refer [prompt]]
              [me.raynes.fs :refer [directory? exists? parent]]))

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
    (when-not (exists? (as-file FILE_PATH))
              (spit FILE_PATH (with-out-str (print empty-config)))))

(defn add-repo
    [name path]
    (let [gitdir   (str path "/.git")
          conf     (load-config)
          repos    (:repos conf)
          newrepos (merge repos {(keyword name) gitdir})
          newconf  (assoc conf :repos newrepos)]
        (save-config newconf))
    {:ret 0 :out ""})

(defn add-resursive-repos [path]
    (let [res (->> (file-seq (as-file path))
                   (filter
                       (fn find-git-dir [f]
                           (and (ends-with? (str f) "/.git")
                                (directory? f))))
                   (map parent)
                   (map
                       (fn to-named-map [f]
                           {:path (str f)
                            :name (prompt (str "Name for " f))}))
                   (filter
                       (fn filter-empty-names [m]
                           (not-empty (:name m))))
                   (map
                       (fn add-to-cfg [m]
                           (add-repo (:name m) (:path m)))))]
        {:ret 0 :out (format "%d repos added" (count res))}))

(defn find-repo-path [name]
    (let [conf    (load-config)
          kw-name (keyword name)]
        (kw-name (:repos conf))))

(defn remove-repo [name]
    (let [cfg (load-config)
          repos (:repos cfg)
          kwname (keyword name)]
        (if (kwname repos)
            (let [newrepos (dissoc repos kwname)
                  newcfg (assoc cfg :repos newrepos)]
                (save-config newcfg)
                {:ret 0 :out ""})
            {:ret 0 :out "unknown repo"})))
