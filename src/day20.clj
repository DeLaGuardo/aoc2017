(ns day20
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [util :refer [resource-reducible]]
   [criterium.core :refer [quick-bench]]))

(defn data
  []
  (into []
        (comp
         (map #(str/replace % #"[<>=pva]" ""))
         (map #(format "[%s]" %))
         (map edn/read-string))
        (resource-reducible "day20.txt")))

(defn update-particle
  [[^long px ^long py ^long pz
    ^long vx ^long vy ^long vz
    ^long ax ^long ay ^long az]]
  (let [vx (+ vx ax)
        vy (+ vy ay)
        vz (+ vz az)]
    [(+ px vx) (+ py vy) (+ pz vz)
     vx        vy        vz
     ax        ay        az]))

(defn manhattan
  [[^long px ^long py ^long pz & _]]
  (+ (Math/abs px)
     (Math/abs py)
     (Math/abs pz)))

(defn closest
  [particles]
  (apply min-key manhattan particles))

(defn solve1
  [particles]
  (mapv update-particle particles))

(defn converge
  ([iterations comp ^long init ^long max]
   (converge iterations comp
             (first (drop init iterations))
             (* 2 init) max))
  ([iterations comp prev n max]
   (let [iterations (drop n iterations)
         new (first iterations)
         cprev (comp prev)
         cnew  (comp new)
         next-n (* 2 ^long n)]
     (if (= cprev cnew)
       cprev
       (if (> next-n ^long max)
         (throw (ex-info "Not converged below max." {}))
         (recur iterations comp new next-n max))))))

;; We could have used Newton, of course...
(defn part-1
  []
  (converge (iterate solve1 (data))
            #(.indexOf ^clojure.lang.PersistentVector %
                       (closest %))
            512
            1000000))

(defn position
  [[px py pz & _]]
  [px py pz])

(defn remove-by-position
  [particles [x y z]]
  (set
   (remove (fn [[px py pz & _]]
             (= [x y z]
                [px py pz]))
           particles)))

;; this ugly function is the result of a wrong understanding at first
;; of part 2 and then some refactoring
(defn remove-colliding-v1
  [particles]
  (first
   (reduce
    (fn [[removed positions]
         particle]
      (let [pos (position particle)]
        (if (contains? positions pos)
          [(remove-by-position removed pos)
           positions]
          [(conj removed particle)
           (conj positions pos)])))
    [#{} #{}]
    particles)))

;; and it isn't even faster than this clean looking one
(defn remove-colliding-v2
  [particles]
  (into #{}
        (comp
         (remove (fn [[k v]]
                   (> (count v) 1)))
         (map (comp first val)))
        (group-by position particles)))

(comment
  (remove-colliding [[0 0 0 1 1 1] [0 0 0 1 2 3] [0 1 0 1 0 1]]))

(defn solve2
  [particles]
  (remove-colliding-v2
   (into #{} (map update-particle particles))))

(defn part-2
  []
  (converge (iterate solve2 (set (data)))
            count
            512
            100000))

;;;; Scratch

(comment
  (set! *print-length* 20)
  (set! *warn-on-reflection* true)
  (set! *unchecked-math* :warn-on-boxed)
  (time (part-1)) ;; 157, ~116ms 
  (time (part-2)) ;; 499, ~1.2s
  (quick-bench (part-2)) ;; 1.13s with remove-colliding-v1
  (quick-bench (part-2)) ;; 1.05s with remove-colliding-v1
  )
