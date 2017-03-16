(set-env!
  :project 'bendworks.modules
  :version "0.1.0-SNAPHOT"
  :description "Sample project demonstrating Advance ClojureScript"
  :source-paths #{"src/main" "src/test"}
  :dependencies '[[org.clojure/clojure "1.9.0-alpha14"]
                  [org.clojure/clojurescript "1.9.494"]
                  [onetom/boot-lein-generate "0.1.3" :scope "test"]])

(require '[boot.core :as core])

(deftask lein-profile []
  "Make a Lein profile from a boo build file"
  (require 'boot.lein)

  (task-options!
    pom {:project (core/get-env :project)
         :version (core/get-env :version)})

  (let [lein (resolve 'boot.lein/generate)]
    (lein)))
