(ns clotp.core
  (:require [clojure.core.match :refer [match]]))

(def next-process-id (atom 0))
(def processes (atom {}))

(defn next-pid []
  (swap! next-process-id inc))

(defn spawn [f & args]
  (let [pid (next-pid)]
    (swap! processes #(assoc % pid (java.util.concurrent.LinkedBlockingQueue.)))
    (future (apply f pid args))
    pid))

(defn ! [pid message]
  (.put (get @processes pid) message))

(defn ? [pid]
  (.take (get @processes pid)))

(defn counter [self]
  (let [[n pid] (? self)]
    (match [n]
           [0]   (println "Done!")
           :else (do (println n)
                     (! pid [(dec n) self]))))
  (recur self))

(def a (spawn counter))
(def b (spawn counter))

(! a [10 b])

