(ns akar.syntax-test
  (:require [clojure.test :refer :all]
            [akar.primitives :refer :all]
            [akar.patterns.basic :refer :all]
            [akar.patterns.collection :refer :all]
            [n01se.syntax :as sy]
            [akar.syntax :refer :all])
  (:import [java.io StringWriter]))

(deftest syntax-test

  (testing "Translation of primitives"

    (testing "clause"
      (is (= `(clause* !any (fn [] :val))
             (macroexpand-1 `(clause :_ :val)))))

    (testing "clauses"
      (is (= `(or-else (clause* (!cst 1) (fn [] :one))
                       (clause* !any (fn [] :val)))
             (macroexpand-1 `(clauses 1 :one
                                      :_ :val)))))

    (testing "match"
      (is (= `(match* 3 (or-else (clause* (!cst 1) (fn [] :one))
                                 (clause* !any (fn [] :val))))
             (macroexpand-1 `(match 3
                                    1 :one
                                    :_ :val)))))

    (testing "try-match"
      (is (= `(try-match* 3 (or-else (clause* (!cst 1) (fn [] :one))
                                     (clause* !any (fn [] :val))))
             (macroexpand-1 `(try-match 3
                                        1 :one
                                        :_ :val))))))

  (testing "Translation of patterns"

    (testing "'any' patterns"
      ; Yes, this is a duplicate test.
      (is (= `(clause* !any (fn [] :val))
             (macroexpand-1 `(clause :_ :val))))
      (is (= `(clause* !any (fn [] :val))
             (macroexpand-1 `(clause :any :val)))))

    (testing "number literals"
      (is (= `(clause* (!cst 2) (fn [] :val))
             (macroexpand-1 `(clause 2 :val)))))

    (testing "string literals"
      (is (= `(clause* (!cst 2) (fn [] :val))
             (macroexpand-1 `(clause 2 :val)))))

    (testing "boolean literals"
      (is (= `(clause* (!cst true) (fn [] :val))
             (macroexpand-1 `(clause true :val)))))

    (testing "keyword literals"
      (is (= `(clause* (!cst :kartofell) (fn [] :val))
             (macroexpand-1 `(clause :kartofell :val)))))

    (testing "nil literal"
      (is (= `(clause* (!cst nil) (fn [] :val))
             (macroexpand-1 `(clause nil :val)))))

    (testing "simple binding"
      (is (= `(clause* !var (fn [x] (inc x)))
             (macroexpand-1 `(clause x (inc x))))))

    (testing "arbitrary pattern functions"
      (is (= `(clause* !empty (fn [] :zilch))
             (macroexpand-1 `(clause [!empty] :zilch))))))

  (testing "Sensible syndoc"

    (testing "No terminal should be marked as a rule"
      (is (let [writer (StringWriter.)
                _ (binding [*out* writer]
                    (sy/syndoc match))
                doc (.toString writer)]
            (not (.contains doc "#<")))))))