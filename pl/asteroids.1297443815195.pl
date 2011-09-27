ggp_atplanet(S) :- ggp_true(x(15), S), ggp_true(y(5), S).
ggp_goal(ship, 100, S) :- ggp_stopped(S), ggp_atplanet(S).
ggp_goal(ship, 50, S) :- ggp_stopped(S), ~ ggp_atplanet(S).
ggp_goal(ship, 0, S) :- ~ ggp_stopped(S).
ggp_init(x(10)).
ggp_init(y(10)).
ggp_init(heading(north)).
ggp_init(northminus_speed(3)).
ggp_init(eastminus_speed(2)).
ggp_init(step(1)).
ggp_legal(ship, thrust, S).
ggp_legal(ship, turn(clock), S).
ggp_legal(ship, turn(counter), S).
ggp_mapplus_(20, minus_3, 17, S).
ggp_mapplus_(20, minus_2, 18, S).
ggp_mapplus_(20, minus_1, 19, S).
ggp_mapplus_(20, 0, 20, S).
ggp_mapplus_(20, 1, 1, S).
ggp_mapplus_(20, 2, 2, S).
ggp_mapplus_(20, 3, 3, S).
ggp_mapplus_(19, minus_3, 16, S).
ggp_mapplus_(19, minus_2, 17, S).
ggp_mapplus_(19, minus_1, 18, S).
ggp_mapplus_(19, 0, 19, S).
ggp_mapplus_(19, 1, 20, S).
ggp_mapplus_(19, 2, 1, S).
ggp_mapplus_(19, 3, 2, S).
ggp_mapplus_(18, minus_3, 15, S).
ggp_mapplus_(18, minus_2, 16, S).
ggp_mapplus_(18, minus_1, 17, S).
ggp_mapplus_(18, 0, 18, S).
ggp_mapplus_(18, 1, 19, S).
ggp_mapplus_(18, 2, 20, S).
ggp_mapplus_(18, 3, 1, S).
ggp_mapplus_(17, minus_3, 14, S).
ggp_mapplus_(17, minus_2, 15, S).
ggp_mapplus_(17, minus_1, 16, S).
ggp_mapplus_(17, 0, 17, S).
ggp_mapplus_(17, 1, 18, S).
ggp_mapplus_(17, 2, 19, S).
ggp_mapplus_(17, 3, 20, S).
ggp_mapplus_(16, minus_3, 13, S).
ggp_mapplus_(16, minus_2, 14, S).
ggp_mapplus_(16, minus_1, 15, S).
ggp_mapplus_(16, 0, 16, S).
ggp_mapplus_(16, 1, 17, S).
ggp_mapplus_(16, 2, 18, S).
ggp_mapplus_(16, 3, 19, S).
ggp_mapplus_(15, minus_3, 12, S).
ggp_mapplus_(15, minus_2, 13, S).
ggp_mapplus_(15, minus_1, 14, S).
ggp_mapplus_(15, 0, 15, S).
ggp_mapplus_(15, 1, 16, S).
ggp_mapplus_(15, 2, 17, S).
ggp_mapplus_(15, 3, 18, S).
ggp_mapplus_(14, minus_3, 11, S).
ggp_mapplus_(14, minus_2, 12, S).
ggp_mapplus_(14, minus_1, 13, S).
ggp_mapplus_(14, 0, 14, S).
ggp_mapplus_(14, 1, 15, S).
ggp_mapplus_(14, 2, 16, S).
ggp_mapplus_(14, 3, 17, S).
ggp_mapplus_(13, minus_3, 10, S).
ggp_mapplus_(13, minus_2, 11, S).
ggp_mapplus_(13, minus_1, 12, S).
ggp_mapplus_(13, 0, 13, S).
ggp_mapplus_(13, 1, 14, S).
ggp_mapplus_(13, 2, 15, S).
ggp_mapplus_(13, 3, 16, S).
ggp_mapplus_(12, minus_3, 9, S).
ggp_mapplus_(12, minus_2, 10, S).
ggp_mapplus_(12, minus_1, 11, S).
ggp_mapplus_(12, 0, 12, S).
ggp_mapplus_(12, 1, 13, S).
ggp_mapplus_(12, 2, 14, S).
ggp_mapplus_(12, 3, 15, S).
ggp_mapplus_(11, minus_3, 8, S).
ggp_mapplus_(11, minus_2, 9, S).
ggp_mapplus_(11, minus_1, 10, S).
ggp_mapplus_(11, 0, 11, S).
ggp_mapplus_(11, 1, 12, S).
ggp_mapplus_(11, 2, 13, S).
ggp_mapplus_(11, 3, 14, S).
ggp_mapplus_(10, minus_3, 7, S).
ggp_mapplus_(10, minus_2, 8, S).
ggp_mapplus_(10, minus_1, 9, S).
ggp_mapplus_(10, 0, 10, S).
ggp_mapplus_(10, 1, 11, S).
ggp_mapplus_(10, 2, 12, S).
ggp_mapplus_(10, 3, 13, S).
ggp_mapplus_(9, minus_3, 6, S).
ggp_mapplus_(9, minus_2, 7, S).
ggp_mapplus_(9, minus_1, 8, S).
ggp_mapplus_(9, 0, 9, S).
ggp_mapplus_(9, 1, 10, S).
ggp_mapplus_(9, 2, 11, S).
ggp_mapplus_(9, 3, 12, S).
ggp_mapplus_(8, minus_3, 5, S).
ggp_mapplus_(8, minus_2, 6, S).
ggp_mapplus_(8, minus_1, 7, S).
ggp_mapplus_(8, 0, 8, S).
ggp_mapplus_(8, 1, 9, S).
ggp_mapplus_(8, 2, 10, S).
ggp_mapplus_(8, 3, 11, S).
ggp_mapplus_(7, minus_3, 4, S).
ggp_mapplus_(7, minus_2, 5, S).
ggp_mapplus_(7, minus_1, 6, S).
ggp_mapplus_(7, 0, 7, S).
ggp_mapplus_(7, 1, 8, S).
ggp_mapplus_(7, 2, 9, S).
ggp_mapplus_(7, 3, 10, S).
ggp_mapplus_(6, minus_3, 3, S).
ggp_mapplus_(6, minus_2, 4, S).
ggp_mapplus_(6, minus_1, 5, S).
ggp_mapplus_(6, 0, 6, S).
ggp_mapplus_(6, 1, 7, S).
ggp_mapplus_(6, 2, 8, S).
ggp_mapplus_(6, 3, 9, S).
ggp_mapplus_(5, minus_3, 2, S).
ggp_mapplus_(5, minus_2, 3, S).
ggp_mapplus_(5, minus_1, 4, S).
ggp_mapplus_(5, 0, 5, S).
ggp_mapplus_(5, 1, 6, S).
ggp_mapplus_(5, 2, 7, S).
ggp_mapplus_(5, 3, 8, S).
ggp_mapplus_(4, minus_3, 1, S).
ggp_mapplus_(4, minus_2, 2, S).
ggp_mapplus_(4, minus_1, 3, S).
ggp_mapplus_(4, 0, 4, S).
ggp_mapplus_(4, 1, 5, S).
ggp_mapplus_(4, 2, 6, S).
ggp_mapplus_(4, 3, 7, S).
ggp_mapplus_(3, minus_3, 20, S).
ggp_mapplus_(3, minus_2, 1, S).
ggp_mapplus_(3, minus_1, 2, S).
ggp_mapplus_(3, 0, 3, S).
ggp_mapplus_(3, 1, 4, S).
ggp_mapplus_(3, 2, 5, S).
ggp_mapplus_(3, 3, 6, S).
ggp_mapplus_(2, minus_3, 19, S).
ggp_mapplus_(2, minus_2, 20, S).
ggp_mapplus_(2, minus_1, 1, S).
ggp_mapplus_(2, 0, 2, S).
ggp_mapplus_(2, 1, 3, S).
ggp_mapplus_(2, 2, 4, S).
ggp_mapplus_(2, 3, 5, S).
ggp_mapplus_(1, minus_3, 18, S).
ggp_mapplus_(1, minus_2, 19, S).
ggp_mapplus_(1, minus_1, 20, S).
ggp_mapplus_(1, 0, 1, S).
ggp_mapplus_(1, 1, 2, S).
ggp_mapplus_(1, 2, 3, S).
ggp_mapplus_(1, 3, 4, S).
ggp_next(heading(VAR_h), S) :- ggp_true(heading(VAR_h), S), ggp_does(ship, thrust, S).
ggp_next(heading(west), S) :- ggp_true(heading(north), S), ggp_does(ship, turn(counter), S).
ggp_next(heading(south), S) :- ggp_true(heading(west), S), ggp_does(ship, turn(counter), S).
ggp_next(heading(east), S) :- ggp_true(heading(south), S), ggp_does(ship, turn(counter), S).
ggp_next(heading(north), S) :- ggp_true(heading(east), S), ggp_does(ship, turn(counter), S).
ggp_next(heading(east), S) :- ggp_true(heading(north), S), ggp_does(ship, turn(clock), S).
ggp_next(heading(south), S) :- ggp_true(heading(east), S), ggp_does(ship, turn(clock), S).
ggp_next(heading(west), S) :- ggp_true(heading(south), S), ggp_does(ship, turn(clock), S).
ggp_next(heading(north), S) :- ggp_true(heading(west), S), ggp_does(ship, turn(clock), S).
ggp_next(northminus_speed(VAR_s), S) :- ggp_true(northminus_speed(VAR_s), S), ggp_does(ship, turn(clock), S).
ggp_next(northminus_speed(VAR_s), S) :- ggp_true(northminus_speed(VAR_s), S), ggp_does(ship, turn(counter), S).
ggp_next(northminus_speed(VAR_s), S) :- ggp_true(northminus_speed(VAR_s), S), ggp_true(heading(east), S).
ggp_next(northminus_speed(VAR_s), S) :- ggp_true(northminus_speed(VAR_s), S), ggp_true(heading(west), S).
ggp_next(northminus_speed(VAR_s2), S) :- ggp_true(northminus_speed(VAR_s1), S), ggp_true(heading(north), S), ggp_does(ship, thrust, S), ggp_speedplus_(VAR_s1, VAR_s2, S).
ggp_next(northminus_speed(VAR_s2), S) :- ggp_true(northminus_speed(VAR_s1), S), ggp_true(heading(south), S), ggp_does(ship, thrust, S), ggp_speedminus_(VAR_s1, VAR_s2, S).
ggp_next(eastminus_speed(VAR_s), S) :- ggp_true(eastminus_speed(VAR_s), S), ggp_does(ship, turn(clock), S).
ggp_next(eastminus_speed(VAR_s), S) :- ggp_true(eastminus_speed(VAR_s), S), ggp_does(ship, turn(counter), S).
ggp_next(eastminus_speed(VAR_s), S) :- ggp_true(eastminus_speed(VAR_s), S), ggp_true(heading(north), S).
ggp_next(eastminus_speed(VAR_s), S) :- ggp_true(eastminus_speed(VAR_s), S), ggp_true(heading(south), S).
ggp_next(eastminus_speed(VAR_s2), S) :- ggp_true(eastminus_speed(VAR_s1), S), ggp_true(heading(east), S), ggp_does(ship, thrust, S), ggp_speedplus_(VAR_s1, VAR_s2, S).
ggp_next(eastminus_speed(VAR_s2), S) :- ggp_true(eastminus_speed(VAR_s1), S), ggp_true(heading(west), S), ggp_does(ship, thrust, S), ggp_speedminus_(VAR_s1, VAR_s2, S).
ggp_next(x(VAR_new), S) :- ggp_true(x(VAR_old), S), ggp_true(eastminus_speed(VAR_s), S), ggp_mapplus_(VAR_old, VAR_s, VAR_new, S).
ggp_next(y(VAR_new), S) :- ggp_true(y(VAR_old), S), ggp_true(northminus_speed(VAR_s), S), ggp_mapplus_(VAR_old, VAR_s, VAR_new, S).
ggp_next(step(VAR_nplus_plus_), S) :- ggp_true(step(VAR_n), S), ggp_succ(VAR_n, VAR_nplus_plus_, S).
ggp_role(ship).
ggp_speedplus_(minus_3, minus_2, S).
ggp_speedplus_(minus_2, minus_1, S).
ggp_speedplus_(minus_1, 0, S).
ggp_speedplus_(0, 1, S).
ggp_speedplus_(1, 2, S).
ggp_speedplus_(2, 3, S).
ggp_speedplus_(3, 3, S).
ggp_speedminus_(3, 2, S).
ggp_speedminus_(2, 1, S).
ggp_speedminus_(1, 0, S).
ggp_speedminus_(0, minus_1, S).
ggp_speedminus_(minus_1, minus_2, S).
ggp_speedminus_(minus_2, minus_3, S).
ggp_speedminus_(minus_3, minus_3, S).
ggp_stopped(S) :- ggp_true(northminus_speed(0), S), ggp_true(eastminus_speed(0), S).
ggp_succ(1, 2, S).
ggp_succ(2, 3, S).
ggp_succ(3, 4, S).
ggp_succ(4, 5, S).
ggp_succ(5, 6, S).
ggp_succ(6, 7, S).
ggp_succ(7, 8, S).
ggp_succ(8, 9, S).
ggp_succ(9, 10, S).
ggp_succ(10, 11, S).
ggp_succ(11, 12, S).
ggp_succ(12, 13, S).
ggp_succ(13, 14, S).
ggp_succ(14, 15, S).
ggp_succ(15, 16, S).
ggp_succ(16, 17, S).
ggp_succ(17, 18, S).
ggp_succ(18, 19, S).
ggp_succ(19, 20, S).
ggp_succ(20, 21, S).
ggp_succ(21, 22, S).
ggp_succ(22, 23, S).
ggp_succ(23, 24, S).
ggp_succ(24, 25, S).
ggp_succ(25, 26, S).
ggp_succ(26, 27, S).
ggp_succ(27, 28, S).
ggp_succ(28, 29, S).
ggp_succ(29, 30, S).
ggp_succ(30, 31, S).
ggp_succ(31, 32, S).
ggp_succ(32, 33, S).
ggp_succ(33, 34, S).
ggp_succ(34, 35, S).
ggp_succ(35, 36, S).
ggp_succ(36, 37, S).
ggp_succ(37, 38, S).
ggp_succ(38, 39, S).
ggp_succ(39, 40, S).
ggp_succ(40, 41, S).
ggp_succ(41, 42, S).
ggp_succ(42, 43, S).
ggp_succ(43, 44, S).
ggp_succ(44, 45, S).
ggp_succ(45, 46, S).
ggp_succ(46, 47, S).
ggp_succ(47, 48, S).
ggp_succ(48, 49, S).
ggp_succ(49, 50, S).
ggp_terminal(S) :- ggp_stopped(S).
ggp_terminal(S) :- ggp_timeout(S).
ggp_timeout(S) :- ggp_true(step(50), S).
