(require '[cljs.build.api :as b])

(b/build (b/inputs "src/main/conwip" "src/test/conwip")
         {:optimizations :simple
          :pretty-print true
          :source-map true
          :main 'conwip.test.runner
          :module-info {:module/uris {"extra" "js/extra.js"
                                      "dev" "js/test.js"}
                        :module/deps {"extra" []
                                      "dev" []}}
          :modules {:extra {:output-to "resources/simple_test/js/extra.js"
                            :entries #{"conwip.test.extra"}
                            :depends-on #{:dev}}
                    :dev {:output-to "resources/simple_test/js/test.js"
                          :entries #{"conwip.test.runner"}}}
          :closure-defines {'conwip.modules.PRODUCTION true}
          :asset-path "js"
          :output-dir    "resources/simple_test/js"})

(System/exit 0)