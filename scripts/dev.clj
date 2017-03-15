(require '[cljs.build.api :as b])
(require 'cljs.repl)
(require 'cljs.repl.browser)

(b/build "src/main/bendyworks"
         {:optimizations :none
          :preloads '[bendyworks.modules.dev]
          :main 'bendyworks.modules
          :verbose       true
          :output-dir    "resources/dev"
          :output-to     "resources/dev/app.js"})

(cljs.repl/repl* (cljs.repl.browser/repl-env)
                 {:optimizations :none
                  :preloads '[bendyworks.modules.dev]
                  :main 'bendyworks.modules
                  :watch "src/main/bendyworks"
                  :output-dir     "resources/dev"
                  :output-to     "resources/dev/app.js"})