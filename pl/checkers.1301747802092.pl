ggp_adjacent(VAR_x1, VAR_x2, S) :- ggp_nextunterstrich_file(VAR_x1, VAR_x2, S).
ggp_adjacent(VAR_x1, VAR_x2, S) :- ggp_nextunterstrich_file(VAR_x2, VAR_x1, S).
ggp_adjacent(VAR_y1, VAR_y2, S) :- ggp_nextunterstrich_rank(VAR_y1, VAR_y2, S).
ggp_adjacent(VAR_y1, VAR_y2, S) :- ggp_nextunterstrich_rank(VAR_y2, VAR_y1, S).
ggp_coordinate(1, S).
ggp_coordinate(2, S).
ggp_coordinate(3, S).
ggp_coordinate(4, S).
ggp_coordinate(5, S).
ggp_coordinate(6, S).
ggp_coordinate(7, S).
ggp_coordinate(8, S).
ggp_coordinate(a, S).
ggp_coordinate(b, S).
ggp_coordinate(c, S).
ggp_coordinate(d, S).
ggp_coordinate(e, S).
ggp_coordinate(f, S).
ggp_coordinate(g, S).
ggp_coordinate(h, S).
ggp_differentunterstrich_cells(VAR_x1, VAR_y1, VAR_x2, VAR_y2, S) :- VAR_x1 \= VAR_x2, ggp_coordinate(VAR_x1, S), ggp_coordinate(VAR_x2, S), ggp_coordinate(VAR_y1, S), ggp_coordinate(VAR_y2, S).
ggp_differentunterstrich_cells(VAR_x1, VAR_y1, VAR_x2, VAR_y2, S) :- VAR_y1 \= VAR_y2, ggp_coordinate(VAR_x1, S), ggp_coordinate(VAR_x2, S), ggp_coordinate(VAR_y1, S), ggp_coordinate(VAR_y2, S).
ggp_goal(white, 100, S) :- ggp_true(pieceunterstrich_count(white, VAR_rc), S), ggp_true(pieceunterstrich_count(black, VAR_bc), S), ggp_greater(VAR_rc, VAR_bc, S).
ggp_goal(white, 50, S) :- ggp_true(pieceunterstrich_count(white, VAR_x), S), ggp_true(pieceunterstrich_count(black, VAR_x), S).
ggp_goal(white, 0, S) :- ggp_true(pieceunterstrich_count(white, VAR_rc), S), ggp_true(pieceunterstrich_count(black, VAR_bc), S), ggp_greater(VAR_bc, VAR_rc, S).
ggp_goal(black, 100, S) :- ggp_true(pieceunterstrich_count(white, VAR_rc), S), ggp_true(pieceunterstrich_count(black, VAR_bc), S), ggp_greater(VAR_bc, VAR_rc, S).
ggp_goal(black, 50, S) :- ggp_true(pieceunterstrich_count(white, VAR_x), S), ggp_true(pieceunterstrich_count(black, VAR_x), S).
ggp_goal(black, 0, S) :- ggp_true(pieceunterstrich_count(white, VAR_rc), S), ggp_true(pieceunterstrich_count(black, VAR_bc), S), ggp_greater(VAR_rc, VAR_bc, S).
ggp_greater(VAR_a, VAR_b, S) :- ggp_succ(VAR_b, VAR_a, S).
ggp_greater(VAR_a, VAR_b, S) :- VAR_a \= VAR_b, ggp_succ(VAR_c, VAR_a, S), ggp_greater(VAR_c, VAR_b, S).
ggp_hasunterstrich_legalunterstrich_move(VAR_player, S) :- ggp_pieceunterstrich_ownerunterstrich_type(VAR_piece, VAR_player, VAR_type, S), (ggp_legal(VAR_player, move(VAR_piece, VAR_u, VAR_v, VAR_x, VAR_y), S) ; ggp_legal(VAR_player, doublejump(VAR_piece, VAR_u, VAR_v, VAR_x1, VAR_y1, VAR_x, VAR_y), S) ; ggp_legal(VAR_player, triplejump(VAR_piece, VAR_u, VAR_v, VAR_x1, VAR_y1, VAR_x2, VAR_y2, VAR_x, VAR_y), S)).
ggp_init(cell(a, 1, b)).
ggp_init(cell(a, 3, b)).
ggp_init(cell(a, 4, b)).
ggp_init(cell(a, 5, b)).
ggp_init(cell(a, 7, b)).
ggp_init(cell(b, 2, b)).
ggp_init(cell(b, 4, b)).
ggp_init(cell(b, 5, b)).
ggp_init(cell(b, 6, b)).
ggp_init(cell(b, 8, b)).
ggp_init(cell(c, 1, b)).
ggp_init(cell(c, 3, b)).
ggp_init(cell(c, 4, b)).
ggp_init(cell(c, 5, b)).
ggp_init(cell(c, 7, b)).
ggp_init(cell(d, 2, b)).
ggp_init(cell(d, 4, b)).
ggp_init(cell(d, 5, b)).
ggp_init(cell(d, 6, b)).
ggp_init(cell(d, 8, b)).
ggp_init(cell(e, 1, b)).
ggp_init(cell(e, 3, b)).
ggp_init(cell(e, 4, b)).
ggp_init(cell(e, 5, b)).
ggp_init(cell(e, 7, b)).
ggp_init(cell(f, 2, b)).
ggp_init(cell(f, 4, b)).
ggp_init(cell(f, 5, b)).
ggp_init(cell(f, 6, b)).
ggp_init(cell(f, 8, b)).
ggp_init(cell(g, 1, b)).
ggp_init(cell(g, 3, b)).
ggp_init(cell(g, 4, b)).
ggp_init(cell(g, 5, b)).
ggp_init(cell(g, 7, b)).
ggp_init(cell(h, 2, b)).
ggp_init(cell(h, 4, b)).
ggp_init(cell(h, 5, b)).
ggp_init(cell(h, 6, b)).
ggp_init(cell(h, 8, b)).
ggp_init(cell(a, 2, wp)).
ggp_init(cell(b, 1, wp)).
ggp_init(cell(c, 2, wp)).
ggp_init(cell(d, 1, wp)).
ggp_init(cell(e, 2, wp)).
ggp_init(cell(f, 1, wp)).
ggp_init(cell(g, 2, wp)).
ggp_init(cell(h, 1, wp)).
ggp_init(cell(b, 3, wp)).
ggp_init(cell(d, 3, wp)).
ggp_init(cell(f, 3, wp)).
ggp_init(cell(h, 3, wp)).
ggp_init(cell(a, 8, bp)).
ggp_init(cell(c, 8, bp)).
ggp_init(cell(e, 8, bp)).
ggp_init(cell(g, 8, bp)).
ggp_init(cell(h, 7, bp)).
ggp_init(cell(f, 7, bp)).
ggp_init(cell(d, 7, bp)).
ggp_init(cell(b, 7, bp)).
ggp_init(cell(a, 6, bp)).
ggp_init(cell(c, 6, bp)).
ggp_init(cell(e, 6, bp)).
ggp_init(cell(g, 6, bp)).
ggp_init(control(white)).
ggp_init(step(1)).
ggp_init(pieceunterstrich_count(white, 12)).
ggp_init(pieceunterstrich_count(black, 12)).
ggp_kingjump(VAR_player, VAR_u, VAR_v, VAR_x, VAR_y, S) :- ggp_role(VAR_player), ggp_role(VAR_player2), ggp_pawnjump(VAR_player2, VAR_u, VAR_v, VAR_x, VAR_y, S).
ggp_kingmove(VAR_player, VAR_u, VAR_v, VAR_x, VAR_y, S) :- ggp_role(VAR_player), ggp_role(VAR_player2), ggp_pawnmove(VAR_player2, VAR_u, VAR_v, VAR_x, VAR_y, S).
ggp_legal(VAR_player, move(VAR_piece, VAR_u, VAR_v, VAR_x, VAR_y), S) :- ggp_true(control(VAR_player), S), ggp_true(cell(VAR_u, VAR_v, VAR_piece), S), ggp_pieceunterstrich_ownerunterstrich_type(VAR_piece, VAR_player, pawn, S), ggp_pawnmove(VAR_player, VAR_u, VAR_v, VAR_x, VAR_y, S), ggp_true(cell(VAR_x, VAR_y, b), S).
ggp_legal(VAR_player, move(VAR_piece, VAR_u, VAR_v, VAR_x, VAR_y), S) :- ggp_true(control(VAR_player), S), ggp_true(cell(VAR_u, VAR_v, VAR_piece), S), ggp_pieceunterstrich_ownerunterstrich_type(VAR_piece, VAR_player, king, S), ggp_kingmove(VAR_player, VAR_u, VAR_v, VAR_x, VAR_y, S), ggp_true(cell(VAR_x, VAR_y, b), S).
ggp_legal(VAR_player, move(VAR_piece, VAR_u, VAR_v, VAR_x, VAR_y), S) :- ggp_true(control(VAR_player), S), ggp_true(cell(VAR_u, VAR_v, VAR_piece), S), ggp_pieceunterstrich_ownerunterstrich_type(VAR_piece, VAR_player, pawn, S), ggp_pawnjump(VAR_player, VAR_u, VAR_v, VAR_x, VAR_y, S), ggp_true(cell(VAR_x, VAR_y, b), S), ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_u, VAR_v, VAR_c, VAR_d, VAR_x, VAR_y, S).
ggp_legal(VAR_player, move(VAR_piece, VAR_u, VAR_v, VAR_x, VAR_y), S) :- ggp_true(control(VAR_player), S), ggp_true(cell(VAR_u, VAR_v, VAR_piece), S), ggp_pieceunterstrich_ownerunterstrich_type(VAR_piece, VAR_player, king, S), ggp_kingjump(VAR_player, VAR_u, VAR_v, VAR_x, VAR_y, S), ggp_true(cell(VAR_x, VAR_y, b), S), ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_u, VAR_v, VAR_c, VAR_d, VAR_x, VAR_y, S).
ggp_legal(VAR_player, doublejump(VAR_piece, VAR_u, VAR_v, VAR_x1, VAR_y1, VAR_x2, VAR_y2), S) :- ggp_true(control(VAR_player), S), ggp_true(cell(VAR_u, VAR_v, VAR_piece), S), ggp_pieceunterstrich_ownerunterstrich_type(VAR_piece, VAR_player, pawn, S), ggp_pawnjump(VAR_player, VAR_u, VAR_v, VAR_x1, VAR_y1, S), ggp_true(cell(VAR_x1, VAR_y1, b), S), ggp_pawnjump(VAR_player, VAR_x1, VAR_y1, VAR_x2, VAR_y2, S), ggp_true(cell(VAR_x2, VAR_y2, b), S), ggp_differentunterstrich_cells(VAR_u, VAR_v, VAR_x2, VAR_y2, S), ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_u, VAR_v, VAR_c, VAR_d, VAR_x1, VAR_y1, S), ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x1, VAR_y1, VAR_c1, VAR_d1, VAR_x2, VAR_y2, S).
ggp_legal(VAR_player, doublejump(VAR_piece, VAR_u, VAR_v, VAR_x1, VAR_y1, VAR_x2, VAR_y2), S) :- ggp_true(control(VAR_player), S), ggp_true(cell(VAR_u, VAR_v, VAR_piece), S), ggp_pieceunterstrich_ownerunterstrich_type(VAR_piece, VAR_player, king, S), ggp_kingjump(VAR_player, VAR_u, VAR_v, VAR_x1, VAR_y1, S), ggp_true(cell(VAR_x1, VAR_y1, b), S), ggp_kingjump(VAR_player, VAR_x1, VAR_y1, VAR_x2, VAR_y2, S), ggp_true(cell(VAR_x2, VAR_y2, b), S), ggp_differentunterstrich_cells(VAR_u, VAR_v, VAR_x2, VAR_y2, S), ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_u, VAR_v, VAR_c, VAR_d, VAR_x1, VAR_y1, S), ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x1, VAR_y1, VAR_c1, VAR_d1, VAR_x2, VAR_y2, S).
ggp_legal(VAR_player, triplejump(VAR_piece, VAR_u, VAR_v, VAR_x1, VAR_y1, VAR_x2, VAR_y2, VAR_x3, VAR_y3), S) :- ggp_true(control(VAR_player), S), ggp_true(cell(VAR_u, VAR_v, VAR_piece), S), ggp_pieceunterstrich_ownerunterstrich_type(VAR_piece, VAR_player, pawn, S), ggp_pawnjump(VAR_player, VAR_u, VAR_v, VAR_x1, VAR_y1, S), ggp_true(cell(VAR_x1, VAR_y1, b), S), ggp_pawnjump(VAR_player, VAR_x1, VAR_y1, VAR_x2, VAR_y2, S), ggp_true(cell(VAR_x2, VAR_y2, b), S), ggp_differentunterstrich_cells(VAR_u, VAR_v, VAR_x2, VAR_y2, S), ggp_pawnjump(VAR_player, VAR_x2, VAR_y2, VAR_x3, VAR_y3, S), ggp_true(cell(VAR_x3, VAR_y3, b), S), ggp_differentunterstrich_cells(VAR_x1, VAR_y1, VAR_x3, VAR_y3, S), ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_u, VAR_v, VAR_c, VAR_d, VAR_x1, VAR_y1, S), ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x1, VAR_y1, VAR_c1, VAR_d1, VAR_x2, VAR_y2, S), ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x2, VAR_y2, VAR_c2, VAR_d2, VAR_x3, VAR_y3, S).
ggp_legal(VAR_player, triplejump(VAR_piece, VAR_u, VAR_v, VAR_x1, VAR_y1, VAR_x2, VAR_y2, VAR_x3, VAR_y3), S) :- ggp_true(control(VAR_player), S), ggp_true(cell(VAR_u, VAR_v, VAR_piece), S), ggp_pieceunterstrich_ownerunterstrich_type(VAR_piece, VAR_player, king, S), ggp_kingjump(VAR_player, VAR_u, VAR_v, VAR_x1, VAR_y1, S), ggp_true(cell(VAR_x1, VAR_y1, b), S), ggp_kingjump(VAR_player, VAR_x1, VAR_y1, VAR_x2, VAR_y2, S), ggp_true(cell(VAR_x2, VAR_y2, b), S), ggp_differentunterstrich_cells(VAR_u, VAR_v, VAR_x2, VAR_y2, S), ggp_kingjump(VAR_player, VAR_x2, VAR_y2, VAR_x3, VAR_y3, S), ggp_true(cell(VAR_x3, VAR_y3, b), S), ggp_differentunterstrich_cells(VAR_x1, VAR_y1, VAR_x3, VAR_y3, S), ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_u, VAR_v, VAR_c, VAR_d, VAR_x1, VAR_y1, S), ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x1, VAR_y1, VAR_c1, VAR_d1, VAR_x2, VAR_y2, S), ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x2, VAR_y2, VAR_c2, VAR_d2, VAR_x3, VAR_y3, S).
ggp_legal(white, noop, S) :- ggp_true(control(black), S).
ggp_legal(black, noop, S) :- ggp_true(control(white), S).
ggp_minus1(12, 11, S).
ggp_minus1(11, 10, S).
ggp_minus1(10, 9, S).
ggp_minus1(9, 8, S).
ggp_minus1(8, 7, S).
ggp_minus1(7, 6, S).
ggp_minus1(6, 5, S).
ggp_minus1(5, 4, S).
ggp_minus1(4, 3, S).
ggp_minus1(3, 2, S).
ggp_minus1(2, 1, S).
ggp_minus1(1, 0, S).
ggp_minus2(12, 10, S).
ggp_minus2(11, 9, S).
ggp_minus2(10, 8, S).
ggp_minus2(9, 7, S).
ggp_minus2(8, 6, S).
ggp_minus2(7, 5, S).
ggp_minus2(6, 4, S).
ggp_minus2(5, 3, S).
ggp_minus2(4, 2, S).
ggp_minus2(3, 1, S).
ggp_minus2(2, 0, S).
ggp_minus3(12, 9, S).
ggp_minus3(11, 8, S).
ggp_minus3(10, 7, S).
ggp_minus3(9, 6, S).
ggp_minus3(8, 5, S).
ggp_minus3(7, 4, S).
ggp_minus3(6, 3, S).
ggp_minus3(5, 2, S).
ggp_minus3(4, 1, S).
ggp_minus3(3, 0, S).
ggp_next(cell(VAR_u, VAR_v, b), S) :- ggp_does(VAR_player, move(VAR_p, VAR_u, VAR_v, VAR_x, VAR_y), S).
ggp_next(cell(VAR_u, VAR_v, b), S) :- ggp_does(VAR_player, doublejump(VAR_p, VAR_u, VAR_v, VAR_x, VAR_y, VAR_x3, VAR_y3), S).
ggp_next(cell(VAR_u, VAR_v, b), S) :- ggp_does(VAR_player, triplejump(VAR_p, VAR_u, VAR_v, VAR_x, VAR_y, VAR_x3, VAR_y3, VAR_x4, VAR_y4), S).
ggp_next(cell(VAR_x, VAR_y, VAR_p), S) :- ggp_does(VAR_player, move(VAR_p, VAR_u, VAR_v, VAR_x, VAR_y), S), (VAR_p \= wp ; VAR_y \= 8), (VAR_p \= bp ; VAR_y \= 1).
ggp_next(cell(VAR_x, VAR_y, VAR_p), S) :- ggp_does(VAR_player, doublejump(VAR_p, VAR_u, VAR_v, VAR_x3, VAR_y3, VAR_x, VAR_y), S), (VAR_p \= wp ; VAR_y \= 8), (VAR_p \= bp ; VAR_y \= 1).
ggp_next(cell(VAR_x, VAR_y, VAR_p), S) :- ggp_does(VAR_player, triplejump(VAR_p, VAR_u, VAR_v, VAR_x3, VAR_y3, VAR_x4, VAR_y4, VAR_x, VAR_y), S), (VAR_p \= wp ; VAR_y \= 8), (VAR_p \= bp ; VAR_y \= 1).
ggp_next(cell(VAR_x, VAR_y, VAR_p), S) :- ggp_does(VAR_player, move(VAR_piece, VAR_x1, VAR_y1, VAR_x2, VAR_y2), S), ggp_true(cell(VAR_x, VAR_y, VAR_p), S), ~ ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x1, VAR_y1, VAR_x, VAR_y, VAR_x2, VAR_y2, S), ggp_differentunterstrich_cells(VAR_x, VAR_y, VAR_x1, VAR_y1, S), ggp_differentunterstrich_cells(VAR_x, VAR_y, VAR_x2, VAR_y2, S).
ggp_next(cell(VAR_x, VAR_y, VAR_p), S) :- ggp_does(VAR_player, doublejump(VAR_piece, VAR_x1, VAR_y1, VAR_x2, VAR_y2, VAR_x3, VAR_y3), S), ggp_true(cell(VAR_x, VAR_y, VAR_p), S), ~ ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x1, VAR_y1, VAR_x, VAR_y, VAR_x2, VAR_y2, S), ~ ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x2, VAR_y2, VAR_x, VAR_y, VAR_x3, VAR_y3, S), ggp_differentunterstrich_cells(VAR_x, VAR_y, VAR_x1, VAR_y1, S), ggp_differentunterstrich_cells(VAR_x, VAR_y, VAR_x3, VAR_y3, S).
ggp_next(cell(VAR_x, VAR_y, VAR_p), S) :- ggp_does(VAR_player, triplejump(VAR_piece, VAR_x1, VAR_y1, VAR_x2, VAR_y2, VAR_x3, VAR_y3, VAR_x4, VAR_y4), S), ggp_true(cell(VAR_x, VAR_y, VAR_p), S), ~ ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x1, VAR_y1, VAR_x, VAR_y, VAR_x2, VAR_y2, S), ~ ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x2, VAR_y2, VAR_x, VAR_y, VAR_x3, VAR_y3, S), ~ ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x3, VAR_y3, VAR_x, VAR_y, VAR_x4, VAR_y4, S), ggp_differentunterstrich_cells(VAR_x, VAR_y, VAR_x1, VAR_y1, S), ggp_differentunterstrich_cells(VAR_x, VAR_y, VAR_x4, VAR_y4, S).
ggp_next(cell(VAR_x, VAR_y, b), S) :- ggp_does(VAR_player, move(VAR_piece, VAR_x1, VAR_y1, VAR_x2, VAR_y2), S), ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x1, VAR_y1, VAR_x, VAR_y, VAR_x2, VAR_y2, S).
ggp_next(cell(VAR_x, VAR_y, b), S) :- ggp_does(VAR_player, doublejump(VAR_piece, VAR_x1, VAR_y1, VAR_x2, VAR_y2, VAR_x3, VAR_y3), S), (ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x1, VAR_y1, VAR_x, VAR_y, VAR_x2, VAR_y2, S) ; ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x2, VAR_y2, VAR_x, VAR_y, VAR_x3, VAR_y3, S)).
ggp_next(cell(VAR_x, VAR_y, b), S) :- ggp_does(VAR_player, triplejump(VAR_piece, VAR_x1, VAR_y1, VAR_x2, VAR_y2, VAR_x3, VAR_y3, VAR_x4, VAR_y4), S), (ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x1, VAR_y1, VAR_x, VAR_y, VAR_x2, VAR_y2, S) ; ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x2, VAR_y2, VAR_x, VAR_y, VAR_x3, VAR_y3, S) ; ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_x3, VAR_y3, VAR_x, VAR_y, VAR_x4, VAR_y4, S)).
ggp_next(control(white), S) :- ggp_true(control(black), S).
ggp_next(control(black), S) :- ggp_true(control(white), S).
ggp_next(step(VAR_y), S) :- ggp_true(step(VAR_x), S), ggp_succ(VAR_x, VAR_y, S).
ggp_next(cell(VAR_x, 8, wk), S) :- ggp_does(white, move(wp, VAR_u, VAR_v, VAR_x, 8), S).
ggp_next(cell(VAR_x, 1, bk), S) :- ggp_does(black, move(bp, VAR_u, VAR_v, VAR_x, 1), S).
ggp_next(cell(VAR_x, 8, wk), S) :- ggp_does(white, doublejump(wp, VAR_u, VAR_v, VAR_x3, VAR_y3, VAR_x, 8), S).
ggp_next(cell(VAR_x, 1, bk), S) :- ggp_does(black, doublejump(bp, VAR_u, VAR_v, VAR_x3, VAR_y3, VAR_x, 1), S).
ggp_next(cell(VAR_x, 8, wk), S) :- ggp_does(white, triplejump(VAR_p, VAR_u, VAR_v, VAR_x3, VAR_y3, VAR_x4, VAR_y4, VAR_x, 8), S).
ggp_next(cell(VAR_x, 1, bk), S) :- ggp_does(black, triplejump(VAR_p, VAR_u, VAR_v, VAR_x3, VAR_y3, VAR_x4, VAR_y4, VAR_x, 1), S).
ggp_next(pieceunterstrich_count(VAR_player, VAR_n), S) :- (ggp_does(VAR_player, move(VAR_p, VAR_u, VAR_v, VAR_x, VAR_y), S) ; ggp_does(VAR_player, doublejump(VAR_p, VAR_u, VAR_v, VAR_x3, VAR_y3, VAR_x, VAR_y), S) ; ggp_does(VAR_player, triplejump(VAR_p, VAR_u, VAR_v, VAR_x3, VAR_y3, VAR_x4, VAR_y4, VAR_x, VAR_y), S)), ggp_true(pieceunterstrich_count(VAR_player, VAR_n), S).
ggp_next(pieceunterstrich_count(white, VAR_n), S) :- ggp_does(black, move(VAR_p, VAR_x1, VAR_y1, VAR_x2, VAR_y2), S), ggp_kingmove(black, VAR_x1, VAR_y1, VAR_x2, VAR_y2, S), ggp_true(pieceunterstrich_count(white, VAR_n), S).
ggp_next(pieceunterstrich_count(white, VAR_lower), S) :- ggp_does(black, move(VAR_p, VAR_x1, VAR_y1, VAR_x2, VAR_y2), S), ggp_singleunterstrich_jumpunterstrich_capture(black, VAR_x1, VAR_y1, VAR_x, VAR_y, VAR_x2, VAR_y2, S), ggp_true(pieceunterstrich_count(white, VAR_higher), S), ggp_minus1(VAR_higher, VAR_lower, S).
ggp_next(pieceunterstrich_count(white, VAR_lower), S) :- ggp_does(black, doublejump(VAR_p, VAR_u, VAR_v, VAR_x3, VAR_y3, VAR_x, VAR_y), S), ggp_true(pieceunterstrich_count(white, VAR_higher), S), ggp_minus2(VAR_higher, VAR_lower, S).
ggp_next(pieceunterstrich_count(white, VAR_lower), S) :- ggp_does(black, triplejump(VAR_p, VAR_u, VAR_v, VAR_x3, VAR_y3, VAR_x4, VAR_y4, VAR_x, VAR_y), S), ggp_true(pieceunterstrich_count(white, VAR_higher), S), ggp_minus3(VAR_higher, VAR_lower, S).
ggp_next(pieceunterstrich_count(black, VAR_n), S) :- ggp_does(white, move(VAR_p, VAR_x1, VAR_y1, VAR_x2, VAR_y2), S), ggp_kingmove(white, VAR_x1, VAR_y1, VAR_x2, VAR_y2, S), ggp_true(pieceunterstrich_count(black, VAR_n), S).
ggp_next(pieceunterstrich_count(black, VAR_lower), S) :- ggp_does(white, move(VAR_p, VAR_x1, VAR_y1, VAR_x2, VAR_y2), S), ggp_singleunterstrich_jumpunterstrich_capture(white, VAR_x1, VAR_y1, VAR_x, VAR_y, VAR_x2, VAR_y2, S), ggp_true(pieceunterstrich_count(black, VAR_higher), S), ggp_minus1(VAR_higher, VAR_lower, S).
ggp_next(pieceunterstrich_count(black, VAR_lower), S) :- ggp_does(white, doublejump(VAR_p, VAR_u, VAR_v, VAR_x3, VAR_y3, VAR_x, VAR_y), S), ggp_true(pieceunterstrich_count(black, VAR_higher), S), ggp_minus2(VAR_higher, VAR_lower, S).
ggp_next(pieceunterstrich_count(black, VAR_lower), S) :- ggp_does(white, triplejump(VAR_p, VAR_u, VAR_v, VAR_x3, VAR_y3, VAR_x4, VAR_y4, VAR_x, VAR_y), S), ggp_true(pieceunterstrich_count(black, VAR_higher), S), ggp_minus3(VAR_higher, VAR_lower, S).
ggp_nextunterstrich_file(a, b, S).
ggp_nextunterstrich_file(b, c, S).
ggp_nextunterstrich_file(c, d, S).
ggp_nextunterstrich_file(d, e, S).
ggp_nextunterstrich_file(e, f, S).
ggp_nextunterstrich_file(f, g, S).
ggp_nextunterstrich_file(g, h, S).
ggp_nextunterstrich_rank(1, 2, S).
ggp_nextunterstrich_rank(2, 3, S).
ggp_nextunterstrich_rank(3, 4, S).
ggp_nextunterstrich_rank(4, 5, S).
ggp_nextunterstrich_rank(5, 6, S).
ggp_nextunterstrich_rank(6, 7, S).
ggp_nextunterstrich_rank(7, 8, S).
ggp_opponent(white, black, S).
ggp_opponent(black, white, S).
ggp_pawnjump(white, VAR_u, VAR_v, VAR_x, VAR_y, S) :- ggp_nextunterstrich_rank(VAR_v, VAR_v1, S), ggp_nextunterstrich_rank(VAR_v1, VAR_y, S), ggp_nextunterstrich_file(VAR_u, VAR_x1, S), ggp_nextunterstrich_file(VAR_x1, VAR_x, S).
ggp_pawnjump(white, VAR_u, VAR_v, VAR_x, VAR_y, S) :- ggp_nextunterstrich_rank(VAR_v, VAR_v1, S), ggp_nextunterstrich_rank(VAR_v1, VAR_y, S), ggp_nextunterstrich_file(VAR_x, VAR_x1, S), ggp_nextunterstrich_file(VAR_x1, VAR_u, S).
ggp_pawnjump(black, VAR_u, VAR_v, VAR_x, VAR_y, S) :- ggp_nextunterstrich_rank(VAR_y, VAR_v1, S), ggp_nextunterstrich_rank(VAR_v1, VAR_v, S), ggp_nextunterstrich_file(VAR_u, VAR_x1, S), ggp_nextunterstrich_file(VAR_x1, VAR_x, S).
ggp_pawnjump(black, VAR_u, VAR_v, VAR_x, VAR_y, S) :- ggp_nextunterstrich_rank(VAR_y, VAR_v1, S), ggp_nextunterstrich_rank(VAR_v1, VAR_v, S), ggp_nextunterstrich_file(VAR_x, VAR_x1, S), ggp_nextunterstrich_file(VAR_x1, VAR_u, S).
ggp_pawnmove(white, VAR_u, VAR_v, VAR_x, VAR_y, S) :- ggp_nextunterstrich_rank(VAR_v, VAR_y, S), (ggp_nextunterstrich_file(VAR_u, VAR_x, S) ; ggp_nextunterstrich_file(VAR_x, VAR_u, S)).
ggp_pawnmove(black, VAR_u, VAR_v, VAR_x, VAR_y, S) :- ggp_nextunterstrich_rank(VAR_y, VAR_v, S), (ggp_nextunterstrich_file(VAR_u, VAR_x, S) ; ggp_nextunterstrich_file(VAR_x, VAR_u, S)).
ggp_pieceunterstrich_ownerunterstrich_type(wk, white, king, S).
ggp_pieceunterstrich_ownerunterstrich_type(wp, white, pawn, S).
ggp_pieceunterstrich_ownerunterstrich_type(bk, black, king, S).
ggp_pieceunterstrich_ownerunterstrich_type(bp, black, pawn, S).
ggp_role(white).
ggp_role(black).
ggp_singleunterstrich_jumpunterstrich_capture(VAR_player, VAR_u, VAR_v, VAR_c, VAR_d, VAR_x, VAR_y, S) :- ggp_kingjump(VAR_player, VAR_u, VAR_v, VAR_x, VAR_y, S), ggp_kingmove(VAR_player, VAR_u, VAR_v, VAR_c, VAR_d, S), ggp_kingmove(VAR_player, VAR_c, VAR_d, VAR_x, VAR_y, S), ggp_true(cell(VAR_c, VAR_d, VAR_piece), S), ggp_opponent(VAR_player, VAR_opponent, S), ggp_pieceunterstrich_ownerunterstrich_type(VAR_piece, VAR_opponent, VAR_type, S).
ggp_stuck(VAR_player, S) :- ggp_role(VAR_player), ~ ggp_hasunterstrich_legalunterstrich_move(VAR_player, S).
ggp_succ(0, 1, S).
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
ggp_succ(50, 51, S).
ggp_succ(51, 52, S).
ggp_succ(52, 53, S).
ggp_succ(53, 54, S).
ggp_succ(54, 55, S).
ggp_succ(55, 56, S).
ggp_succ(56, 57, S).
ggp_succ(57, 58, S).
ggp_succ(58, 59, S).
ggp_succ(59, 60, S).
ggp_succ(60, 61, S).
ggp_succ(61, 62, S).
ggp_succ(62, 63, S).
ggp_succ(63, 64, S).
ggp_succ(64, 65, S).
ggp_succ(65, 66, S).
ggp_succ(66, 67, S).
ggp_succ(67, 68, S).
ggp_succ(68, 69, S).
ggp_succ(69, 70, S).
ggp_succ(70, 71, S).
ggp_succ(71, 72, S).
ggp_succ(72, 73, S).
ggp_succ(73, 74, S).
ggp_succ(74, 75, S).
ggp_succ(75, 76, S).
ggp_succ(76, 77, S).
ggp_succ(77, 78, S).
ggp_succ(78, 79, S).
ggp_succ(79, 80, S).
ggp_succ(80, 81, S).
ggp_succ(81, 82, S).
ggp_succ(82, 83, S).
ggp_succ(83, 84, S).
ggp_succ(84, 85, S).
ggp_succ(85, 86, S).
ggp_succ(86, 87, S).
ggp_succ(87, 88, S).
ggp_succ(88, 89, S).
ggp_succ(89, 90, S).
ggp_succ(90, 91, S).
ggp_succ(91, 92, S).
ggp_succ(92, 93, S).
ggp_succ(93, 94, S).
ggp_succ(94, 95, S).
ggp_succ(95, 96, S).
ggp_succ(96, 97, S).
ggp_succ(97, 98, S).
ggp_succ(98, 99, S).
ggp_succ(99, 100, S).
ggp_succ(100, 101, S).
ggp_succ(101, 102, S).
ggp_succ(102, 103, S).
ggp_succ(103, 104, S).
ggp_succ(104, 105, S).
ggp_succ(105, 106, S).
ggp_succ(106, 107, S).
ggp_succ(107, 108, S).
ggp_succ(108, 109, S).
ggp_succ(109, 110, S).
ggp_succ(110, 111, S).
ggp_succ(111, 112, S).
ggp_succ(112, 113, S).
ggp_succ(113, 114, S).
ggp_succ(114, 115, S).
ggp_succ(115, 116, S).
ggp_succ(116, 117, S).
ggp_succ(117, 118, S).
ggp_succ(118, 119, S).
ggp_succ(119, 120, S).
ggp_succ(120, 121, S).
ggp_succ(121, 122, S).
ggp_succ(122, 123, S).
ggp_succ(123, 124, S).
ggp_succ(124, 125, S).
ggp_succ(125, 126, S).
ggp_succ(126, 127, S).
ggp_succ(127, 128, S).
ggp_succ(128, 129, S).
ggp_succ(129, 130, S).
ggp_succ(130, 131, S).
ggp_succ(131, 132, S).
ggp_succ(132, 133, S).
ggp_succ(133, 134, S).
ggp_succ(134, 135, S).
ggp_succ(135, 136, S).
ggp_succ(136, 137, S).
ggp_succ(137, 138, S).
ggp_succ(138, 139, S).
ggp_succ(139, 140, S).
ggp_succ(140, 141, S).
ggp_succ(141, 142, S).
ggp_succ(142, 143, S).
ggp_succ(143, 144, S).
ggp_succ(144, 145, S).
ggp_succ(145, 146, S).
ggp_succ(146, 147, S).
ggp_succ(147, 148, S).
ggp_succ(148, 149, S).
ggp_succ(149, 150, S).
ggp_succ(150, 151, S).
ggp_succ(151, 152, S).
ggp_succ(152, 153, S).
ggp_succ(153, 154, S).
ggp_succ(154, 155, S).
ggp_succ(155, 156, S).
ggp_succ(156, 157, S).
ggp_succ(157, 158, S).
ggp_succ(158, 159, S).
ggp_succ(159, 160, S).
ggp_succ(160, 161, S).
ggp_succ(161, 162, S).
ggp_succ(162, 163, S).
ggp_succ(163, 164, S).
ggp_succ(164, 165, S).
ggp_succ(165, 166, S).
ggp_succ(166, 167, S).
ggp_succ(167, 168, S).
ggp_succ(168, 169, S).
ggp_succ(169, 170, S).
ggp_succ(170, 171, S).
ggp_succ(171, 172, S).
ggp_succ(172, 173, S).
ggp_succ(173, 174, S).
ggp_succ(174, 175, S).
ggp_succ(175, 176, S).
ggp_succ(176, 177, S).
ggp_succ(177, 178, S).
ggp_succ(178, 179, S).
ggp_succ(179, 180, S).
ggp_succ(180, 181, S).
ggp_succ(181, 182, S).
ggp_succ(182, 183, S).
ggp_succ(183, 184, S).
ggp_succ(184, 185, S).
ggp_succ(185, 186, S).
ggp_succ(186, 187, S).
ggp_succ(187, 188, S).
ggp_succ(188, 189, S).
ggp_succ(189, 190, S).
ggp_succ(190, 191, S).
ggp_succ(191, 192, S).
ggp_succ(192, 193, S).
ggp_succ(193, 194, S).
ggp_succ(194, 195, S).
ggp_succ(195, 196, S).
ggp_succ(196, 197, S).
ggp_succ(197, 198, S).
ggp_succ(198, 199, S).
ggp_succ(199, 200, S).
ggp_succ(200, 201, S).
ggp_terminal(S) :- ggp_true(control(VAR_player), S), ggp_stuck(VAR_player, S).
ggp_terminal(S) :- ggp_true(pieceunterstrich_count(VAR_player, 0), S).
ggp_terminal(S) :- ggp_true(step(102), S).
