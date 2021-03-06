(ns day14
  (:require
   [clojure.edn :as edn]
   [clojure.pprint :refer (cl-format)]
   [clojure.string :as str]
   [criterium.core :refer [quick-bench]]
   [day10 :refer [knot-hash]]
   [day12 :refer [bhauman-group]]
   [util :refer [parse-int]]
   [net.cgrand.xforms.rfs :as rfs]))

(def hash-key "jzgqcdpd")
(def grid-size 128)

(defn row
  [key n]
  (transduce
   (comp
    (map #(parse-int (str %) 16))
    (map #(cl-format nil "~4,'0',B" %)))
   rfs/str
   (knot-hash (str key "-" n))))

(defn count-ones
  [s]
  (reduce
   (fn [^long acc c]
     (if (= \1 c)
       (inc acc)
       acc))
   0
   s))

(defn mk-grid
  [key grid-size]
  (reduce str
          (map #(row key %)
               (range grid-size))))

(defn part-1
  []
  (let [grid (mk-grid hash-key grid-size)]
    (count-ones grid)))

(defn neighbours
  [^String grid ^long pos ^long grid-size]
  (let [row (quot pos grid-size)
        col (rem  pos grid-size)]
    (for [[^long dx ^long dy] [[-1 0] [1 0] [0 -1] [0 1]]
          :let [col' (+ col dx)
                row' (+ row dy)
                pos' (+ col' (* grid-size row'))]
          :when (and
                 (nat-int? row')
                 (nat-int? col')
                 (< col' grid-size)
                 (< pos' (count grid))
                 (= \1 (.charAt grid pos')))]
      pos')))

(defn mk-graph
  [^String grid grid-size]
  (into {}
        (keep
         (fn [idx]
           (when (= \1 (.charAt grid idx))
             [idx (set (neighbours grid idx grid-size))]))
         (range 0 (count grid)))))

(defn count-groups
  [grid grid-size]
  (let [graph (mk-graph grid grid-size)]
    (count
     (distinct (map #(bhauman-group graph %)
                    (keys graph))))))

(defn part-2
  ([]
   (part-2 (mk-grid hash-key grid-size)))
  ([grid]
   (count-groups grid
                 grid-size)))

;;;; Scratch

(comment
  (set! *print-length* 20)
  (set! *warn-on-reflection* true)
  (set! *unchecked-math* :warn-on-boxed)
  (time (part-1)) ;; 8074, ~9s, knot-hash is a bit slow, we could
                  ;; optimize it later
  (def grid (mk-grid hash-key grid-size))
  (time (part-2 grid)) ;; 1212, ~1.6s
  )
