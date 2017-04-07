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
              ahead    (if (nil? bts) nil (.getAheadCount bts))]
            {:name     name
             :branch   branch
             :clean    (if is-clean "✓" "✗")
             :modified (str (.size (.getModified status)))
             :ahead    (or ahead "?")
             :_show    (or show-unchanged (not is-clean) (> (or ahead 1) 0))})
        (catch NoWorkTreeException e
               {:name     (:name repo-parsed)
                :branch   "check path"
                :clean    "?"
                :modified "?"
                :ahead    "?"
                :_show    true})))

(defn status-table
    [config show-unchanged]
    (with-out-str
        (print-table
            [:name :branch :clean :modified :ahead]
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
                            (:repos config)))))))

(defn status-changed [config]
    (status-table config false))

(defn status-all [config]
    (status-table config true))
