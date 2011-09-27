package ASP;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;



/**
 * Centurio a General Game Player
 *
 * Copyright (C) 2008 Felix Maximilian Möller, Marius Schneider and Martin Wegner
 *
 * This file is part of Centurio.
 *
 * Centurio is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Centurio is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Centurio. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * This class implements the translation from KIF to ASP, adding a time denoter as
 * additional parameter to each predicate. It is from the Centurio GGP Project
 *
 * It bases on STListStatesTranslator from dth project (first three authors)
 * 
 * @author Holger Troelenberg
 * @author David Boehme
 * @author Kristine Jetzke
 * @author Felix Maximilian Möller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */


public class ASPEncodingConstructor extends ASPTranslator {
    
	private boolean debug = true;
	
	// name of the added parameter that denotes a time for icling (default: "t")
    public String timeDenoter = "t";
    
    //Links same predicates in rule
	private HashMap<String,LinkedList<ParsedTerm>> linker = new HashMap<String,LinkedList<ParsedTerm>>();
	
	//nested head predicates with variables	
	private HashMap<String,LinkedList<ParsedFunction>> nestedPreds = new HashMap<String,LinkedList<ParsedFunction>>();
	
	//Options for ASP
	
	// should be true
	private boolean orElemination = true;
	private boolean nonsenseElemination= true;
	
	// modus options
	private boolean timeAdd = false;
	private boolean domainAdd = false;
	private boolean dependencyGraph = false;
	private boolean sortrulesIclingo = false;
	private boolean hide = false;
	private boolean flatpredicate = false;
	private boolean addTimeDomain = false;
	private boolean guessTime = false;
	private boolean flattholdrecursion = false;
	private boolean eleminateHolds = false;

    //overwrite modus options
	private boolean iclingo = false;
	private boolean standardASP = false; 
	private boolean dlv = false;
	private boolean dlvcomplex = false;
    
    int maxTimeSteps = 201;
	
    
    //Constructor 
    public ASPEncodingConstructor(String modi)
    {
    	if (modi.toUpperCase().equals("CLINGO"))
    	{
    		standardASP = true;
    	}
    	else if (modi.toUpperCase().equals("ICLINGO"))
    	{
    		iclingo = true;
    	}
    	else if (modi.toUpperCase().equals("DLV"))
    	{
    		dlv = true;
    	}
    	
    	if (! modi.toUpperCase().equals("CLINGO") && !modi.toUpperCase().equals("ICLINGO") && !modi.toUpperCase().equals("DLV"))
    	{
    		standardASP = true;
    	}
    }
    
    // prints a list as a Prolog knowledge base, appending another parameter, <stateDenoter>, to each relation
    public void printKnowledgeBase(LinkedList<ParsedTerm> rules, PrintStream out) {
        // initialize stateDenoter and make first letter upper case
        
    	int trues = 0;
    	if (iclingo) trues++;
    	if (standardASP) trues++;
    	if (dlv) trues++;
    	if (dlvcomplex) trues++;
    	if (trues != 1)
    	{
    		iclingo = true;
    		standardASP = false; 
    		dlv = false;
    		dlvcomplex = false;
    	}
    	
    	if (iclingo)
    	{
    		timeAdd = true;
    		dependencyGraph = true;
    		domainAdd = true;
    		sortrulesIclingo = true;
    		hide = true;
    		flatpredicate = false;
    		addTimeDomain = false;
    		guessTime = false;
    		flattholdrecursion = false;

    	}
    	
    	if (standardASP)
    	{
    		timeAdd = true;
    		dependencyGraph = true;
    		domainAdd = true;
    		sortrulesIclingo = false;
    		hide = true;
    		flatpredicate = false;
    		addTimeDomain = true;
    		guessTime = true;

    	}
    	
    	if (dlv)
    	{
    		timeAdd = true;
    		dependencyGraph = true;
    		domainAdd = false;
    		sortrulesIclingo = false;
    		hide = false;
    		flatpredicate = true;
    		addTimeDomain = false;
    		guessTime = true;
    	}
    	
    	if (dlvcomplex)
    	{
    		timeAdd = true;
    		dependencyGraph = true;
    		domainAdd = false;
    		sortrulesIclingo = false;
    		hide = false;
    		flatpredicate = false;
    		addTimeDomain = true;
    		guessTime = true;
    	}
    	
    	if (flattholdrecursion)
    	{
    		//timeDoman is not necessary if holdrecursion will be flatted
    		addTimeDomain = false;
    	}
    	
    	System.out.println("###ASP Compiler###");
    	
    	if (timeDenoter == null || timeDenoter.length() == 0)
        	timeDenoter = "t";
    	
        if (!iclingo)
        {
        	timeDenoter = timeDenoter.toUpperCase();
        }
        
        if (orElemination && !flattholdrecursion)
        {
     	   if (debug)
     		   System.out.println("eleminate disjunctions");
            eleminateOrs(rules);
        }

       if (nonsenseElemination)
       {
    	   if (debug)
    		   System.out.println("eleminate nonsense");
           filter(rules);
       }
       
       HashMap<String, LinkedList<String> > graph = null;
       if (dependencyGraph)
       {
    	   graph = createDependecyGraph(rules);
    	   if (debug)
    		   System.out.println("nested head predicates : "+nestedPreds);
       }
       
       if (domainAdd && dependencyGraph)
       {
    	   if (debug)
    		   System.out.println("add Domains");
           addDomains(rules,graph);
       }
        
       if (iclingo)
       {
           out.println("#base.");
       }

       if (timeAdd)
       {
    	  if (debug) 
    		  System.out.println("add Time Parameter");
	      int index = 0;
	      for (ParsedTerm term : rules)
	      {
	      	ParsedTerm termNew = addTimeParameter(term);
	       	if (termNew != null) rules.set(index, termNew);
	       	index++;
	      }
	      if (debug)
	    	  System.out.println("iterative add Time Paramter");
	      iterativeTimeAdd(rules);
       }
 
       if (sortrulesIclingo)
       {
    	   if (debug)
    		   System.out.println("sort rules (Iclingo)");
    	   rules = sortRules(rules);
       }

       if (flatpredicate)
       {
    	   if (debug)
    		   System.out.println("flat predicates");
    	   flatpreds(rules);
       }
       
       if (addTimeDomain)
       {
    	   addTimeDomain(rules);
       }
       
       if (guessTime)
       {
    	   maxTimeSteps = tryToGuessTimeMax(rules)+1;
    	   int guessStep = tryToGuessTimeMax2(rules,graph);
    	   if (guessStep < maxTimeSteps)
    		   maxTimeSteps = guessStep;
       }
       
       if (flattholdrecursion)
       {
    	   rules = flattholdrec(rules);
           if (orElemination)
           {
        	   if (debug)
        		   System.out.println("eleminate disjunctions");
               eleminateOrs(rules);
           }
       }
       
       if (eleminateHolds)
       {
    	   eleminateHolds(rules);
       }
       

       
        System.out.println("PrintRule");
        for (ParsedTerm term: rules) {
            // print one rule per line, followed by a "."
            if (term instanceof ParsedFunction) {
                ParsedFunction rule = (ParsedFunction) term;
                if (rule.head.equals("<=")) {
                    // rule is a real rule, print using ":-"
                    if (rule.parameters.size() > 0)
                        printRelation(rule.parameters.get(0), out);
                    if (rule.parameters.size() > 1) {
                        out.print(" :- ");
                        printRelation(rule.parameters.get(1), out);
                    }
                    for (int i = 2; i < rule.parameters.size(); i++) {
                        out.print(", ");
                        printRelation(rule.parameters.get(i), out);
                    }
                } else
                    // rule is a fact, print as relation
                    printRelation(rule, out);
            } else if (term instanceof ParsedConstant)
                // term is a constant fact
                printConstantPlusState((ParsedConstant) term, out);
            else if (term instanceof ParsedVariable)
                throw new Error("Fact is Variable: "+(ParsedVariable)term+"; expected ParsedFunction or ParsedConstant");
            else
                throw new Error("ParsedTerm has unknown type: "+term.getClass()+"; expected ParsedFunction or ParsedConstant");
            out.println(".");
        }
        
        out.println("");
       
        if (standardASP && !flattholdrecursion)
        {
        	
        	out.println("timeDomain(1.."+maxTimeSteps+").");
        	out.println("1{does(R,M,"+timeDenoter+"):moveDomain(M)}1:- role(R), timeDomain("+timeDenoter+"), not terminal("+timeDenoter+").");
        	out.println("terminal("+timeDenoter+"+1):- terminal("+timeDenoter+"), timeDomain("+timeDenoter+").");
        	out.println(":-does(R,M,"+timeDenoter+"),not legal(R,M,"+timeDenoter+"),role(R).");
        	out.println(":-0{terminal("+timeDenoter+"):timeDomain("+timeDenoter+")}0.");
        	out.println(":-terminal("+timeDenoter+"), not terminal("+timeDenoter+"-1), role(R), not goal(R,100,"+timeDenoter+").");
        	out.println(":-terminal(1), role(R), not goal(R,100,1).");
        	out.println(":-not terminal("+maxTimeSteps+").");
        }
        
        if (standardASP && flattholdrecursion)
        {
        	for (int i = 1; i<= maxTimeSteps; i++)
        	{
              	out.println("1{does"+i+"(R,M):moveDomain(M)}1:- role(R), not terminal"+i+".");
            	out.println("terminal"+(i+1)+":- terminal"+i+".");
            	out.println(":-does"+i+"(R,M),not legal"+i+"(R,M),role(R).");
            	if (i != 1)
            		out.println(":-terminal"+i+", not terminal"+(i-1)+", role(R), not goal"+i+"(R,100).");
         
        	}
        	out.print(":-0{");
        	for (int i=1; i<=maxTimeSteps; i++)
        	{
        		out.print("terminal"+i);
        		if (i != maxTimeSteps)
        			out.print(",");
        	}
        	out.println("}0.");
        	out.println(":-terminal1, role(R), not goal1(R,100).");
        	out.println(":-not terminal"+maxTimeSteps+".");
        }
        
        if (iclingo)
        {
        	out.println("1{does(R,M,"+timeDenoter+"):moveDomain(M)}1 :- role(R), not terminal("+timeDenoter+").");
            out.println(":-does(R,M,"+timeDenoter+"), not legal(R,M,"+timeDenoter+"),role(R).");
            //out.println(":-terminal("+timeDenoter+"),terminal("+timeDenoter+"-1).");
            out.println(":-terminal("+timeDenoter+"-1).");
            out.println("");
            out.println("#volatile "+timeDenoter+".");
            out.println(":-terminal("+timeDenoter+"), not goal(Role,100,"+timeDenoter+"), role(Role).");
            out.println(":-not terminal("+timeDenoter+").");
        }
        
        if (dlv)
        {
        	//ob ich das jemals wirklich implementieren soll, steht noch in den sternen!
        	
        	
        	
       
        }
        
        if (dlvcomplex)
        { 	
        	out.println("timeDomain(1.."+maxTimeSteps+").");
         	out.println("does(R,M,"+timeDenoter+") | ndoes(R,M,"+timeDenoter+") :- legal(R,M,"+timeDenoter+"), role(R), timeDomain("+timeDenoter+"),  not terminal("+timeDenoter+").");
        	out.println(":- does(R,M,"+timeDenoter+"), does(R,MR,"+timeDenoter+"), M!=MR.");
        	out.println("did("+timeDenoter+"):-does(R,M,"+timeDenoter+").");
        	out.println(":- not did("+timeDenoter+"), timeDomain("+timeDenoter+"), not terminal("+timeDenoter+").");
        	out.println("terminal("+timeDenoter+"+1):-terminal("+timeDenoter+"), timeDomain("+timeDenoter+").");
        	out.println(":- not terminal("+maxTimeSteps+")."); 
        	out.println(":- terminal("+timeDenoter+"), #prec("+timeDenoter+","+timeDenoter+""+timeDenoter+"), not terminal("+timeDenoter+""+timeDenoter+"), role(R), not goal(R,100,"+timeDenoter+").");
        
        	
        }
        
        
        
        if (hide && !flattholdrecursion)
        {
            out.println("#hide.");
            out.println("#show does/3.");
        }
        
        if (hide && flattholdrecursion)
        {
            out.println("#hide.");
            for (int i = 1; i<= maxTimeSteps; i++)
            	out.println("#show does"+i+"/2.");
        }
        
        System.out.println("###ASP ready!###");

    }
    
    //eleminate Ors in Bodys
    protected void eleminateOrs(LinkedList<ParsedTerm> rules){
    	// old for-loop because of modification in rules    	
    	for (int index = 0; index<rules.size(); index++)
    	{
    		ParsedTerm rule = rules.get(index);
    		if (rule instanceof ParsedFunction && ((ParsedFunction)rule).head.equals("<="))
    		{
    			ParsedFunction realRule = (ParsedFunction) rule;
    			// old for-loop because of modification in realRule
    			// indexRule=1 -> look in Body (not Head)
    			for (int indexRule=1; indexRule<realRule.parameters.size(); indexRule++)
    			{
    				ParsedTerm partRule = realRule.parameters.get(indexRule);
    				if (partRule instanceof ParsedFunction && ((ParsedFunction) partRule).head.equals("or"))
    				{
    					ParsedFunction orPred = (ParsedFunction) partRule;
    					realRule.parameters.remove(indexRule);
    					indexRule--;
    					rules.remove(index);
    					index--;
    					
    					for (ParsedTerm orPart : orPred.parameters)
    					{
    						  						
    						//copy Rule
    						LinkedList<ParsedTerm> params = new LinkedList<ParsedTerm>();
    						for (ParsedTerm copyPreds : realRule.parameters)
    						{
    							params.add(copyPreds);
    						}
    						ParsedFunction orPredIn = new ParsedFunction(realRule.head,params);
    						
    						orPredIn.parameters.add(orPart);
    						rules.addLast(orPredIn);
    					}
    					break;
    				}
    			}
    		}
    	}
    }
    
    //adding timeDenoter if necessary and rename init, next and true
    protected ParsedTerm addTimeParameter(ParsedTerm term){
    	
    	
    		ParsedTerm termNew = null;
    		if (term instanceof ParsedConstant)
    		{
    			
    			if (((ParsedConstant) term).toString().equals("terminal"))
    			{	
    				LinkedList<ParsedTerm> parameters = new LinkedList<ParsedTerm>();
    				parameters.add(new ParsedConstant(timeDenoter));
    				termNew = new ParsedFunction("terminal",parameters);
       			}
    		}
    			
        	if (term instanceof ParsedFunction)
        	{
        		ParsedFunction rule = (ParsedFunction) term;
        		if (rule.head.equals("init"))
        		{	
        			if (!(rule.parameters.getLast() instanceof ParsedConstant && ((ParsedConstant)rule.parameters.getLast()).name.equals(timeDenoter)))
        			{
            			rule.head = "holds";
            			rule.parameters.add(new ParsedConstant("1"));
        			}

        		}
        		else if (rule.head.equals("next") && ! rule.parameters.getLast().equals(timeDenoter+"+1"))
        		{
        			if (!(rule.parameters.getLast() instanceof ParsedConstant && ((ParsedConstant)rule.parameters.getLast()).name.equals(timeDenoter)))			{
            			rule.head = "holds";
           				rule.parameters.add(new ParsedConstant(timeDenoter+"+1"));
        			}
        		}
        		else if (rule.head.equals("true") && ! rule.parameters.getLast().equals(timeDenoter))
        		{
        			if (!(rule.parameters.getLast() instanceof ParsedConstant && ((ParsedConstant)rule.parameters.getLast()).name.equals(timeDenoter)))		{
            			rule.head = "holds";
           				rule.parameters.add(new ParsedConstant(timeDenoter));
        			}

        		}
        		else if (rule.head.equals("legal")||rule.head.equals("does")||rule.head.equals("terminal")||rule.head.equals("goal") )
        		{
        			if (!(rule.parameters.getLast() instanceof ParsedConstant && ((ParsedConstant)rule.parameters.getLast()).name.equals(timeDenoter)))				
        				rule.parameters.add(new ParsedConstant(timeDenoter));
        		}
        		
        		
        		for (int i=0; i<rule.parameters.size();i++)
        		{
        			ParsedTerm param = rule.parameters.get(i);
        			addTimeParameter(param);
        		   	//if (termRec != null) rule.parameters.set(i, termRec);
        			
        		}
        				
        	}
        	return termNew;
        
    } 
    
    //add time denoter iterative
    protected void iterativeTimeAdd (LinkedList<ParsedTerm> rules)
    {
    	LinkedList<String> rememberAll = new LinkedList<String>();
    	LinkedList<String> rememberOld = new LinkedList<String>();
    	LinkedList<String> rememberNew = new LinkedList<String>();
    	rememberOld.add("holds");
    	rememberOld.add("legal");
    	rememberOld.add("does");
    	rememberOld.add("goal");
    	
    	while(!rememberOld.isEmpty())
    	{
        	for (ParsedTerm term: rules)
        	{
        		if (term instanceof ParsedFunction && ((ParsedFunction)term).head.equals("<="))
        		{
        			ParsedFunction rule = (ParsedFunction) term;
        			int index = 0;
        			ParsedTerm head = null;
        			Boolean headTime = false;
        			for (ParsedTerm part : rule.parameters)
        			{
        				//TODO: BUG!!! NOT wird nicht wieder mit in die neue Regel übernommen!
        				boolean notE = false;
        				if (part instanceof ParsedFunction && ((ParsedFunction)part).head.equals("not"))
        				{
        					part = ((ParsedFunction) part).parameters.getFirst();
        					notE = true;
        				}
        				if (index==0)
        				{
        					
        					if(
        						part instanceof ParsedFunction && 
        						((ParsedFunction)part).parameters.getLast() instanceof ParsedConstant && 
        						(
        							((ParsedConstant)((ParsedFunction)part).parameters.getLast()).name.equals(timeDenoter) || 
        							((ParsedConstant)((ParsedFunction)part).parameters.getLast()).name.equals(timeDenoter+"+1") ||
        							( 
        								((ParsedConstant)((ParsedFunction)part).parameters.getLast()).name.equals("1") && 
        								((((ParsedFunction)part).head.equals("holds")))
        							)
        						 )
        						)
        						headTime = true;
        					else 
        						head  = part;
        				}
        			
        				if (!(headTime && index==0))
        				{
        					if (part instanceof ParsedFunction  && rememberOld.contains(((ParsedFunction)part).head))
    						{
    							ParsedFunction partF = (ParsedFunction) part;
    							if ((! (partF.parameters.getLast() instanceof ParsedConstant ) )|| !( ((ParsedConstant)partF.parameters.getLast()).name.equals(timeDenoter) || ((ParsedConstant)partF.parameters.getLast()).name.equals(timeDenoter+"+1") ))
    							{
    								ParsedConstant var = new ParsedConstant(timeDenoter);
    								partF.parameters.addLast(var);
    							}
    						}
    						else if (part instanceof ParsedConstant && rememberOld.contains(((ParsedConstant)part).name))
    						{
    							ParsedConstant var = new ParsedConstant(timeDenoter);
    							LinkedList<ParsedTerm> param = new LinkedList<ParsedTerm>();
    							param.add(var);
    							ParsedFunction fun = new ParsedFunction(((ParsedConstant) part).name,param);
    							if (notE)
    							{
    								LinkedList<ParsedTerm> notParam = new LinkedList<ParsedTerm>();
    								notParam.add(fun);
    								ParsedFunction funNot = new ParsedFunction("not",notParam);
    								rule.parameters.set(index, funNot);
    								
    							}
    							else
    							{
    								rule.parameters.set(index, fun);
    							}
    							
    							part = fun;
    						}
        					
        					//body has timeDenoter -> add timeDenoter to head
        					if(index != 0 && !headTime && part instanceof ParsedFunction && rememberOld.contains((((ParsedFunction)part).head)))
        					{
        						ParsedConstant var = new ParsedConstant(timeDenoter);
        						if(head instanceof ParsedFunction)
        						{
        							ParsedFunction headF = (ParsedFunction) head;
        							if ((! (headF.parameters.getLast() instanceof ParsedConstant ) )|| !( ((ParsedConstant)headF.parameters.getLast()).name.equals(timeDenoter) || ((ParsedConstant)headF.parameters.getLast()).name.equals(timeDenoter+"+1") ))
            						{
            							((ParsedFunction) head).parameters.addLast(var);
            							if (!rememberNew.contains(((ParsedFunction) head).head))
            								rememberNew.add(((ParsedFunction) head).head);
        							}

        						}
        						else if (head instanceof ParsedConstant)
        						{
        							LinkedList<ParsedTerm> param = new LinkedList<ParsedTerm>();
        							param.add(var);
        							ParsedFunction fun = new ParsedFunction(((ParsedConstant) head).name,param);
        							rule.parameters.set(0, fun);
        							if (! rememberNew.contains(((ParsedConstant) head).name))
        								rememberNew.add(((ParsedConstant) head).name);
        						}
        					}
        				}
        					

        				
        				index++;
        			}
        		}
        	}

        	rememberAll.addAll(rememberOld);
        	rememberOld.clear();
        	rememberOld.addAll(rememberNew);
        	rememberNew.clear();
    	}

    }
    
    //sort of base and cumulative
    protected LinkedList<ParsedTerm>  sortRules(LinkedList<ParsedTerm> rules)
    {
    	if (debug)
    		System.out.println("Sort Rules");
    	
    	int border = 1;
    	boolean firstT = true;
    	LinkedList<ParsedTerm> sortedRules = new LinkedList<ParsedTerm>();
    	for (ParsedTerm rule: rules)
    	{
    		if(rule instanceof ParsedFunction)
    		{
    			ParsedFunction func = (ParsedFunction) rule;
    			//all functions with timeDenoter to the end
    			boolean withTime = withTimeDentor(func);
		
    			if(withTime)
    			{
    				if (firstT)
    					//sortedRules.addLast(func);
    					sortedRules.add(border-1,func);
    				else
    					sortedRules.add(border,func);
    			}
    			else
    			{	
    			//	sortedRules.addFirst(func);
    				sortedRules.add(border-1,func);
    				border++;
    			}
    		}
    	}
    	
    	//special iclingo syntax
    	if (iclingo && !standardASP)
    	{    	
    		sortedRules.add(border-1,new ParsedConstant("#cumulative "+timeDenoter));
       	}

    	return sortedRules;
    }
    
    protected boolean withTimeDentor (ParsedFunction func)
    {
    	if (func.head.equals("<="))
    	{
    		for(ParsedTerm param: func.parameters)
    		{
    			if (param instanceof ParsedFunction)
    			{
    				ParsedTerm last = ((ParsedFunction) param).parameters.getLast();
    				if (last instanceof ParsedConstant && (last.toString().equals(timeDenoter) || last.toString().equals(timeDenoter+"+1")))
    					return true;
    				
    			}
    		}
    		return false;
    	}
    	else
    	{
    		ParsedTerm last = func.parameters.getLast();
			if (last instanceof ParsedConstant && (last.toString().equals(timeDenoter) || last.toString().equals(timeDenoter+"+1")))
				return true;
			else
				return false;
    	}
    }
    
    //creates a dependency graph for all predicates
    protected HashMap<String,LinkedList<String>> createDependecyGraph(LinkedList<ParsedTerm> rules)
    {
    	// predicate/id -> [predicate/id]LIST
    	HashMap<String,LinkedList<String>> graph = new HashMap<String,LinkedList<String>>();
    	
    	
    	for (ParsedTerm term : rules)
    	{
    		if (term instanceof ParsedFunction)
    		{
    			ParsedFunction rule = (ParsedFunction) term;
       			if (rule.head.equals("<="))
    			{
    				ParsedTerm headE = rule.parameters.getFirst();
					
    				if (headE instanceof ParsedConstant)
    				{
    					for (ParsedTerm part : rule.parameters)
    						addLinker(part);
    				}
    				
    				if (headE instanceof ParsedFunction && (((ParsedFunction)headE).head.equals("next") || ((ParsedFunction)headE).head.equals("true") || ((ParsedFunction)headE).head.equals("init")))
						headE = ((ParsedFunction) headE).parameters.getFirst();
					if (headE instanceof ParsedFunction && (((ParsedFunction)headE).head.equals("legal")) && ((ParsedFunction) headE).parameters.get(1) instanceof ParsedFunction)
						headE = ((ParsedFunction) headE).parameters.get(1);					
					
    				if (headE instanceof ParsedFunction)
    				{
    					int index = 0;
    					ParsedFunction headF = (ParsedFunction) headE;
    					LinkedList<ParsedFunction> heads = new LinkedList<ParsedFunction>();
    					for (ParsedTerm cons : headF.parameters)
    					{
    						addLinker(cons);
    						index++;
    						
    						//something like: h(q(1,2))
    						boolean consFunction = false;
    						if (cons instanceof ParsedFunction)
    						{
    							consFunction = true;
    							ParsedFunction consF = (ParsedFunction) cons;
    							for (ParsedTerm t : consF.parameters)
    							{
    								if (!(t instanceof ParsedConstant))
    								{
    									if (consFunction)
    									{
    										heads.add(consF);
    										boolean contains = false;
    										LinkedList<ParsedFunction> nesteds = nestedPreds.get(headF.head);
    										if (nesteds == null)
    										{
    											nesteds = new LinkedList<ParsedFunction>();
    											nesteds.add(headF);
    											nestedPreds.put(headF.head, nesteds);
    										}
    										for (ParsedTerm rem : nesteds)
    										{
    											if (rem.toString().equals(headF.toString()))
    												contains = true;
    										}
    										if (!contains)
    											nesteds.add(headF);
    										
    									}
    									consFunction = false;
    								}
    							}
    						}
   						
    						if (cons instanceof ParsedConstant || consFunction)
    						{
	        					String var = cons.toString();
	        					LinkedList<String> dependencieMap = null;
								if (graph.containsKey(((ParsedFunction) headE).head+"/"+index))
									 dependencieMap = graph.get(headF.head+"/"+index);
								else
	   							 	dependencieMap = new LinkedList<String>();
								if (!dependencieMap.contains(var))
								{	
									dependencieMap.add(var);
									graph.put(headF.head+"/"+index,dependencieMap);
								}
    						}
    						
    						
    					}
    					
    				//	if (heads.size() == 0)
    				//	{
    						heads.add(headF);
    				//	}
    					
    					for (ParsedFunction head: heads)
    					{
    	   					index = 1;
            				for (ParsedTerm param : rule.parameters)
            				{
            					addLinker(param);
            		//			System.out.println(" head : "+heads+" params : "+param);
            					if (index != 1 && param != rule.parameters.getFirst() && param instanceof ParsedFunction )
            					{
            			//			System.out.println(" head : "+head+" params : "+param);
            						ParsedFunction params = (ParsedFunction) param;
            						//negativ bodys are worthless
            						if (!params.head.equals("not"))
            						{
            							boolean noParsedCons = true;
            							if ((params.head.equals("next") || params.head.equals("true")))
            							{
            								if (params.parameters.getFirst() instanceof ParsedFunction)
            									params = (ParsedFunction) params.parameters.getFirst();
            								else
            									noParsedCons = false; //ParsedConstants in true predicate are worthless for domains
            							}
    	        						if (noParsedCons)
    	        						{
        	        						if (params.head.equals("does") && params.parameters.get(1) instanceof ParsedFunction)
        	        							params = (ParsedFunction) params.parameters.get(1);
        	        					
        	        			//			if (!((ParsedFunction) head).head.equals(params.head))
        	        			//			{

        	        							for (int internIndex=1;internIndex<=((ParsedFunction) head).parameters.size();internIndex++)
        	        							{
        	    	        						LinkedList<String> vars = findSameVariables(head,params,internIndex);
        	    	        				//		System.out.println("index : "+internIndex+" head : "+head+" params : "+params+" vars : "+vars);
        	    	        						LinkedList<String> dependencieMap = null;
        	    	        						if (graph.containsKey(((ParsedFunction) head).head+"/"+internIndex))
        	    	        						{
        	    	        							dependencieMap = graph.get(((ParsedFunction) head).head+"/"+internIndex);
        	    	        							for (String var : vars)
        	    	        							{
        	    	        						//		System.out.println("headE: "+(((ParsedFunction) headE).head+"/"+internIndex)+" add: "+var);
        	    	        								if(!var.equals("distinct/2")&&!var.equals("distinct/1")&&!(((ParsedFunction) headE).head+"/"+internIndex).equals(var) && ! dependencieMap.contains(var))
        	    	        									dependencieMap.add(var);
        	    	        							}
        	    	        						}
        	    	    							else
        	    	    								graph.put(((ParsedFunction) head).head+"/"+internIndex,vars);
        	        							}
        	
        	
        	        					//	}
    	        						}

            						}
            					}
            					index++;
            				}
    					}
 
    				}

    			}
    			// facts -> find domains
    			else 
    			{
    				// filter init predicate
    				if (rule.head.equals("init") && rule.parameters.getFirst() instanceof ParsedFunction)
    					rule = (ParsedFunction) rule.parameters.getFirst();
    				
    				int index = 0;
    				for (ParsedTerm internTerm: rule.parameters)
    				{
    					index++;
    					if (internTerm instanceof ParsedConstant)
    					{
    						addDependency(internTerm, graph, rule, index);
    					}
    					if (internTerm instanceof ParsedFunction)
    					{
    						ParsedFunction internFun = (ParsedFunction) internTerm;
    						//check variable free
    						boolean varFree = true;
    						for(ParsedTerm test : internFun.parameters)
    						{
    							if (test instanceof ParsedVariable)
    							{
    								varFree = false;
    							}
    						}
    						if (varFree)
    						{
    							// add as function to Domains (nested Predicates Domains)
    							addDependency(internTerm, graph, rule, index);
    							
    							//add as a normal domain
    							int inIndex = 0;
    							for (ParsedTerm inIntern : internFun.parameters)
    							{
    								inIndex++;
    								if (!(inIntern instanceof ParsedVariable))
    								{
    									addDependency(inIntern, graph, internFun, inIndex);
    								}
    							}
    						}
    					}
    					
    				}
    			}
    		}
    	}
    	if (debug)
    		System.out.println("Graph: "+graph);
    	return graph;
    }
    
    //add to dependency graph
    protected void addDependency (ParsedTerm internTerm, HashMap<String,LinkedList<String>> graph, ParsedFunction rule, int index)
    {
		String var = internTerm.toString();
		LinkedList<String>  dependencieMap = null;
		if (graph.containsKey(rule.head+"/"+index))
			 dependencieMap = graph.get(rule.head+"/"+index);
		else
			dependencieMap = new LinkedList<String> ();
		if (!dependencieMap.contains(var))
		{	
			dependencieMap.add(var);
			graph.put(rule.head+"/"+index,dependencieMap);
		}
    }
    
    //add to Linker (unnecessary?)
    protected void addLinker(ParsedTerm term)
    {
    	if (term instanceof ParsedFunction && ! (((ParsedFunction) term).head.equals("not") || ((ParsedFunction) term).head.equals("distinct")) )
    	{
    		ParsedFunction fun = (ParsedFunction) term;
    		
    		if (fun.head.equals("true") || fun.head.equals("next") || fun.head.equals("legal"))
    		{
    			if (fun.parameters.getFirst() instanceof ParsedFunction)
    				fun = (ParsedFunction) fun.parameters.getFirst();
    			else
    				return;
    		}
    		
    		if (fun.head.equals("does"))
    		{
    			if (fun.parameters.get(1) instanceof ParsedFunction)
    				fun = (ParsedFunction) fun.parameters.get(1);
    			else
    				return;
    		}
    		
    		LinkedList<ParsedTerm> links = new LinkedList<ParsedTerm>();
    		if (linker.containsKey(fun.head+"/"+fun.parameters.size()))
    		{
    			links = linker.get(fun.head+"/"+fun.parameters.size());
    			if (!links.contains(term))
    				links.add(term);
    		}
    		else
    		{
    			links.add(term);
    			linker.put(fun.head+"/"+fun.parameters.size(), links);
    		}
    	}
    	if (term instanceof ParsedConstant)
    	{
    		ParsedConstant con = (ParsedConstant) term;
    		LinkedList<ParsedTerm> links = new LinkedList<ParsedTerm>();
    		if (linker.containsKey(con.name+"/"+0))
    		{
    			links = linker.get(con.name+"/"+0);
    			if (!links.contains(term))
    				links.add(term);
    		}
    		else
    		{
    			links.add(term);
    			linker.put(con.name+"/"+0, links);
    		}
    	}
    	
    }
    
    //find the same variables in two Functions
    protected LinkedList<String> findSameVariables(ParsedTerm first, ParsedTerm second, int index)
    {
    	LinkedList<String> sameVar = new LinkedList<String> ();
    	if (first instanceof ParsedFunction && second instanceof ParsedFunction)
    	{
    		ParsedFunction firstR = (ParsedFunction) first;
    		ParsedFunction secondR = (ParsedFunction) second;
    		ParsedTerm var = firstR.parameters.get(index-1);

    			if (var instanceof ParsedVariable )
    			{
    				int index2 = 0;
    				for (ParsedTerm t : secondR.parameters)
    				{
    					index2++;
    					if (t instanceof ParsedVariable && t.toString().equals(var.toString()))
    	    			{
        					sameVar.add(secondR.head+"/"+index2);
        				}
    				}


    			}
    				


    	}
    //	System.out.println("first: "+first+" second: "+second+" Vars: "+sameVar);
    	return sameVar;
    }
    
    //unnecessary(?)
    protected LinkedList<String> findSameTerms(ParsedTerm first, ParsedTerm second, int index)
    {
    	LinkedList<String> sameVar = new LinkedList<String> ();
    	if (first instanceof ParsedFunction && second instanceof ParsedFunction)
    	{
    		ParsedFunction firstR = (ParsedFunction) first;
    		ParsedFunction secondR = (ParsedFunction) second;
    		ParsedTerm var = firstR.parameters.get(index-1);
    		
			if (var instanceof ParsedFunction)
			{
				System.out.println(firstR+" "+var);
				ParsedFunction intern = (ParsedFunction) var;
				for (ParsedTerm varIntern: intern.parameters)
				{
					int index2 = 0;
					if (varIntern instanceof ParsedFunction)
					{
						ParsedFunction varInternF = (ParsedFunction) varIntern;
						for (ParsedTerm t : varInternF.parameters)
	    				{
	    					index2++;
	    					if (t instanceof ParsedVariable && t.toString().equals(var.toString()))
	    	    			{
	        					sameVar.add(varInternF.head+"/"+index2);
	        				}
	    				}
					}
				}
			}
    	}
    	
		return sameVar;
    }
    
    //adds domain predicates in <= rules with hold() as head
    protected LinkedList<ParsedTerm> addDomains(LinkedList<ParsedTerm> rules, HashMap<String,LinkedList<String>> graph)
    {
    	if (debug)
    		System.out.println("addDomains");
    	
    	//for (ParsedTerm rule : rules) nicht möglich, da in der Schleife die Liste verändert wird
    	LinkedList<String> remember = new LinkedList<String>();
    	for(int i=0; i<rules.size(); i++)
    	{
    		ParsedTerm rule = rules.get(i);
    		if (rule instanceof ParsedFunction && ((ParsedFunction)rule).head.equals("<="))
    		{
    			//Filter "<="

    			LinkedList<ParsedTerm> params = ((ParsedFunction) rule).parameters;
    		
    			//head of rule must be remembered
    			String headremember = "";
    			Boolean filter = false;
    			LinkedList<String> rememberVars = new LinkedList<String>();
    			
    			//for (ParsedTerm param : params) nicht möglich, da in der Schleife die Liste verändert wird
    			for (int y=0; y< params.size();y++)
    			{
    				ParsedTerm param = params.get(y);
    				filter = false;
    				
    				// Für "Domains" muss nicht eine Domaine vergeben werden ;)		
    				if (param instanceof ParsedFunction  && !((ParsedFunction)param).head.contains("Domain"))
					{
						ParsedTerm head = param;
						if (( ((ParsedFunction) param).head.equals("next") || ((ParsedFunction) param).head.equals("true")))
						{
							head = ((ParsedFunction) param).parameters.getFirst();
							filter = true;
						}
							
						

        				 if (head instanceof ParsedFunction)
        				 {
        					 
        					 ParsedFunction headF = (ParsedFunction) head;
	        					 
     						if (y==0)
    						{
    							headremember = headF.head;
    						}

     						// distinct braucht keine Domain und Domains werden nur über Variablen vergeben
        					 if (!headF.head.equals("distinct") && (headF.head.equals(headremember) || filter))
	        					 {
		        					 int index = 0;
		        					 for (ParsedTerm headParams : headF.parameters)
		        					 {
		        						 index++;
		        						 if(headParams instanceof ParsedVariable && !rememberVars.contains(((ParsedVariable)headParams).name))
		        						 {
		        							rememberVars.add(((ParsedVariable)headParams).name);
		        							LinkedList<String> domains = buildDomain(graph,headF.head+"/"+index,new LinkedList<String>());
		        							if (! remember.contains(headF.head+index+"Domain"))
		        							{
		    	    							 if (domains == null)
		    	    							 {
		    	    								 if (debug)
		    	    									 System.out.println("Removed Rule: "+headF.head +" : "+ index + " : "+rules.remove(i));
		    	    								 rules.remove(i);
		    	    								 i--;
		    	    								 break;
		    	    							 }
		    	    							 for (String dom : domains)
		    	    							 {
		    	    								// System.out.println(dom);
		    	    								 if (dom.equals("does/2"))
		    	    								 {
			    	    								 LinkedList<ParsedTerm> temp = new LinkedList<ParsedTerm>();
			    	    								 temp.add(new ParsedVariable("Move"));
			    	    								 ParsedFunction domainHead = new ParsedFunction(headF.head+index+"Domain",temp);
			    	    								 LinkedList<ParsedTerm> temp2 = new LinkedList<ParsedTerm>();
			    	    								 temp2.add(new ParsedVariable("Move"));
			    	    								 ParsedFunction domainBody = new ParsedFunction("moveDomain",temp2);
			    	    								 LinkedList<ParsedTerm> tempN = new LinkedList<ParsedTerm>();
			    	    								 tempN.add(domainHead);
			    	    								 tempN.add(domainBody);
			    	    								 rules.add(new ParsedFunction("<=",tempN));
		    	    								 }
		    	    								 else
		    	    								 {
			    	    								 LinkedList<ParsedTerm> temp = new LinkedList<ParsedTerm>();
			    	    								 temp.add(new ParsedConstant(dom));
			    	    								 rules.add(new ParsedFunction(headF.head+index+"Domain",temp));
		    	    								 }

		    	    								
		    	    							 }


		    	    							 remember.add(headF.head+index+"Domain");
		        							}
			           						 LinkedList<ParsedTerm> temp = new LinkedList<ParsedTerm>();
			        						 temp.add(headParams);
			        						 if (domains.size() != 0)
			        							 params.add(new ParsedFunction(headF.head+index+"Domain",temp));
		        						}
	        						 
	        					 }
        					 }
        				 }
        				     				 
					}
    			}
    		
    		}
    	}
    	//System.out.println(rules);
    	if (debug)
    		System.out.println("MoveDomain bestimmen");

    	// Move Domain bestimmen
    	LinkedList<String> rememberMoves = new LinkedList<String>();
    	for(int i=0; i<rules.size(); i++)
    	{
    		ParsedTerm term = rules.get(i);
   		
   			if (term instanceof ParsedFunction && ((ParsedFunction) term).head.equals("<="))
   			{
   				
    			ParsedFunction fun = (ParsedFunction) term;
    			if (fun.parameters.getFirst() instanceof ParsedFunction && ((ParsedFunction) fun.parameters.getFirst()).head.equals("legal"))
    			{
    				
    				ParsedFunction legal = (ParsedFunction) fun.parameters.getFirst();
    				if (legal.parameters.get(1) instanceof ParsedFunction)
   					{
    					
   						ParsedFunction headF = (ParsedFunction) legal.parameters.get(1);

   						if (!rememberMoves.contains(headF.toString()))
   						{
   							rememberMoves.add(headF.toString());
   							ParsedFunction moveDomain = new ParsedFunction("<=",new LinkedList<ParsedTerm>());
   	   						ParsedFunction moveDomainSub = new ParsedFunction("moveDomain",new LinkedList<ParsedTerm>());
   	   						moveDomain.parameters.add(moveDomainSub);
   	   						moveDomainSub.parameters.add(headF);
   	   						
   	   						LinkedList<ParsedTerm> params = moveDomain.parameters;
   	   						
   	   						
   	      					 int index = 0;
   	    					 for (ParsedTerm headParams : headF.parameters)
   	    					 {
   	    						 index++;
   	    						 if(headParams instanceof ParsedVariable)
   	    						 {
   	    							if (! remember.contains(headF.head+index+"Domain"))
   	    							{
   		    							 LinkedList<String> domains = buildDomain(graph,headF.head+"/"+index,new LinkedList<String>());
   		    							 for (String dom : domains)
   		    							 {
   		    								 //System.out.println(dom);
   		    								 if (! dom.equals("does/2"))
   		    								 {
   		    									 LinkedList<ParsedTerm> temp = new LinkedList<ParsedTerm>();
   	   		    								 temp.add(new ParsedConstant(dom));
   	   		    								 rules.add(new ParsedFunction(headF.head+index+"Domain",temp));
   	     		    							//	 rules.addFirst(new ParsedFunction(headF.head+index+"Domain",temp));
   	     		    							//	 i = i+1;
   		    								 }
   		    		

   		    							 
   		    							 }
   		    							 remember.add(headF.head+index+"Domain");
   	    							}
   	    							
   	    							
   	       						 LinkedList<ParsedTerm> temp = new LinkedList<ParsedTerm>();
   	    						 temp.add(headParams);
   	    						 params.add(new ParsedFunction(headF.head+index+"Domain",temp));
   	    						 }
   	    						 
   	    					 }
   	    				//	 i = i+1;
   	   					//	rules.addFirst(moveDomain);
   	    					rules.add(moveDomain);
   						}
   					}
   				}
    		}
   			
    	}
  //  	System.out.println(rules);
    	if (debug)
    		System.out.println("MoveDomain per Constant bestimmen");
    	
        LinkedList<String> domains = buildDomain(graph,"legal/2",new LinkedList<String>());
    	if (domains != null)
    	{
    		//direktes herleiten der MoveDomain
        	for (String dom : domains)
        	{
    			 LinkedList<ParsedTerm> temp = new LinkedList<ParsedTerm>();
    			 temp.add(new ParsedConstant(dom));
    			// rules.addFirst(new ParsedFunction("moveDomain",temp));
    			 rules.add(new ParsedFunction("moveDomain",temp));
        	}
    	}

    //	System.out.println(rules);
    	return rules;
    }
    
    //build Domain
    protected LinkedList<String>  buildDomain(HashMap<String,LinkedList<String>> graph, String head, LinkedList<String> cycles)
    {
    //	System.out.println(head);
    	LinkedList<String> depends = graph.get(head);
    	if (depends == null) return null;
    	LinkedList<String> cons = new LinkedList<String>();
    	for (String depend : depends)
    	{
    		if (!cycles.contains(depend))
    		{
        		if (depend.contains("/"))
        		{
        			cycles.add(depend);
        			LinkedList<String> rCons = buildDomain(graph,depend,cycles);
        			if (rCons != null)
        			{	
    	    			for (String rCon : rCons)
    	    			{
    	    				if (!cons.contains(rCon))
    	    					cons.add(rCon);
    	    			}
        			}
        			if (depend.equals("does/2"))
        			{
        				cons.add(depend);
        			}
        		}
        		else
        		{
        			if (!cons.contains(depend))
        				cons.add(depend);
        		}

    		}

    	}
    	return cons;
    }
    
    
    //filter nonsense: not(distinct(X,Y))
    protected void filter(LinkedList<ParsedTerm> rules)
    {
    	for (ParsedTerm term : rules)
    	{
    		if(term instanceof ParsedFunction && ((ParsedFunction)term).head.equals("<="))
    		{
    			ParsedFunction rule = (ParsedFunction) term;
    			int index = 0;
    			for (ParsedTerm part : rule.parameters)
    			{
    				if (part instanceof ParsedFunction && ((ParsedFunction)part).head.equals("not"))
    				{
    					ParsedTerm partNot = ((ParsedFunction) part).parameters.getFirst();
    					
    					if (partNot instanceof ParsedFunction && ((ParsedFunction)partNot).head.equals("distinct"))
    					{
    						ParsedTerm partDis1 = ((ParsedFunction) partNot).parameters.getFirst();
    						ParsedTerm partDis2 = ((ParsedFunction) partNot).parameters.getLast();
    					//	System.out.println(partDis1 + " : "+partDis2);
    					//	System.out.println(partDis1.getClass() + " : "+partDis2.getClass());
    						LinkedList<ParsedTerm> params = new LinkedList<ParsedTerm>();
    						params.add(partDis1);
    						params.add(partDis2);
    						ParsedFunction equal = new ParsedFunction("equals",params);
    					//	System.out.println(equal);
    						rule.parameters.set(index, equal);
    					}
    				}
    				index++;
    			}
    		}
    	}
    }
    
    // flat predicates 
    protected void flatpreds (LinkedList<ParsedTerm> rules)
    {
    	for (ParsedTerm term : rules)
    	{
    		if (term instanceof ParsedFunction)
    		{
    			ParsedFunction rule = (ParsedFunction) term;
    			if (rule.head.equals("<="))
    			{
    				for(ParsedTerm part : rule.parameters)
    				{
    					if (part instanceof ParsedFunction)
    					{
    						ParsedFunction partF = (ParsedFunction) part;
    						if (partF.head.equals("holds"))
    							flatholds(partF);
    						else if (partF.head.equals("does") || partF.head.equals("legal"))
    							flatDoesLegal(partF);
    										
    					}
    				}
    			}
    			else if (rule.head.equals("holds"))
    			{
    				flatholds(rule);
    			}
    		}
    	}
    }
    
    protected void flatholds(ParsedFunction hold)
    {
    	if (hold.parameters.getFirst() instanceof ParsedFunction)
    	{
    		ParsedFunction in = (ParsedFunction) hold.parameters.getFirst();
    		hold.head+=in.head;
    		hold.parameters.remove(0);
    		hold.parameters.addAll(0, in.parameters);
    	}
    }
    
    protected void flatDoesLegal(ParsedFunction partF)
    {
    	if (partF.parameters.get(1) instanceof ParsedFunction)
    	{
    		ParsedFunction in = (ParsedFunction) partF.parameters.get(1);
    		partF.head += in.head;
    		partF.parameters.remove(1);
    		partF.parameters.addAll(1, in.parameters);
    	}
    }
    
    //add time Domain in rules
    protected void addTimeDomain(LinkedList<ParsedTerm> rules)
    {
    	ParsedConstant time = new ParsedConstant(timeDenoter);
    	LinkedList<ParsedTerm> timeP = new LinkedList<ParsedTerm>();
    	timeP.add(time);
    	ParsedFunction timeF = new ParsedFunction("timeDomain",timeP);
    	int index = 0;
    	for (ParsedTerm term : rules)
    	{
    		if (term instanceof ParsedFunction && ((ParsedFunction) term).head.equals("<="))
    		{
    			ParsedFunction rule = (ParsedFunction) term;
    			if (rule.parameters.getFirst() instanceof ParsedFunction)
    			{
    				ParsedFunction head = (ParsedFunction) rule.parameters.getFirst();
    				boolean nsafe = safetyCheckTime(rule);
    				if (
    					((     head.head.equals("goal") 
    						|| head.head.equals("legal") 
    						|| head.head.equals("terminal")) 
    					  && !nsafe) 
    					||
    					head.head.equals("holds") 
    					|| nsafe)
    				{
    					rule.parameters.add(timeF);
    					
    				}
    	    		
    			}
    		}
    		else if (term instanceof ParsedFunction)
    		{
    			ParsedFunction fun = (ParsedFunction) term;
   			
    			if (fun.parameters.getLast().toString().equals(timeDenoter))
    			{
    				ParsedFunction rule = new ParsedFunction("<=",new LinkedList<ParsedTerm>());
    				rule.parameters.add(fun);
    				rule.parameters.add(timeF);
    				rules.set(index, rule);
    			}
    		}
    		index++;
    	}
    }
    
    //checks wether T occurs in the head or positive body
    protected boolean safetyCheckTime(ParsedFunction rule )
    {
    	for (ParsedTerm term: rule.parameters)
    	{
    		if (term instanceof ParsedFunction)
    		{
    			ParsedFunction fun = (ParsedFunction) term;
    			if (fun.parameters.getLast() instanceof ParsedConstant)
    			{
    				ParsedConstant con = (ParsedConstant) fun.parameters.getLast();
    				if (con.name.equals(timeDenoter))
    				{
    					return true;
    				}
    			}
    		}
    	}
    	//System.out.println("false");
    	return false;
    }
    
    // find a rule with goal or terminal in head and a step in the body
    protected int tryToGuessTimeMax (LinkedList<ParsedTerm> rules)
    {
    	int maxTime = maxTimeSteps; 
    	for (ParsedTerm term : rules)
    	{
    		if (term instanceof ParsedFunction && ((ParsedFunction) term).head.equals("<=") )
    		{
    			ParsedFunction rule = (ParsedFunction) term;
    			ParsedTerm head = rule.parameters.getFirst();
    			if (head instanceof ParsedFunction && (((ParsedFunction) head).head.equals("terminal") || ((ParsedFunction) head).head.equals("goal") ))
    			{
    				//only 100 points goals
    				boolean valid = true;
    				if (((ParsedFunction) head).head.equals("goal"))
    				{
    					ParsedFunction goal = (ParsedFunction) head;
    					if (goal.parameters.get(1) instanceof ParsedConstant)
    					{
        					ParsedConstant point = (ParsedConstant) goal.parameters.get(1);
        					int points;
        					try {
        						points = Integer.valueOf( point.name ).intValue();
    						}
    						catch (Exception e)
    						{
    							points = 0;
    						}
    						if (points != 100)
    							valid = false;
    					}
    					else
    						valid = false;

    				}
    				
    				if (valid)
    				{
    					int index = 0;
        				for(ParsedTerm part : rule.parameters)
        				{
        					if (index != 0)
        					{
        						if (part instanceof ParsedFunction && ((ParsedFunction) part).head.equals("holds"))
        						{
        							ParsedFunction hold = (ParsedFunction) part;
        							ParsedTerm partHold = hold.parameters.getFirst();
        							if (partHold instanceof ParsedFunction && ((ParsedFunction) partHold).head.equals("step"))
        							{
        								ParsedFunction step = (ParsedFunction) partHold;
        								ParsedTerm stepPart = step.parameters.getFirst();
        								if (stepPart instanceof ParsedConstant)
        								{
        									ParsedConstant con = (ParsedConstant) stepPart;
        									int time;
        									try {
        										time = Integer.valueOf( con.name ).intValue();
        									}
        									catch (Exception e)
        									{
        										 time = Integer.MAX_VALUE;
        									}
        									if (time < maxTime)
        										maxTime = time;
        								}
        								
        							}
        						}
        					}
        					index++;
        				}
    			
    				}
    			}
    		}
    	}
    	System.out.println("Guess 1 time steps: "+maxTime);
    	return maxTime; 
    }
    
    // second approach
    // try to find Udpate-Rule for time steps and count Domain
    protected int tryToGuessTimeMax2(LinkedList<ParsedTerm> rules, HashMap<String, LinkedList<String> > graph)
    {
    	int maxTime = 65;
    	LinkedList<String> stepName = new LinkedList<String>();
    	String stepNameGuess = "";
    	for (ParsedTerm term : rules)
    	{
    		if (term instanceof ParsedFunction && ((ParsedFunction) term).head.equals("<="))
    		{
    			ParsedFunction rule = (ParsedFunction) term;
    			ParsedTerm head = rule.parameters.getFirst();
    			if (head instanceof ParsedFunction && ((ParsedFunction) head).head.equals("holds"))
    			{
    				ParsedTerm part = ((ParsedFunction) head).parameters.getFirst();
    				// step predikate haben nur ein Argument
    				if (part instanceof ParsedFunction && ((ParsedFunction)part).parameters.size() == 1)
    				{
    					stepNameGuess = ((ParsedFunction)part).head;
    					int index = 0;
    					boolean valid = true;
    					boolean holdBody = false;
    					boolean succBody = false;

    					//Body betrachten
    					for (ParsedTerm elem : rule.parameters)
    					{
    						// head aussparen
    						if (index != 0)
    						{
    							if (elem instanceof ParsedFunction)
    							{
    								ParsedFunction elemF = (ParsedFunction) elem;
    								if (elemF.head.equals("holds"))
    								{
    									ParsedTerm holdPart = elemF.parameters.getFirst();
    									if (!(holdPart instanceof ParsedFunction && ((ParsedFunction)holdPart).head.equals(stepNameGuess)))
    									{
    										valid = false;
    										// nur das predikat holds([step](X)) wird akzeptiert - sonst ist es die falsche Regel
    									}
    									else
    									{
    										holdBody = true;
    									}
    								}
    								else
    								{
    									if (elemF.head.equals("does"))
    									{
    										valid = false;
    										// Step darf nicht vom Zug abhängen (sonst wäre das Spiel wahrscheinlich nicht endlich
    									}
    									else
    									{
    										if(!(elemF.parameters.size() <= 2))
    										{
    					//						System.out.println("wrong parameter Size : "+elemF);												
    											valid = false;
    											// das succ-Prädikat muss genau zwei Argumente haben (succ(X,Y))
    											// alternativ nur Domains erlaubt
    										}
    										else if (elemF.parameters.size() == 2)
    										{
    											succBody = true;
    										}
    									}
    								}
    							}
    						}
    						if (!valid)
    							break;
    						index++;
    					}
    					if (valid & holdBody & succBody)
    					{
    						System.out.println("rule : "+rule);
    						stepName.add(stepNameGuess);
    					}
    				}
    			}
    		}
    	}
    	if (stepName.size() != 0)
    	{
    		maxTime = 0;
    	}
    	for(String stepNameItem : stepName)
    	{
    		//System.out.println(stepName);
        	LinkedList<String> domains = buildDomain(graph,stepNameItem+"/1",new LinkedList<String>());
        	//System.out.println("Time Domain : "+domains);
        	//System.out.println("Max Time Domain : "+domains.size());
        	maxTime += domains.size();
    	}
    	System.out.println("Guess 2 time steps: "+maxTime);
    	return maxTime;
    }
    
    //eliminates hold recursion and flatts time steps in predicate names
    protected LinkedList<ParsedTerm> flattholdrec(LinkedList<ParsedTerm> rules)
    {
    	LinkedList<ParsedTerm> newRules = new LinkedList<ParsedTerm>();
    	for (ParsedTerm term : rules)
    	{
    		if (term instanceof ParsedFunction)
    		{
    			ParsedFunction termF = (ParsedFunction) term;
    			if(termF.head.equals("<="))
    			{
    				ParsedTerm head = termF.parameters.getFirst();
    				if (head instanceof ParsedFunction)
    				{
    					ParsedFunction headF = (ParsedFunction) head;
    					
    					//next rule (holds(...,T+1)
    					if (headF.head.equals("holds"))
    					{
  							LinkedList<String> rememberTimePredicates = new LinkedList<String>();
  							for (ParsedTerm part : termF.parameters)
							{
								if (part instanceof ParsedFunction)
								{
									ParsedFunction partF = (ParsedFunction) part;
									if (partF.head.equals("not") && partF.parameters.getFirst() instanceof ParsedFunction)
									{
										part = partF.parameters.getFirst();
									}
									ParsedTerm partLast = ((ParsedFunction) part).parameters.getLast();
									if (partLast instanceof ParsedConstant && (((ParsedConstant)partLast).name.equals(timeDenoter)  || ((ParsedConstant)partLast).name.equals(timeDenoter+"+1") ))
									{
										rememberTimePredicates.add(((ParsedFunction)part).head);
										((ParsedFunction)part).parameters.removeLast();
									}
										
									
								}
							}
							
							for (int i = 1; i <=maxTimeSteps; i++)
							{
								ParsedFunction newRule = new ParsedFunction("<=",new LinkedList<ParsedTerm>());
								int j = 1;
								for (ParsedTerm part : termF.parameters)
								{
									if (part instanceof ParsedFunction)
									{
										Boolean negation = false;
										ParsedFunction partF = (ParsedFunction) part;
										if (partF.head.equals("not") && partF.parameters.getFirst() instanceof ParsedFunction)
										{
											partF = (ParsedFunction) partF.parameters.getFirst();
											negation = true;
										}
										if( rememberTimePredicates.contains(partF.head))
										{	
											
											
											ParsedFunction newPart = null;
											if (j == 1)
												newPart = new ParsedFunction(partF.head+(i+1),partF.parameters);
											else
												newPart = new ParsedFunction(partF.head+i,partF.parameters);
											if (negation)
											{
												ParsedFunction newPartN = new ParsedFunction("not",new LinkedList<ParsedTerm>());
												newPartN.parameters.add(newPart);
												newRule.parameters.add(newPartN);
											}
											else
												newRule.parameters.add(newPart);
										}
										else
											newRule.parameters.add(part);
										j++;
									}

								}
								newRules.add(newRule);
							}
    						
    						
    					}
    					// x(...,T), x!=holds
    					else 
    					{
    						ParsedTerm headLast = headF.parameters.getLast();
    						
    						if (headLast instanceof ParsedConstant && ((ParsedConstant) headLast).name.equals(timeDenoter))
    						{
    							LinkedList<String> rememberTimePredicates = new LinkedList<String>();
    							for (ParsedTerm part : termF.parameters)
    							{
    								if (part instanceof ParsedFunction)
    								{
    									ParsedFunction partF = (ParsedFunction) part;
    									if (partF.head.equals("not") && partF.parameters.getFirst() instanceof ParsedFunction)
    									{
    										part = partF.parameters.getFirst();
    									}
    									ParsedTerm partLast = ((ParsedFunction) part).parameters.getLast();
    									if (partLast instanceof ParsedConstant && ((ParsedConstant)partLast).name.equals(timeDenoter))
    									{
    										rememberTimePredicates.add(((ParsedFunction)part).head);
    										((ParsedFunction)part).parameters.removeLast();
    									}
    										
    									
    								}
    							}
    							
    							for (int i = 1; i <=maxTimeSteps; i++)
    							{
    								ParsedFunction newRule = new ParsedFunction("<=",new LinkedList<ParsedTerm>());
    								for (ParsedTerm part : termF.parameters)
    								{
    									if (part instanceof ParsedFunction)
    									{
    									
											Boolean negation = false;
											ParsedFunction partF = (ParsedFunction) part;
											if (partF.head.equals("not") && partF.parameters.getFirst() instanceof ParsedFunction)
											{
												partF = (ParsedFunction) partF.parameters.getFirst();
												negation = true;
											}
	    									if( rememberTimePredicates.contains(partF.head))
	    									{	
	    										
	    										ParsedFunction newPart = new ParsedFunction(partF.head+i,partF.parameters);
	    										if (negation)
												{
													ParsedFunction newPartN = new ParsedFunction("not",new LinkedList<ParsedTerm>());
													newPartN.parameters.add(newPart);
													newRule.parameters.add(newPartN);
												}
												else
													newRule.parameters.add(newPart);
	    									}
	    									else
	    										newRule.parameters.add(part);
    									}
    								}
    								newRules.add(newRule);
    							}
    						}
    						else
    							newRules.add(term);
    					}
    				}
    				else  // can't have a T in head and Body because head is a Constant
    					newRules.add(term);
    			}
    			else
    			{
    				ParsedTerm lastElement = termF.parameters.getLast();

    				//change of init predicates
    				if (termF.head.equals("holds") &&((ParsedConstant) lastElement).name.equals("1"))
    				{
    					
    					ParsedFunction newFun = new ParsedFunction(termF.head+"1",new LinkedList<ParsedTerm>());
    					termF.parameters.removeLast();
    					newFun.parameters.addAll(termF.parameters);
    					newRules.add(newFun);
    				}
    				else
    					newRules.add(term);
    			}
    			
    		}
    		else
    			newRules.add(term);
    	}
    	
    	return newRules;
    }
    
    protected void eleminateHolds(LinkedList<ParsedTerm> rules)
    {
    	for (ParsedTerm term : rules)
    	{
    		if (term instanceof ParsedFunction)
    		{
    			ParsedFunction termF = (ParsedFunction) term;
    			if (termF.head.equals("<="))
    			{
    				for (ParsedTerm part : termF.parameters )
    				{
    					if (part instanceof ParsedFunction && ((ParsedFunction)part).head.equals("not"))
    						part = ((ParsedFunction)part).parameters.getFirst();
    					if (part instanceof ParsedFunction && ((ParsedFunction)part).head.equals("holds"))
    						reformHold((ParsedFunction) part);
    				}
    			}
    			else if (termF.head.equals("holds"))
    			{
    				reformHold(termF);
    			}
    		}

    	}
    }
    
    protected void reformHold (ParsedFunction fun)
    {
    	ParsedTerm first = fun.parameters.getFirst();

    	if (first instanceof ParsedFunction)
    	{
    		fun.head = ((ParsedFunction) first).head;
    		fun.parameters.addAll(1,((ParsedFunction) first).parameters);
    		fun.parameters.removeFirst();
    	}

    	else if (first instanceof ParsedConstant)
    	{
    		fun.head = ((ParsedConstant) first).name;
    		fun.parameters.removeFirst();
    	}
    	else if (first instanceof ParsedVariable)
    	{
    		System.err.println("ERROR: Something went wrong with reformHold()!");
    	}

    }
    
    
    // prints a function head in ASP syntax
    protected void printFunctionHead(String head, PrintStream out) {
        if (head.toString().equals("#cumulative "+timeDenoter))
        	   out.print(head);
        else
     	   out.print(replaceSpecialChars(head));
    }
    
    // @overwrite
    // prints a constant in ASP syntax
    protected void printConstant(ParsedConstant constant, PrintStream out) {
       if (constant.toString().equals(timeDenoter+"+1"))
       	   out.print(constant);
       else
    	   out.print(replaceSpecialChars(constant.name));
    }
    
    
    // prints a relation, appending a state parameter to each relation
    protected void printRelation(ParsedTerm term, PrintStream out) {
        if (term instanceof ParsedFunction) {
            ParsedFunction function = (ParsedFunction) term;
            if ("role".equals(function.head))
                // handle "role" specially to allow not adding the state denoter
                printRole(function, out);
            else if ("init".equals(function.head))
                // handle "init" specially to allow not adding the state denoter
                printInit(function, out);
            else if ("not".equals(function.head))
                // handle "not" specially to allow translation to an efficient implementation
                printNot(function, out);
            else if ("distinct".equals(function.head))
                // handle "distinct" specially to allow translation to an efficient implementation
                printDistinct(function, out);
            else if ("or".equals(function.head))
                // handle "or" specially to allow translation to an efficient implementation
                printOr(function, out);
            else if ("equals".equals(function.head))
            	// hanlde "equals"
            	printEquals(function,out);
            else
                // print function including additional parameter <stateDenoter>
                printRelationPlusState(function, out);
        } else if (term instanceof ParsedConstant)
            // print zero-arity relation with one parameter denoting the state
            printConstantPlusState((ParsedConstant) term, out);
        else if (term instanceof ParsedVariable)
            throw new Error("Fact is Variable: "+(ParsedVariable)term+"; expected ParsedFunction or ParsedConstant");
        else
            throw new Error("ParsedTerm has unknown type: "+term.getClass()+"; expected ParsedFunction or ParsedConstant");
    }
    
    // prints a function, adding parameter <stateDenoter>
    protected void printRelationPlusState(ParsedFunction function, PrintStream out) {
    	printFunctionHead(function.head, out);
       
        int size = function.parameters.size();
        if (size != 0)
        	out.print("(");
        ParsedTerm t = null;
        for(int i=0; i<function.parameters.size(); i++)
        {
        	t = function.parameters.get(i);
        	printTerm(t, out);
            if (i!= size-1) 
            	out.print(", ");
        }
       // for (ParsedTerm t: function.parameters) {
       //     printTerm(t, out);
       //     out.print(", ");
       // }
        if (size != 0)
        	out.print(")");
    }
    
    // prints a function, not adding another parameter
    protected void printRelationWithoutState(ParsedFunction function, PrintStream out) {
    	printFunctionHead(function.head, out);
        if (function.parameters.size() > 0) {
            out.print("(");
            printTerm(function.parameters.get(0), out);
            for (int i = 1; i < function.parameters.size(); i++) {
                out.print(", ");
                printTerm(function.parameters.get(i), out);
            }
            if (function.parameters.size() > 0)
                out.print(")");
        }
    }
    
    // prints a constant as a relation, adding parameter <stateDenoter>
    protected void printConstantPlusState(ParsedConstant constant, PrintStream out) {
    	printFunctionHead(constant.name, out);
  //      out.print("("+timeDenoter+")");
    }
    
    // prints a relation head in Prolog syntax, prepending the ggp_-prefix
  //  protected void printRelationHead(String head, PrintStream out) {
  //      out.print("ggp_"+replaceSpecialChars(head));
  //  }
   
    
    // prints the special role relation as a normal function with no additional state denoter
    protected void printRole(ParsedFunction function, PrintStream out) {
        printRelationWithoutState(function, out);
    }
    
    // prints the special init relation as a normal function with no additional state denoter
    protected void printInit(ParsedFunction function, PrintStream out) {
        printRelationWithoutState(function, out);
    }
    
    // prints the special not relation as the "not"-operator
    protected void printNot(ParsedFunction function, PrintStream out) {
        out.print("not");
        // enclose parameters in parentheses if more or less than one
        out.print(function.parameters.size() != 1 ? "(" : " ");
        if (function.parameters.size() > 0)
            printRelation(function.parameters.get(0), out);
        for (int i = 1; i < function.parameters.size(); i++) {
            out.print(", ");
            printRelation(function.parameters.get(i), out);
        }
        if (function.parameters.size() > 1)
            out.print(")");
    }
    
    // prints the special distinct relation as the "!="-operator
    protected void printDistinct(ParsedFunction function, PrintStream out) {
        if (function.parameters.size() == 2) {
            printTerm(function.parameters.get(0), out);
            out.print(" != ");
            printTerm(function.parameters.get(1), out);
        } else {
            // enclose parameters in parentheses if more or less than two
            out.print("!=(");
            if (function.parameters.size() > 0)
                printTerm(function.parameters.get(0), out);
            for (int i = 1; i < function.parameters.size(); i++) {
                out.print(", ");
                printTerm(function.parameters.get(i), out);
            }
            out.print(")");
        }
    }
    
    // prints the special or relation as the ";"-operator
    protected void printOr(ParsedFunction function, PrintStream out) {
        if (function.parameters.size() > 1)
            out.print("(");
        if (function.parameters.size() > 0)
            printRelation(function.parameters.get(0), out);
        for (int i = 1; i < function.parameters.size(); i++) {
            out.print(" ; ");
            printRelation(function.parameters.get(i), out);
        }
        if (function.parameters.size() > 1)
            out.print(")");
    }
    
    // prints the special equals relation as the "="-operator
    protected void printEquals(ParsedFunction function, PrintStream out) {
    	printTerm(function.parameters.getFirst(),out);
    	out.print("=");
    	printTerm(function.parameters.getLast(),out);

    }
}
