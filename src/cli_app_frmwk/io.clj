(ns cli-app-frmwk.io
    (:require [clojure.pprint]))

(defn prompt [label]
    (println label)
    (read-line))

(defn create-table [headers content]
    (with-out-str (clojure.pprint/print-table headers content)))
