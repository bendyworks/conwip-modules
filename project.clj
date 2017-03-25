(defproject
  conwip.modules
  "0.1.0"
  :description
  "Library for dynamically loading ClojureScript modules"
  :repositories
  [["clojars" {:url "https://repo.clojars.org/"}]
   ["maven-central" {:url "https://repo1.maven.org/maven2"}]]
  :dependencies
  [[org.clojure/clojure "1.9.0-alpha14"]
   [org.clojure/clojurescript "1.9.494"]
   [onetom/boot-lein-generate "0.1.3" :scope "test"]]
  :source-paths
  ["src/test" "src/main"]
  :resource-paths
  ["resources"])