(ns bendyworks.test.runner
  (:require [bendyworks.modules :as bm]
            [bendyworks.test.shared :as shared]
            ;; Keep extra loaded in dev mode
            [bendyworks.test.extra]
            [cljs.test :refer-macros [is async are deftest run-tests]]))

(enable-console-print!)
;; Moudles are automatically loaded in development mode
(if ^boolean bm/PRODUCTION
  (deftest extra-not-auto-loaded-in-production
    (is (false? (bm/loaded? "extra")))
    (is (nil? (get @shared/data :extra)))
    (is (= {:call-ns "bendyworks.test.shared"} (shared/cross-module-fn {})))))

(deftest dynamic-module-load
  (async load-module
    (bm/load-module "extra"
      (fn [] (is (true? (bm/loaded? "extra")))
        (is (= "works" (get @shared/data :extra)))
        (is (= {:call-ns "bendyworks.test.extra" :type :extra} (shared/cross-module-fn {:type :extra})))
        (load-module)))))

(run-tests)