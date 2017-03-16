(ns bendyworks.test.shared)

(def data (atom {}))

(defmulti cross-module-fn (fn [opts] (:type opts)))
(defmethod cross-module-fn :default [opts]
  (assoc opts :call-ns "bendyworks.test.shared"))