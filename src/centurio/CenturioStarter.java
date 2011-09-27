package centurio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
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
 * This class is responsible for starting and configure Centurio.
 *
 * @author Felix Maximilian M�ller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */


public class CenturioStarter {

	/**
	 * starts the game player and waits for messages from the game master <br>
	 * Command line options: [port]
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		HashMap<String, Object> configMap = new HashMap<String, Object>();
		FileReader fr = null;

		try {

			BufferedReader in = new BufferedReader(fr = new FileReader("./config.xml")); // ./config.xml

			String line;

			line = in.readLine();

			if(!"<config>".equals(line)) {

				System.err.println("Invalid config file.");
				System.exit(-1);

			}

			Pattern regex = Pattern.compile("^\t\\<(.+)\\>(.+)\\<\\/(.+)\\>$", Pattern.DOTALL);

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

			if((!"</config>".equals(line)) || ((line = in.readLine()) != null)) {

				System.err.println("Invalid config file.");
				System.exit(-1);

			}

		}

		catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error during reading the config file.");
		}

		finally {

			if (fr != null) {

				try {
					fr.close();
				}

				catch(IOException e) {
					System.err.println("Error during closing the config file.");
				}

			}

		}

		try {

			// After creation config map, start game player. 
			//
			GamePlayer gp = null;


			if(args.length == 1) {

				try {
					gp = new GamePlayer(Integer.parseInt(args[0]));
				}

				catch(NumberFormatException e) {

					System.err.println("Port argument is not a number.");
					System.exit(-1);

				}

			}

			else
				gp = new GamePlayer(((Float)configMap.get("port")).intValue());

			GamePlayer.setConfigMap(configMap);
			GamePlayer.saveGamePlayerObject(gp);
			gp.waitForExit();

		}

		catch(Exception ex) {

			ex.printStackTrace();
			System.exit(-1);

		}

	}

}
