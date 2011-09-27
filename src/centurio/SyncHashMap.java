package centurio;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * Centurio a General Game Player
 *
 * Copyright (C) 2009 Felix Maximilian Möller, Marius Schneider and Martin Wegner
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
 * This class implements a hashmap which is synchronized. This is necessary for a
 * multithreaded artificial intelligence with a hashmap.
 *
 * It is a thread which is always running and expand the game tree (respectively the
 * informations about the game tree).
 *
 * @author Felix Maximilian Möller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 2.1
 */

@SuppressWarnings("serial")
final class SyncHashMap<K, V> extends HashMap<K, V>
{

	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
	private final ReadLock readLock = readWriteLock.readLock();
	private final WriteLock writeLock = readWriteLock.writeLock();
	private final int maxSize;

	/**
	 * Constructor
	 * @param maxSize
	 */
	SyncHashMap(final int maxSize)
	{
		super((int) (maxSize * (1 / 0.75f) + 100), 0.75f);
		this.maxSize = maxSize;
	}

	/**
	 * Returns the value to which the specified key is mapped,
	 * or null if this map contains no mapping for the key.
	 * This version guarantees that the access is limited to one thread.
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	final V syncGet(final K key)
	{
		try {
			readLock.lock();
			return get(key);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 * This version guarantees that the access is limited to one thread.
	 * @return the number of key-value mappings in this map
	 */
	final int syncSize() 
	{
		try {
			readLock.lock();
			return size();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Associates the specified value with the specified key in this map.
	 * If the map previously contained a mapping for the key, the old value is replaced.
	 * This version guarantees that the access is limited to one thread.
	 * @param key  key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 */
	final void syncPutMaxSizeControl(final K key, final V value) 
	{
		if (syncSize() > maxSize) {
			syncRemoveRandom();
		}
		try {
			writeLock.lock();
			put(key, value);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Associates the specified value with the specified key in this map.
	 * If the map previously contained a mapping for the key, the old value is replaced.
	 * This version guarantees that the access is limited to one thread.
	 * @param key  key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 */
	final void syncPut(final K key, final V value) 
	{
		try {
			writeLock.lock();
			put(key, value);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Removes all of the mappings from this map. The map will be empty after this call returns.
	 * This version guarantees that the access is limited to one thread.
	 */
	final void syncClear()
	{
		try {
			writeLock.lock();
			clear();
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Removes the mapping for the specified key from this map if present.
	 * This version guarantees that the access is limited to one thread.
	 * @param value key whose mapping is to be removed from the map
	 */
	final void syncRemove(final V value)
	{
		try {
			writeLock.lock();
			remove(value);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Removes the mapping for a random key from this map.
	 * This version guarantees that the access is limited to one thread.
	 */
	final void syncRemoveRandom()
	{
		try {
			writeLock.lock();
			final int random = new Random().nextInt(syncSize());
			final Iterator<K> iterator = keySet().iterator();
			for (int i = 0; i < random - 1; i++) {
				iterator.next();
			}
			remove(iterator.next());
		} finally {
			writeLock.unlock();
		}
	}

}
