(ns udp-recv.core)
(use 'lamina.core 'aleph.http 'aleph.udp)


; TODO - write iOS app w/2 buttons: stop and go
; send UDP message w/seq # every 100ms until stop is pressed

; question: does phone see every access point on same WiFi network as
; separate or the same? from iwlist manpage, it looks like it sees individual
; access points

; log is a list of recieved sequence numbers
(def recv-log (agent []))


(defn decode-message [message]
  (apply str (map #(char %) (seq (.array message)))))

(defn print-message [message]
  (let [decoded (decode-message (:message message))]
    (println decoded)
    (def recent message)
    (try
      (send recv-log #(conj % [%2 (:host message)]) (Integer. decoded))
      (catch Exception e (println "exception: " (.getMessage e))))))

(defn create-server [port]
  (receive-all @(udp-socket {:port port}) print-message)
  (str "starting server on port " port))

(defn seq-nums [data]
  (map #(nth % 0) data))

(defn hostname [recvd seqn]
  (some #(when (= (nth % 0) seqn) (nth % 1)) recvd))

(defn format-data [recvd]
  "Returns a string containing properly formatted data"
  (when (seq recvd)
    (let [csv (apply str (map #(if (some #{%} (seq-nums recvd))
          (str "1," (hostname recvd %) ",\n")
          ; (str "1," + (nth @recv-log )
          (str "0," (nth (nth recvd 0) 1) ",\n"))
        (range (inc (apply max (seq-nums recvd))))))]
      (.substring csv 0 (dec (dec (count csv)))))))

; For CTRL+C - stop logging and write data to file
(.addShutdownHook (Runtime/getRuntime)
  (Thread. (fn []
    (spit "data.csv" (format-data @recv-log))
    (println "Finished writing log"))))

(defn -main[port & args]
  (create-server (Integer. port)))
