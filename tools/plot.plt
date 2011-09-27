set terminal png
set output 'breakthroughx.png'
set title "Breakthrough"
set xlabel "Game Number"
set ylabel "Avg Simulations/Move"
set yrange [500:3200]
set style line 1 lt rgb "red" lw 2
set style line 2 lt rgb "blue" lw 2
plot "random_games_move.dat" ls 1 title "Random" with lines, "rrl_games_move.dat" ls 2 title "RRL" with lines

reset 

set terminal png
set output 'breakthroughy.png'
set title "Breakthrough"
set xlabel "Game Number"
set ylabel "Avg Simulations/Move"
set yrange [500:2000]
set style line 1 lt rgb "red" lw 2
set style line 2 lt rgb "blue" lw 2
plot "random_games_s.dat" ls 1 title "Random" with lines, "rrl_games_s.dat" ls 2 title "RRL" with lines
