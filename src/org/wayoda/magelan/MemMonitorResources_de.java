/* This File is part of the wayoda-project                                    */
/* Filename : MemMonitorResources_de.java                                   */
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

package org.wayoda.magelan;

import java.util.*;

/**
 * ResourceBundle for i18n of the classes that use a MemMonitor.
 * <p>
 * This class provides the default messages in english. 
 * <p>
 * @author Eberhard Fahle
 * @version 0.0
 */
public class MemMonitorResources_de extends ListResourceBundle {
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
 				{"action.name","Speicher Monitor"}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"action.description.short","Anzeige der Hauptspeicherbelegung"}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"action.description.long","Zeigt die Auslastung Hauptspeicher durch magelan an"}, //$NON-NLS-1$ //$NON-NLS-2$
 				{"action.group","Werkzeuge"} //$NON-NLS-1$ //$NON-NLS-2$
		};
		
}

