(ns cli-app-frmwk.core
    (:require [cli-app-frmwk.io :refer [prompt]]))

(def EXIT_CODE -256)

(def NO_STATE -10)

(def exit-fn
    {:test #(= "exit" %) :task (fn [l] {:ret EXIT_CODE})})

(defn nvl [val nn]
    (if (nil? val) nn val))

(defn handle-states [config line]
    (let [states   (:states config)
          filtered (filter #((:test %) line) states)]
        (if-not (empty? filtered)
                (let [first-state (first filtered)
                      state-task  (:task first-state)]
                    (state-task line))
                {:ret NO_STATE})))

(defn run-lifecycle
    "config: {
      :welcome string
      :states [
        {:test func :task func}
      ]}"
    [config]
    (println (nvl (:welcome config) "Welcome"))
    (loop [line (prompt "=> ")]
        (let [{ret-state :ret out :out}
                 (handle-states config line)]
            (when-not (nil? out)
                      (println out))
            (when-not (= ret-state EXIT_CODE)
                      (recur (prompt "=> "))))))
