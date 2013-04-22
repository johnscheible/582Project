plot "./sigStrentgLeavingEnteringEECS.txt" w lines smooth bezier, \
"./sigStrentgLeavingEnteringEECS.txt"
pause -1

set ytics ("No" 0, "Yes" 1)
set yrange [-0.2:1.2]
set ylabel "Packet Received"
set xlabel "Packet Number"

plot "./paneraWithPolicy.csv" notitle w dots
pause -1
plot "./paneraNoPolicy.csv" notitle w dots
pause -1
plot "./dataRoundTripWithWifi.csv" notitle w dots
pause -1
