ggp_col(VAR_j, VAR_mark, S) :- ggp_true(mark(1, VAR_j, VAR_mark), S), ggp_true(mark(2, VAR_j, VAR_mark), S), ggp_true(mark(3, VAR_j, VAR_mark), S).
ggp_diag(VAR_mark, S) :- ggp_true(mark(1, 1, VAR_mark), S), ggp_true(mark(2, 2, VAR_mark), S), ggp_true(mark(3, 3, VAR_mark), S).
ggp_diag(VAR_mark, S) :- ggp_true(mark(1, 3, VAR_mark), S), ggp_true(mark(2, 2, VAR_mark), S), ggp_true(mark(3, 1, VAR_mark), S).
ggp_emptycell(VAR_i, VAR_j, S) :- ggp_index(VAR_i, S), ggp_index(VAR_j, S), ~ ggp_true(mark(VAR_i, VAR_j, x), S), ~ ggp_true(mark(VAR_i, VAR_j, o), S).
ggp_goal(xplayer, 100, S) :- ggp_line(x, S).
ggp_goal(xplayer, 50, S) :- ~ ggp_line(x, S), ~ ggp_line(o, S), ~ ggp_open(S).
ggp_goal(xplayer, 0, S) :- ggp_line(o, S).
ggp_goal(xplayer, 0, S) :- ~ ggp_line(x, S), ~ ggp_line(o, S), ggp_open(S).
ggp_goal(oplayer, 100, S) :- ggp_line(o, S).
ggp_goal(oplayer, 50, S) :- ~ ggp_line(x, S), ~ ggp_line(o, S), ~ ggp_open(S).
ggp_goal(oplayer, 0, S) :- ggp_line(x, S).
ggp_goal(oplayer, 0, S) :- ~ ggp_line(x, S), ~ ggp_line(o, S), ggp_open(S).
ggp_index(1, S).
ggp_index(2, S).
ggp_index(3, S).
ggp_init(control(xplayer)).
ggp_legal(xplayer, noop, S) :- ggp_true(control(oplayer), S).
ggp_legal(xplayer, play(VAR_i, VAR_j, x), S) :- ggp_true(control(xplayer), S), ggp_emptycell(VAR_i, VAR_j, S).
ggp_legal(oplayer, noop, S) :- ggp_true(control(xplayer), S).
ggp_legal(oplayer, play(VAR_i, VAR_j, o), S) :- ggp_true(control(oplayer), S), ggp_emptycell(VAR_i, VAR_j, S).
ggp_line(VAR_mark, S) :- ggp_index(VAR_i, S), ggp_row(VAR_i, VAR_mark, S).
ggp_line(VAR_mark, S) :- ggp_index(VAR_j, S), ggp_col(VAR_j, VAR_mark, S).
ggp_line(VAR_mark, S) :- ggp_diag(VAR_mark, S).
ggp_next(mark(VAR_i, VAR_j, VAR_mark), S) :- ggp_role(VAR_player), ggp_does(VAR_player, play(VAR_i, VAR_j, VAR_mark), S).
ggp_next(mark(VAR_i, VAR_j, VAR_mark), S) :- ggp_true(mark(VAR_i, VAR_j, VAR_mark), S).
ggp_next(control(xplayer), S) :- ggp_true(control(oplayer), S).
ggp_next(control(oplayer), S) :- ggp_true(control(xplayer), S).
ggp_open(S) :- ggp_emptycell(VAR_i, VAR_j, S).
ggp_role(xplayer).
ggp_role(oplayer).
ggp_row(VAR_i, VAR_mark, S) :- ggp_true(mark(VAR_i, 1, VAR_mark), S), ggp_true(mark(VAR_i, 2, VAR_mark), S), ggp_true(mark(VAR_i, 3, VAR_mark), S).
ggp_terminal(S) :- ggp_line(x, S).
ggp_terminal(S) :- ggp_line(o, S).
ggp_terminal(S) :- ~ ggp_open(S).
