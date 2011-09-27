ggp_goal(row, VAR_r1, S) :- ggp_true(did(row, VAR_m1), S), ggp_true(did(column, VAR_m2), S), ggp_true(reward(VAR_m1, VAR_m2, VAR_r1, VAR_r2), S).
ggp_goal(column, VAR_r2, S) :- ggp_true(did(row, VAR_m1), S), ggp_true(did(column, VAR_m2), S), ggp_true(reward(VAR_m1, VAR_m2, VAR_r1, VAR_r2), S).
ggp_init(reward(r1, c1, 90, 90)).
ggp_init(reward(r1, c2, 40, 30)).
ggp_init(reward(r1, c3, 20, 80)).
ggp_init(reward(r2, c1, 30, 40)).
ggp_init(reward(r2, c2, 0, 0)).
ggp_init(reward(r2, c3, 50, 100)).
ggp_init(reward(r3, c1, 80, 20)).
ggp_init(reward(r3, c2, 100, 50)).
ggp_init(reward(r3, c3, 10, 10)).
ggp_legal(row, VAR_m1, S) :- ggp_true(reward(VAR_m1, VAR_m2, VAR_r1, VAR_r2), S).
ggp_legal(column, VAR_m2, S) :- ggp_true(reward(VAR_m1, VAR_m2, VAR_r1, VAR_r2), S).
ggp_next(did(VAR_p, VAR_m), S) :- ggp_does(VAR_p, VAR_m, S).
ggp_next(reward(VAR_m1, VAR_m2, VAR_r1, VAR_r2), S) :- ggp_true(reward(VAR_m1, VAR_m2, VAR_r1, VAR_r2), S).
ggp_role(row).
ggp_role(column).
ggp_terminal(S) :- ggp_true(did(VAR_p, VAR_m), S).
