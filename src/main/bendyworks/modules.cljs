(ns bendyworks.modules
  "ClojureScript Wrapper for goog.module.Manager
  This allows dynamically loading ClojureScript modules from the client side

  Here's an sample project using dynamic loading

  Setup these ClojureScript namespaces
  ;;Root module namespace
  (ns my.app
    (:require [bendyworks.modules :as bm]))

    (bm/load-module \"extra\" (fn [] (.log js/console \"The extra module has loaded\")))

  ;;Extra Module
  (ns my.app.extra
    (:require [bendyworks.modules :as bm]))

  (bm/set-loaded! \"extra\")

  In your build / compiler settings

  :module-info {:module/uris {\"extra\" \"path/to/extra.js\"
                              \"dev\" \"path/to/dev.js\"}
                :module/deps {\"extra\" []
                              \"dev\" []}}

  In Development add all your module namespaces to :preloads

  :preloads '[my.app.extra]

  To set this up in production add this compiler option to your build

  :closure-defines {'bendyworks.modules.PRODUCTION true}"
  (:require [goog.events :as ge])
  (:require-macros [bendyworks.modules :refer [env-module-uris env-module-deps]])
  (:import goog.module.ModuleManager
           goog.module.ModuleLoader
           goog.Timer))

(goog-define
  ^{:doc "Closure define boolean marker for if we are loading modules in production.
  Use the following compiler option to load modules in
  production (:simple or :advanced optimization)
  :closure-defines {'bendyworks.modules.PRODUCTION true}"}
  PRODUCTION false)

(def manager (.getInstance goog.module.ModuleManager))
(def loader (goog.module.ModuleLoader.))
(.setLoader manager loader)
(.setAllModuleInfo manager (env-module-deps))
(.setModuleUris manager (env-module-uris))

(defn get-module-info
  "Get the module information for a module
  Returns a goog.modules.ModuleInfo object"
  [id]
  (.getModuleInfo manager id))

(defn ^boolean loaded? [id]
  "Checks if a module has been loaded"
  (if-let [module (.getModuleInfo manager id)]
    (.isLoaded module)
    false))

(defn- load-module-dev
  "Loads a module then fires a callback. The callback function takes no arguements

  In development (:none optimizations) modules are automatically loaded so we fire off
  a timer that checks every 100 milliseconds if the module has been loaded.

  (load-module-dev \"extra\" (fn [] (.log js/console \"The extra module has loaded\")))"
  [id callback]
  (let [interval (goog.Timer. 100)
        tick-fn (fn [_]
                  (when (loaded? id)
                    (.stop interval)
                    (ge/removeAll interval)
                    (callback)))]
    (ge/listen interval "tick" tick-fn)
    (.start interval)))

(defn- load-module-prod
  "Loads a module then fires a callback. The callback function takes no arguements

  This is for production (:simple or :advanced optimizations) module loading

  (load-module-dev \"extra\" (fn [] (.log js/console \"The extra module has loaded\")))"
  [id callback]
  (.execOnLoad manager id callback))

(def
  ^{:doc "Loads a module then fires a callback. The callback function
  takes no arguements

  (load-module \"extra\" (fn [] (.log js/console \"The extra module has loaded\")))"}
  load-module
  (if ^boolean PRODUCTION
    load-module-prod
    load-module-dev))

(defn set-loaded!
  "Tells goog.module.Manager that a module has loaded. set-loaded! needs to
  be called in a namespace in a module.

  For example say we have a extra moudle

  :modules {:extra {:output-to \"path/to/extra.js\"
                    :entries #{\"my.app.extra\"
                               \"my.app.bouns\"}}}

  The we would need to call set-loaded! in the my.app.extra namsspace

  (ns my.app.extra
    (:require [bendyworks.modules :as bm]))

  (bm/set-loaded! \"extra\")

  or my.app.bonus namespaces.

  (ns my.app.bonus
    (:require [bendyworks.modules :as bm]))

  (bm/set-loaded! \"extra\")

  set-loaded! only needs to be called for one namesapace in the module"
  [id]
  (-> goog.module.ModuleManager .getInstance (.setLoaded id)))