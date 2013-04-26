(ns split-csv.core
  (:require [clojure.string :as string]
            clojure.stacktrace))


(defn get-successes [file-text]
  "returns a vector of 0s and 1s indicating success/failure"
  (map #(subs % 0 2)
    (string/split file-text #"\n")))


(defn get-ips [file-text]
  "returns vector of IPs used in the test"
  (vec (remove #{"NONE"}
    (distinct
      (map #(subs % 2)
        (string/split file-text #"\n"))))))

(defn add-new-line [ips ip output]
  ; (println (string/join (map #(if (= ip %) "1\t" "0\t") ips)))
  (println ip)
  (str 
    output
    (string/join (map #(if (= ip %) "1\t" "0\t") ips))
    "\n"))

(defn create-file [file-text]
  (let [ips (get-ips file-text)]
    (println "ips:" ips)
    (loop [output ""
           successes (seq (get-successes file-text))
           lines (seq (string/split file-text #"\n"))]
      ; (println (count lines))
      ; (println (seq? lines))
      (if (seq lines)
        (recur (add-new-line ips (subs (first lines) 2) output)
         (rest successes) (rest lines))
        ; (for [ip ips]
        ;     (if (re-find (re-pattern ip) (first lines))
        ;       (recur (new-line ips ip output) (rest successes) (rest lines))))
        output))))

(defn -main[file]
  (println "Processing" file)
  (spit "new-csv.csv" (create-file (slurp file))))