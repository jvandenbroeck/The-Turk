Requirements
=============

* Java

* Ruby 1.8.x
	* rparsec gem
	* net/scp gem 
	* net/ssh gem

* ACE

* EclipseCLP

Install
========
Random MCTS player
------------------------
	git clone https://jvandenbroeck@github.com/jvandenbroeck/The-Turk.git
	git checkout random
	./fixlib.sh
	./run.sh
	or
	./run.sh > run.log

RRL-Benchmark-Update  (RRL Server)
-----------------------------------
	cd /tmp/
	git clone https://jvandenbroeck@github.com/jvandenbroeck/The-Turk.git
	mv The-Turk Thesis-RRL-MCTS
	cd Thesis-RRL-MCTS	
	git checkout rrl-benchmark-update

Setup SSH/SCP connection and ACE server
-----------------------------------------
* Both servers need to communicate with SSH/SCP without logging in, you might need to generate an SSH keypair without password. 
* Change the SSH username & password in the `./rrl_process.rb` `./rrl_start.rb` and `/ace/rrl_process.rb` files.
* Copy the files from the ace_client to the correct directory on the ACE server. (replace $user and $aceserver)

	`scp -r /tmp/Thesis-RRL-MCTS/ace_client/ $user@$aceserver:/tmp/ace`

On the ACE Server, install ace and change the `$ace` variable in the `/tmp/ace/rrl_process.rb`-file to point to the ace binary. 

Configureer de benchmark
-------------------
Configureer ./benchmark.xml of ./config.xml correct. Omdat de benchmark versie geen random episodes in het begin speelt, maar meteen episodes met RRL moet er een al bestaande regressieboom beschikbaar zijn voor de speler in het bestand `qtree.false.pl`. Voor breakthrough zit er een die al is gegenereerd in het repository

	cp qtree.breakthrough.pl qtree.false.pl

Start the RRL Server
---------------------
	./fixlib.sh
	./run.sh
	or
	./run.sh > run.log

Production
-----------
* verwijder lock(s) in UCT.java
