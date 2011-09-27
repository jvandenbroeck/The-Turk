% regression
tilde_mode(regression).
euclid(q(X),X).
output_options([prolog]).


% execute tilde and quit afterwards
execute(t).
execute(q).



%%%%%% ACE %%%%%%%%%%

load(models).

minimal_cases(20).


% accuracy(0.7).
% pruning(vsb). 

% use_packs(2).
% prune_rules(false).

typed_language(yes).
%type(member(_, _)).
%type(get_model(modelinfo, action, state)).

%root((model_info(MI), get_model(MI, Action, State), playerrole(State, Player))).

%type(montecarloHgood(state)).
%rmode(montecarloHgood(+State)).
%type(guaranteedWin2(state)).
%rmode(guaranteedWin2(+State)).

%%%%%%%%% GENERATED BIAS %%%%%%%%%%
