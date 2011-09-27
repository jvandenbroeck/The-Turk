class ProcessQ

def initialize(player)
	
	# createEpisodes("xplayer", 1)


	#learning rate
	@player = player 
	@rw_def = 0 #default reward
	@rw = @rw_def
	@rw_limit = 0.05 #rewards under this threshold are put on zero

end


#
# creates a two dimentional array with episodes
#

def createEpisode(fa, horizon, gammaq)

  	a = []
  	c = 0
  	i = 0

	fa.each_index {|linei| 
	
		if fa[linei].eql?("spinazie123\n")
			i = 0 
			c += 1
			a[c]=[]
			#fa.shift
		else
		
			a[c][i] = fa[linei]
			i += 1
		
		end
	}	

	a.compact!
	a = a.delete_if {|x| x.eql?([])}
        
        episodes = Episodes.new(gammaq, horizon)

        a.each {|s|
          episodes.addState(State.new(s, @player))
        }

        episodes
end

#
#	Calc Rewards
#
#	- We don't delete episodes in this method
#	- We calculate a reward value for EVERY episode
#

def calcRewards(a,minHorizon)
	

  minHorizon = minHorizon.to_i

  episode_counter = 0
  found = false 	

  a = a.map { |s| 

		# In case no goal found.. 
		# make place to add Q-value
		s << "" 
	


		s = s.each_with_index.map { |l, i|

		terminal = l.split("TERMINAL: ")[1]
		goal = l.split("score(#{@player}, ")[1]

		if i == 1
			found = false
		end

		if !goal.nil?  #if goal is found

			# use default reward ? better play?! 
			# instead of using 0 ? 
			# + scaling  // no scaling
			sfactor = 1
                        episode_counter = 0
            
			if !terminal.nil?
				@rw = @def_rew
			end


			found_rw = goal.split(")")[0]
			@rw = @gamma * @rw.to_f + found_rw.to_f*sfactor   # if a new goal is found on the way of 
			# an episode, add it to the current reward
			
			found = true

			"q(" + @rw.to_s + ").\n"
		elsif (i+1 == s.size) & !found 
                       
                  if episode_counter < minHorizon 

			if @rw.to_f < @rw_limit
				"q(0). \n"
			else
				@rw = @gamma * @rw.to_f
				rwts = @rw.to_s
				"q(" + rwts + ").\n"
			end

                    else
                      ""
                    end

		else
		
                  if episode_counter < minHorizon 
                  l
                  else
                    ""
                  end
	end

	}

        episode_counter += 1

	}
	
end


#
#	We moeten onze eigen episodes verwijderen,
#	we leren van episodes van andere speler (afterstate)
#	delete players
#
def deletePlayerEpisodes(a)

	a = a.delete_if {|x| 

	marked = false

	x.each { |l| 

	control = l.split("control(")[1]			# we gaan ervan uit x player!!

	if !control.nil?
	 if control.split(")")[0].eql?(@player)
		marked = true
	 end
	end

	}
	marked
	}

end
#
#	Find n replace
#
def findNReplaceOne(find, replace, a)

	a = a.map { |s|

		s = s.map { |e| 

			found = e.split(find)
			isFound = found[1]

			if !isFound.nil?
				found[0] + replace + found[1]
			else
				e
			end

		}
	}

end


#
#
#

#
#	Find n replace
#
def findNReplaceArray(find, replace, s)
	
s = s.map { |e| 

e.gsub(find, replace)

}

end




def addModels(a)

modelnr = 0
a = a.reverse

a = a.map{ |s| 
	s = s.reverse
	s << "begin(model(" + modelnr.to_s + ")).\n"
	s = s.reverse
	s << "end(model(" + modelnr.to_s  + ")).\n"
	modelnr += 1
	s
}

end



end #class
