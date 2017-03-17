#!/usr/bin/env boot
(set-env! :dependencies '[[org.clojure/clojure "1.9.0-alpha14"]
                          [org.clojure/clojurescript "1.9.494"]]
          :source-paths #{"src/main" "src/test"})
(load-file "scripts/dev_test.clj")
