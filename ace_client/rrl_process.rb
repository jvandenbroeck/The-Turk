$this_dir = File.dirname(__FILE__)

require 'pp'
require $this_dir + '/rb/process.rb'
require 'ftools'
require $this_dir + '/rb/qtree'
require 'rubygems'
require 'net/scp'
require 'net/ssh'
require $this_dir + '/rb/state'

# 	Generates RRL logic
#
# 	REQUIRES: ACE/TILDE
#
# TODO nu maar 1 reward op einde mogelijk >> change

def ace_log(message)
  # File.open($this_dir + '/ace_client.log', 'a') {|f| f.write(message) }
end

def time_log(message) 
    File.open($this_dir + '/time.log', 'a') {|f| f.write(Time.now.to_s + ":   " + message + "\n") }
end

$matchID = ARGV[0]

time_log "-------------------------------"
time_log "start processing #{$matchID}"
ace_log "-------------------------------"
ace_log "start processing #{$matchID}"


# $ace = '/home/joris/ace/ACE'
$ace = '/home/s0168656/ACE-ilProlog-1.2.20/linux/bin/ace'

system 'source $HOME/.bashrc'

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



# Create Episodes 
# 	file = file to save episode
# 	player = player for which to generate episode
# 	pos = position of player in the actions-array  <! 
#
#	payer = white, pos = 1
def createEpisodes(player)
	
		
	dirp = $dir + player + '/' 
	begin
		Dir.mkdir(dirp)
	rescue 
	end


	f = File.open($dir + 'game.tp', "r")

	a = []
	fa = f.to_a

	s = ProcessQ.new(player)

	#split
	episodes = s.createEpisode(fa, $minHorizon, $gammaQ)
	
#	pp a

## calc rewards
        episodes.calculateQvalues(player)	
	# pp a
	
	## delete episodes from own player, we want to learn afterstates. 
#	a = s.deletePlayerEpisodes(a)
        episodes.episode.delete_if{|s|
          s.player == player
        }
	## delete empty

	# find n replace one
	#a = s.findNReplaceOne("(", "(S, ", a)
	#a = s.findNReplaceOne("q(S, ", "q(", a)

        ## add first and last model info
        File.open(dirp + 'game.kb', 'w') { |w|
          counter = 0
          terminal_counter = 0

          episodes.episode.each { |s|
          
            if terminal_counter < $minHorizon.to_f

              w.puts "begin(model(" + counter.to_s + ")).\n"
              s.state.each{ |line|
                w.puts line
              }
              w.puts "q(" + s.qvalue.to_s + ").\n"
              w.puts "end(model(" + counter.to_s + ")).\n"
              counter += 1

            end

            terminal_counter += 1
            if s.terminal
              terminal_counter = 0
            end

          }

        }

        f.close


        
end

#
#	Find n replace
#
def findNReplaceArray(find, replace, s)
	
s = s.map { |e| 

e.gsub(find, replace)

}

end




def processQtree 
	begin	
		File.delete($dir + 'qtree.pl')
	rescue

	end	

        maxqvalue = 0.0

	$players.each{|p|

		f = File.open($dir + p + '/qtree.pl', 'r')
		fa = f.to_a
		fa = findNReplaceArray("])","],S,"+p+")", fa)
		fa = findNReplaceArray(":- ",":- ggp_true(", fa)
		fa = findNReplaceArray("),","),S),ggp_true(", fa)
		fa = findNReplaceArray("ggp_true( !","!", fa)



		File.open($dir + 'qtree.pl', 'a') { |w|
			fa.each { |s|

                         
                                qvalue = s.split("class([")[1]
                                

                                if !qvalue.nil?
                                
                                qvalue = qvalue.split("]")[0].to_f

                                  if qvalue > maxqvalue
                                    maxqvalue = qvalue
                                  end

                                end

                                w.puts s 
                        }
                }
        }	

        File.open($dir + 'qtree.pl', 'a') { |w|
          w.puts 'qtree :- true.' 
          w.puts 'maxqvalue(' + maxqvalue.to_s + ').'

        }
end

$dir = $this_dir + '/' 
$minHorizon = ARGV[3]
pp "minhorizon: " + $minHorizon
$gammaQ = ARGV[4]

playersin = ARGV[1]
$players = playersin.split(" ")


# File.open("processing_" + $matchID, 'w') {|f| f.puts $players.to_s }


# wait a few seconds for episodes to be generated
# sleep(20)

# C) Create episodes for each player
#
# TODO make generic for input players 
# TODO IMPORTANT do this for every x seconds
$players.each{ |p|
	createEpisodes(p)
}

# Set bias files good
#
	# files in subdir 
	# & remove in parent dir
	$players.each{ |p|

		File.makedirs $dir + p
		File.copy($dir + 'game.s', $dir + p + '/game.s')
		File.copy($dir + 'game.bg', $dir + p + '/game.bg')
	}
	#File.delete($dir +'game.s')
	#File.delete($dir +'game.bg')
	

time_log "starting ace for each player"

# D) Run ACE
# Runtime.Exec(ace)
$players.each{ |p|
  command = "cd #{$dir + p}/; #{$ace}"
  pp command
  ace_log `#{command}`
}

time_log "end running ace"

# F) Postprocess Q-Tree
# TODO needs location of created ACE FILES
# processTree
$players.each{ |p|
	f = File.open($dir + p + '/tilde/game.out', "r")
	fa = f.to_a
	fa = getQtree fa


	File.open($dir + p + '/qtree.pl', 'w') { |w|

	fa.each { |s|
	w.puts s 
	}

	}
}

# G)

processQtree


Net::SCP.upload!(ARGV[2], "s0168656",
    "/tmp/ace/qtree.pl", "/home/s0168656/the-turk/KB/qtree.pl",
    :password => "password")


puts "DONE"

t = Time.now
t = t.hour.to_s + t.min.to_s + t.sec.to_s

begin
system "mkdir #{$this_dir}/logs"
rescue
end

system "mkdir #{$this_dir}/logs/#{$matchID}"
#system "mv #{$this_dir}/game.s #{$this_dir}/logs/#{$matchID}"
# system "mv #{$this_dir}/game.tp #{$this_dir}/logs/#{$matchID}"
#system "mv #{$this_dir}/game.bg #{$this_dir}/logs/#{$matchID}"
# system "mv #{$this_dir}/ace_client.log #{$this_dir}/logs/#{$matchID}"
# system "mv #{$this_dir}/time.log #{$this_dir}/logs/#{$matchID}"
system "cp #{$this_dir}/qtree.pl #{$this_dir}/logs/#{$matchID}/#{t}.pl"
$players.each{ |p|
system "rm -r -f #{$this_dir}/#{p}"
}
# system 'mkdir old; mv game.s old/game.s; mv game.kb old/game.kb'
