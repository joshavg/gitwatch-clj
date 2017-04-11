(ns cli-app-frmwk.core-test
  (:require [clojure.test :refer :all]
            [cli-app-frmwk.core :refer :all]))

(deftest test-exit-fn
  (testing "exit-fn"
           (testing ":task returns EXIT_CODE"
                    (is (= ((:task exit-fn) "") {:ret EXIT_CODE})))
           (testing ":test listens to exit"
                    (is ((:test exit-fn) "exit")))))

(deftest test-nvl
  (testing "nvl"
           (testing "returns not-null value"
                    (is (= 5 (nvl nil 5))))
           (testing "returns val on non-null"
                    (is (= 3 (nvl 3 5))))
           (testing "returns nil when both are nil"
                    (is (nil? (nvl nil nil))))))

(def test-config
  {:states [{:test (fn [l] false)
             :task (fn [l] nil)}
            {:test #(= "a" %) :task (fn [l] [5])}]})

(deftest test-handle-states
  (testing "handle-states"
           (testing "finds correct state"
                    (is (= (handle-states test-config "a") [5])))
           (testing "returns NO_STATE and line on unknown input"
                    (is (= (handle-states test-config "b") {:ret NO_STATE})))))
