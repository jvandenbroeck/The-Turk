#!/usr/bin/ruby
#
## Zet een GDL beschrijving om in iets waar ACE mee kan werken
#
## auteur: Ewoud Nuyts

# Lees GDL bestand in, zet om naar array
def gdl_to_array(file_name)
    sgdl = Array.new
    File.open(file_name).each do |line|
        # Verwijder regeleinden en commentaar
        sgdl.push(line.chomp("\n").gsub(/;.*/, ''))
    end
    # sexpressions BUG: (<= terminal<newline>something) wordt
    # "follows(terminalsomething,])." i.p.v. "follows(terminal, [something])."
    return symbol_to_string(sgdl.join.parse_sexp)
end

# parse_sexp verandert sommige namen in Symbols, we willen enkel Strings
# voegt ook gdl_... toe, en brengt de ariteit in de naam om conflicten te
# vermijden bij predicaten met dezelfde naam en verschillende ariteit
def symbol_to_string(statement)
    statement.each_with_index do |pred, i|
        # recursief
        if pred.class == Array
            symbol_to_string(pred)
        # alles behalve een spelspecifiek predicaat
        elsif pred.class == Fixnum || standard_gdl?(pred.to_s) || i != 0
            statement[i] = pred.to_s
        else
            statement[i] = "#{pred}"
            # statement[i] = "gdl_#{pred}_#{statement.size-1}"
        end
    end
end

# standaard GDL predicaatnaam?
def standard_gdl?(predicate_name)
    gdl_preds = ["role", "true", "init", "next", "legal", "does",
                 "goal", "terminal", "or", "not", "<=", "distinct"]
    return gdl_preds.include?(predicate_name)
end

def dont_process?(predicate_name)
    gdl_preds = ["role", "next", "legal", "does", "control"]
    return gdl_preds.include?(predicate_name)
end


# bepaalde posities van de standaard GDL predicaten hebben ook
# een typebepaling nodig
def type_exception?(location)
    return [["role", 1], ["legal", 1],
            ["does", 1], ["goal", 1], ["goal", 2]].include?(location)
end

# GDL variabele? (die beginnen steeds met een '?')
def gdl_variable?(name)
    return name =~ /^\?.*/
end

# verzamel de verschillende spelers
def collect_players(gdl)
    players = Array.new
    gdl.each do |statement|
        players.push(statement[1]) if statement[0] == "role"
    end
    return players
end

# repareer het onnodig samensmelten van andere types met het player type
def fix_player_types(types, players)
    types.each_pair do |type, values|
        if type.include?(["role", 1]) 
            if type.size > 4
                [["role", 1], ["legal", 1],
                 ["does", 1], ["goal", 1]].each do |player|
                    type.delete(player)
                end
            else
                types.delete(type)
            end
            types[ [["role", 1], ["legal", 1],
                    ["does", 1], ["goal", 1]] ] = players
        end
    end
    return types
end

# verzamel niet-standaard GDL predicaatposities, en de constanten die voorkomen
# op die posities
def collect_predicates(gdl)
    composites = Hash.new
    gdl.each { |statement| collect_composites(statement, composites) }
    predicates = Hash.new
    arity = Hash.new
    # de GDL beschrijving is een array van statements
    gdl.each do |statement|
        collect_preds(statement, predicates, arity, composites)
    end
    # verwijder dubbels
    predicates.each_value { |value| value.uniq! }
    composites.each_value { |value| value.uniq! }
    # volgende standaard GDL predicaatnamen (en hun ariteit) zijn ook
    # belangrijk voor de typebepaling later:
    arity.merge!({"role" => 1, "legal" => 2, "does" => 2, "goal" => 2})
    return predicates, arity, composites
end
# recursieve hulpfunctie
def collect_preds(statement, predicates, arity, composites)
    head = statement[0]
    # bewaar de ariteit
    if !standard_gdl?(head) && !arity.has_key?(head)
        arity[head] = statement.size-1
    end
    statement[1..(statement.size-1)].each_with_index do |pred, index|
        # recursief
        if pred.class == Array
            collect_preds(pred, predicates, arity, composites)
        else
            # bewaar de constanten die op die predicaatpositie voorkomen
            # dit zorgt er ook voor dat enkel predicaatposities worden
            # toegevoegd als er constanten/variabelen gevonden worden op die
            # positie
            current = [head, index+1]
            # eerst checken of het geen deel uitmaakt van een composite type
            if composites.has_key?([current])
                composites[[current]].push(pred)
            elsif !standard_gdl?(head) || type_exception?(current)
                predicates[current] = Array.new if !predicates.has_key?(current)
                predicates[current].push(pred) if !gdl_variable?(pred)
            end
        end
    end
end
# verzamel composite types, dit moet eerst gebeuren, want er kunnen ook
# gewoon waarden op die plaatsten staan, anders pikt collect_preds() die op
def collect_composites(statement, composites)
    head = statement[0]
    statement[1..(statement.size-1)].each_with_index do |pred, index|
        current = [head, index+1]
        if pred.class == Array
            if !standard_gdl?(head) ||
                    [["does", 2], ["legal", 2]].include?(current)
                if !composites.has_key?([current])
                    composites[[current]] = Array.new
                end
                composites[[current]].push(pred[0])
            end
            # recursief
            collect_composites(pred, composites)
        end
    end
end

# zit de positie al in één van de positielijsten?
def find_in_hash(list, position)
    list.keys.each do |key|
        return key if key.include?(position)
    end
    return false
end

# zoek naar het voorkomen van een bepaalde variabele in een statement
def cluster_vars(statement, variable, match)
    head = statement[0]
    statement[1..(statement.size-1)].each_with_index do |pred, index|
        # recursief
        if pred.class == Array
            cluster_vars(pred, variable, match)
        else
            current = [head, index+1]
            # vermijd overbodige type-unificatie door 'distinct'
            if pred == variable && head != "distinct"
                match.push(current)
            end
        end
    end
end

# voeg types samen wanner in een zelfde statement een variabele voorkomt
# op posities die tot een verschillend type behoren
def find_variables(gdl, predicates)
    predicates2 = Hash.new
    # hash van posities -> hash van arrays van posities
    predicates.each_pair { |key, value| predicates2[[key]] = value }
    # volgende predicaatposities hebben hetzelfde type (de spelers)
    merge_types(predicates2, [["role", 1], ["legal", 1],
                              ["does", 1], ["goal", 1]])
    gdl.each do |statement|
        # atomische uitdrukkingen bevatten geen variabelen
        find_vars(statement, predicates2) if statement[0] == "<="
    end
    return predicates2
end
# hulpfunctie
def find_vars(statement, list)
    # zoek variabelen die meer dan één keer voorkomen
    variables = statement.flatten.delete_if { |item| not gdl_variable?(item) }
    variables.delete_if do |item|
        variables.find_all { |item2| item == item2 }.size < 2
    end
    # zoek de posities van die variabelen
    if !variables.empty?
        variables.uniq!
        variables.each do |variable|
            # zoek posities
            match = Array.new
            cluster_vars(statement, variable, match)
            match.uniq!
            # posities gevonden, voeg de types samen
            merge_types(list, match) if match.size > 1
        end
    end
end

# voeg types samen bij het vinden van posities van overeenkomstige variabelen
def merge_types(list, match)
    # vergelijk elke paar van posities in de gevonden match
    match.combination(2) do |pair|
        type1 = find_in_hash(list, pair[0])
        type2 = find_in_hash(list, pair[1])
        # als de 2 delen van het paar gevonden worden in verschillende types,
        # voeg de types samen
        if type1 != type2
            list[type1 | type2] = list[type1] | list[type2]
            list.delete(type1)
            list.delete(type2)
        end
    end
end

# OVERBODIG:
# voeg identieke types samen
#def merge_identical_types(types)
#    types_reduced = types.dup
#    # herhaal zolang we kunnen vereenvoudigen
#    loop do
#        types = types_reduced.dup
#        # vergelijk elk mogelijk paar van types
#        types.keys.combination(2).each do |pair|
#            # verwijder 1 van de 2 als ze identiek zijn
#            if types[pair[0]].sort == types[pair[1]].sort
#                types_reduced[pair[0] | pair[1]] = types[pair[0]]
#                types_reduced.delete(pair[0])
#                types_reduced.delete(pair[1])
#                break
#            end
#        end
#        break unless types != types_reduced
#    end
#    return types_reduced
#end

# OVERBODIG:
# voeg types samen bij overeenkomstige variabelenwaarden
#def reduce_types(types)
#    types_reduced = types.dup
#    loop do
#        types = types_reduced.dup
#        types.values.combination(2).each do |pair|
#            if !((pair[0] & pair[1]).empty?)
#                types_reduced[types.index(pair[0]) | types.index(pair[1])] =
#                    pair[0] | pair[1]
#                types_reduced.delete(types.index(pair[0]))
#                types_reduced.delete(types.index(pair[1]))
#                break
#            end
#        end
#        break unless types != types_reduced
#    end
#    return types_reduced
#end

# converteer GDL beschrijving naar geldige Prolog code
def gdl_to_prolog(gdl)
    prolog = Array.new
    gdl.each { |statement| prolog.push("#{to_prolog(statement)}.") }
    return prolog
end
# converteer een GDL statement naar Prolog code
def to_prolog(statement)
    if statement.class == Array
        prolog = String.new
        head = statement[0]
        if head == "<="
            prolog += "#{to_prolog(statement[1])} :- "
            statement[2..(statement.size-1)].each do |pred|
                prolog += "#{to_prolog(pred)}, "
            end
            return prolog.chop.chop + ""
        elsif head == "or"
            prolog += "or(["
            statement[1..(statement.size-1)].each do |pred|
                prolog += "#{to_prolog(pred)}, "
            end
            return prolog.chop.chop + "])"
        elsif head == "true"
            return prolog + to_prolog(statement[1])
        else
            prolog = "#{statement[0]}("
            statement[1..(statement.size-1)].each do |term|
                prolog += "#{to_prolog(term)}, "
            end
            return prolog.chop.chop + ")"
        end
    else
        if gdl_variable?(statement)
            return statement[1..statement.size].capitalize
        else
            return statement
        end
    end
end

# bepaal alle predicaatnamen waar een typebepaling voor moet gebeuren
def to_type(predicates, composites)
    names = Array.new
    compos = composites.values.flatten.uniq
    predicates.each_key do |key|
        # composite types overslaan
        names.push(key[0]) unless compos.include?(key[0])
    end
    return names.uniq
end

# genereer een leesbare vorm van alle types (debug/commentaar doeleinden)
def print_types(types, composites)
    list = Array.new
    types.values.each_with_index do |type, i|
        list.push("% type#{i} = {#{type.join(', ')}}")
    end
    composites.values.each_with_index do |type, i|
        list.push("% type#{types.size+i} = {#{type.join(', ')}}")
    end
    return list
end

# genereer achtergrondinformatie
def background_info(predicates, arities)
    bg_info = Array.new
    predicates.each do |pred|
        alphabet = ('A'..'Z').to_a
        arity = arities[pred]
        vars = alphabet[0..arity-1].join(', ')
        info = "#{pred}(#{vars}) :- #{pred}(#{vars})."
        bg_info.push(info)
    end
    return bg_info
end

# genereer type() informatie
def generate_types(predicates, arities, types, composites)
    type_info = Array.new
    filtered_predicates = predicates.select { |p| !dont_process?(p) }
    filtered_predicates.each do |pred|
        arity = arities[pred]
        type_vars = Array.new
        1.upto(arity) do |position|
            type_col = find_in_hash(types, [pred, position])
            if type_col
                type_vars.push("type#{types.keys.index(type_col)}")
            else
                compo_col = find_in_hash(composites, [pred, position])
                type_vars.push("type#{composites.keys.index(compo_col) +
                                      types.size}")
            end
        end
        type = "type(#{pred}(#{type_vars.join(", ")}))."
        type_info.push(type)
    end
    return type_info
end

# genereer rmode() statements
def generate_rmodes(predicates, arities, types, composites)
    rmodes = Array.new
    sum = 0
    filtered_predicates = predicates.select { |p| !dont_process?(p) }
    filtered_predicates.each do |pred|
        # per predicaatnaam
        struct = generate_domains(pred, arities, types, composites, 
                                  ('A'..'Z').to_a)
        # bouw een rmode statement op uit de verzamelde gegevens
        struct.each_pair do |pred, domains|
            product = 1
            # voeg '+State' toe aan het eerste deel
            if pred =~ /^(\w*\()(.*)$/
                pred = "#{$1}#{$2}"
            end
            rmode = "rmode("
            # zijn er gebonden variabelen?
            if !domains.values.compact.empty?
                # het #(A, B, ...) deel
                rmode += "#("
                vars = Array.new
                domains.keys.each do |var|
                    if domains[var] != nil
                        if var =~ /^\+\-(.*)$/
                            vars.push($1)
                        else
                            vars.push(var)
                        end
                    end
                end
                rmode += "(#{vars.join(", ")}): ("
                # het (member(A, [...]), member(...)) deel
                domains.each_pair do |var, domain|
                    if domain != nil
                        rmode += "member(#{var}, [#{domain.join(", ")}]), "
                        product *= domain.size
                    end
                end
                rmode.chop!.chop!
                rmode += "), "
                # het predicaat zelf
                rmode += "#{pred}))."
            else
                # het predicaat zelf
                rmode += "#{pred})."
            end
            sum += product
            rmodes.push(rmode)
        end
    end
    puts "# rmodes: #{sum}"
    return rmodes
end
# meerdere rmode() statements per predicaat
def generate_domains(predicate, arities, types, composites, alphabet)
    arity = arities[predicate]
    # genereer voor elke predicaatpositie de verschillende mogelijkheden
    join = Array.new
    1.upto(arity) do |position|
        domain = Hash.new
        type_col = find_in_hash(types, [predicate, position])
        if type_col
            # normaal type
            domain["#{alphabet[0]}"] = types[type_col]
            domain["+-#{alphabet[0]}"] = nil
            # gebruik een variabelenaam op
            alphabet.delete_at(0)
        else
            # composite type
            compo_col = find_in_hash(composites, [predicate, position])
            # de verschillende mogelijkheden voor het composite type
            composites[compo_col].each do |elem|
                if !arities.has_key?(elem)
                    # 'noop' meestal
                    domain[elem] = nil
                else
                    # recursief
                    domain.merge!(generate_domains(elem, arities, types,
                                                   composites, alphabet))
                end
            end
        end
        join.push(domain)
    end
    # genereer alle mogelijke combinaties van die mogelijkheden
    result = Hash.new
    combine(join, 0, Array.new(join.size) { 0 }, predicate, result)
    return result
end
# da magical combinator!
def combine(array, level, map, predicate, result)
    if level < array.size
        # recursief
        0.upto(array[level].size-1) do |i|
            map[level] = i
            combine(array, level+1, map, predicate, result)
        end
    else
        # einde recursietak, combinatie (in 'map') toepassen 
        values = Hash.new
        keys = Array.new
        map.each_with_index do |elem, i|
            key = array[i].keys[elem]
            keys.push(key)
            temp = array[i][key]
            if temp.class == Hash
                # composite type
                values.merge!(temp)
            else
                # normaal type
                values[key] = temp
            end
        end
        result["#{predicate}(#{keys.join(", ")})"] = values
    end
end


#
#	Find n replace
#
def findNReplaceArray(find, replace, s)
	
s = s.map { |e| 

e.gsub(find, replace)

}

end



def replaceStatesGGP file
	
	file = Dir.getwd + file 

	f = File.open(file, "r")
	
	a = []
	fa = f.to_a
	
  	fa = findNReplaceArray(", S)","", fa)
  	fa = findNReplaceArray("ggp_","", fa)
	
	fa = fa.map { |a|	a.downcase }
		

	File.open($dir + 'game.gdl', 'w') { |w|

	fa.each { |s|
	w.puts s 
	}

	}


end




# creëer mappen, schrijf alles weg naar de juiste bestanden
# enkele kleine aanpassingen zijn nodig voor het geval er meerdere spelers zijn
def write_out(name, gdl, predicate_names, types, arities, composites)
        
	File.copy(Dir.getwd + '/ace_templates/game.bg', $dir + 'game.bg')
	#File.copy(Dir.getwd + '/ace_templates/game.env', $dir + 'game.env')
	File.copy(Dir.getwd + '/ace_templates/game.s', $dir + 'game.s')
	File.copy(Dir.getwd + '/ace_templates/qtree.pl', $dir + 'qtree.pl')

        prolog = gdl_to_prolog(gdl)
	# prolog = nil 
	write_files(name, prolog, predicate_names, types, arities, composites)
end

def write_files(name, prolog, predicate_names, types, arities, composites)
	# spelbeschrijving
	#game_env = File.open($dir + 'game.env', "a")
	#game_env.puts(prolog)

	# achtergrondinformatie
	game_bg = File.open($dir + 'game.bg', "a")
#	game_bg.puts(background_info(predicate_names, arities))
	game_bg.puts("%%%%%% PROLOG %%%%%%%")
	game_bg.puts(prolog)
        game_bg.close

	# types & rmodes
	game_s = File.open($dir + 'game.s', "a")
	game_s.puts(print_types(types, composites))
	game_s.puts
	game_s.puts(generate_types(predicate_names, arities, types, composites))
	game_s.puts
	game_s.puts(generate_rmodes(predicate_names, arities, types, composites))
        game_s.close 

end

###############################################################################
def createBias 
 	fileN = $dir + 'game.gdl'
	$gdl = gdl_to_array(fileN)
	$players = collect_players($gdl)
	$name = File.basename(fileN).chomp(".gdl")

	$predicates, $arities, $composites = collect_predicates($gdl)
	$predicate_names = to_type($predicates, $composites)
	$types = find_variables($gdl, $predicates)
	#$types = reduce_types($types)
	#$types = merge_identical_types($types)
	$types = fix_player_types($types, $players)
	#$types = merge_identical_types($types)
	#$composites = merge_identical_types($composites)

	write_out($name, $gdl, $predicate_names, $types, $arities, $composites)
	


end
