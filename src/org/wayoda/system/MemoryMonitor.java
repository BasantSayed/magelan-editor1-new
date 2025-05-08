/* This File is part of the wayoda-project                                    */
/* Filename : MemoryMonitor.java                                              */
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

import java.util.ArrayList;

/**
 * This class monitors the memory usage of the JVM 
 * in which it is created.
 * <p>
 * The class creates a Thread that checks the current memory usage and reports
 * either a state via some constants, or absolute values to interested listeners.
 * There are three types of listeners that can register with this class.<br>
 * 1. A MemoryStateListener will receive a MemoryStateEvent when the memory usage
 * exceeds or is reset to one of the limits that are configured.<br>
 * 2. A MemoryValueListener will receive a MemoryValueEvent each time the 
 * thread reads new values.<br>
 * 3. A ChangeListener will receive a ChangeEvent when the configuration of the 
 * MemoryMonitor (delay,limits) was changed. 
 * <p>
 * The thread that checks the memory usage will be started when there is at least
 * one MemoryValueListener OR MemoryStateListener registered with the MemoryMonitor.
 * After the last on of this listeners is gone. The thread will be stopped again. 
 * <p>
 * The Class also provides two more static methods that can be used to convert
 * a value that is reported by this MemoryMonitor to a String that represents
 * that value as a megabyte or kilobyte value. These two methods have nothing
 * to do with the functionality of the monitor itself. There implemented here
 * because creating a new Class for these small methods seems not really neccessary.
 * @author Eberhard Fahle
 * @version 0.0
 */
public class MemoryMonitor implements Runnable {
		/**
		 * A constant for the default maximum amount of memory the JVM is 
		 * using for allocating objects. This is the maximum value
		 * <code>Runtime.totalMemeory() can grow to. This constant is currently 
		 * set to 64MB.
		 */
		private static final long DEFAULT_MAX_OBJECT_MEMORY=0x4000000;
		/** 
		 * If true monitor is started when the first listener registers,
		 * and stopped when the last one is removed. Default behaviour is
		 * autoStart==true
		 */
		private boolean autoStart=true;
		/** The Thread that will read the memory usage */
		private Thread monitor;
		/** 
		 * This boolean is true as long as the thread is allowed to run.
		 * This value could change when the thread is executing, so we make that volatile.
		 */
		private volatile boolean do_run=false; 
		/** 
		 * The time that the thread waits between updates of the memory usage 
		 * This value could change when the thread is executing, so we make that volatile.
		 */
		private volatile long delay;
		/** 
		 * the limit for reporting an error 
		 * This value could change when the thread is executing, so we make that volatile.
		 */
		private volatile long errorLimit;
		/** 
		 * the limit for clearing a pending error 
		 * This value could change when the thread is executing, so we make that volatile.
		 */
		private volatile long errorClearLimit;
		/** 
		 * the limit for reporting a warning 
		 * This value could change when the thread is executing, so we make that volatile.
		 */
		private volatile long warnLimit;
		/** 
		 * the limit for clearing a pending warning 
		 * This value could change when the thread is executing, so we make that volatile.
		 */
		private volatile long warnClearLimit;
		/**
		 * The last status that was reported to the status-listeners
		 */
		private volatile int lastState=MemoryStateEvent.NO_REPORT;

		/** the maximum memory the JVM will use */
		private long maxMemory;
		/** the maximum memory the JVM will use for  object allocation */
		private long objectLimit=DEFAULT_MAX_OBJECT_MEMORY;

		/** the list of listeners for the curent state of the memory usage */
		private ArrayList stateListenerList=new ArrayList(1);
		/** the list of listeners for the current values of the memory usage */
		private ArrayList valueListenerList=new ArrayList(1);
 		/** the list of listeners for changes in the current configuration of the memory monitor */
		private ArrayList changeListenerList=new ArrayList(1);
		/** 
		 * A listener for the current memory usage values that prints
		 * this information to System.out
		 */
		private ValuePrinter vprinter=null;
		/** 
		 * A listener for the current memory usage status that prints
		 * this information to System.out
		 */
		private StatePrinter sprinter=null;

		/**
		 * Creates a new MemoryMonitor. 
		 * @param delay The delay in seconds between updates of the memory usage 
		 * @param objectLimit The maximum size of the memory in megabytes the JVM will use
		 * for object allocating.
		 */
		public MemoryMonitor(int delay, int objectLimit) {
				this(delay,objectLimit,
						 (objectLimit*90)/100,
						 (objectLimit*85)/100,
						 (objectLimit*80)/100,
						 (objectLimit*75)/100);
		}

		/**
		 * Creates a new MemoryMonitor.
		 * @param delay The delay in seconds between updates of the memory usage 
		 * @param objectLimit The maximum size of the memory in megabytes the JVM will use
		 * for object allocating.
		 * @param errorLimit The limit in megabytes for reporting an error because of low memory 
		 * @param errorClearLimit The limit in megabytes for clearing the last error because of low memory
		 * @param warnLimit The limit in megabytes for reporting an warning because of low memory 
		 * @param warnClearLimit The in megabytes limit for clearing the last warning because of low memory
		 */
		public MemoryMonitor(int delay, int objectLimit,
												 int errorLimit, int errorClearLimit,
												 int warnLimit, int warnClearLimit) {
				this.delay=(long)delay*1000;
				this.objectLimit=(long)objectLimit*0x100000;
				this.errorLimit=(long)errorLimit*0x100000;
				this.errorClearLimit=(long)errorClearLimit*0x100000;
				this.warnLimit=(long)warnLimit*0x100000;
				this.warnClearLimit=(long)warnClearLimit*0x100000;
				maxMemory=Runtime.getRuntime().maxMemory();
		}

		/**
		 * Sets wether the thread should be started automaticly when at least one
		 * listener registers, and stopped when the last listener is gone, or starting/stopping
		 * the thread should be done manually.
		 * @param autoStart if true the monitor is started/stopped automaticly, if false
		 * starting and stopping should be done manually.
		 * You need to extend the MemoryManager to use this feature. The default behaviour
		 * is that the monitor starts/stop on listener registration/removal.
		 */
		protected void setAutoStartOnListener(boolean autoStart) {
				this.autoStart=autoStart;
		}

		/**
		 * Gets wether the thread is started automaticly when at least one
		 * listener registers, and stops when the last listener is gone, or starting/stopping
		 * the thread is done manually.
		 * @return boolean True if the monitor is started/stopped automaticly, False if 
		 * starting and stopping must be done manually.
		 * Default behaviour is AutoStartOnListener==TRUE.<br>
		 * (Only classes that extend the MemoryManager can change the default behaviour
		 * via setAutoStartOnListener(boolean).)
		 */
		public boolean getAutoStartOnListener() {
				return autoStart;
		}

		/**
		 * Starts the MemoryMonitor.
		 * As the monitor is automaticly started when at least one listener 
		 * registers, there actually should be no need to call this method.
		 * But for any classes that extend MemoryMonitor and have a special need to 
		 * start and stop the monitor manually we include it anyway.
		 */
		protected synchronized void startMonitor() {
				if(monitor==null) {
						monitor=new Thread(this);
						do_run=true;
						lastState=MemoryStateEvent.NO_REPORT;
						monitor.start();
				}
		}

		/**
		 * Stops the MemoryMonitor
		 * As the monitor is automaticly stopped when the last listener 
		 * is gone, there actually should be no need to call this method.
		 * But for any classes that extend MemoryMonitor and have a special need to 
		 * start and stop the monitor manually we include it anyway.
		 */
		protected void stopMonitor() {
				if(monitor==null || (!monitor.isAlive())) {
						//we haven't been started yet, or the thread already died
						return;
				}
				do_run=false;
				try {
						//if the thread is currently asleep, wake him up
						monitor.notify();
				}
				catch(IllegalMonitorStateException imse) {
				}
				try {
						//now wait for the thread to die
						monitor.join();
				}
				catch(InterruptedException ie) {
				}
				lastState=MemoryStateEvent.NO_REPORT;
				monitor=null;
		}
		
 		/**
 		 * Adds a listener for MemoryValueEvents to the list.
		 * @param mvl The MemoryValueListener that wants to be registered
		 * with the monitor.
		 */
		public synchronized void addValueListener(MemoryValueListener mvl) {
				if(mvl==null) 
						return;
				valueListenerList.add(mvl);
				if(autoStart) {
						//its save to call start multiple times
						//nothing will happen if we're already running
						startMonitor();
				}
		}
						
		/**
 		 * Removes a listener for MemoryValueEvents from the list.
		 * @param mvl The MemoryValueListener that wants to be removed from
		 * the list of active listeners.
		 */
		public synchronized void removeValueListener(MemoryValueListener mvl) {
				System.out.println(mvl);
				if(mvl==null) 
						return;
				int i=0;
				while(i<valueListenerList.size()) {
						Object o=valueListenerList.get(i);
						if(o==null) {
								valueListenerList.remove(i);
						}
						else if(mvl==(MemoryValueListener)o) {
								valueListenerList.remove(i);
						}
						else {
								//no match go to next item in the list
								i++;
						}
				}
				if(autoStart) {
						//if we start and stop on listeners attached
						if(valueListenerList.isEmpty()
							 && stateListenerList.isEmpty()) {
								//No more listeners attached
								stopMonitor();
						}
				}
		}
 						
		/**
		 * Create and add a Listener that prints the current memory usage
		 * values to System.out.
		 */
		public synchronized void addValuePrinter() {
				if(vprinter==null) {
						vprinter=new ValuePrinter();
						addValueListener(vprinter);
				}
		}

		/**
		 * Remove the Listener that prints the current memory usage
		 * values to System.out.
		 */
		public synchronized void removeValuePrinter() {
				removeValueListener(vprinter);
		}
		
		/**
		 * Reports the current memory usage to all registered 
		 * MemoryValueListeners. The method is synchronized because, if the delay
		 * between updates from the measure-thread is short and there are many listeners
		 * this method could get called again, even though the last event wasn't reproted to all
		 * listeners yet. 
		 * @param tmem the total amount of memory currently available in the JVM 
		 * for current and future objects, measured in bytes.
		 * @param umem an approximation to the total amount of memory 
		 * currently used in the JVM for allocated objects, measured in bytes.
		 * @param fmem an approximation to the total amount of memory 
		 * currently available in the JVM for future allocated objects, measured in bytes.
		 */
		protected synchronized void fireValueEvent(long tmem, long umem, long fmem) {
				int len=valueListenerList.size();
				if(len==0)
						return;
				MemoryValueListener mvl=null;
				for(int i=0;i<len;i++) {
						mvl=(MemoryValueListener)valueListenerList.get(i);
						mvl.update(new MemoryValueEvent(this,maxMemory,objectLimit,tmem,umem,fmem));
				}
		}

 		/**
 		 * Adds a listener for MemoryStateEvents to the list.
		 * @param mvl The MemoryStateListener that wants to be registered
		 * with the monitor.
		 */
		public synchronized void addStateListener(MemoryStateListener mvl) {
				if(mvl==null) 
						return;
				stateListenerList.add(mvl);
				if(autoStart) {
						//its save to call start multiple times
						//nothing will happen if we're already running
						startMonitor();
				}
				//if the listener was added to a running monitor
				//report the current state once to the new listener
				if(lastState!=MemoryStateEvent.NO_REPORT) {
						//there was a report
						mvl.update(new MemoryStateEvent(this,lastState));
				}
		}
						
		/**
 		 * Removes a listener for MemoryStateEvents from the list.
		 * @param mvl The MemoryStateListener that wants to be removed from
		 * the list of active listeners.
		 */
		public synchronized void removeStateListener(MemoryStateListener mvl) {
				if(mvl==null) 
						return;
				int i=0;
				while(i<stateListenerList.size()) {
						Object o=stateListenerList.get(i);
						if(o==null) {
								//somehow a null state slipped in
								stateListenerList.remove(i);
						}
						else if(mvl==(MemoryStateListener)o) {
								//we found the listener
								stateListenerList.remove(i);
						}
						else {
								//no match go to next item in the list
								i++;
						}
				}
				if(autoStart) {
						//if we start and stop on listeners attached
						if(stateListenerList.isEmpty()
							 && stateListenerList.isEmpty()) {
								//No more listeners attached
								stopMonitor();
						}
				}
		}
						
		/**
		 * Create and add a Listener that prints the current memory usage
		 * status to System.out.
		 */
		public synchronized void addStatePrinter() {
				if(sprinter==null) {
						sprinter=new StatePrinter();
						addStateListener(sprinter);
				}
		}

		/**
		 * Remove the Listener that prints the current memory usage
		 * states to System.out.
		 */
		public synchronized void removeStatePrinter() {
				removeStateListener(sprinter);
		}
		
		/**
		 * Reports the state of the current memory usage regarding the limits to all registered 
		 * MemoryStateListeners. The method is synchronized because, if the delay
		 * between updates from the measure-thread is short and there are many listeners
		 * this method could get called again, even though the last event wasn't reproted to all
		 * listeners yet. 
		 * @param state one of the constants:<code><pre>
		 *   MemoryStateEvent.LOW_ERROR - the total amount of memory used for 
		 *       allocated objects exceeds the errorlimit
		 *   MemoryStateEvent.LOW_ERROR_HYSTERESIS - the total amount of memory used for 
		 *       allocated objects exceeded the errorlimit but has now gone below 
		 *       that error limit again.
		 *   MemoryStateEvent.LOW_WARNING - the total amount of memory used for 
		 *       allocated objects exceeds the warnlimit
		 *   MemoryStateEvent.LOW_WARNING_HYSTERESIS - the total amount of memory used for 
		 *       allocated objects exceeded the warnlimit but has now gone below that 
		 *       warning limit again.
		 *   MemoryStateEvent.SAFE - the total amount of memory used for allocated 
		 *       objects does not exceed the warning limit
		 * </pre></code>
		 * If state is not one of the constants above an IllegalArgumentException will be thrown by the 
		 * constructor of the MemoryStateEvent.class. 
		 */
		protected synchronized void fireStateEvent(int state) {
				int len=stateListenerList.size();
				if(len==0)
						return;
				MemoryStateListener msl=null;
				for(int i=0;i<len;i++) {
						msl=(MemoryStateListener)stateListenerList.get(i);
						msl.update(new MemoryStateEvent(this,state));
				}
		}
		
 		/**
		 * This starts the monitor.
		 * NEVER call this method from another class. It is public only because
		 * the Runnable-Interface declared it public.
		 * Use <code>startMonitor()</code> and <code>stopMonitor()</code> to control
		 * the MemoryMonitor.
		 */
		public void run() {
				long tmem=0;
				long fmem=0;
				long umem=0;
				long used=0;
				if(!do_run) //we have a start-stop race-condition here
						return;
				Runtime rt=Runtime.getRuntime();
				while(do_run) {
						tmem=rt.totalMemory();
						fmem=rt.freeMemory();
						umem=tmem-fmem;
						fireValueEvent(tmem,umem,fmem);
						if((umem>errorLimit) && lastState!=MemoryStateEvent.LOW_ERROR) {
								lastState=MemoryStateEvent.LOW_ERROR;
								fireStateEvent(MemoryStateEvent.LOW_ERROR);
						}
						else if((umem>warnLimit) && lastState!=MemoryStateEvent.LOW_WARNING) {
								lastState=MemoryStateEvent.LOW_WARNING;
								fireStateEvent(MemoryStateEvent.LOW_WARNING);
						}
						else if(lastState!=MemoryStateEvent.SAFE) {
								lastState=MemoryStateEvent.SAFE;
								fireStateEvent(MemoryStateEvent.SAFE);
						}
						try {
								Thread.sleep(delay);
						}
						catch(InterruptedException ie) {
						}
				}
		}

		/**
		 * This private class implements MemoryStatusListener and
		 * prints the current status reported by the MemoryMonitor
		 * to System.out.
		 */
		private class StatePrinter implements MemoryStateListener {
				public void update(MemoryStateEvent mse) {
						System.out.println(mse);
				}
		}

		/**
		 * This private class implements MemoryStatusListener and
		 * prints the current status reported by the MemoryMonitor
		 * to System.out.
		 */
		private class ValuePrinter implements MemoryValueListener {
				public void update(MemoryValueEvent mve) {
						System.out.println(mve);
				}
		}

		/**
		 * Convert a byte-value to a String that displays this value
		 * in megabytes with a fractional part of one digit. <br>
		 * Example : <code>byteToMegaByteString(0x4000000)</code> (thats 64MB) would
		 * return the String "64.0 MB".
		 * @param val the value to be converted
		 * @return String the value as a String in megabytes with the unit "MB" attached
		 */
		public static String byteToMegaByteString(long val) {
				long mag=val/0x100000;	
				long frac=((val%0x100000)/0x400)/0x67;	//small rounding error with 0x67,but we need a base 10 value 					
				StringBuffer sb=new StringBuffer(String.valueOf(mag));
				sb.append(".");	//not a localized DecimalSeparator, but understood around the world I think  //$NON-NLS-1$
				sb.append(String.valueOf(frac));
				sb.append(" MB"); //$NON-NLS-1$
				return sb.toString();
		}

		/**
		 * Convert a byte-value to a String that displays this value
		 * in kilobytes.<br>
		 * Example : <code>byteToMegaByteString(0x4000000)</code> (thats 64MB) would
		 * return the String "65536 KB".
		 * @param val the value to be converted
		 * @return String the value as a String in kilobytes with the unit kB attached.
		 */
		public static String byteToKiloByteString(long val) {
				return new String(String.valueOf(val/0x400)+" KB"); //$NON-NLS-1$
		}


}
					 


