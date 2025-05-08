/* This File is part of the wayoda-project                                    */
/* Filename : KeyCodeMap.java                                                 */
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

package org.wayoda.util;

import java.awt.event.KeyEvent;
import java.util.Hashtable;

/**
 * Provides static methods to map characters to their keycode.
 * <p>
 * Documenting 
 * <p>
 * @author Eberhard Fahle
 * @version 0.0
 */
public final class KeyCodeMap {
		/** 
		 * Create the Hashtable and fills it with the
		 * keycodes for all uppercase characters on a keyboard
		 * @return Hashtable the hashtable that contains the keycodes
		 */
		private static Hashtable initHashTable() {
				Hashtable h=new Hashtable(26);
				h.put("A",new Integer(KeyEvent.VK_A)); //$NON-NLS-1$
				h.put("B",new Integer(KeyEvent.VK_B)); //$NON-NLS-1$
				h.put("C",new Integer(KeyEvent.VK_C)); //$NON-NLS-1$
				h.put("D",new Integer(KeyEvent.VK_D)); //$NON-NLS-1$
				h.put("E",new Integer(KeyEvent.VK_E)); //$NON-NLS-1$
				h.put("F",new Integer(KeyEvent.VK_F)); //$NON-NLS-1$
				h.put("G",new Integer(KeyEvent.VK_G)); //$NON-NLS-1$
				h.put("H",new Integer(KeyEvent.VK_H)); //$NON-NLS-1$
				h.put("I",new Integer(KeyEvent.VK_I)); //$NON-NLS-1$
				h.put("J",new Integer(KeyEvent.VK_J)); //$NON-NLS-1$
				h.put("K",new Integer(KeyEvent.VK_K)); //$NON-NLS-1$
				h.put("L",new Integer(KeyEvent.VK_L)); //$NON-NLS-1$
				h.put("M",new Integer(KeyEvent.VK_M)); //$NON-NLS-1$
				h.put("N",new Integer(KeyEvent.VK_N)); //$NON-NLS-1$
				h.put("O",new Integer(KeyEvent.VK_O)); //$NON-NLS-1$
				h.put("P",new Integer(KeyEvent.VK_P)); //$NON-NLS-1$
				h.put("Q",new Integer(KeyEvent.VK_Q)); //$NON-NLS-1$
				h.put("R",new Integer(KeyEvent.VK_R)); //$NON-NLS-1$
				h.put("S",new Integer(KeyEvent.VK_S)); //$NON-NLS-1$
				h.put("T",new Integer(KeyEvent.VK_T)); //$NON-NLS-1$
				h.put("U",new Integer(KeyEvent.VK_U)); //$NON-NLS-1$
				h.put("V",new Integer(KeyEvent.VK_V)); //$NON-NLS-1$
				h.put("W",new Integer(KeyEvent.VK_W)); //$NON-NLS-1$
				h.put("X",new Integer(KeyEvent.VK_X)); //$NON-NLS-1$
				h.put("Y",new Integer(KeyEvent.VK_Y)); //$NON-NLS-1$
				h.put("Z",new Integer(KeyEvent.VK_Z)); //$NON-NLS-1$
				return h;
		}

		/** The Hashtable with the keycodes */
		private static final Hashtable ht=initHashTable();

		/**
		 * A private constructor, so it doesn't show up
		 * in the API-Documentation
		 */
		private KeyCodeMap() {
		}

		/**
		 * Maps a char in the range [a..z,A..Z] to one of the constants
		 * from java.awt.event.KeyEvent.
		 * @param c the char for which the constant is requested
		 * @return int one of the constants in java.awt.event.KeyEvent
		 * that start with VK_ followed by the uppercase version
		 * of the requested character. If the char is not one of the letters in
		 * [a..z,A..Z] we return 0;
		 */
		public static int getVK_KeyCode(char c) {
				Integer i=get(c);
				if(i==null)
						return 0;
				return i.intValue();
		}

		/**
		 * Maps a String that contains a single char in the range of [a..z,A..Z] to one of the constants
		 * from java.awt.event.KeyEvent.
		 * @param s the String that contains the char for which the constant is requested
		 * @return int one of the constants in java.awt.event.KeyEvent
		 * that start with VK_ followed by the uppercase version
		 * of the requested character. If the char is not one of the letters in
		 * [a..z,A..Z] we return 0;
		 */
		public static int getVK_KeyCode(String s) {
				Integer i=get(s);
				if(i==null)
						return 0;
				return i.intValue();
		}

		/**
		 * Maps a char in the range [a..z,A..Z] to one of the constants
		 * from java.awt.event.KeyEvent.
		 * @param c the char for which the constant is requested
		 * @return Integer An Integer with a value equal to
		 * one of the constants in java.awt.event.KeyEvent
		 * that start with VK_ followed by the uppercase version
		 * of the requested character. If the char is not one of the letters in
		 * [a..z,A..Z] we return null;
		 */
		public static Integer get(char c) {
				return (Integer)ht.get((String.valueOf(c)).toUpperCase());
		}

		/**
		 * Maps a String that contains a single char in the range of [a..z,A..Z] to one of the constants
		 * from java.awt.event.KeyEvent.
		 * @param s the String that contains the char for which the constant is requested
		 * @return Integer An Integer with a value equal to
		 * one of the constants in java.awt.event.KeyEvent
		 * that start with VK_ followed by the uppercase version
		 * of the requested character. If the char is not one of the letters in
		 * [a..z,A..Z] we return null;
		 */
		public static Integer get(String s) {
				return (Integer)ht.get(s.toUpperCase());
		}
}
		






