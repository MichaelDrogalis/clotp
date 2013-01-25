(ns clotp.core)

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

(defn my-process [self]
  (let [message (? self)]
    (println message)))

(def pid (spawn my-process))
(! pid "Message hereee")
(! pid "Message hereeedsf")

