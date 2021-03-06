(ns day23
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [day18 :refer [get-val assoc-reg]]
   [util :refer [resource-reducible]]))

(defn data
  [path]
  (into []
        (comp
         (map #(str/replace % #";;.*" ""))
         (remove empty?)
         (map #(format "[%s]" %))
         (map edn/read-string))
        (resource-reducible path)))

(defn solve1
  [instructions registers]
  (loop [{:keys [^long ctr
                 registers]
          :as p}
         {:ctr 0
          :registers registers
          :mul-count 0}]
    (if (>= ctr (count instructions))
      p
      (recur
       (let [[instr reg arg] (get instructions ctr)
             p (update p :ctr inc)]
         (case instr
           set (assoc-reg p reg (get-val registers arg))
           mul (->
                p
                (assoc-reg reg (* ^long (get registers reg 0)
                                  (get-val registers arg)))
                (update :mul-count inc))
           sub (assoc-reg p reg (- ^long (get registers reg 0)
                                   (get-val registers arg)))
           jnz (let [x-val (get-val registers reg)
                     y-val (get-val registers arg)
                     offset (if-not (zero? x-val)
                              y-val
                              1)]
                 (assoc p :ctr (+ ctr offset)))))))))

(defn part-1
  []
  (:mul-count (solve1
               (data "day23.txt")
               {})))

(defn part-2-impossibly-slow
  []
  (get (solve1
        (data "day23-analysis.txt")
        {'a 1})
       'h))

;;;; For an analysis of the assembly, look in resources/day23-analysis.txt

(defn non-prime
  "If non-prime, returns first factor, nil otherwise"
  [^long n]
  (when (> n 1)
    (first
     (let [root (int (Math/sqrt n))]
       (for [^long i (range 2 (inc root))
             :when (zero? (rem n i))]
         i)))))

(defn part-2
  []
  (count (keep non-prime (range 106500 (inc 123500) 17))))

;;;; Scratch

(comment
  (set! *print-length* 20)
  (set! *warn-on-reflection* true)
  (set! *unchecked-math* :warn-on-boxed)
  (time (part-1)) ;; 3969, ~26ms
  (time (part-2)) ;; 917, ~1.6ms
  (time (part-2-impossibly-slow))
  )
