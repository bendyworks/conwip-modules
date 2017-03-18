(ns conwip.modules.dev
  (:require [clojure.browser.repl :as repl]))

(enable-console-print!)

(defonce conn
  (repl/connect "http://localhost:9000/repl"))