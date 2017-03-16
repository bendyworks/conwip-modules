(ns bendyworks.test.init
  (:require [bendyworks.modules :as bm]))

;; Paramertize build for extra module
(goog-define EXTRA_URI "extra.js")
;; Set up modules information
(bm/init-manager #js{"extra" EXTRA_URI} #js{"extra" #js []})
