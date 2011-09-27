require 'pp'
require 'rb/process.rb'
require 'ftools'
require 'rb/qtree'
require 'rubygems'
require 'net/scp'
require 'net/ssh'

# 	Generates RRL logic
#
# 	REQUIRES: ACE/TILDE
#
# TODO nu maar 1 reward op einde mogelijk >> change


#learning rate
# 
# > MyRole
# > Other Roles
#
# Which V function to learn
#
# 	> One for each player
#
# Which types of Games do we have
# 	> Multiplayer each for their own >>> Diff V function
# 	> Singleplayer >> ASP >> OR one V function
# 	> 2P against each other =? zero sum TODO
# 		>> tic tac toe
# 			- veralgemening niet mogelijk over 2 spelers x-o staat vast (Q!) TODO
# 			- 1 V-functie voor speler leren->> rewards van tegenspeler als negatieve rewards voor Q gebruiken TODO 
# 	> 2P totally different
# 		>> pacman
# 			- 2 verschillende V-functions leren & gebruiken TODO
# 	==> How to find out totally different / against each other
# 	==> When is it possible to user opponent Q function as negative reward// always? 
# 	==> Is it the same?
#
# 	> Teams game
# 		>> calculate Q functions for all players // find common rewards 
#
#
# 	Discussion Points 
# 	--------------------
#
#	WHICH Q FUNCTION TO CALCULATE
#	===============================
#
# 	>> Use win of opponent as negative reward
# 		+ better Q function prediction
# 		- possible wrong if on the same team -- if win US is related to win THEM 
# 	==> Don't use win of oponent as negative reward, only positive reward
# 	
# 	>> Use of ONE q function for 2 or more players
# 		+ less calculation time
# 		- possible wrong
# 		- how to <veralgemenen> over q functions? 
# 	==> one Q-function for every player
#	
#	HOW QFUNCTION MAY INFLUENCE GAME PLAY
#	=======================================
#
#	HOW TO CALCULATE QFUNCTION?
#	============================
#	>> Everytime new qfunction
#	>> Use old ...  (more with formules)
#	==> Now (temp) One Qfunction on start
#
#	CHOSEN APPROACH
#	=================
#	Learn one Q function per player
#		> From every afterstate 
#			> With reward propagation
#	

pp "ruby /tmp/ace/rrl_process.rb " + ARGV[0] + ' "' + ARGV[1] + '"'


# state is outputted by write from logic
# send it to ACE server
Net::SCP.upload!(ARGV[2], "s0168656",
    "/home/s0168656/the-turk/KB/state", "/tmp/ace/game.tp",
    :password => "password")


# process on ACE server
Net::SSH.start(ARGV[2], 's0168656', :password => "password") do |ssh|

  # open a new channel and configure a minimal set of callbacks, then run
  # the event loop until the channel finishes (closes)
  channel = ssh.open_channel do |ch|
    ch.exec "ruby /tmp/ace/rrl_process.rb " + ARGV[0] + " \"" + ARGV[1] + "\" " + ARGV[3] + " " + ARGV[4] + " " + ARGV[5] do |ch, success|
      raise "could not execute command" unless success

      # "on_data" is called when the process writes something to stdout
      ch.on_data do |c, data|
        # $STDOUT.print data
	puts data      
	end
      # "on_extended_data" is called when the process writes something to stderr
      ch.on_extended_data do |c, type, data|
      puts data
	#  $STDERR.print data
      end

      ch.on_close { puts "done!" }
    end
  end

  channel.wait

end

system "rm state" # remove state file

