(ns conwip.test.extra
  (:require [conwip.modules :as bm]
            [conwip.test.shared :as bts]))

(swap! bts/data (fn [data] (assoc data :extra "works")))

(defmethod bts/cross-module-fn :extra [opts]
  (assoc opts :call-ns "conwip.test.extra"))

(bm/set-loaded! "extra")
