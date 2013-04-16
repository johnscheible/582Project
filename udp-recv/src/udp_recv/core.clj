(ns udp-recv.core)
(use 'lamina.core 'aleph.udp)

; log of seq nums and addresses
(def recv-log (agent []))


; server ============================================================

(defn decode-message [message]
  (apply str (map #(char %) (seq (.array message)))))

(defn log-message [message]
  (let [decoded (decode-message (:message message))]
    (println decoded (:host message))
    (try
      (send recv-log #(conj % [%2 (:host message)]) (Integer. decoded))
      (catch Exception e (println "exception: " (.getMessage e))))))

(defn create-server [port]
  (receive-all @(udp-socket {:port port}) log-message)
  (str "starting server on port " port))

; output formatting =================================================

(defn seq-nums [data]
  (map #(nth % 0) data))

(defn construct-list [recvd]
  "Returns function that, given the recvd seq-nums and addresses, returns a line of the CSV"
  (fn [num]
    (if-let [x (seq (filter #(= (nth % 0) num) recvd))]
      (str "1," (nth (flatten x) 1) "\n")
      "0,NONE\n")))

(defn format-data [recvd]
  "Returns a string containing properly formatted data"
  (when (seq recvd)
    (apply str
      (map (construct-list recvd)
        (range (inc (apply max (seq-nums recvd))))))))

; actually running ==================================================

; For CTRL-C - stop logging and write data to file
(.addShutdownHook (Runtime/getRuntime)
  (Thread. (fn []
    (spit "data.csv" (format-data @recv-log))
    (println "Finished writing log"))))

(defn -main[port & args]
  (create-server (Integer. port)))
