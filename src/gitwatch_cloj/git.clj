(ns gitwatch-cloj.git
    (:import org.eclipse.jgit.storage.file.FileRepositoryBuilder
             org.eclipse.jgit.api.Git
             org.eclipse.jgit.lib.BranchTrackingStatus
             org.eclipse.jgit.errors.NoWorkTreeException
             java.io.File)
    (:require [gitwatch-cloj.format :refer [format-table]]))

(defn create-git
    [path]
    (let [repo (.build
                   (.setGitDir
                       (FileRepositoryBuilder.)
                       (File. path)))]
        [(Git. repo) repo]))

(defn status-git
    [repo-parsed show-unchanged]
    (try
        (let [git      (:git repo-parsed)
              repo     (:repo repo-parsed)
              name     (:name repo-parsed)
              branch   (.getBranch repo)
              status   (.call (.status git))
              is-clean (.isClean status)
              bts      (BranchTrackingStatus/of repo branch)
              ahead    (.getAheadCount bts)]
            {:clean    (if is-clean "✓" "✗")
             :branch   branch
             :modified (str (.size (.getModified status)))
             :name     name
             :ahead    (str ahead)
             :_show    (or show-unchanged (not is-clean) (> ahead 0))})
        (catch NoWorkTreeException e
               {:clean    "?"
                :branch   "check path"
                :modified "?"
                :name     (:name repo-parsed)
                :ahead    "?"
                :_show    true})))

(defn status-table
    [config show-unchanged]
    (format-table
        ["name"
         "branch"
         "clean"
         "modified"
         "ahead"]
        (filter #(:_show %)
                (map
                    #(status-git % show-unchanged)
                    (map
                        (fn [repo-entry]
                            (let [path       (val repo-entry)
                                  repo-name  (key repo-entry)
                                  [git repo] (create-git path)]
                                {:name (name repo-name)
                                 :git  git
                                 :repo repo}))
                        (:repos config))))))

(defn status-changed [config]
    (status-table config false))

(defn status-all [config]
    (status-table config true))
