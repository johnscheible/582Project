#set term post eps color

set output 'sigStrength.eps'
set xlabel "Test Number"
set ylabel "Signal Strength (dB)"
plot "./sigStrentgLeavingEnteringEECS.txt" notitle #w lines smooth bezier#, \
"./sigStrentgLeavingEnteringEECS.txt"
pause -1

set ytics ("No" 0, "Yes" 1)
set yrange [-0.2:1.2]
set ylabel "Packet Received"
set xlabel "Packet Number"

set output "leavingCourtyardsWithPolicy.eps"
plot "./lolData.csv" every ::0::2500 notitle w dots # leaving apartment, with policy

set output 'paneraWithPolicy.eps'
plot "./paneraWithPolicy.csv" notitle w dots
pause -1
set output 'paneraNoPolicy.eps'
plot "./paneraNoPolicy.csv" notitle w dots
pause -1
set output 'leavingCourtyardsNoPolicy.eps'
plot "./data.csv" every ::0::2500 notitle w dots # leaving apartment
pause -1

#set style data histogram
#set style fill solid border
#set style histogram clustered gap 1
##plot [2:2] "./lastDay/bar.txt" title columnheader
#
#binwidth=5
#set boxwidth binwidth
#bin(x,width)=width*floor(x/width) + binwidth/2.0
# 
#plot "./lastDay/bar.txt" using (bin($1,binwidth)):(1.0) smooth freq with boxes
#
#
#pause -1

set output 'bar.eps'
set style line 1 lc rgb "red"
set style line 2 lc rgb "blue"

set style fill solid
set boxwidth 0.5
set style data histogram
set style histogram cluster gap 1

set ylabel "Dropped Packets"

plot "./lastDay/bar.txt" using 1:3:xtic(2) with boxes notitle
pause -1

#plot "./lastDay/bar.txt" every ::0::0 using 1:3:xtic(2) with boxes ls 1 notitle, \
#     "./lastDay/bar.txt" every ::1::1 using 1:3:xtic(2) with boxes ls 2 notitle
#
#pause -1
