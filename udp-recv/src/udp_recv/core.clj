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
    (send recv-log #(conj % %2) (Integer. decoded))))

(defn create-server [port]
  (receive-all @(udp-socket {:port port}) print-message)
  (str "starting server on port " port))

(defn format-data [recvd]
  "Returns a string containing properly formatted data"
  (when (seq recvd)
    (let [csv (apply str (map #(if (some #{%} recvd)
          "1,"
          "0,")
        (range (inc (apply max recvd)))))]
      (.substring csv 0 (dec (count csv))))))

; For CTRL+C - stop logging and write data to file
(.addShutdownHook (Runtime/getRuntime)
  (Thread. (fn []
    (spit "data.csv" (format-data @recv-log))
    (println "Finished writing log"))))

(defn -main[port & args]
  (create-server (Integer. port)))
