(ns template
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [criterium.core :refer [quick-bench]]
   [util :refer [resource-reducible
                 parse-int find-first]]))

(defn data
  [])

(defn part-1
  [])

(defn part-2
  [])

;;;; Scratch

(comment
  (set! *print-length* 20)
  (set! *warn-on-reflection* true)
  (set! *unchecked-math* :warn-on-boxed)
  (time (part-1)) 
  (time (part-2)) 
  (quick-bench (part-2)) 
  )
