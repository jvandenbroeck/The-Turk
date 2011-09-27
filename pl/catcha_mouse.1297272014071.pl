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
ggp_caught(S) :- ggp_true(cell(VAR_x, VAR_y, mouse), S), ggp_true(cell(VAR_x, VAR_y, trap), S).
ggp_connected(VAR_first, VAR_second, S) :- ggp_succ(VAR_first, VAR_second, S).
ggp_connected(VAR_second, VAR_first, S) :- ggp_succ(VAR_first, VAR_second, S).
ggp_escaped(S) :- ggp_true(cell(VAR_x, VAR_y, mouse), S), ggp_true(cell(VAR_x, VAR_y, hole), S).
ggp_goal(catcher, 100, S) :- ggp_caught(S).
ggp_goal(catcher, 0, S) :- ~ ggp_caught(S).
ggp_goal(mouse, 100, S) :- ggp_escaped(S).
ggp_goal(mouse, 0, S) :- ~ ggp_escaped(S).
ggp_init(control(catcher)).
ggp_init(cell(1, 1, empty)).
ggp_init(cell(1, 2, empty)).
ggp_init(cell(1, 3, empty)).
ggp_init(cell(1, 4, empty)).
ggp_init(cell(1, 5, empty)).
ggp_init(cell(1, 6, hole)).
ggp_init(cell(1, 7, empty)).
ggp_init(cell(1, 8, empty)).
ggp_init(cell(1, 9, empty)).
ggp_init(cell(2, a, empty)).
ggp_init(cell(2, b, empty)).
ggp_init(cell(2, c, empty)).
ggp_init(cell(2, d, hole)).
ggp_init(cell(2, e, hole)).
ggp_init(cell(2, f, empty)).
ggp_init(cell(2, g, empty)).
ggp_init(cell(2, h, empty)).
ggp_init(cell(3, 1, empty)).
ggp_init(cell(3, 2, empty)).
ggp_init(cell(3, 3, empty)).
ggp_init(cell(3, 4, hole)).
ggp_init(cell(3, 5, trap)).
ggp_init(cell(3, 6, empty)).
ggp_init(cell(3, 7, hole)).
ggp_init(cell(3, 8, empty)).
ggp_init(cell(3, 9, empty)).
ggp_init(cell(4, a, empty)).
ggp_init(cell(4, b, empty)).
ggp_init(cell(4, c, empty)).
ggp_init(cell(4, d, empty)).
ggp_init(cell(4, e, empty)).
ggp_init(cell(4, f, empty)).
ggp_init(cell(4, g, hole)).
ggp_init(cell(4, h, empty)).
ggp_init(cell(5, 1, empty)).
ggp_init(cell(5, 2, empty)).
ggp_init(cell(5, 3, empty)).
ggp_init(cell(5, 4, empty)).
ggp_init(cell(5, 5, empty)).
ggp_init(cell(5, 6, hole)).
ggp_init(cell(5, 7, empty)).
ggp_init(cell(5, 8, empty)).
ggp_init(cell(5, 9, empty)).
ggp_init(cell(6, a, empty)).
ggp_init(cell(6, b, empty)).
ggp_init(cell(6, c, trap)).
ggp_init(cell(6, d, empty)).
ggp_init(cell(6, e, empty)).
ggp_init(cell(6, f, hole)).
ggp_init(cell(6, g, empty)).
ggp_init(cell(6, h, empty)).
ggp_init(cell(7, 1, mouse)).
ggp_init(cell(7, 2, empty)).
ggp_init(cell(7, 3, empty)).
ggp_init(cell(7, 4, empty)).
ggp_init(cell(7, 5, empty)).
ggp_init(cell(7, 6, empty)).
ggp_init(cell(7, 7, empty)).
ggp_init(cell(7, 8, empty)).
ggp_init(cell(7, 9, empty)).
ggp_init(step(0)).
ggp_legal(VAR_p, noop, S) :- ggp_role(VAR_p), ~ ggp_true(control(VAR_p), S).
ggp_legal(catcher, layunterstrich_trap(VAR_x, VAR_y), S) :- ggp_true(control(catcher), S), ggp_true(cell(VAR_x, VAR_y, empty), S).
ggp_legal(mouse, move(VAR_x, VAR_y), S) :- ggp_true(control(mouse), S), ggp_true(cell(VAR_mx, VAR_my, mouse), S), ggp_true(cell(VAR_x, VAR_y, VAR_something), S), ggp_connected(VAR_mx, VAR_x, S), ggp_connected(VAR_my, VAR_y, S).
ggp_legal(mouse, move(VAR_x, VAR_y), S) :- ggp_true(control(mouse), S), ggp_true(cell(VAR_x, VAR_my, mouse), S), ggp_true(cell(VAR_x, VAR_y, VAR_something), S), ggp_connected(VAR_my, VAR_y, S).
ggp_next(step(VAR_newunterstrich_step), S) :- ggp_true(step(VAR_oldunterstrich_step), S), ggp_plus_plus_(VAR_oldunterstrich_step, VAR_newunterstrich_step, S).
ggp_next(cell(VAR_x, VAR_y, mouse), S) :- ggp_does(mouse, move(VAR_x, VAR_y), S).
ggp_next(cell(VAR_x, VAR_y, mouse), S) :- ggp_true(cell(VAR_x, VAR_y, mouse), S), ggp_true(control(catcher), S).
ggp_next(cell(VAR_x, VAR_y, trap), S) :- ggp_true(cell(VAR_x, VAR_y, trap), S).
ggp_next(cell(VAR_x, VAR_y, hole), S) :- ggp_true(cell(VAR_x, VAR_y, hole), S).
ggp_next(cell(VAR_x, VAR_y, trap), S) :- ggp_does(catcher, layunterstrich_trap(VAR_x, VAR_y), S).
ggp_next(cell(VAR_x, VAR_y, empty), S) :- ggp_true(cell(VAR_x, VAR_y, empty), S), ~ ggp_does(catcher, layunterstrich_trap(VAR_x, VAR_y), S), ~ ggp_does(mouse, move(VAR_x, VAR_y), S).
ggp_next(cell(VAR_x, VAR_y, empty), S) :- ggp_true(cell(VAR_x, VAR_y, mouse), S), ggp_true(control(mouse), S).
ggp_next(control(mouse), S) :- ggp_true(control(catcher), S).
ggp_next(control(catcher), S) :- ggp_true(control(mouse), S).
ggp_role(mouse).
ggp_role(catcher).
ggp_succ(1, 2, S).
ggp_succ(2, 3, S).
ggp_succ(3, 4, S).
ggp_succ(4, 5, S).
ggp_succ(5, 6, S).
ggp_succ(6, 7, S).
ggp_succ(7, 8, S).
ggp_succ(9, 10, S).
ggp_succ(1, a, S).
ggp_succ(2, b, S).
ggp_succ(3, c, S).
ggp_succ(4, d, S).
ggp_succ(5, e, S).
ggp_succ(6, f, S).
ggp_succ(7, g, S).
ggp_succ(8, h, S).
ggp_succ(a, b, S).
ggp_succ(b, c, S).
ggp_succ(c, d, S).
ggp_succ(d, e, S).
ggp_succ(e, f, S).
ggp_succ(f, g, S).
ggp_succ(g, h, S).
ggp_succ(h, 9, S).
ggp_terminal(S) :- ggp_caught(S).
ggp_terminal(S) :- ggp_escaped(S).
ggp_terminal(S) :- ggp_true(step(20), S).
