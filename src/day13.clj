(ns day13
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [criterium.core :refer [quick-bench]]
   [util :refer [resource-reducible
                 parse-int find-first]]))

(defn process-data
  [m]
  (let [with-ranges
        (zipmap (keys m)
                (map
                 (fn [v]
                   (reduce into []
                           [(range 0 (dec ^long v))
                            (range (dec ^long v) 0 -1)]))
                 (vals m)))
        depth (apply max (map first m))]
    (into []
          (map #(get with-ranges %)
               (range 0 (inc ^long depth))))))

(defn data
  []
  (let [m (into {}
                (comp
                 (map #(str/split % #":\s+"))
                 (map #(mapv parse-int %)))
                (resource-reducible "day13.txt"))]
    (process-data m)))

(defn position
  [layer scans]
  (when layer
    (get layer (mod scans (count layer)))))

(defn severity
  [layers
   picosecond]
  (if-let [layer (get layers picosecond)]
    (let [pos (position layer picosecond)]
      (if (zero? ^long pos)
        (* ^long picosecond (inc ^long
                                 (/ (count layer)
                                    2)))
        0))
    0))

(defn trip-severity
  [layers]
  (let [depth (count layers)]
    (reduce +
            (map #(severity layers %)
                 (range 0 depth)))))

(defn part-1
  ([] (part-1 (data)))
  ([input]
   (trip-severity input)))

(defn caught?
  [layers delay]
  (find-first
   (fn [idx]
     (let [layer (get layers idx)
           pos (position layer
                         (+ ^long idx ^long delay))]
       (and pos (zero? ^long pos))))
   (range 0 (count layers))))

(defn part-2
  ([] (part-2 (data)))
  ([input]
   (find-first
    (fn [delay]
      (not (caught? input delay)))
    (range))))

;;;; Scratch

(comment
  (set! *print-length* 20)
  (set! *warn-on-reflection* true)
  (set! *unchecked-math* :warn-on-boxed)
  (time (part-1)) ;; 1580, 1ms
  (time (part-2)) ;; 3943252, 2158ms
  (quick-bench (part-2)) ;; 3943252, with some 3.9s, with find-first 2.11s

  (time (some #(when (> ^long % 10000000) %) (range))) ;; 558 ms
  (time (find-first #(> ^long % 10000000)    (range))) ;; 95 ms
  )
