/* This File is part of the wayoda-project                                    */
/* Filename : MemoryMonitorResources_de.java                                   */
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
 * This class provides the messages in german. 
 * <p>
 * @author Eberhard Fahle
 * @version 0.0
 */
public class MemoryMonitorResources_de extends ListResourceBundle {
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
 				{"event.state.info","Speicherauslastung (Status) : "}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.state.error","Fehler (Out Of Memory)"}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.state.warning","Hoch (Warnung)"}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.state.safe","Niedrig"}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.value.info","Speicherbelegung (MB)\n"}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.value.jvm","            JVM = "}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.value.heap","    Objekte max = "}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.value.total","  Objekte total = "}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.value.used"," Objekte belegt = "}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"event.value.free","   Objekte frei = "}, //$NON-NLS-1$ //$NON-NLS-2$
				{"event.value.available","       unbelegt = "}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorytooltip.label.header","Speicherbelegung"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorytooltip.label.jvm","JVM Total"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorytooltip.label.max","Maximum fur Objekte"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorytooltip.label.total","Angeforderter Objekt-Speicher"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorytooltip.label.used","Belegt durch Objekte"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorytooltip.label.free","Frei fur neue Objekte"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.header","Speicherbelegung"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.jvm","JVM Total"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.max","Maximum fur Objekte"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.total","Angeforderter Objekt-Speicher"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.used","Belegt durch Objekte"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.free","Frei fur neue Objekte"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.totalcolor","Angefordert Total "}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.usedcolor","Belegt"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.freecolor","Frei"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"memorydisplay.label.delay","Verzugerung"} //$NON-NLS-1$ //$NON-NLS-2$
				
		};
		
}

