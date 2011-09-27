package ASP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import centurio.GamePlayer;

public class ASPOracle extends Thread{
	
	private String gameDescription;
	private String matchID;
	private static boolean foundAnswer = false;
	private String[] doesArray = null;
	private int stepCounter = 0;
	private int maxSteps = 0;
	private boolean windows = false;
	private Process p = null;
	
	public ASPOracle(String description, String ID)
	{
		gameDescription = description;
		matchID = ID;
	}
	
	public void run() 
	{
		//find os
		String os = ManagementFactory.getOperatingSystemMXBean().getName().toUpperCase();
		if (os.contains("WINDOWS")) {
			windows = true;
		} else {
			new MemoryCheckThread(this).start();
		}
	
		
		String solver = "clingo".toLowerCase(); //possibilites: Clingo, DLV, Iclingo
		//TODO: Derzeit ist nur Clingo lauffähig!
		
		ASPEncodingConstructor ASPtranslator = new ASPEncodingConstructor(solver);
		try {
			ASPtranslator.translate(new StringReader(gameDescription), new PrintStream(new File("./ASPEncodings/" + matchID + solver +".pl")));
		} 
		catch (FileNotFoundException e) {
			System.err.println("Couldn't find File for ASP Encoding (ASPOracle)");
			e.printStackTrace();
		} 
		catch (IOException e) {
			System.err.println("IOException - wtf - (ASPOracle)");
			e.printStackTrace();
		} 
		catch (ASP.ASPTranslator.ParseException e) {
			System.err.println("ParseException while translating ASP to GDL (ASPOracle)");
			e.printStackTrace();
		}

		ProcessBuilder builder = null;
		if (windows)
		{
			builder = new ProcessBuilder("./ASPEncodings/" + solver + ".exe", "--shift", "--sat-p=true", matchID + solver + ".pl" ); 
		}
		else
		{
			builder = new ProcessBuilder("./" + solver, "--shift", "--sat-p=true", matchID + solver + ".pl" );
		}
	    

	    builder.directory(new File("./ASPEncodings/"));

		try {
			p = builder.start();
		} 
		catch (IOException e) {
			System.err.println("Couldn't start ASP System!");
			e.printStackTrace();
		} 

		//InputStream stdin = p.getErrorStream();
		InputStream stdin = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(stdin);
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		String asp = "";
		String models = "";

		try {
			int index = 0;
			while ( (line = br.readLine()) != null)
			{
				System.out.println(index + " : "+line);
				index++;
				if (index == 1)
					models = line;
				if (index == 2)
					asp = line;
			}
		} catch (IOException e) {
			System.err.println("Reading the Output of ASP-System went wrong (ASPOralce)");
			System.err.println("Most likely the ASP-System did not found a solution in the given time.");
			//e.printStackTrace();
		}

		try{
			if (p != null)
				p.destroy();			
		}
		catch(Exception e)
		{
			System.err.println("ASP aborted before finished!");
		}



		Pattern model = Pattern.compile("^Answer:\\s([0-9]+)$", Pattern.DOTALL);
		Matcher modelmatch = model.matcher(models);
		int modelNr = 0;
		if(modelmatch.matches()) {
			modelNr = Integer.valueOf(modelmatch.group(1));
		}
		if (asp!= null && modelNr == 1)
		{
			String[] AS = asp.split(" ");
			
			 doesArray = new String[AS.length]; 
			
			for (String a : AS)
			{
				Pattern regex = Pattern.compile("does\\((\\S+?),(\\S+),(\\d+)\\)", Pattern.DOTALL);
				Matcher matcher = regex.matcher(a);
				if(matcher.matches()) {
					//String role = matcher.group(1);
					String does = "("+ASPTranslator.resetSpecialChars(matcher.group(2)).replace("," , " ").replace(")", "").replace("(", " ")+")";
					if (!does.contains(" "))
					{
						does = does.substring(1, does.length()-1);
					}
					Integer step = Integer.valueOf(matcher.group(3));
					
					doesArray[step-1] = does;
										
				}
				else
					System.err.println("Something went wrong with Pattern (ASPOracle)");
			}
			stepCounter = 0;
			maxSteps = AS.length-1;
			foundAnswer = true;
			GamePlayer.wakeUp();
		}

	}

	public String askOracle()
	{
		if (stepCounter <= maxSteps)
		{
			stepCounter++;
			return doesArray[stepCounter-1];
			
		}
		else
			return null;
	}

	public final static boolean getFoundAnswer() {
		return foundAnswer;
	}

	public final static void resetFoundAnswer() {
		foundAnswer = false;
	}
	
	public final void destroyASP()
	{
		if (p != null) {
			p.destroy();
		}
	}
}
