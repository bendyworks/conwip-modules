(ns bendyworks.modules
  (:require [cljs.core :as cc]
            [cljs.env]))

(defmacro env-module-uris
  "Gets the modules URI loaction from the compiler option
  :module-info {:moudle/uris <module-uris>} and turns it into
  a JavaScript object for ClojureScript consumption

  For example

  :module-info {:module/uris {\"dev\"   \"js/dev.js\"
                              \"extra\" \"js/extra.js\"}}

  is transformed into

  #js {\"dev\"   \"js/dev.js\"
       \"extra\" \"js/extra.js\"}"
  []
  (let [module-uris (get-in @cljs.env/*compiler* [:options :module-info :module/uris])]
    `(cc/js-obj ~@(mapcat (fn [[module uri]] [module uri]) module-uris))))

(defmacro env-module-deps []
  "Gets the modules dependency information from the compiler option
  :module-info {:moudle/deps <module-deps>} and turns it into a
  JavaScript object for ClojureScript consumption

  For example

  :module-info {:module/deps {\"dev\"    []
                              \"tools\"  []
                              \"hammer\" [\"tools\"]}

  is transformed into

  #js {\"dev\"    #js []
       \"tools\"  #js []
       \"hammer\" #js[\"tools\"]}"
  (let [module-deps (get-in @cljs.env/*compiler* [:options :module-info :module/deps])]
    `(cc/js-obj ~@(mapcat (fn [[module deps]] [module `(cc/array ~@deps)]) module-deps))))