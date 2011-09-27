package benchmark.tud.gamecontroller.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.Formatter;

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
 * Part of the GameMaster from the University Dresden.
 *
 * @author University Dresden
 * @version 1.0
 */

public class PlainTextLogFormatter extends Formatter {
	private static final DateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
	private static final String lineSep = System.getProperty("line.separator");
	
	public synchronized String format(LogRecord record) {
		return record.getLevel().getName()+"("+format.format(new Date(record.getMillis()))+")"+": "+record.getMessage()+lineSep;
	}
}
