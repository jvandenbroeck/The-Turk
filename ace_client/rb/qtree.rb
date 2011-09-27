def getQtree a

alreadyfound = false

	a = a.map { |s| 


		s = s.each_with_index.map { |l, i|

		terminal = l.split("Equivalent prolog")[1]

		if !terminal.nil?  #if goal is found

			alreadyfound = true

			""
		elsif alreadyfound 
			l
		else
		""
	end

	}
	}

	a = a.map{ |q| 
		q = q.delete_if { |e| e.eql?("")}
	}


	
end
