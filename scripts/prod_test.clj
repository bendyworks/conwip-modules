(require '[cljs.build.api :as b])

(b/build (b/inputs "src/main/bendyworks" "src/test/bendyworks")
         {:optimizations :advanced
          :main 'bendyworks.test.runner
          :module-info {:module/uris {"extra" "js/extra.js"
                                      "dev" "js/dev.js"}
                        :module/deps {"extra" []
                                      "dev" []}}
          :modules {:extra {:output-to "resources/prod_test/js/extra.js"
                            :entries #{"bendyworks.test.extra"}}
                    :dev {:output-to "resources/prod_test/js/test.js"
                          :entries #{"bendyworks.test.runner"}
                          :depends-on #{:extra}}}
          :closure-defines {'bendyworks.modules.PRODUCTION true}
          :asset-path "js"
          :output-dir    "resources/prod_test/js"})

(System/exit 0)