/* This File is part of the wayoda-project                                    */
/* Filename : MemoryStateListener.java                                        */
/* Copyright (C) <2002>  <Eberhard Fahle>
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA  */

package org.wayoda.system;


/**
 * An Interface to be implemented by all components
 * interested in receiving MemoryStateEvents from a MemoryMonitor.
 * <p>
 * @author Eberhard Fahle
 * @version 0.1
 */
public interface MemoryStateListener {
		/**
		 * Invoked when the state of the memory usage in the JVM
		 * changes.
		 * @param mse the MemoryStateEvent sent by the MemoryMonitor.
		 */
		public void update(MemoryStateEvent mse);
}

		


















