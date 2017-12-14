(ns day14
  (:require
   [clojure.edn :as edn]
   [clojure.pprint :refer (cl-format)]
   [clojure.string :as str]
   [criterium.core :refer [quick-bench]]
   [day10 :refer [knot-hash]]
   [day12 :refer [bhauman-group]]
   [util :refer [parse-int char-at]]))

(def hash-key "jzgqcdpd")
(def grid-size 128)

(defn row
  [key n]
  (let [kh
        (knot-hash (str key "-" n))]
    (transduce
     (comp
      (keep #(parse-int (str %) 16))
      (map #(cl-format nil "~4,'0',B" %)))
     str
     kh)))

(defn count-ones
  [s]
  (reduce
   (fn [^long acc c]
     (if (= \1 c)
       (inc acc)
       acc))
   0
   s))

(defn grid
  [key grid-size]
  (reduce str
          (map #(row key %)
               (range grid-size))))

(defn part-1
  []
  (let [g (grid hash-key grid-size)]
    ;; we wrap the grid in a delay, because we don't want to print the
    ;; whole thing if *print-length* isn't set
    [(count-ones g) (delay g)]))

(defn neighbours
  [grid ^long pos ^long grid-size]
  (let [row (quot pos grid-size)
        col (rem  pos grid-size)]
    (for [[^long dx ^long dy] [[-1 0] [1 0] [0 -1] [0 1]]
          :let [col' (+ col dx)
                row' (+ row dy)
                pos' (+ col' (* grid-size row'))]
          :when (and
                 (= \1 (char-at grid pos'))
                 (nat-int? row')
                 (nat-int? col')
                 (< col' grid-size))]
      pos)))

(defn graph
  [grid grid-size]
  (into {}
        (keep
         (fn [idx]
           (when (= \1 (char-at grid idx))
             [idx (set (neighbours grid idx grid-size))]))
         (range 0 (count grid)))))

(defn count-groups
  [s grid-size]
  (let [graph (graph s grid-size)]
    (count
     (distinct (map #(bhauman-group graph %)
                    (keys graph))))))

(defn part-2
  ([] (part-2 @(second (part-1))))
  ([part-1]
   (count-groups part-1 grid-size)))

;;;; Scratch

(comment
  (set! *print-length* 20)
  (set! *warn-on-reflection* true)
  (set! *unchecked-math* :warn-on-boxed)
  (def p1 (time (part-1))) ;; 8074, ~9s
  (time (part-2 @(second p1))) ;; 1212, ~1.6s
  )
