# Conwip Modules
## Dynamic Module Loading for ClojureScript

Conwip Modules allows you to dynamically load ClojureScript modules from the client side. This is a wrapper around the
Google Closure Library's Module Manager `goog.module.Manager`.

### Leiningen / Boot

```clojure
[conwip.modules "0.1.0"]
```

### Terminology

**Development** refers to ClojureScript compiled with `:none` or `:whitespace` optimizations

**Production** refers to ClojureScript compiled with `:simple` or `:advanced` optimizations

### Basic Example

Our example application has two modules `:dev` the root module and `:extra` the module to be loaded dynamically

```clojure
:modules {:extra {:output-to "path/to/extra.js"
                  :entries #{"my.app.extra"}
                  :depends-on #{:dev}}
          :dev   {:output-to "path/to/dev.js"
                  :entries #{"my.app.core"}}}
```

The `my.app.core` namespace looks like this

```clojure
(ns my.app.core
  (:require [conwip.modules :as cm]))

(cm/load-module "extra" (fn [] (.log js/console "The extra module has loaded")))
```

It loads the `extra` module without any extra boiler plate and can dynamically load `extra` in both Development and
Production

Below is the ClojureScript and compiler options plumbing needed to get this working

**ClojureScript Side**

ClojureScript modules need to be marked as loaded with `conwip.modules/set-loaded!`. To do this go into a namespace in
one of your modules, in our example case the `my.app.extra` namespace of the `extra` module.

```clojure
(ns my.app.extra
  (:require [conwip.modules :as cm]))

(cm/set-loaded! "extra")
````
The `set-loaded!` call is needed to relay that a module has been loaded to the Google Closure Library module manager `goog.module.ModuleManager`

**Module Information Compiler Option**

Conwip Modules adds a new compiler option `:module-info` to pass in ClojureScript module dependencies.  To dynamically
load ClojureScript Modules the module URI and dependency information is needed.  Module URI information is passed in
via `:module/uris` and module dependencies via `:module/deps`. Module id's need to be in passed as strings not  keywords.

```clojure
:module-info {:module/uris {"extra" "path/to/extra.js"
                            "dev" "path/to/dev.js"}
              :module/deps {"extra" []
                            "dev" []}}
```

**Development Compiler Options**

In development we need to add all of the dynamically loaded module namespaces to the `:preloads` compiler option. There
is only the `extra` module so our `:preloads` needs to have `my.app.extra` in it.

```clojure
:preloads '[my.app.extra]
```

Putting the dynamically loaded module namespaces in `:preloads` allows development to work the same as production
without having to add unnecessary boilerplate to our ClojureScript code.

**Production**

To enable Conwip Modules in production set `conwip.modules.PRODUCTION` to `true` through [`:closure-defines`](https://clojurescript.org/reference/compiler-options#closure-defines)

```clojure
:closure-defines {'conwip.modules.PRODUCTION true}
```

### Usage

All functionality is in the `conwip.modules` namespace

**`(loaded? module-id)`**

```clojure
(if  (loaded? "my-module")
  "module loaded"
  "module has not loaded")
```

Checks if a module has been loaded or not

**`(get-module-info module-id)`**

```clojure
(let [module-info (get-module-info "my-module")
  {:loaded? (.isLoaded module-info)
   :uris (.getUris module-info)})
```

Returns a `goog.module.ModuleInfo` object for the ClojureScript module

**`(load-module module-id callback)`**

```clojure
(load-module "my-module" (fn [] (.log js/console "The module has loaded")))
```

Loads a ClojureScript module and fires a callback function when finished. No arguments are passed to the callback function

**`(set-loaded! module-id)`**

```clojure
(set-loaded! "my-module")
```

Mark a module as loaded. If this is not done then `loaded?` will never return `true` and the callback for `load-module`
will never fire.

For a module with multiple namespaces like

```clojure
:modules {:colors {:output-to "path/to/colors.js"
                   :entries #{"my.app.red" "my.app.blue"}}}
```

`set-loaded!` only needs to be called in one of the namespaces like so

```clojure
(ns [my.app.blue]
  (:require [conwip.modules :as cm]))

(cm/set-loaded! "colors")
```

adding `set-loaded!` to the `my.app.red` namespace will not cause any harm it will just be redundant.

### Compiler settings

**`:module-info`**

The `:module-info` compiler option is required for Conwip Modules to work. Module URI and dependency information is needed
for the Google Closure Library Module Manager to work properly. Both module URI's (through `:module/uris` and module
dependencies (through `:module/deps` are required

```clojure
:module-info {:module/uris {"red"    "path/to/red.js"
                            "colors" "path/to/colors.js"
                            "core"   "path/to/core.js"}
              :module/deps {"red"    ["colors"]
                            "colors" []
                            "core"   []}}
```

**`:preloads`**

In development all namespaces in modules need to be in the `:preloads` compiler option.

For these modules

```clojure
:modules {:colors {:output-to "path/to/colors.js"
                   :entries #{"my.app.red" "my.app.blue"}}
          :fruit   {:output-to "path/to/fruit.js"
                    :entries #{"my.app.apple" "my.app.orange"}}}
```

The following `:preloads` are needed

```clojure
:preloads '[my.app.red my.app.blue my.app.apple my.app.orange]
```

**`:closure-defines`**

Setting the define `conwip.modules.PRODUCTION` to true turns module loading from development to production

```clojure
:closure-defines {'conwip.modules.PRODUCTION true}
```

### Node Support

Dynamically loading modules inside a node application is not feasible with the current Google Closure Library [Module Loader](https://google.github.io/closure-library/api/goog.module.Loader.html). The module loader was designed for dynamic loading in the browser and follows this algorithm to load modules

- Get all the uris (in dependecy order) for a given module
- Retrieve the raw JavaScript (text) of all the files pointed to via the uris
- Load the JavaScript into the global scope through the `eval` function

This is incompatible with the way Node's module scope works. Each module would need to correctly import it's dependencies and export all variables it created. See this Google Closure issue for more details google/closure-compiler#2406

### Potential Deprecation Notice

The current functionality of `conwip-modules` may be getting rolled into ClojureScript under a compiler option of `:module-loader` (see ClojureScript ticket [2077](https://dev.clojure.org/jira/browse/CLJS-2077)). This *may* be similar to how [Shadow CLJS](https://github.com/thheller/shadow-cljs/wiki/ClojureScript-for-the-browser) currently works.

See [cljs-dev 2017-06-09](https://clojurians-log.clojureverse.org/cljs-dev/2017-06-09.html) and [cljs-dev 2017-06-10](https://clojurians-log.clojureverse.org/cljs-dev/2017-06-10.html) for the relevant discussions. The ClojureScript tickets that will make this possible are [2076](https://dev.clojure.org/jira/browse/CLJS-2076), [2077](https://dev.clojure.org/jira/browse/CLJS-2077), and
[2078](https://dev.clojure.org/jira/browse/CLJS-2078).

### FAQ

**Why is Moudle X not loading?**

There could be several reasons why a module is not loading
- The module's information is not in the `:module-info` compiler option
- The module's URI is incorrect in `:modules/uris`
- `set-loaded!` was not called in any of the modules namespaces or was called with the incorrect module id
- You are working in development and did not add the modules namespaces to `:preloads`
- You are working in production and did not set the define `conwip.modules.PRODUCTION` to true

### Future Features

The `:module-info` compiler option could be completely removed by using information from the `:modules` compiler option.
There are several edge cases to consider for this to be a viable option.

Removing the need for `set-loaded!`would require integration with ClojureScript.

### Thanks

[Bendyworks](http://bendyworks.com/) for supporting the development of Conwip Modules

[Allen Rohner](https://github.com/arohner) for doing much of the ground work for [dynamic modules](https://rasterize.io/blog/cljs-dynamic-module-loading.html)

[Antonin Hildebrand](https://github.com/binaryage) for his ideas on how to import [arbitrary compiler options](https://github.com/binaryage/cljs-devtools/blob/master/src/lib/devtools/prefs.clj)

### Copyright and  License

Copyright © 2017 Bendyworks

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
