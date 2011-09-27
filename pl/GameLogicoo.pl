% Centurio a General Game Player
%
% Copyright (C) 2009 Felix Maximilian Möller, Marius Schneider and Martin Wegner
%
% This file is part of Centurio.
%
% Centurio is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
%
% Centurio is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License along with Centurio. If not, see <http://www.gnu.org/licenses/>.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%                                                                           %%
%% AI for Centurio a General Game Player                                     %%
%%                                                                           %%
%% Authors: Felix Maximilian Möller, Marius Schneider and Martin Wegner      %%
%%                                                                           %%
%% Date: 07.06.2009                                                          %%
%%                                                                           %%
%% Version: 2.1                                                              %%
%%                                                                           %%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% TODO: Weil nextState von Java aufgerufen wird mit Liste von Moves ohne ggp_move() Struktur voll blöd, deswegen expandedState Prädikat, eigentlich hat randomMoves diese Struktur ausgegeben
%       da aktuelle Strategie bei parallelen Spielen unhaltbar ist, könnte combine Prädikat raus und dann kann man wieder überlegen randomMove diese Struktur aufbauen zu lassen
% TODO: initState und roles prädikate oft aufgerufen, obwohl es Java bekannt sein sollte, z.B. bei isParallelGame, zwar billige Operationen, aber evtl. als Parameter übergeben, was wieder größe Anfragen bedeutet
% TODO: Auf terminal state prüfen bei randomMoves und legalMoves...?
% TODO: randomGame Punkte aller Spieler zurück geben, terminal state dann unwichtig
% TODO: Mergen von Aufrufen, z.B. alle Zustände aller Childs mit einem Mal
% TODO: Prädikatensprünge minimieren in dem man den Code der einzelnen Prädikate in ein Prädikat steckt?

:- set_flag(debug_compile, off).
:- set_flag(variable_names, off).

% Event 68: calling undefined procedure
% We want to fail if an undefined procedure is called
:- set_event_handler(68, fail/0).

% Needed for the random_element(+Values, -X) predicate
:- lib(tentative).

%writing
:-mode output_data(++,+).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%                                                                           %%
%% Help predicates                                                           %%
%%                                                                           %%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

sortedFindall(Element, Expression, ListOfElements) :-
	findall(Element, Expression, ListOfElementsTemp),
	sort(ListOfElementsTemp, ListOfElements).

% Only for use in ggp_does(Role, Move, CurrentState)
fastMember(Head, [Head | _Tail]).
fastMember(Head1, [_Head2, ggp_move(Role, Move) | Tail]) :-
	fastMember(Head1, [ggp_move(Role, Move) | Tail]).

% Converts a List [a, b, c, ...] into a List [[a], [b], [c], ...]
antiflatten([], []) :- !.
antiflatten([Head | Tail], [[Head] | Result]) :-
	antiflatten(Tail, Result).

% Calculates all combinations for a List [[[a, ...], [b, ...], ...], [[c, ...], [d, ...], ...], ...]
% Example1:
% Query: [[[a], [b]], [[c], [d]], [[e], [f]]]
% Result: [[a, c, e], [a, c, f], [a, d, e], [a, d, f], [b, c, e], [b, c, f], [b, d, e], [b, d, f]]
% Example2:
% Query: [[[a, c], [a, d], [b, c], [b, d]], [[e], [f]]]
% Result: [[a, c, e], [a, c, f], [a, d, e], [a, d, f], [b, c, e], [b, c, f], [b, d, e], [b, d, f]]
combine([ListOfLists | []], ListOfLists) :- !.
combine([ListOfLists1, ListOfLists2 | []], Result) :- !,
	outerLoop(ListOfLists1, ListOfLists2, Result).
combine([ListOfLists1, ListOfLists2 | ListOfListsOfLists], Result) :-
	outerLoop(ListOfLists1, ListOfLists2, OuterLoopResult),
	combine([OuterLoopResult | ListOfListsOfLists], Result).

% Result from outerLoop is a ListOfLists
outerLoop([], _ListOfLists, []) :- !.
outerLoop([List | ListOfLists1], ListOfLists2, Result) :-
	innerLoop(List, ListOfLists2, InnerLoopResult),
	outerLoop(ListOfLists1, ListOfLists2, OuterLoopResult),
	append(InnerLoopResult, OuterLoopResult, Result).

% Result from innerLoop is a ListOfLists
innerLoop(_List, [], []) :- !.
innerLoop(List1, [List2 | ListOfLists], [List3 | InnerLoopResult]) :-
	append(List1, List2, List3),
	innerLoop(List1, ListOfLists, InnerLoopResult).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%                                                                           %%
%% Main predicates                                                           %%
%%                                                                           %%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

ggp_true(Fact, CurrentState) :-
	member(Fact, CurrentState).

ggp_does(Role, Move, CurrentState) :-
	fastMember(ggp_move(Role, Move), CurrentState).

% sortedFindall not possible because the order of the roles is important for interpreting the PLAY and STOP messages
roles(Roles) :-
	findall(Role, ggp_role(Role), Roles).

initState(InitState) :-
	sortedFindall(Fact, ggp_init(Fact), InitState).

terminalState(CurrentState) :-
	ggp_terminal(CurrentState), !.

nextState(CurrentState, Moves, NextState) :-
	expandedState(CurrentState, Moves, ExpandedState),
	sortedFindall(Fact, ggp_next(Fact, ExpandedState), NextState).




expandedState(CurrentState, Moves, ExpandedState) :-
	roles(Roles),
	(
		foreach(Role, Roles),
		foreach(Move, Moves),
		foreach(ggp_move(Role, Move), ListOfRolesWithMoves)
		do
		(
			true
		)
	),
	append(ListOfRolesWithMoves, CurrentState, ExpandedState).

legalMoves(CurrentState, Role, LegalMoves) :-
    sortedFindall(Move, ggp_legal(Role, Move, CurrentState), LegalMoves).

randomMoves(CurrentState, RandomMoves) :-
	roles(Roles),
	(
		foreach(Role, Roles),
		foreach(RandomMove, RandomMoves),
		param(CurrentState)
		do
		(
			legalMoves(CurrentState, Role, LegalMoves),
			random_element(LegalMoves, RandomMove)
		)
	).

randomGame(CurrentState, TerminalState, MovesTemp, Moves) :-
open('state','append',S), randomGameWR(CurrentState, TerminalState, MovesTemp, Moves, S), close(S).

randomGameWR(CurrentState, TerminalState, MovesTemp, Moves, S) :-
	(
		terminalState(CurrentState) ->
		(
			TerminalState = CurrentState,
			Moves = MovesTemp
		);
		(
			randomMoves(CurrentState, RandomMoves),
			nextState(CurrentState, RandomMoves, NextState),
			append(MovesTemp, [RandomMoves], MovesTemp2),
				%random(N1),
				%output_data([begin(model(N1))], S), 
				writeq(S,sample), %writeln(S,'.'),				
				output_data(RandomMoves,S), output_data(CurrentState,S),
				writeq(S,sample), %writeln(S,'.'),
			randomGameWR(NextState, TerminalState, MovesTemp2, Moves,S)
		)
	).
% ggp_goal(oplayer, 100, S)

output_data(L,S):-
        (foreach(X,L),
        param(S) do
           writeq(S,X),writeln(S,'.')
        ).


scoresOfAllPlayers(CurrentState, ScoreSet) :-
	roles(Roles),
	(
		foreach(Role, Roles),
		foreach(score(Role, Score), ScoreSet),
		param(CurrentState)
		do
		(
			(
				(ggp_goal(Role, ScoreTemp, CurrentState), !) ->
				(
					Score = ScoreTemp
				);
				(
					Score = 0
				)
			)
		)
	).

currentPlayer(CurrentState, CurrentPlayer) :-
	roles(Roles),
	(
		foreach(Role, Roles),
		foreach((Role, NumberOfLegalMoves), ListOfRolesWithNumberOfLegalMoves),
		param(CurrentState)
		do
		(
			legalMoves(CurrentState, Role, LegalMoves),
			length(LegalMoves, NumberOfLegalMoves)
		)
	),
	sort(2, >, ListOfRolesWithNumberOfLegalMoves, [(CurrentPlayer, _HighestNumberOfLegalMoves) | _TailFromListOfRolesWithNumberOfLegalMoves]).

legalMovesOfAllPlayersCombination(CurrentState, LegalMovesOfAllPlayersCombination) :-
	roles(Roles),
	(
		foreach(Role, Roles),
		foreach(LegalMoves, ListOfLegalMoves),
		param(CurrentState)
		do

		(
			legalMoves(CurrentState, Role, LegalMovesTemp),
			antiflatten(LegalMovesTemp, LegalMoves)
		)
	),
	combine(ListOfLegalMoves, LegalMovesOfAllPlayersCombination).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%                                                                           %%
%% Game classification predicates                                            %%
%%                                                                           %%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

isParallelGame :-
	initState(InitState),
	roles(Roles),
	findStateWhichProvesParallelity(InitState, Roles, 5, 5).

findStateWhichProvesParallelity(_CurrentState, _Roles, _InitTestDepth, 0) :- !, fail.
findStateWhichProvesParallelity(CurrentState, Roles, InitTestDepth, CurrentTestDepth) :-
	(
		terminalState(CurrentState) ->
		(
			initState(InitState),
			TestDepthTemp is InitTestDepth - 1,
			findStateWhichProvesParallelity(InitState, Roles, TestDepthTemp, TestDepthTemp)
		);
		(
			(
				isParallelState(CurrentState, Roles, 0) ->
				(
					true
				);
				(
					randomMoves(CurrentState, RandomMoves),
					nextState(CurrentState, RandomMoves, NextState),
					TestDepthTemp is CurrentTestDepth - 1,
					findStateWhichProvesParallelity(NextState, Roles, InitTestDepth, TestDepthTemp)
				)
			)
		)
	).

isParallelState(_CurrentState, _Roles, 2) :- !.
isParallelState(_CurrentState, [], _NumberOfRolesWithMoreThanOneMove) :- !, fail.
isParallelState(CurrentState, [Role | Roles], NumberOfRolesWithMoreThanOneMove) :-
	legalMoves(CurrentState, Role, LegalMoves),
	length(LegalMoves, NumberOfLegalMoves),
	(
		NumberOfLegalMoves > 1 ->
		(
			NumberOfRolesWithMoreThanOneMoveTemp is NumberOfRolesWithMoreThanOneMove + 1,
			isParallelState(CurrentState, Roles, NumberOfRolesWithMoreThanOneMoveTemp)
		);
		(
			isParallelState(CurrentState, Roles, NumberOfRolesWithMoreThanOneMove)
		)
	).

numberOfLegalMovesOfAllPlayersCombination(CurrentState, NumberOfLegalMovesOfAllPlayersCombination) :-
	roles(Roles),
	(
		foreach(Role, Roles),
		fromto(1, In, Out, NumberOfLegalMovesOfAllPlayersCombination),
		param(CurrentState)
		do
		(
			legalMoves(CurrentState, Role, LegalMoves),
			length(LegalMoves, NumberOfLegalMoves),
			Out is In * NumberOfLegalMoves			
		)
	).

isInPanicMode(CurrentState) :-
	not(terminalState(CurrentState)),
	numberOfLegalMovesOfAllPlayersCombination(CurrentState, NumberOfLegalMovesOfAllPlayersCombination),
	(
		NumberOfLegalMovesOfAllPlayersCombination > 1000 ->
		(
			true
		);
		(
			fail
		)
	).
