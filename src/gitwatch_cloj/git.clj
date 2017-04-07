(ns gitwatch-cloj.git
    (:import org.eclipse.jgit.storage.file.FileRepositoryBuilder
             org.eclipse.jgit.api.Git
             org.eclipse.jgit.lib.BranchTrackingStatus
             org.eclipse.jgit.errors.NoWorkTreeException
             java.io.File)
    (:require [clojure.pprint :refer [print-table]]))

(defn create-git
    [path]
    (let [repo (.build
                   (.setGitDir
                       (FileRepositoryBuilder.)
                       (File. path)))]
        [(Git. repo) repo]))

(defn fetch-status
    [repo-instances show-unchanged]
    (try
        (let [git      (:git repo-instances)
              repo     (:repo repo-instances)
              name     (:name repo-instances)
              branch   (.getBranch repo)
              status   (.call (.status git))
              is-clean (.isClean status)
              bts      (BranchTrackingStatus/of repo branch)
              ahead    (if (nil? bts) nil (.getAheadCount bts))]
            {:name     name
             :branch   branch
             :clean    (if is-clean "✓" "✗")
             :modified (str (.size (.getModified status)))
             :ahead    (or ahead "?")
             :_show    (or show-unchanged (not is-clean) (> (or ahead 1) 0))})
        (catch NoWorkTreeException e
               {:name     (:name repo-instances)
                :branch   "check path"
                :clean    "?"
                :modified "?"
                :ahead    "?"
                :_show    true})))

(defn table-content
    [config show-unchanged]
    (map
        #(fetch-status % show-unchanged)
        (map
            (fn [[repo-name path]]
                (let [[git repo] (create-git path)]
                    {:name (name repo-name)
                     :git  git
                     :repo repo}))
            (:repos config))))

(defn status-table
    [config show-unchanged]
    (with-out-str
        (print-table
            [:name :branch :clean :modified :ahead]
            (sort-by :name
                     (filter :_show
                             (table-content config show-unchanged))))))

(defn status-changed [config]
    (status-table config false))

(defn status-all [config]
    (status-table config true))
