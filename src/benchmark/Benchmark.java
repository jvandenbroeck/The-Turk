package benchmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Centurio a General Game Player
 *
 * Copyright (C) 2008 Felix Maximilian M�ller, Marius Schneider and Martin Wegner
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
 * This class is responsible for controlling the benchmark.
 * @author Felix Maximilian M�ller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */

public class Benchmark {

	/**
	 * starts the benchmark
	 * @param args
	 */
	public static void main(String[] args) {

		HashMap<String, Object> configMap = new HashMap<String, Object>();
		HashMap<String, String> benchmarkMap = new HashMap<String, String>();
		HashMap<String, Short> globalResultMap = new HashMap<String, Short>();
		FileReader fr = null;
		Pattern regex;
		String line;
		Short matchIDCounter = 1;
		Short matchCounter = 0;
		Short runs;
		Integer remotePlayerCounter, innerRoleCounter;
		Byte outerRoleCounter = 0;
		Byte indexCounter = 8;
		Byte remotePlayerMapIndex = 1;
		Byte maxRoles = 1;
		Byte passCounter = 1;
		Player player = null;
		Boolean firstGame = true;
		Set<String>  gameSet = new HashSet<String>();
		Set<String>  playerSet = new HashSet<String>();
		Set<String>  globalGameSet = new HashSet<String>();
		FileWriter plotFileWriter;
		

		try {

			BufferedReader in = new BufferedReader(fr = new FileReader("./benchmark.xml"));

			// begin of XML format prove

			line = in.readLine();

			if(!"<benchmark>".equals(line)) {

				System.err.println("Invalid benchmark file.");
				System.exit(-1);

			}

			line = in.readLine();

			while(true) {

				if(!"\t<pass>".equals(line)) {

					System.err.println("Invalid benchmark file.");
					System.exit(-1);

				}

				line = in.readLine();

				if(!"\t\t<config>".equals(line)) {

					System.err.println("Invalid benchmark file.");
					System.exit(-1);

				}

				regex = Pattern.compile("^\t\t\t\\<(.+)\\>(.+)\\<\\/(.+)\\>$", Pattern.DOTALL);

				while((line = in.readLine()) != null) {

					Matcher matcher = regex.matcher(line);

					if(matcher.matches() && matcher.group(1).equals(matcher.group(3))) {

						try {
							Float.valueOf(matcher.group(2));
						}

						catch(NumberFormatException e) {
							
							//System.err.println("Invalid config values.");
							//System.exit(-1);

						}

					}

					else
						break;

				}

				if((!"\t\t</config>".equals(line))) {

					System.err.println("Invalid benchmark file.");
					System.exit(-1);

				}

				line = in.readLine();

				if((!"\t\t<games>".equals(line))) {

					System.err.println("Invalid benchmark file.");
					System.exit(-1);

				}

				line = in.readLine();

				while(true) {

					if((!"\t\t\t<game>".equals(line))) {

						System.err.println("Invalid benchmark file.");
						System.exit(-1);

					}

					regex = Pattern.compile("^\t\t\t\t\\<(.+)\\>(.+)\\<\\/(.+)\\>$", Pattern.DOTALL);

					while((line = in.readLine()) != null) {

						Matcher matcher = regex.matcher(line);

						if(matcher.matches() && matcher.group(1).equals(matcher.group(3)))
							benchmarkMap.put(matcher.group(1), matcher.group(2));

						else
							break;

					}

					if(!new File("./kif/" + benchmarkMap.get("name") + ".kif").exists()) {

						System.err.println("The File " + "./kif/" + benchmarkMap.get("name") + ".kif not exist.");
						System.exit(-1);

					}

					remotePlayerCounter = (benchmarkMap.size() - 5);

					if((!"\t\t\t</game>".equals(line)) || remotePlayerCounter < 0) {

						System.err.println("Invalid benchmark file.");
						System.exit(-1);

					}

					line = in.readLine();

					if((!"\t\t</games>".equals(line)) && (!"\t\t\t<game>".equals(line))) {

						System.err.println("Invalid benchmark file.");
						System.exit(-1);

					}

					benchmarkMap.clear();

					if("\t\t</games>".equals(line))
						break;

				}

				line = in.readLine();

				if(!"\t</pass>".equals(line)) {

					System.err.println("Invalid benchmark file.");
					System.exit(-1);

				}

				line = in.readLine();

				if("</benchmark>".equals(line))
					break;

			}

			if((line = in.readLine()) != null) {

				System.err.println("Invalid benchmark file.");
				System.exit(-1);

			}

			// end of XML format prove

			in.close();

			try {
				new FileWriter("./visits.log", false); // delete visits.log
			}

			catch(IOException e) {
				System.err.println("(Benchmark - main) error message: write gameDescription - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());				
			}

			in = new BufferedReader(fr = new FileReader("./benchmark.xml"));
			FileWriter fw = new FileWriter("./benchmark.log", false);
			line = in.readLine();
			line = in.readLine();

			while(true) {

				line = in.readLine();
				regex = Pattern.compile("^\t\t\t\\<(.+)\\>(.+)\\<\\/(.+)\\>$", Pattern.DOTALL);

				while((line = in.readLine()) != null) {

					Matcher matcher = regex.matcher(line);

					if(matcher.matches() && matcher.group(1).equals(matcher.group(3))) {
										
							try {
								configMap.put(matcher.group(1), Float.valueOf(matcher.group(2)));
							}

							catch(NumberFormatException e) {
								
								configMap.put(matcher.group(1), matcher.group(2));
								// System.err.println("Invalid config values.");
								// System.exit(-1);

							}
							
					}
					else
						break;

				}

				if(firstGame) {

					player = new Player(configMap);
					player.setPriority(Thread.MAX_PRIORITY);
					player.start();
					firstGame = false;

				}

				else {

					try {

						Class GamePlayer = Class.forName("centurio.GamePlayer");
						Method method = GamePlayer.getMethod("setConfigMap", configMap.getClass());
						method.invoke(null, new Object[]{ configMap } );
					
					}

					catch(InvocationTargetException e) {
						System.err.println("(Benchmark - main) error message: dynamic method - InvocationTargetException\n\n" + e.getMessage() + "\n\n" + e.toString());
					}
					catch(IllegalAccessException e) {
						System.err.println("(Benchmark - main) error message: dynamic method - IllegalAccessException\n\n" + e.getMessage() + "\n\n" + e.toString());
					}
					catch(NoSuchMethodException e) {
						System.err.println("(Benchmark - main) error message: dynamic method - NoSuchMethodException\n\n" + e.getMessage() + "\n\n" + e.toString());
					}
					catch(ClassNotFoundException e) {
						System.err.println("(Benchmark - main) error message: dynamic class - ClassNotFoundException\n\n" + e.getMessage() + "\n\n" + e.toString());
					}

				}

				line = in.readLine();
				line = in.readLine();

				while(true) {

					regex = Pattern.compile("^\t\t\t\t\\<(.+)\\>(.+)\\<\\/(.+)\\>$", Pattern.DOTALL);

					while((line = in.readLine()) != null) {

						Matcher matcher = regex.matcher(line);

						if(matcher.matches() && matcher.group(1).equals(matcher.group(3)))
							benchmarkMap.put(matcher.group(1), matcher.group(2));

						else
							break;

					}

					if(maxRoles < Byte.parseByte(benchmarkMap.get("roles")))
						maxRoles = Byte.parseByte(benchmarkMap.get("roles"));

					remotePlayerCounter = (benchmarkMap.size() - 5);
					runs = Short.parseShort(benchmarkMap.get("runs"));

					String[] config = new String[remotePlayerCounter * 2 + 8];
					config[1] = "./kif/" + benchmarkMap.get("name") + ".kif";
					config[2] = benchmarkMap.get("startclock");
					config[3] = benchmarkMap.get("playclock");
					config[4] = "-remote";
					config[6] = "localhost";
					config[7] = Integer.toString(((Float)configMap.get("port")).intValue());
					playerSet.add("remote(" + config[6] + ":" + config[7] + ")");

					for(int i = remotePlayerCounter; i > 0; i = i - 2) {

						config[indexCounter++] = "-remote";
						indexCounter++;
						config[indexCounter++] = benchmarkMap.get("remoteip" + remotePlayerMapIndex);
						config[indexCounter++] = benchmarkMap.get("remoteport" + remotePlayerMapIndex++);
						playerSet.add("remote(" + config[indexCounter - 2] + ":" + config[indexCounter - 1] + ")");

					}

					for(; runs > 0; runs--) {

						matchCounter++;
						config[0] = "benchmarkMatch" + matchIDCounter++ + "_" + benchmarkMap.get("name");
						config[5] = Integer.toString(outerRoleCounter % Integer.parseInt(benchmarkMap.get("roles")) + 1);
						indexCounter = 8;
						innerRoleCounter = outerRoleCounter++ % Integer.parseInt(benchmarkMap.get("roles")) + 1;

						for(int i = remotePlayerCounter; i > 0; i = i - 2) {
							indexCounter++;
							config[indexCounter++] = Integer.toString(innerRoleCounter++ % Integer.parseInt(benchmarkMap.get("roles")) + 1);
							indexCounter++;
							indexCounter++;
						}

						benchmark.tud.gamecontroller.GameController.main(config);
						System.gc();

						try {
							Thread.sleep(10000);
						}

						catch(InterruptedException e) {
							System.err.println("(Benchmark - main) error message: sleeping - InterruptedException\n\n" + e.getMessage() + "\n\n" + e.toString());
						}

					}

					line = in.readLine();
					remotePlayerMapIndex = 1;
					indexCounter = 8;
					outerRoleCounter = 0;
					gameSet.add(benchmarkMap.get("name"));
					globalGameSet.add(benchmarkMap.get("name"));
					benchmarkMap.clear();

					if("\t\t</games>".equals(line))
						break;

				}

				String[] keyArray = configMap.keySet().toArray(new String[configMap.keySet().size()]);
				fw.write("Config:\n");

				for(Integer i = 0; i < keyArray.length; i++) {
					fw.write(keyArray[i] + ": " + configMap.get(keyArray[i]).toString() + "\n");
				}

				fw.write("\nInsgesamt:\n");
				fw.write("Spiele: " + matchCounter + "\n");

				String[] playerArray = playerSet.toArray(new String[playerSet.size()]);

				for(Short i = 0; i < playerArray.length; i++) {

					if(benchmark.tud.gamecontroller.GameController.getResultMap().containsKey(playerArray[i] + "timeOuts"))
						fw.write(playerArray[i] + " Time outs: " + benchmark.tud.gamecontroller.GameController.getResultMap().get(playerArray[i] + "timeOuts") + "\n");
					else
						fw.write(playerArray[i] + " Time outs: 0\n");

					if(benchmark.tud.gamecontroller.GameController.getResultMap().containsKey(playerArray[i] + "illegalMoves"))
						fw.write(playerArray[i] + " Illegal moves: " + benchmark.tud.gamecontroller.GameController.getResultMap().get(playerArray[i] + "illegalMoves") + "\n");
					else
						fw.write(playerArray[i] + " Illegal moves: 0\n");

				}

				plotFileWriter = new FileWriter("./plot/config" + passCounter.toString() + ".dat", false);

				if(benchmark.tud.gamecontroller.GameController.getResultMap().containsKey("-1")) {

					fw.write("Verloren: " + benchmark.tud.gamecontroller.GameController.getResultMap().get("-1") + "\n");
					plotFileWriter.write("lose\t" + benchmark.tud.gamecontroller.GameController.getResultMap().get("-1") + "\n");

				}

				else {

					fw.write("Verloren: 0\n");
					plotFileWriter.write("lose\t0\n");

				}


				if(benchmark.tud.gamecontroller.GameController.getResultMap().containsKey("0")) {

					fw.write("Unentschieden: " + benchmark.tud.gamecontroller.GameController.getResultMap().get("0") + "\n");
					plotFileWriter.write("draw\t" + benchmark.tud.gamecontroller.GameController.getResultMap().get("0") + "\n");

				}

				else {

					fw.write("Unentschieden: 0\n");
					plotFileWriter.write("draw\t0\n");

				}

				for(Byte i = 1; i <= maxRoles; i++) {

					if(benchmark.tud.gamecontroller.GameController.getResultMap().containsKey(i.toString()))
						fw.write("Rank " + i + ": " + benchmark.tud.gamecontroller.GameController.getResultMap().get(i.toString()) + "\n");
					else
						fw.write("Rank " + i + ": 0\n");

				}

				for(Byte i = maxRoles; i > 0; i--) {

					if(benchmark.tud.gamecontroller.GameController.getResultMap().containsKey(i.toString()))
						plotFileWriter.write("rank" + i + "\t" + benchmark.tud.gamecontroller.GameController.getResultMap().get(i.toString()) + "\n");
					else
						plotFileWriter.write("rank" + i + "\t0\n");

				}

				plotFileWriter.flush();
				plotFileWriter.close();
				String[] gameArray = gameSet.toArray(new String[gameSet.size()]);

				for(Short i = 0; i < gameArray.length; i++) {

					fw.write("\n" + gameArray[i] + ":\n");
					fw.write("Spiele: " + benchmark.tud.gamecontroller.GameController.getResultMap().get(gameArray[i] + "matches") + "\n");

					for(Short k = 0; k < playerArray.length; k++) {

						if(benchmark.tud.gamecontroller.GameController.getResultMap().containsKey(gameArray[i] + playerArray[k] + "timeOuts"))
							fw.write(playerArray[k] + " Time outs: " + benchmark.tud.gamecontroller.GameController.getResultMap().get(gameArray[i] + playerArray[k] + "timeOuts") + "\n");
						else
							fw.write(playerArray[k] + " Time outs: 0\n");

						if(benchmark.tud.gamecontroller.GameController.getResultMap().containsKey(gameArray[i] + playerArray[k] + "illegalMoves"))
							fw.write(playerArray[k] + " Illegal moves: " + benchmark.tud.gamecontroller.GameController.getResultMap().get(gameArray[i] + playerArray[k] + "illegalMoves") + "\n");
						else
							fw.write(playerArray[k] + " Illegal moves: 0\n");

					}

					for(Byte j = 0; j < 101; j++) {

						if(benchmark.tud.gamecontroller.GameController.getResultMap().containsKey(gameArray[i] + j.toString())) {

							fw.write(j + " Punkte: " + benchmark.tud.gamecontroller.GameController.getResultMap().get(gameArray[i] + j.toString()) + "\n");
							globalResultMap.put("config" + passCounter + gameArray[i] + j, benchmark.tud.gamecontroller.GameController.getResultMap().get(gameArray[i] + j.toString()));

						}

						else
							globalResultMap.put("config" + passCounter + gameArray[i] + j, Short.parseShort("0"));

					}

				}

				fw.write("\n");
				benchmark.tud.gamecontroller.GameController.clearResultMap();
				configMap.clear();
				gameSet.clear();
				playerSet.clear();
				matchCounter = 0;
				fw.flush();
				passCounter++;
				line = in.readLine();
				line = in.readLine();

				if("</benchmark>".equals(line))
					break;

			}

			fw.close();
			in.close();

		}

		catch(IOException e) {
			System.err.println("(Benchmark - main) error message: read benchmark file and write the results - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
		}

		finally {

			if (fr != null) {

				try {
					fr.close();
				}

				catch(IOException e) {
					System.err.println("Error during closing the benchmark file.");
				}

			}

		}

		Boolean append = false;
		String[] gameArray = globalGameSet.toArray(new String[globalGameSet.size()]);

		try {

			for(Short i = 0; i < gameArray.length; i++) {

				plotFileWriter = new FileWriter("./plot/" + gameArray[i] + ".dat", false);
				plotFileWriter.write("Points");
				Set<Byte> validConfigSet = new HashSet<Byte>();

				for(Byte k = 1; k < passCounter; k++) {

					if(globalResultMap.containsKey("config" + k.toString() + gameArray[i] + "0")) {

						plotFileWriter.write("\tConfig" + k);
						validConfigSet.add(k);

					}
							
				}

				plotFileWriter.write("\n");
				Byte[] validConfigArray = validConfigSet.toArray(new Byte[validConfigSet.size()]);

				for(Byte j = 0; j < 101; j++) {

					Boolean found = false;

					for(Byte k = 0; k < validConfigArray.length; k++) {

						if(globalResultMap.get("config" + validConfigArray[k].toString() + gameArray[i] + j.toString()) != 0) {

							found = true;
							break;

						}

					}

					if(found) {

						plotFileWriter.write(j.toString());

						for(Byte k = 0; k < validConfigArray.length; k++) {
								plotFileWriter.write("\t" + globalResultMap.get("config" + validConfigArray[k].toString() + gameArray[i] + j.toString()));
						}

						plotFileWriter.write("\n");

					}

				}

				plotFileWriter.flush();
				plotFileWriter.close();

				plotFileWriter = new FileWriter("./plot/gnuplot.plt", append);
				append = true;
				plotFileWriter.write("set terminal png transparent truecolor nocrop enhanced font arial 10 size 800,600\n");
				plotFileWriter.write("set title 'Benchmark Ergebnis - " + gameArray[i].replace('_', ' ') + "'\n");
				plotFileWriter.write("set key outside\n");
				plotFileWriter.write("set xtics out nomirror\n");
				plotFileWriter.write("set xlabel 'Punkte'\n");
				plotFileWriter.write("set ylabel 'Anzahl'\n");
				plotFileWriter.write("set style histogram clustered gap " + validConfigArray.length + "\n");
				plotFileWriter.write("set style data histograms\n");
				plotFileWriter.write("plot '" + gameArray[i] + ".dat' using 2:xtic(1) title col fill transparent pattern " + validConfigArray[0]);

				for(Byte k = 1; k < validConfigArray.length; k++) {
					plotFileWriter.write(", '' using " + (k + 2) + " title col fill transparent pattern " + validConfigArray[k]);
				}

				plotFileWriter.write("\n");
				plotFileWriter.write("max = GPVAL_Y_MAX + 1\n");
				plotFileWriter.write("if(max > 4) set yrange[0:max]\n");
				plotFileWriter.write("if(max < 5) set yrange[0:5]\n");
				plotFileWriter.write("set output '" + gameArray[i] + ".png'\n");
				plotFileWriter.write("replot\n");
				plotFileWriter.write("\nreset\n\n");
				plotFileWriter.flush();
				plotFileWriter.close();

			}

			plotFileWriter = new FileWriter("./plot/gnuplot.plt", true);
			plotFileWriter.write("set terminal png transparent truecolor nocrop enhanced font arial 10 size 800,600\n");
			plotFileWriter.write("set title 'Benchmark Ergebnis - Insgesamt'\n");
			plotFileWriter.write("set key outside\n");
			plotFileWriter.write("set xtics out nomirror\n");
			plotFileWriter.write("set ylabel 'Anzahl'\n");
			plotFileWriter.write("set style histogram clustered gap " + (passCounter - 1) + "\n");
			plotFileWriter.write("set style data histograms\n");
			plotFileWriter.write("plot 'config1.dat' using 2:xtic(1) title 'Config 1' fill transparent pattern 1");

			for(Byte k = 2; k < passCounter; k++) {
				plotFileWriter.write(", 'config" + k + ".dat' using 2 title 'Config " + k + "' fill transparent pattern " + k);
			}

			plotFileWriter.write("\n");
			plotFileWriter.write("max = GPVAL_Y_MAX + 1\n");
			plotFileWriter.write("if(max > 4) set yrange[0:max]\n");
			plotFileWriter.write("if(max < 5) set yrange[0:5]\n");
			plotFileWriter.write("set output 'insgesamt.png'\n");
			plotFileWriter.write("replot\n");
			plotFileWriter.write("\nreset\n\n");
			plotFileWriter.flush();
			plotFileWriter.close();

		}

		catch(IOException e) {
			System.err.println("(Benchmark - main) error message: write plot file - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
		}

		System.exit(0);

	}

}
