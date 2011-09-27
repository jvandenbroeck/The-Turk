role(ship).
holds(x(10), 1).
holds(y(10), 1).
holds(heading(north), 1).
holds(northminus_speed(3), 1).
holds(eastminus_speed(2), 1).
holds(step(1), 1).
legal(ship, thrust, T) :- timeDomain(T).
legal(ship, turn(clock), T) :- timeDomain(T).
legal(ship, turn(countReplicer), T) :- timeDomain(T).
holds(heading(H), T+1) :- holds(heading(H), T), does(ship, thrust, T), heading1Domain(H), timeDomain(T).
holds(heading(west), T+1) :- holds(heading(north), T), does(ship, turn(countReplicer), T), timeDomain(T).
holds(heading(south), T+1) :- holds(heading(west), T), does(ship, turn(countReplicer), T), timeDomain(T).
holds(heading(east), T+1) :- holds(heading(south), T), does(ship, turn(countReplicer), T), timeDomain(T).
holds(heading(north), T+1) :- holds(heading(east), T), does(ship, turn(countReplicer), T), timeDomain(T).
holds(heading(east), T+1) :- holds(heading(north), T), does(ship, turn(clock), T), timeDomain(T).
holds(heading(south), T+1) :- holds(heading(east), T), does(ship, turn(clock), T), timeDomain(T).
holds(heading(west), T+1) :- holds(heading(south), T), does(ship, turn(clock), T), timeDomain(T).
holds(heading(north), T+1) :- holds(heading(west), T), does(ship, turn(clock), T), timeDomain(T).
holds(northminus_speed(S), T+1) :- holds(northminus_speed(S), T), does(ship, turn(clock), T), northminus_speed1Domain(S), timeDomain(T).
holds(northminus_speed(S), T+1) :- holds(northminus_speed(S), T), does(ship, turn(countReplicer), T), northminus_speed1Domain(S), timeDomain(T).
holds(northminus_speed(S), T+1) :- holds(northminus_speed(S), T), holds(heading(east), T), northminus_speed1Domain(S), timeDomain(T).
holds(northminus_speed(S), T+1) :- holds(northminus_speed(S), T), holds(heading(west), T), northminus_speed1Domain(S), timeDomain(T).
holds(northminus_speed(S2), T+1) :- holds(northminus_speed(S1), T), holds(heading(north), T), does(ship, thrust, T), speedplus_(S1, S2), northminus_speed1Domain(S2), northminus_speed1Domain(S1), timeDomain(T).
holds(northminus_speed(S2), T+1) :- holds(northminus_speed(S1), T), holds(heading(south), T), does(ship, thrust, T), speedminus_(S1, S2), northminus_speed1Domain(S2), northminus_speed1Domain(S1), timeDomain(T).
holds(eastminus_speed(S), T+1) :- holds(eastminus_speed(S), T), does(ship, turn(clock), T), eastminus_speed1Domain(S), timeDomain(T).
holds(eastminus_speed(S), T+1) :- holds(eastminus_speed(S), T), does(ship, turn(countReplicer), T), eastminus_speed1Domain(S), timeDomain(T).
holds(eastminus_speed(S), T+1) :- holds(eastminus_speed(S), T), holds(heading(north), T), eastminus_speed1Domain(S), timeDomain(T).
holds(eastminus_speed(S), T+1) :- holds(eastminus_speed(S), T), holds(heading(south), T), eastminus_speed1Domain(S), timeDomain(T).
holds(eastminus_speed(S2), T+1) :- holds(eastminus_speed(S1), T), holds(heading(east), T), does(ship, thrust, T), speedplus_(S1, S2), eastminus_speed1Domain(S2), eastminus_speed1Domain(S1), timeDomain(T).
holds(eastminus_speed(S2), T+1) :- holds(eastminus_speed(S1), T), holds(heading(west), T), does(ship, thrust, T), speedminus_(S1, S2), eastminus_speed1Domain(S2), eastminus_speed1Domain(S1), timeDomain(T).
holds(x(NEW), T+1) :- holds(x(OLD), T), holds(eastminus_speed(S), T), mapplus_(OLD, S, NEW), x1Domain(NEW), x1Domain(OLD), eastminus_speed1Domain(S), timeDomain(T).
holds(y(NEW), T+1) :- holds(y(OLD), T), holds(northminus_speed(S), T), mapplus_(OLD, S, NEW), y1Domain(NEW), y1Domain(OLD), northminus_speed1Domain(S), timeDomain(T).
holds(step(Nplus_plus_), T+1) :- holds(step(N), T), succ(N, Nplus_plus_), step1Domain(Nplus_plus_), step1Domain(N), timeDomain(T).
terminal(T) :- stopped(T), timeDomain(T).
terminal(T) :- timeout(T), timeDomain(T).
goal(ship, 100, T) :- stopped(T), atplanet(T), timeDomain(T).
goal(ship, 50, T) :- stopped(T), not atplanet(T), timeDomain(T).
goal(ship, 0, T) :- not stopped(T), timeDomain(T).
stopped(T) :- holds(northminus_speed(0), T), holds(eastminus_speed(0), T), timeDomain(T).
atplanet(T) :- holds(x(15), T), holds(y(5), T), timeDomain(T).
timeout(T) :- holds(step(50), T), timeDomain(T).
mapplus_(20, minus_3, 17).
mapplus_(20, minus_2, 18).
mapplus_(20, minus_1, 19).
mapplus_(20, 0, 20).
mapplus_(20, 1, 1).
mapplus_(20, 2, 2).
mapplus_(20, 3, 3).
mapplus_(19, minus_3, 16).
mapplus_(19, minus_2, 17).
mapplus_(19, minus_1, 18).
mapplus_(19, 0, 19).
mapplus_(19, 1, 20).
mapplus_(19, 2, 1).
mapplus_(19, 3, 2).
mapplus_(18, minus_3, 15).
mapplus_(18, minus_2, 16).
mapplus_(18, minus_1, 17).
mapplus_(18, 0, 18).
mapplus_(18, 1, 19).
mapplus_(18, 2, 20).
mapplus_(18, 3, 1).
mapplus_(17, minus_3, 14).
mapplus_(17, minus_2, 15).
mapplus_(17, minus_1, 16).
mapplus_(17, 0, 17).
mapplus_(17, 1, 18).
mapplus_(17, 2, 19).
mapplus_(17, 3, 20).
mapplus_(16, minus_3, 13).
mapplus_(16, minus_2, 14).
mapplus_(16, minus_1, 15).
mapplus_(16, 0, 16).
mapplus_(16, 1, 17).
mapplus_(16, 2, 18).
mapplus_(16, 3, 19).
mapplus_(15, minus_3, 12).
mapplus_(15, minus_2, 13).
mapplus_(15, minus_1, 14).
mapplus_(15, 0, 15).
mapplus_(15, 1, 16).
mapplus_(15, 2, 17).
mapplus_(15, 3, 18).
mapplus_(14, minus_3, 11).
mapplus_(14, minus_2, 12).
mapplus_(14, minus_1, 13).
mapplus_(14, 0, 14).
mapplus_(14, 1, 15).
mapplus_(14, 2, 16).
mapplus_(14, 3, 17).
mapplus_(13, minus_3, 10).
mapplus_(13, minus_2, 11).
mapplus_(13, minus_1, 12).
mapplus_(13, 0, 13).
mapplus_(13, 1, 14).
mapplus_(13, 2, 15).
mapplus_(13, 3, 16).
mapplus_(12, minus_3, 9).
mapplus_(12, minus_2, 10).
mapplus_(12, minus_1, 11).
mapplus_(12, 0, 12).
mapplus_(12, 1, 13).
mapplus_(12, 2, 14).
mapplus_(12, 3, 15).
mapplus_(11, minus_3, 8).
mapplus_(11, minus_2, 9).
mapplus_(11, minus_1, 10).
mapplus_(11, 0, 11).
mapplus_(11, 1, 12).
mapplus_(11, 2, 13).
mapplus_(11, 3, 14).
mapplus_(10, minus_3, 7).
mapplus_(10, minus_2, 8).
mapplus_(10, minus_1, 9).
mapplus_(10, 0, 10).
mapplus_(10, 1, 11).
mapplus_(10, 2, 12).
mapplus_(10, 3, 13).
mapplus_(9, minus_3, 6).
mapplus_(9, minus_2, 7).
mapplus_(9, minus_1, 8).
mapplus_(9, 0, 9).
mapplus_(9, 1, 10).
mapplus_(9, 2, 11).
mapplus_(9, 3, 12).
mapplus_(8, minus_3, 5).
mapplus_(8, minus_2, 6).
mapplus_(8, minus_1, 7).
mapplus_(8, 0, 8).
mapplus_(8, 1, 9).
mapplus_(8, 2, 10).
mapplus_(8, 3, 11).
mapplus_(7, minus_3, 4).
mapplus_(7, minus_2, 5).
mapplus_(7, minus_1, 6).
mapplus_(7, 0, 7).
mapplus_(7, 1, 8).
mapplus_(7, 2, 9).
mapplus_(7, 3, 10).
mapplus_(6, minus_3, 3).
mapplus_(6, minus_2, 4).
mapplus_(6, minus_1, 5).
mapplus_(6, 0, 6).
mapplus_(6, 1, 7).
mapplus_(6, 2, 8).
mapplus_(6, 3, 9).
mapplus_(5, minus_3, 2).
mapplus_(5, minus_2, 3).
mapplus_(5, minus_1, 4).
mapplus_(5, 0, 5).
mapplus_(5, 1, 6).
mapplus_(5, 2, 7).
mapplus_(5, 3, 8).
mapplus_(4, minus_3, 1).
mapplus_(4, minus_2, 2).
mapplus_(4, minus_1, 3).
mapplus_(4, 0, 4).
mapplus_(4, 1, 5).
mapplus_(4, 2, 6).
mapplus_(4, 3, 7).
mapplus_(3, minus_3, 20).
mapplus_(3, minus_2, 1).
mapplus_(3, minus_1, 2).
mapplus_(3, 0, 3).
mapplus_(3, 1, 4).
mapplus_(3, 2, 5).
mapplus_(3, 3, 6).
mapplus_(2, minus_3, 19).
mapplus_(2, minus_2, 20).
mapplus_(2, minus_1, 1).
mapplus_(2, 0, 2).
mapplus_(2, 1, 3).
mapplus_(2, 2, 4).
mapplus_(2, 3, 5).
mapplus_(1, minus_3, 18).
mapplus_(1, minus_2, 19).
mapplus_(1, minus_1, 20).
mapplus_(1, 0, 1).
mapplus_(1, 1, 2).
mapplus_(1, 2, 3).
mapplus_(1, 3, 4).
speedplus_(minus_3, minus_2).
speedplus_(minus_2, minus_1).
speedplus_(minus_1, 0).
speedplus_(0, 1).
speedplus_(1, 2).
speedplus_(2, 3).
speedplus_(3, 3).
speedminus_(3, 2).
speedminus_(2, 1).
speedminus_(1, 0).
speedminus_(0, minus_1).
speedminus_(minus_1, minus_2).
speedminus_(minus_2, minus_3).
speedminus_(minus_3, minus_3).
succ(1, 2).
succ(2, 3).
succ(3, 4).
succ(4, 5).
succ(5, 6).
succ(6, 7).
succ(7, 8).
succ(8, 9).
succ(9, 10).
succ(10, 11).
succ(11, 12).
succ(12, 13).
succ(13, 14).
succ(14, 15).
succ(15, 16).
succ(16, 17).
succ(17, 18).
succ(18, 19).
succ(19, 20).
succ(20, 21).
succ(21, 22).
succ(22, 23).
succ(23, 24).
succ(24, 25).
succ(25, 26).
succ(26, 27).
succ(27, 28).
succ(28, 29).
succ(29, 30).
succ(30, 31).
succ(31, 32).
succ(32, 33).
succ(33, 34).
succ(34, 35).
succ(35, 36).
succ(36, 37).
succ(37, 38).
succ(38, 39).
succ(39, 40).
succ(40, 41).
succ(41, 42).
succ(42, 43).
succ(43, 44).
succ(44, 45).
succ(45, 46).
succ(46, 47).
succ(47, 48).
succ(48, 49).
succ(49, 50).
heading1Domain(north).
heading1Domain(west).
heading1Domain(south).
heading1Domain(east).
northminus_speed1Domain(3).
northminus_speed1Domain(minus_2).
northminus_speed1Domain(minus_1).
northminus_speed1Domain(0).
northminus_speed1Domain(1).
northminus_speed1Domain(2).
northminus_speed1Domain(minus_3).
eastminus_speed1Domain(2).
eastminus_speed1Domain(minus_2).
eastminus_speed1Domain(minus_1).
eastminus_speed1Domain(0).
eastminus_speed1Domain(1).
eastminus_speed1Domain(3).
eastminus_speed1Domain(minus_3).
x1Domain(10).
x1Domain(17).
x1Domain(18).
x1Domain(19).
x1Domain(20).
x1Domain(1).
x1Domain(2).
x1Domain(3).
x1Domain(16).
x1Domain(15).
x1Domain(14).
x1Domain(13).
x1Domain(12).
x1Domain(11).
x1Domain(9).
x1Domain(8).
x1Domain(7).
x1Domain(6).
x1Domain(5).
x1Domain(4).
y1Domain(10).
y1Domain(17).
y1Domain(18).
y1Domain(19).
y1Domain(20).
y1Domain(1).
y1Domain(2).
y1Domain(3).
y1Domain(16).
y1Domain(15).
y1Domain(14).
y1Domain(13).
y1Domain(12).
y1Domain(11).
y1Domain(9).
y1Domain(8).
y1Domain(7).
y1Domain(6).
y1Domain(5).
y1Domain(4).
step1Domain(1).
step1Domain(2).
step1Domain(3).
step1Domain(4).
step1Domain(5).
step1Domain(6).
step1Domain(7).
step1Domain(8).
step1Domain(9).
step1Domain(10).
step1Domain(11).
step1Domain(12).
step1Domain(13).
step1Domain(14).
step1Domain(15).
step1Domain(16).
step1Domain(17).
step1Domain(18).
step1Domain(19).
step1Domain(20).
step1Domain(21).
step1Domain(22).
step1Domain(23).
step1Domain(24).
step1Domain(25).
step1Domain(26).
step1Domain(27).
step1Domain(28).
step1Domain(29).
step1Domain(30).
step1Domain(31).
step1Domain(32).
step1Domain(33).
step1Domain(34).
step1Domain(35).
step1Domain(36).
step1Domain(37).
step1Domain(38).
step1Domain(39).
step1Domain(40).
step1Domain(41).
step1Domain(42).
step1Domain(43).
step1Domain(44).
step1Domain(45).
step1Domain(46).
step1Domain(47).
step1Domain(48).
step1Domain(49).
step1Domain(50).
moveDomain(turn(clock)).
moveDomain(turn(countReplicer)).
moveDomain(thrust).

timeDomain(1..50).
1{does(R,M,T):moveDomain(M)}1:- role(R), timeDomain(T), not terminal(T).
terminal(T+1):- terminal(T), timeDomain(T).
:-does(R,M,T),not legal(R,M,T),role(R).
:-0{terminal(T):timeDomain(T)}0.
:-terminal(T), not terminal(T-1), role(R), not goal(R,100,T).
:-terminal(1), role(R), not goal(R,100,1).
:-not terminal(50).
#hide.
#show does/3.
