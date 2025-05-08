/* This File is part of the wayoda-project                                    */
/* Filename : MemoryMonitorResources.java                                   */
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

import java.util.*;

/**
 * ResourceBundle for i18n of the classes that use a MemoryMonitor.
 * <p>
 * This class provides the default messages in english. 
 * <p>
 * @author Eberhard Fahle
 * @version 0.0
 */
public class MemoryMonitorResources extends ListResourceBundle {
		/**
		 * Get the contents of this resources
		 * @return Object [] the full contents of this resources
		 */
		public Object [][] getContents() {
				return contents;
		}
		
		/** The localized messages and their keys */
		private static final Object [][] contents = {
				//Resources for the MemoryStateEvent
 				{"event.state.info","Memory usage (Status) : "}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.state.error"," Out of memory (Error)"}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.state.warning"," High (Warning)"}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.state.safe"," Low"}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.value.info","Memory usage (MB)\n"}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.value.jvm","             JVM = "}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.value.heap","    Heap maximum = "}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.value.total"," Total allocated = "}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.value.used","            Used = "}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.value.free","            Free = "}, //$NON-NLS-1$ //$NON-NLS-2$
				{"event.value.available","       Available = "}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorytooltip.label.header","Memory usage"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorytooltip.label.jvm","JVM footprint"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorytooltip.label.max","Max for objects"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorytooltip.label.total","Total allocated"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorytooltip.label.used","Used for current objects"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorytooltip.label.free","Free for future objects"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.header","Memory usage"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.jvm","JVM footprint"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.max","Max for objects"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.total","Total allocated"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.used","Used for current objects"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.free","Free for future objects"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.totalcolor","Total allocated"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.usedcolor","Used"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.freecolor","Free"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.delay","Delay"} //$NON-NLS-1$ //$NON-NLS-2$
			
		};
		
}

