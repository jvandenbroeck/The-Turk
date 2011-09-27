ggp_column(VAR_n, VAR_x, S) :- ggp_true(cell(1, VAR_n, VAR_x), S), ggp_true(cell(2, VAR_n, VAR_x), S), ggp_true(cell(3, VAR_n, VAR_x), S).
ggp_diagonal(VAR_x, S) :- ggp_true(cell(1, 1, VAR_x), S), ggp_true(cell(2, 2, VAR_x), S), ggp_true(cell(3, 3, VAR_x), S).
ggp_diagonal(VAR_x, S) :- ggp_true(cell(1, 3, VAR_x), S), ggp_true(cell(2, 2, VAR_x), S), ggp_true(cell(3, 1, VAR_x), S).
ggp_goal(xplayer, 100, S) :- ggp_line(x, S).
ggp_goal(xplayer, 50, S) :- ~ ggp_line(x, S), ~ ggp_line(o, S), ~ ggp_open(S).
ggp_goal(xplayer, 0, S) :- ggp_line(o, S).
ggp_goal(oplayer, 100, S) :- ggp_line(o, S).
ggp_goal(oplayer, 50, S) :- ~ ggp_line(x, S), ~ ggp_line(o, S), ~ ggp_open(S).
ggp_goal(oplayer, 0, S) :- ggp_line(x, S).
ggp_init(cell(1, 1, b)).
ggp_init(cell(1, 2, b)).
ggp_init(cell(1, 3, b)).
ggp_init(cell(2, 1, b)).
ggp_init(cell(2, 2, b)).
ggp_init(cell(2, 3, b)).
ggp_init(cell(3, 1, b)).
ggp_init(cell(3, 2, b)).
ggp_init(cell(3, 3, b)).
ggp_init(control(xplayer)).
ggp_legal(VAR_w, mark(VAR_x, VAR_y), S) :- ggp_true(cell(VAR_x, VAR_y, b), S), ggp_true(control(VAR_w), S).
ggp_legal(xplayer, noop, S) :- ggp_true(control(oplayer), S).
ggp_legal(oplayer, noop, S) :- ggp_true(control(xplayer), S).
ggp_line(VAR_x, S) :- ggp_row(VAR_m, VAR_x, S).
ggp_line(VAR_x, S) :- ggp_column(VAR_m, VAR_x, S).
ggp_line(VAR_x, S) :- ggp_diagonal(VAR_x, S).
ggp_next(cell(VAR_m, VAR_n, x), S) :- ggp_does(xplayer, mark(VAR_m, VAR_n), S), ggp_true(cell(VAR_m, VAR_n, b), S).
ggp_next(cell(VAR_m, VAR_n, o), S) :- ggp_does(oplayer, mark(VAR_m, VAR_n), S), ggp_true(cell(VAR_m, VAR_n, b), S).
ggp_next(cell(VAR_m, VAR_n, VAR_w), S) :- ggp_true(cell(VAR_m, VAR_n, VAR_w), S), VAR_w \= b.
ggp_next(cell(VAR_m, VAR_n, b), S) :- ggp_does(VAR_w, mark(VAR_j, VAR_k), S), ggp_true(cell(VAR_m, VAR_n, b), S), (VAR_m \= VAR_j ; VAR_n \= VAR_k).
ggp_next(control(xplayer), S) :- ggp_true(control(oplayer), S).
ggp_next(control(oplayer), S) :- ggp_true(control(xplayer), S).
ggp_open(S) :- ggp_true(cell(VAR_m, VAR_n, b), S).
ggp_role(xplayer).
ggp_role(oplayer).
ggp_row(VAR_m, VAR_x, S) :- ggp_true(cell(VAR_m, 1, VAR_x), S), ggp_true(cell(VAR_m, 2, VAR_x), S), ggp_true(cell(VAR_m, 3, VAR_x), S).
ggp_terminal(S) :- ggp_line(x, S).
ggp_terminal(S) :- ggp_line(o, S).
ggp_terminal(S) :- ~ ggp_open(S).
