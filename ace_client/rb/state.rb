class State
  
  attr_accessor :state
  attr_accessor :qvalue
  attr_accessor :terminal
  attr_accessor :goal
  attr_accessor :player

  def initialize(state, playerx)
      @state = state
      @qvalue = 0
      @terminal = false
      @goal = 0

      @state = @state.delete_if {|line|
       delete = false

        foundp = line.split("score(#{playerx}, ")[1]
        foundc = line.split("control(")[1]
        foundt = line.split("INAL:")[1]
        if !foundp.nil?
          @goal = foundp.split(")")[0]
          # pp "goal " + @goal
          delete = true
        elsif !foundc.nil?
          @player = foundc.split(")")[0]
          delete = true
        end

        if !foundt.nil?
            @terminal = true
            delete =  true
        end
  
        delete

      }

      
  end
  
 
end

class Episodes

  attr_accessor :episode
  
def initialize(gamma, horizon)
  @@gamma = gamma
  @@horizon = horizon
@episode = Array.new

end

def addState(state)
  @episode << state
end

def calculateQvalues(player)
  current_qvalue = 0
      @episode.reverse.each { |s|
       
        if s.terminal
          current_qvalue = 0
        end
      

      qvalue = @@gamma.to_f*current_qvalue + s.goal.to_f
      s.qvalue = qvalue
      current_qvalue = qvalue
  }
end

end
