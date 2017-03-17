(defproject
  bendworks.modules
  "0.1.0-SNAPHOT"
  :description
  "Sample project demonstrating Advance ClojureScript"
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