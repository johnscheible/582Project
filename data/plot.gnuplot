# All files come in a CSVs of 1,<IP> or 0,NONE
# Run these through split-csv
# Run through this script, which will act on data in the finalData folder
# Should produce six graphs
# 	1) Signal strength over time
# 	2) Duration of paket loss with AbandonShip and with no policy
# 	3) The panera problem
# 	4) The solved panera problem
# 	5) The Courtyards problem
# 	6) The solved courtyards problem

# Uncomment for eps output, as well as the 'set output' lines below
#set term post eps color

# 1)===========================================================================

#set output 'sigStrength.eps'
set xlabel "Test Number"
set ylabel "Signal Strength (dB)"
plot "./sigStrentgLeavingEnteringEECS.txt" notitle #w lines smooth bezier#, \
"./sigStrentgLeavingEnteringEECS.txt"
pause -1

# 2)===========================================================================

unset yrange
set style data histogram
set style fill solid border
set style histogram clustered gap 1
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

#set output 'bar.eps'
set style line 1 lc rgb "red"
set style line 2 lc rgb "blue"

set style fill solid
set boxwidth 0.5
set style data histogram
set style histogram cluster gap 1

set ylabel "Dropped Packets"

plot "./lastDay/bar.txt" using 1:3:xtic(2) with boxes notitle
pause -1

# Configuration for packet graphs)=============================================

set ytics ("No" 0, "Yes" 1)
set yrange [-0.2:1.2]
set ylabel "Packet Received"
set xlabel "Packet Number"

# 3)===========================================================================

#set output 'paneraNoPolicy.eps'
plot "./paneraNoPolicy.csv" notitle w dots
pause -1

# 4)===========================================================================

#set output 'paneraWithPolicy.eps'
plot "./paneraWithPolicy.csv" notitle w dots
pause -1

# 5)===========================================================================

#set output 'leavingCourtyardsNoPolicy.eps'
plot "leavingApartmentNoPolicy.csv" every ::0::2500 notitle w dots # leaving apartment
pause -1

# 6)===========================================================================

#set output "leavingCourtyardsWithPolicy.eps"
plot "leavingApartmentWithPolicy.csv" every ::0::2500 notitle w dots # leaving apartment, with policy
pause -1


