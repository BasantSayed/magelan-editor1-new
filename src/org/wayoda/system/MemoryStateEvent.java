/* This File is part of the wayoda-project                                    */
/* Filename : MemoryStateEvent.java                                           */
/* Copyright (C) <2003>  <Eberhard Fahle>
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

import java.util.*;

/**
 * An event delivered by the MemoryMonitor when the state 
 * of the current memory usage changes.
 * <p>
 * These events are received by classes that implement the MemoryStateListener-interface.
 * The only information besides the source of the event is on of the constants
 * for the memory-status i this class.
 * <p>
 * @author Eberhard Fahle
 * @version 0.0
 */
public class MemoryStateEvent extends EventObject {
		/** The localized identifier for the event */
		private static String info=null;
		/** The localized message for reporting an error */
		private static String errorMessage=null;
		/** The localized message for reporting a warning */
		private static String warningMessage=null;
		/** The localized message for reporting normal memory condition */
		private static String safeMessage=null;

		/**
		 * Constant for the current memory usage state: The amount of memory allocated
		 * for objects is not exceeding the warning limit. Memory usage is in normal regions.
		 */
		public static final int SAFE=0;
		/**
		 * Constant for the current memory usage state: The amount of memory allocated
		 * for objects excceds the warning limit. 
		 */
		public static final int LOW_WARNING=-1;
		/**
		 * Constant for the current memory usage state: The amount of memory allocated
		 * for objects excceds the error limit. 
		 */
		public static final int LOW_ERROR=-2;
		/**
		 * Constant for the current memory usage state: There are no reports yet.
		 * This constant is used by the MemoryMonitor so it can detect, that no state-events
		 * have been sent yet.
		 */
		public static final int NO_REPORT=Integer.MIN_VALUE;

		/** 
		 * The value for the events delivered by the MemoryMonitor.
		 * This will be one of the constants for memory-status defined in this class.
		 */
		private int status;

		/**
		 * Creates a new MemoryStateEvent.
		 * @param source the source of the event
		 * @param status the current status of the memory usage reported by the MemoryMonitor.
		 * @throws IllegalArgumentException if status is not one of the constants:<code><pre>
		 *   LOW_ERROR - the total amount of memory used for allocated objects exceeds the errorlimit
		 *   LOW_WARNING - the total amount of memory used for allocated objects exceeds the warnlimit
		 *   SAFE - the total amount of memory used for allocated objects does not exceed the warning limit
		 * </pre></code>
		 */
		public MemoryStateEvent(Object source, int status) {
				super(source);
				if(!(status==LOW_ERROR
						 || status==LOW_WARNING
						 || status==SAFE))
						throw new IllegalArgumentException("Unknown memory-status constant : "+status); //$NON-NLS-1$
				this.status=status;
		}

		/**
		 * Gets the current status of the memory as reported by the MemoryMonitor.
		 * @return int one of the constants:<code><pre>
		 *   LOW_ERROR - the total amount of memory used for allocated objects exceeds the errorlimit
		 *   LOW_WARNING - the total amount of memory used for allocated objects exceeds the warnlimit
		 *   SAFE - the total amount of memory used for allocated objects does not exceed the warning limit
		 * </pre></code>
		 */
		public int getStatus() {
				return status;
		}

		/**
		 * Retrieve the localized messages to be printed from the ResourceBundle.
		 */
		private synchronized void loadResources() {
				try {
						ResourceBundle rb=ResourceBundle.getBundle("org/wayoda/system/MemoryMonitorResources"); //$NON-NLS-1$
						info=rb.getString("event.state.info"); //$NON-NLS-1$
						errorMessage=rb.getString("event.state.error"); //$NON-NLS-1$
						warningMessage=rb.getString("event.state.warning"); //$NON-NLS-1$
						safeMessage=rb.getString("event.state.safe"); //$NON-NLS-1$
				}
				catch(MissingResourceException mre) {
						System.out.println(mre);
				}
		}

		/**
		 * Returns a localized String representation of this event.
		 * @return String A String representation using a localized message that reflects
		 * the current memory state reported in the event:
		 */
		public String toString() {
				if(info==null) {
						//this happens when to String is called for the first time
						loadResources();
				}
				String retval=null;
				if(status==SAFE)
						retval=new String(info+safeMessage);
				else if(status==LOW_WARNING)
						retval=new String(info+warningMessage);
				else if(status==LOW_ERROR)
						retval=new String(info+errorMessage);
				return retval;
		}
}












