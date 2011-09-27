# visits_random.log
# visits_rrl.log
cat visits_random.log | grep randomGames/Move > random_games_move.dat
cat visits_rrl.log | grep randomGames/Move > rrl_games_move.dat
sed -i 's/randomGames\/Move: //' random_games_move.dat
sed -i 's/randomGames\/Move: //' rrl_games_move.dat

cat visits_random.log | grep ^randomGames: > random_games_s.dat
cat visits_rrl.log | grep ^randomGames: > rrl_games_s.dat
sed -i 's/randomGames: //' random_games_s.dat
sed -i 's/randomGames: //' rrl_games_s.dat
gnuplot plot.plt

