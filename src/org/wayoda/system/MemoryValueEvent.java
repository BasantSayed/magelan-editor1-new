/* This File is part of the wayoda-project                                    */
/* Filename : MemoryValueEvent.java                                           */
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
 * An event delivered by the MemoryMonitor when the value 
 * of the current memory usage changes.
 * <p>
 * These events are received by classes that implement the MemoryValueListener-interface.
 * The information provided is the cureent memory usage of the JVM.
 * <p>
 * @author Eberhard Fahle
 * @version 0.0
 */
public class MemoryValueEvent extends EventObject {
		/** Some static strings for localizing the toString-message */
		private static String info=null;
		private static String jvm=null;
		private static String heap=null;
		private static String total=null;
		private static String used=null;
		private static String free=null;
		private static String available=null;

		/** the maximum memory the JVM is using */
		private long jvmmem;
		/** the maximum memory that can be used for allocating objects */
		private long maxmem;
		/** 
		 * the total amount of memory currently alocated by the JVM 
		 * for current and future objects, measured in bytes.  
		 */
		private long tmem;
		/** 
		 * an approximation of the amount of memory currently used by the JVM 
		 * for allocated objects, measured in bytes.  
		 */
		private long umem;
		/** 
		 * an approximation of the amount of memory currently available in the JVM 
		 * for future allocated objects, measured in bytes.  
		 */
		private long fmem;

		/**
		 * Creates a new MemoryValueEvent.
		 * @param source the source of the event
		 * @param jvmmem The maximum amount of memory that is used by the
		 * Java Virtual Machine that reported this event. 
		 * @param maxmem The maximum amount of memory that can be allocated
		 * by the JVM for Objects without thrown an OutOfMemoryError
		 * @param tmem the total amount of memory currently allocated by the JVM 
		 * for current and future objects, measured in bytes.
		 * @param umem an approximation to the amount of memory 
		 * currently used by the JVM for allocated objects, measured in bytes.
		 * @param fmem an approximation to the amount of memory 
		 * currently available in the JVM for future allocated objects, measured in bytes.
		 */
		public MemoryValueEvent(Object source, long jvmmem, long maxmem,
														long tmem, long umem, long fmem) {
				super(source);
				this.jvmmem=jvmmem;
				this.maxmem=maxmem;
				this.tmem=tmem;
				this.umem=umem;
				this.fmem=fmem;
		}

		/**
		 * Gets the maximum amount of memory the JVM will use during the runtime
		 * of the application, measured in bytes .
		 * This value will be reported with every event, but it will be constant 
		 * once the JVM is started.
		 * @return long the memory-footprint of the JVM
		 */
		public long getJVMMemory() {
				return jvmmem;
		}

		/**
		 * Gets the maximum amount of memory the JVM will use for allocating
		 * Objects,measured in bytes .
		 * This value will be reported with every event, but it will depend
		 * on the configuration of the MemoryMonitor that reported this event.
		 * @return long the maximum amount of memory that can be allocated by the JVM
		 */
		public long getMaximumMemory() {
				return maxmem;
		}

		/**
		 * Gets the total amount of memory the JVM currently allocated 
		 * for existing and future objects.
		 * @return long the total memory of the JVM 
		 */
		public long getTotalMemory() {
				return tmem;
		}

		/**
		 * Gets an approximation of the amount of memory the JVM 
		 * currently allocated for existing objects.
		 * @return long the used memory of the JVM 
		 */
		public long getUsedMemory() {
				return umem;
		}

		/**
		 * Gets an approximation of the amount of memory that is available for
		 * future objects to be allocated by the JVM. 
		 * @return long the free memory of the JVM 
		 */
		public long getFreeMemory() {
				return fmem;
		}

		/**
		 * Retrieve the localized messages to be printed from the ResourceBundle.
		 */
		private synchronized void loadResources() {
				try {
						ResourceBundle rb=ResourceBundle.getBundle("org/wayoda/system/MemoryMonitorResources"); //$NON-NLS-1$
						info=rb.getString("event.value.info"); //$NON-NLS-1$
						jvm=rb.getString("event.value.jvm"); //$NON-NLS-1$
						heap=rb.getString("event.value.heap"); //$NON-NLS-1$
						total=rb.getString("event.value.total"); //$NON-NLS-1$
						used=rb.getString("event.value.used"); //$NON-NLS-1$
						free=rb.getString("event.value.free"); //$NON-NLS-1$
						available=rb.getString("event.value.available"); //$NON-NLS-1$
				}
				catch(MissingResourceException mre) {
						System.out.println(mre);
				}
		}

		/**
		 * Converts the bytes reported by the event to MB-values
		 * @param val the value to be converted
		 * @return int the amount of bytes converted to megabytes
		 */
		private int byteToMB(long val) {
				//rounding on 1/2 megabyte threshold
				boolean roundUp=((val%0x100000)<0x80000);				
				val=val/0x100000;
				if(roundUp)
						val++;
				return (int)val;
		}
				
				
		/**
		 * Returns a String representation of this event.
		 * @return String A String-representation of this event.
		 */
		public String toString() {
				if(info==null) {
						//we do this only once, when this method is called for the first time
						loadResources();
				}
				StringBuffer sb=new StringBuffer();
				sb.append(info);
				sb.append(jvm);
				String s=String.valueOf(byteToMB(jvmmem));
				int flen=s.length();
				sb.append(s+"\n"); //$NON-NLS-1$
				sb.append(heap);
				s=String.valueOf(byteToMB(maxmem));
				int slen=s.length();
				while(slen<flen) {
						sb.append(" "); //$NON-NLS-1$
						slen++;
				}
 				sb.append(s+"\n"); //$NON-NLS-1$
				sb.append(total);
				s=String.valueOf(byteToMB(tmem));
				slen=s.length();
				while(slen<flen) {
						sb.append(" "); //$NON-NLS-1$
						slen++;
				}
 				sb.append(s+"\n"); //$NON-NLS-1$
				sb.append(used);
				s=String.valueOf(byteToMB(umem));
				slen=s.length();
				while(slen<flen) {
						sb.append(" "); //$NON-NLS-1$
						slen++;
				}
 				sb.append(s+"\n"); //$NON-NLS-1$
				sb.append(free);
				s=String.valueOf(byteToMB(fmem));
				slen=s.length();
				while(slen<flen) {
						sb.append(" "); //$NON-NLS-1$
						slen++;
				}
 				sb.append(s+"\n"); //$NON-NLS-1$
				sb.append(available);
				s=String.valueOf(byteToMB(maxmem-umem));
				slen=s.length();
				while(slen<flen) {
						sb.append(" "); //$NON-NLS-1$
						slen++;
				}
 				sb.append(s+"\n"); //$NON-NLS-1$
				return sb.toString();
		}
}












