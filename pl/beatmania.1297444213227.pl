ggp_plus_plus_(0, 1, S).
ggp_plus_plus_(1, 2, S).
ggp_plus_plus_(2, 3, S).
ggp_plus_plus_(3, 4, S).
ggp_plus_plus_(4, 5, S).
ggp_plus_plus_(5, 6, S).
ggp_plus_plus_(6, 7, S).
ggp_plus_plus_(7, 8, S).
ggp_plus_plus_(8, 9, S).
ggp_plus_plus_(9, 10, S).
ggp_plus_plus_(10, 11, S).
ggp_plus_plus_(11, 12, S).
ggp_plus_plus_(12, 13, S).
ggp_plus_plus_(13, 14, S).
ggp_plus_plus_(14, 15, S).
ggp_plus_plus_(15, 16, S).
ggp_plus_plus_(16, 17, S).
ggp_plus_plus_(17, 18, S).
ggp_plus_plus_(18, 19, S).
ggp_plus_plus_(19, 20, S).
ggp_plus_plus_(20, 21, S).
ggp_plus_plus_(21, 22, S).
ggp_plus_plus_(22, 23, S).
ggp_plus_plus_(23, 24, S).
ggp_plus_plus_(24, 25, S).
ggp_plus_plus_(25, 26, S).
ggp_plus_plus_(26, 27, S).
ggp_plus_plus_(27, 28, S).
ggp_plus_plus_(28, 29, S).
ggp_plus_plus_(29, 30, S).
ggp_minus_minus_(30, 29, S).
ggp_minus_minus_(29, 28, S).
ggp_minus_minus_(28, 27, S).
ggp_minus_minus_(27, 26, S).
ggp_minus_minus_(26, 25, S).
ggp_minus_minus_(25, 24, S).
ggp_minus_minus_(24, 23, S).
ggp_minus_minus_(23, 22, S).
ggp_minus_minus_(22, 21, S).
ggp_minus_minus_(21, 20, S).
ggp_minus_minus_(20, 19, S).
ggp_minus_minus_(19, 18, S).
ggp_minus_minus_(18, 17, S).
ggp_minus_minus_(17, 16, S).
ggp_minus_minus_(16, 15, S).
ggp_minus_minus_(15, 14, S).
ggp_minus_minus_(14, 13, S).
ggp_minus_minus_(13, 12, S).
ggp_minus_minus_(12, 11, S).
ggp_minus_minus_(11, 10, S).
ggp_minus_minus_(10, 9, S).
ggp_minus_minus_(9, 8, S).
ggp_minus_minus_(8, 7, S).
ggp_minus_minus_(7, 6, S).
ggp_minus_minus_(6, 5, S).
ggp_minus_minus_(5, 4, S).
ggp_minus_minus_(4, 3, S).
ggp_minus_minus_(3, 2, S).
ggp_minus_minus_(2, 1, S).
ggp_blockcaught(S) :- ggp_does(player, play(1), S), ggp_true(block(1, 1), S).
ggp_blockcaught(S) :- ggp_does(player, play(2), S), ggp_true(block(2, 1), S).
ggp_blockcaught(S) :- ggp_does(player, play(3), S), ggp_true(block(3, 1), S).
ggp_blockdropped(S) :- ggp_does(dropper, place(1), S).
ggp_blockdropped(S) :- ggp_does(dropper, place(2), S).
ggp_blockdropped(S) :- ggp_does(dropper, place(3), S).
ggp_goal(dropper, 100, S).
ggp_goal(player, VAR_goal, S) :- ggp_true(blockscaught(VAR_n), S), ggp_scoremap(VAR_n, VAR_goal, S).
ggp_init(blocksdropped(0)).
ggp_init(blockscaught(0)).
ggp_legal(dropper, place(1), S) :- ~ ggp_true(blocksdropped(30), S).
ggp_legal(dropper, place(2), S) :- ~ ggp_true(blocksdropped(30), S).
ggp_legal(dropper, place(3), S) :- ~ ggp_true(blocksdropped(30), S).
ggp_legal(dropper, noop, S) :- ggp_true(blocksdropped(30), S).
ggp_legal(player, play(1), S).
ggp_legal(player, play(2), S).
ggp_legal(player, play(3), S).
ggp_next(block(1, 30), S) :- ggp_does(dropper, place(1), S).
ggp_next(block(2, 30), S) :- ggp_does(dropper, place(2), S).
ggp_next(block(3, 30), S) :- ggp_does(dropper, place(3), S).
ggp_next(blocksdropped(VAR_nplus_plus_), S) :- ggp_blockdropped(S), ggp_true(blocksdropped(VAR_n), S), ggp_plus_plus_(VAR_n, VAR_nplus_plus_, S).
ggp_next(blocksdropped(VAR_n), S) :- ggp_true(blocksdropped(VAR_n), S), ~ ggp_blockdropped(S).
ggp_next(blockscaught(VAR_nplus_plus_), S) :- ggp_blockcaught(S), ggp_true(blockscaught(VAR_n), S), ggp_plus_plus_(VAR_n, VAR_nplus_plus_, S).
ggp_next(blockscaught(VAR_n), S) :- ggp_true(blockscaught(VAR_n), S), ~ ggp_blockcaught(S).
ggp_next(block(VAR_x, VAR_yminus_minus_), S) :- ggp_true(block(VAR_x, VAR_y), S), ggp_minus_minus_(VAR_y, VAR_yminus_minus_, S).
ggp_role(dropper).
ggp_role(player).
ggp_scoremap(0, 0, S).
ggp_scoremap(1, 3, S).
ggp_scoremap(2, 6, S).
ggp_scoremap(3, 9, S).
ggp_scoremap(4, 12, S).
ggp_scoremap(5, 15, S).
ggp_scoremap(6, 18, S).
ggp_scoremap(7, 21, S).
ggp_scoremap(8, 24, S).
ggp_scoremap(9, 27, S).
ggp_scoremap(10, 30, S).
ggp_scoremap(11, 33, S).
ggp_scoremap(12, 36, S).
ggp_scoremap(13, 39, S).
ggp_scoremap(14, 42, S).
ggp_scoremap(15, 45, S).
ggp_scoremap(16, 48, S).
ggp_scoremap(17, 51, S).
ggp_scoremap(18, 54, S).
ggp_scoremap(19, 57, S).
ggp_scoremap(20, 60, S).
ggp_scoremap(21, 63, S).
ggp_scoremap(22, 66, S).
ggp_scoremap(23, 69, S).
ggp_scoremap(24, 72, S).
ggp_scoremap(25, 75, S).
ggp_scoremap(26, 80, S).
ggp_scoremap(27, 85, S).
ggp_scoremap(28, 90, S).
ggp_scoremap(29, 95, S).
ggp_scoremap(30, 100, S).
ggp_terminal(S) :- ggp_true(blocksdropped(30), S), ~ ggp_true(block(1, 1), S), ~ ggp_true(block(2, 1), S), ~ ggp_true(block(3, 1), S).
