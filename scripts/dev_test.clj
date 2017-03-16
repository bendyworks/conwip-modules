(require '[cljs.build.api :as b])

(b/build (b/inputs "src/main/bendyworks" "src/test/bendyworks/")
         {:optimizations :none
          :main 'bendyworks.test.runner
          :asset-path "js"
          :output-dir    "resources/dev_test/js"
          :output-to     "resources/dev_test/js/test.js"})

(System/exit 0)
