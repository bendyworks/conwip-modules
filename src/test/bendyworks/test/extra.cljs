(ns bendyworks.test.extra
  (:require [bendyworks.modules :as bm]
            [bendyworks.test.shared :as bts]))

(swap! bts/data (fn [data] (assoc data :extra "works")))

(defmethod bts/cross-module-fn :extra [opts]
  (assoc opts :call-ns "bendyworks.test.extra"))

(bm/set-loaded! "extra")
