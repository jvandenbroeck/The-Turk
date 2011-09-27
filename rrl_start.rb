require 'rb/parser'
require 'ftools'
require 'fileutils'
require 'rb/sexpressions'
require 'net/scp'

# system 'cp qtree.false.pl qtree.pl'

begin
  FileUtils.rm_rf 'ace'
rescue
end

begin 
  File.delete('state')
rescue
end


$dir = Dir.getwd + '/ace/' 

begin
	Dir.mkdir $dir
rescue
end

$matchID = ARGV[0]

playersin = ARGV[1]
$players = playersin.split(" ")


# File.open("starting_" + $matchID, 'w') {|f| f.puts $players.to_s }

def processKIF file
	
	file = Dir.getwd + file 

	f = File.open(file, "r")
	
	fa = f.to_a
	
  	fa = findNReplaceArray(") (", ")\n(", fa)
	
	fa = fa.map { |a|	a.downcase }
		

	File.open($dir + 'game.gdl', 'w') { |w|

	fa.each { |s|
	w.puts s 
	}

	}


end


#doc = ARGV[0] + " " + ARGV[1]
#File.open("testfeedback", 'w') {|f| f.write(doc) }

# IN: KIF FILE LOCATION 
# IN: PLAYERS (list)
#
# A) Process GDL 
processKIF '/kif/' + $matchID + '.kif'

# B) Create language bias
createBias

Net::SCP.upload!(ARGV[2], "s0168656",
    "/home/s0168656/the-turk/KB/ace/game.bg", "/tmp/ace/game.bg",
    :password => "password")

Net::SCP.upload!(ARGV[2], "s0168656",
    "/home/s0168656/the-turk/KB/ace/game.s", "/tmp/ace/game.s",
    :password => "password")

