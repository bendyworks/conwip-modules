(require '[cljs.build.api :as b])
(require 'cljs.repl)
(require 'cljs.repl.browser)

(b/build "src/main/conwip"
         {:optimizations :none
          :preloads '[conwip.modules.dev]
          :main 'conwip.modules
          :verbose       true
          :output-dir    "resources/dev"
          :output-to     "resources/dev/app.js"})

(cljs.repl/repl* (cljs.repl.browser/repl-env)
                 {:optimizations :none
                  :preloads '[conwip.modules.dev]
                  :main 'conwip.modules
                  :watch "src/main/conwip"
                  :output-dir     "resources/dev"
                  :output-to     "resources/dev/app.js"})