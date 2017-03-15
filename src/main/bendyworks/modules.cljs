(ns bendyworks.modules
  "ClojureScript Wrapper for goog.module.Manager
  This allows dynamically loading ClojureScript modules from the client side

  Here's the ClojureScript setup for getting development working
  ;;Root module namespace
  (ns my.app
    (:require [my.app.extra]
              [bendyworks.modules :as bm]))
    ;;Initalize module manager
    (def module-uris #js {\"extra\"  \"path/to/extra.js\"})
    (def module-deps #js {\"extra\"  #js []})
    (bm/init-manager #js module-uris module-deps)

    (bm/load-module \"extra\" (fn [] (.log js/console \"The extra module has loaded\")))

  ;;Extra Module
  (ns my.app.extra
    (:require [bendyworks.modules :as bm]))

  (bm/set-loaded! \"extra\")

  To set this up in production add this compiler option to yoour build

  :closure-defines {'bendyworks.modules.PRODUCTION true}"
  (:require [goog.events :as ge])
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

(defn init-manager
  "Initializes the Module Manager with the modules uris and and dependencies
  Both parameters (module-uris and module-deps) need to be passed in as JavaScript objects

  (def module-uris
    #js {\"extra\"  \"path/to/extra.js\"
         \"tools\"  \"path/to/tools.js\"
         \"hammer\" \"path/to/hammer.js\"})
  ;; the extra module has no dependencies
  ;; the hammer module has a dependency on tools
  (def module-deps
    #js {\"extra\"  #js []
         \"tools\"  #js []
         \"hammer\" #js [\"tools\"]})

   (init-manager module-uris module-deps)"
  [module-uris module-deps]
  (.setAllModuleInfo manager module-deps)
  (.setModuleUris manager module-uris))

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