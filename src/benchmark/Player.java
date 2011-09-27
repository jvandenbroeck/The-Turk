package benchmark;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

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
 * This class is responsible for starting Centurio for the benchmark.
 * @author Felix Maximilian M�ller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */

public class Player extends Thread {

	private HashMap<String, Object> configMap = new HashMap<String, Object>();

	public Player(HashMap<String, Object> configMap) {
		this.configMap = configMap;
	}

	/**
	 * starts the General Game Player (which you want to benchmark) for the benchmark
	 */
	public void run() {

		try {

			Class GamePlayer = Class.forName("centurio.GamePlayer");
			Object gp = GamePlayer.getConstructor(Integer.TYPE).newInstance(((Float)configMap.get("port")).intValue());
			Method method = GamePlayer.getMethod("setConfigMap", configMap.getClass());
			method.invoke(null, new Object[]{ configMap } );
			method = GamePlayer.getMethod("saveGamePlayerObject", gp.getClass());
			method.invoke(null,  new Object[]{ gp });
			method = GamePlayer.getMethod("waitForExit", null);
			method.invoke(gp, null);

		}

		catch(InvocationTargetException e) {
			System.err.println("(Player - run) error message: dynamic method - InvocationTargetException\n\n" + e.getMessage() + "\n\n" + e.toString());
		}
		catch(IllegalAccessException e) {
			System.err.println("(Player - run) error message: dynamic method - IllegalAccessException\n\n" + e.getMessage() + "\n\n" + e.toString());
		}
		catch(NoSuchMethodException e) {
			System.err.println("(Player - run) error message: dynamic method - NoSuchMethodException\n\n" + e.getMessage() + "\n\n" + e.toString());
		}
		catch(ClassNotFoundException e) {
			System.err.println("(Player - run) error message: dynamic class - ClassNotFoundException\n\n" + e.getMessage() + "\n\n" + e.toString());
		}
		catch(Exception ex) {

			ex.printStackTrace();
			System.exit(-1);

		}

	}

}
