(ns bendyworks.modules
  (:require [clojure.string :as cs]
            [cljs.core :as cc]
            [cljs.env]))

(defmacro env-module-uris []
  (let [module-uris (get-in @cljs.env/*compiler* [:options :module-info :module/uris])]
    `(cc/js-obj ~@(mapcat (fn [[module uri]] [module uri]) module-uris))))

(defmacro env-module-deps []
  (let [module-deps (get-in @cljs.env/*compiler* [:options :module-info :module/deps])]
    `(cc/js-obj ~@(mapcat (fn [[module deps]] [module `(cc/array ~@deps)]) module-deps))))
