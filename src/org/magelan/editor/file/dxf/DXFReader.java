/*
 * The Magelan Project - Vector Graphics Library and Editor
 * Copyright (c) 2003-2004, Assen Antov and Larisa Feldman. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * o Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 * 
 * o Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 * 
 * o Neither the name Magelan nor the names of project members and
 *   contributors may be used to endorse or promote products derived 
 *   from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * Suggestions, comments and fixes should be sent to:
 * aantov@users.sourceforge.net
 * larisa@users.sourceforge.net
 */
package org.magelan.editor.file.dxf;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.magelan.commons.Lang;
import org.magelan.commons.ui.log.LogItem;
import org.magelan.editor.Editor;

/**
 * Reads pairs group code and value from the DXF input stream.
 * 
 * @version 1.0, 06/2001
 * @version 1.1, 01/2006
 * @author  Assen Antov
 */
public class DXFReader {
	
	private static Logger log = Logger.getLogger(DXFReader.class);
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	

	public static final int STRING = 1;
	public static final int DOUBLE = 2;
	public static final int INT = 3;
	public static final int INT32 = 4;
	public static final int BOOLEAN = 5;
	

	public static final int ERROR = -100;
	public static final int UNKNOWN_GROUP_CODE = -102;
	public static final int WRONG_TYPE = -103;
	public static final int WRONG_GROUP_CODE = -104;
	public static final int ERROR_READING = -105;
	public static final int WRONG_VALUE_FORMAT = -106;

	private String[] purposeDescriptions = {
		"Unknown purpose",  //$NON-NLS-1$
		"Text string indicating the entity type (fixed)", //$NON-NLS-1$
		"Primary text value for an entity", //$NON-NLS-1$
		"Name (attribute tag, block name, and so on)", //$NON-NLS-1$
		"Other text or name values", //$NON-NLS-1$
		"Entity handle; text string of up to 16 hexadecimal digits (fixed)", //$NON-NLS-1$
		"Linetype name (fixed)", //$NON-NLS-1$
		"Text style name (fixed)", //$NON-NLS-1$
		"Layer name (fixed)", //$NON-NLS-1$
		"DXF: variable name identifier (used only in HEADER section of the DXF file)", //$NON-NLS-1$
		"Subclass data marker (with derived class name as a string)", //$NON-NLS-1$
		"Control string, followed by \"{<arbitrary name>\" or \"}\"", //$NON-NLS-1$
		"String", //$NON-NLS-1$
		"ASCII string (up to 255 bytes long) in extended data", //$NON-NLS-1$
		"Registered application name (ASCII string up to 31 bytes long) for extended data", //$NON-NLS-1$
		"Extended data control string (\"{\"or \"}\")", //$NON-NLS-1$
		"Extended data layer name", //$NON-NLS-1$
		"Chunk of bytes (up to 127 bytes long) in extended data",		// ! //$NON-NLS-1$
		"Entity handle in extended data; text string of up to 16 hexadecimal digits", //$NON-NLS-1$
		"Entity visibility; integer value; absence or 0 indicates visibility; 1 indicates invisibility", //$NON-NLS-1$
		"16-bit integer value", //$NON-NLS-1$
		"Color number (fixed)", //$NON-NLS-1$
		"\"Entities follow\" flag (fixed)", //$NON-NLS-1$
		"Space - that is, model or paper space (fixed)", //$NON-NLS-1$
		"APP: identifies whether viewport is on but fully off screen; is not active or is off", //$NON-NLS-1$
		"APP: viewport identification number", //$NON-NLS-1$
		"Integer values, such as repeat counts, flag bits, or modes", //$NON-NLS-1$
		"Extended data 16-bit signed integer", //$NON-NLS-1$
		"32-bit integer value", //$NON-NLS-1$
		"Extended data 32-bit signed long", //$NON-NLS-1$
		"Double precision value", //$NON-NLS-1$
		"8-bit integer value", //$NON-NLS-1$
		"PlotStyleName type enum (AcDb::PlotStyleNameType)", //$NON-NLS-1$
		"Lineweight enum value (AcDb::LineWeight)", //$NON-NLS-1$
		"Arbitrary text string", //$NON-NLS-1$
		"Arbitrary binary chunks with same representation and limits as 1004 group codes: hexadecimal strings of up to 254 characters represent data chunks of up to 127 bytes", //$NON-NLS-1$
		"Arbitrary object handles; handle values that are taken \"as is.\" They are not translated during INSERT and XREF operations", //$NON-NLS-1$
		"String representing handle value of the PlotStyleName object, basically a hard pointer, but has a different range to make backward compatibility easier to deal with", //$NON-NLS-1$
		"Object handle for DIMVAR symbol table entry", //$NON-NLS-1$
		"Soft-pointer handle; arbitrary soft pointers to other objects within same DXF file or drawing. Translated during INSERT and XREF operations", //$NON-NLS-1$
		"Hard-pointer handle; arbitrary hard pointers to other objects within same DXF file or drawing. Translated during INSERT and XREF operations", //$NON-NLS-1$
		"Soft-owner handle; arbitrary soft ownership links to other objects within same DXF file or drawing. Translated during INSERT and XREF operations", //$NON-NLS-1$
		"Hard-owner handle; arbitrary hard ownership links to other objects within same DXF file or drawing. Translated during INSERT and XREF operations", //$NON-NLS-1$
		"DXF: The 999 group code indicates that the line following it is a comment string. SAVEAS does not include such groups in a DXF output file, but OPEN honors them and ignores the comments. You can use the 999 group to include comments in a DXF file that you've edited", //$NON-NLS-1$
		"A point in extended data; DXF: X value (followed by 1020 and 1030 groups); APP: 3D point", //$NON-NLS-1$
		"DXF: Y and Z values of a point", //$NON-NLS-1$
		"A 3D world space position in extended data; DXF: X value (followed by 1021 and 1031 groups); APP: 3D point", //$NON-NLS-1$
		"DXF: Y and Z values of a world space position", //$NON-NLS-1$
		"A 3D world space displacement in extended data; DXF: X value (followed by 1022 and 1032 groups); APP: 3D vector", //$NON-NLS-1$
		"DXF: Y and Z values of a world space displacement", //$NON-NLS-1$
		"A 3D world space direction in extended data; DXF: X value (followed by 1022 and 1032 groups); APP: 3D vector", //$NON-NLS-1$
		"DXF: Y and Z values of a world space direction", //$NON-NLS-1$
		"Extended data floating-point value", //$NON-NLS-1$
		"Extended data distance value", //$NON-NLS-1$
		"Extended data scale factor", //$NON-NLS-1$
		"Floating-point value" //$NON-NLS-1$
	};
	
	private String[] typeDescriptions = {
		"Unknown group code",  //$NON-NLS-1$
		"String", //$NON-NLS-1$
		"Double precision 3D point", //$NON-NLS-1$
		"16-bit integer value", //$NON-NLS-1$
		"32-bit integer value", //$NON-NLS-1$
		"Double precision scalar floating-point value", //$NON-NLS-1$
		"8-bit integer value", //$NON-NLS-1$
		"Arbitrary text string", //$NON-NLS-1$
		"String representing hex value of binary chunk", //$NON-NLS-1$
		"String representing hex handle value", //$NON-NLS-1$
		"String representing hex object IDs", //$NON-NLS-1$
		"Comment (string)", //$NON-NLS-1$
		"Floating-point value" //$NON-NLS-1$
	};
	

	private int purpose;

	private int type;
	
	public int valueType;
	
	public double dval = 0;
	public int ival = 0;
	public long i32val = 0;
	public boolean bval = false;
	public String sval = ""; //$NON-NLS-1$
	
	private boolean doNothing = false;
	public int groupCode = 0;
	
	private LineNumberReader in;
	
	private org.magelan.commons.ui.log.Logger userLog;
	
	/**
	 * Constructs a DXF data reader by a <code>Reader</code> instance.
	 * 
	 * @param in  reader to fetch the data from
	 */
	public DXFReader(Reader in) {
		this.in = new LineNumberReader(in, 1000000);
	}

	/**
	 * Constructs a DXF data reader by a <code>Reader</code> instance.
	 * 
	 * @param in  reader to fetch the data from
	 * @param userLog user readable log file
	 * @since 1.1
	 */
	public DXFReader(Reader in, org.magelan.commons.ui.log.Logger userLog) {
		this(in);
		this.userLog = userLog;
	}
	
	private void recognizeCode(int c) {
	
		type = 0;
		purpose = 0;
		valueType = 0;
				
		if (((c>=0) && (c<=9)) ||
			((c==100) || (c==102)) ||
			((c>=410) && (c<=419)) ||
			((c>=1000) && (c<=1009))
			) {
			
			if (c==0) purpose = 1;		// Text string indicating the entity type (fixed)
			else if (c==1) purpose = 2;		// Primary text value for an entity
			else if (c==2) purpose = 3;		// Name (attribute tag, block name, and so on)
			else if ((c==3) || (c==4)) purpose = 4;	// Other text or name values
			else if (c==5) purpose = 5;		// Entity handle; text string of up to 16 hexadecimal digits (fixed)
			else if (c==6) purpose = 6;		// Linetype name (fixed)
			else if (c==7) purpose = 7;		// Text style name (fixed)
			else if (c==8) purpose = 8;		// Layer name (fixed)
			else if (c==9) purpose = 9;		// DXF: variable name identifier (used only in HEADER section of the DXF file)
			else if (c==100) purpose = 10;	// Subclass data marker (with derived class name as a string)
			else if (c==102) purpose = 11;	// Control string, followed by "{<arbitrary name>" or "}"
			else if ((c>=410) && (c<=419)) purpose = 12;	// String
			else if (c==1000) purpose = 13;	// ASCII string (up to 255 bytes long) in extended data
			else if (c==1001) purpose = 14;	// Registered application name (ASCII string up to 31 bytes long) for extended data
			else if (c==1002) purpose = 15;	// Extended data control string ("{"or "}")
			else if (c==1003) purpose = 16;	// Extended data layer name
			else if (c==1004) purpose = 17;	// Chunk of bytes (up to 127 bytes long) in extended data ???
			else if (c==1005) purpose = 18;	// Entity handle in extended data; text string of up to 16 hexadecimal digits 
			else if ((c>=1006) && (c<=1009)) purpose = 12;	// String
			
			type = 1;		// String
			valueType = STRING;
		}
		
		else if (((c>=10) && (c<=59)) ||
			(c==110) || (c==120) || (c==130) ||
			(c==210) || (c==220) || (c==230) ||
			(c==111) || (c==121) || (c==131) ||
			(c==112) || (c==122) || (c==132)) {	//"Double precision 3D point"
			purpose = 30;
			valueType = DOUBLE;
		}
		
		else if (((c>=60) && (c<=79)) ||
			((c>=170) && (c<=175)) ||
			((c>=400) && (c<=409)) ||
			((c>=1060) && (c<=1070))
			) {
			
			if (c==60) purpose = 19;		// Entity visibility; integer value; absence or 0 indicates visibility; 1 indicates invisibility
			else if ((c==61) || ((c>=63) && (c<=65)) || (c==79)) 
				purpose = 20;		// General purpose 16-bit integer number
			else if (c==62) purpose = 21;		// Color number (fixed)
			else if (c==66) purpose = 22;		// "Entities follow" flag (fixed)
			else if (c==67) purpose = 23;		// Space - that is, model or paper space (fixed)
			else if (c==68) purpose = 24;		// APP: identifies whether viewport is on but fully off screen; is not active or is off
			else if (c==69) purpose = 25;		// APP: viewport identification number
			else if ((c>=70) && (c<=78)) purpose = 26;		// Integer values, such as repeat counts, flag bits, or modes
			else if (((c>=170) && (c<=175)) || 
				((c>=400) && (c<=409)) ||
				((c>=1060) && (c<=1069))
				) purpose = 20;	// General purpose 16-bit integer number
			else if (c==1070) purpose = 27;		// Extended data 16-bit signed integer
			
			type = 3;		//"16-bit integer value";
			valueType = INT;
		}
		
		else if (((c>=90) && (c<=99)) ||
			(c==1071)
			) {
			
			if (c==1071) purpose = 29;	// Extended data 32-bit signed long
			else purpose = 28;		// 32-bit integer value
				
			type = 4;		//"32-bit integer value";
			valueType = INT32;
		}
		
		else if ((c>=140) && (c<=147)
			) {
			purpose = 30;		// Double precision value
			type = 5;		// "Double precision scalar floating-point value";
			valueType = DOUBLE;
		}

		else if (((c>=280) && (c<=289)) ||
			((c>=370) && (c<=379)) ||
			((c>=380) && (c<=389))
			) {
		 
			if ((c>=380) && (c<=389)) purpose = 32;		// PlotStyleName type enum (AcDb::PlotStyleNameType).
			else if ((c>=370) && (c<=379)) purpose = 33;		// Lineweight enum value (AcDb::LineWeight)
			else purpose = 31;		// 8-bit integer values
							
			type = 6;		// "8-bit integer value";
			valueType = INT;
		}
		
		else if ((c>=300) && (c<=309)
			) {
			purpose = 34;		//"Arbitrary text string"
			type = 7;			// "Arbitrary text string";
			valueType = STRING;
		}
		
		else if ((c>=310) && (c<=319)) {
			purpose = 35;	// Arbitrary binary chunks with same representation and limits as 1004 group codes: hexadecimal strings of up to 254 characters represent data chunks of up to 127 bytes
			type = 8;		// "String representing hex value of binary chunk";
			valueType = STRING;
		}
		else if (((c>=320) && (c<=329)) ||
			((c>=390) && (c<=399)) ||
			(c==105)
			){
			
			if ((c>=320) && (c<=329)) purpose = 36;	// Arbitrary object handles; handle values that are taken "as is." They are not translated during INSERT and XREF operations
			else if ((c>=390) && (c<=399)) purpose = 37;		// String representing handle value of the PlotStyleName object, basically a hard pointer, but has a different range to make backward compatibility easier to deal with
			else if (c==105) purpose = 38;		// Object handle for DIMVAR symbol table entry

			type = 9;		// "String representing hex handle value";
			valueType = STRING;
		}
		
		else if ((c>=330) && (c<=369)) {
				
			if ((c>=330) && (c<=339)) purpose = 39;		// Soft-pointer handle; arbitrary soft pointers to other objects within same DXF file or drawing. Translated during INSERT and XREF operations
			else if ((c>=340) && (c<=349)) purpose = 40;		// Hard-pointer handle; arbitrary hard pointers to other objects within same DXF file or drawing. Translated during INSERT and XREF operations
			else if ((c>=350) && (c<=359)) purpose = 41;		// Soft-owner handle; arbitrary hard ownership links to other objects within same DXF file or drawing. Translated during INSERT and XREF operations
			else if ((c>=360) && (c<=369)) purpose = 42;		// Hard-owner handle; arbitrary hard ownership links to other objects within same DXF file or drawing. Translated during INSERT and XREF operations
			
			type = 10;		// "String representing hex object IDs";
			valueType = STRING;
		}
		
		else if (c==999) {
			purpose = 43;	// DXF: The 999 group code indicates that the line following it is a comment string. SAVEAS does not include such groups in a DXF output file, but OPEN honors them and ignores the comments. You can use the 999 group to include comments in a DXF file that you've edited
			type = 11;		// "Comment (string)";
			valueType = STRING;
		}
		
		else if ((c>=1010) && (c<=1059)) {
					
			if (c==1010) purpose = 44;		// A point in extended data; DXF: X value (followed by 1020 and 1030 groups); APP: 3D point
			else if ((c==1020) || (c==1030)) purpose = 45;		// DXF: Y and Z values of a point
			else if (c==1011) purpose = 46;		// A 3D world space position in extended data; DXF: X value (followed by 1021 and 1031 groups); APP: 3D point
			else if ((c==1021) || (c==1031)) purpose = 47;		// DXF: Y and Z values of a world space position
			else if (c==1012) purpose = 48;		// A 3D world space displacement in extended data; DXF: X value (followed by 1022 and 1032 groups); APP: 3D vector
			else if ((c==1022) || (c==1032)) purpose = 49;		// DXF: Y and Z values of a world space displacement
			else if (c==1013) purpose = 50;		// A 3D world space direction in extended data; DXF: X value (followed by 1022 and 1032 groups); APP: 3D vector
			else if ((c==1023) || (c==1033)) purpose = 51;		// DXF: Y and Z values of a world space direction
			else if (c==1040) purpose = 52;		// Extended data floating-point value
			else if (c==1041) purpose = 53;		// Extended data distance value
			else if (c==1042) purpose = 54;		// Extended data scale factor
			else purpose = 55;			// Floating-point value

			type = 12;	// "Floating-point value";
			valueType = DOUBLE;
		}
	}
	
	/**
		*/
	public int read() {
	
		if (doNothing) {
			doNothing = false;
			return groupCode;
		}
		
		int code;
		try {
			String b;
			b = in.readLine();
			code = Integer.parseInt(b.trim());
		} 
		catch (NumberFormatException e) {return WRONG_GROUP_CODE;}
		catch (IOException ioe) {return ERROR_READING;}
	
		groupCode = code;
			
		// 
		recognizeCode(code);
		
		// 
		String s;
		try {
			s = in.readLine();
		} catch (IOException e) {return ERROR_READING;}
		
		try {
			if (valueType==STRING)
				sval = s;
				
			else if (valueType==DOUBLE)
				dval = Double.parseDouble(s.trim());
				
			else if (valueType==INT)
				ival = Integer.parseInt(s.trim());
				
			else if (valueType==INT32)
				i32val = Long.parseLong(s.trim());
				
			else if (valueType==BOOLEAN)
				bval = Boolean.getBoolean(s.trim());
				
			else {
				log.debug("Unrecognized group code "+code+lang.getString("DXFReader.log.line")+in.getLineNumber()); //$NON-NLS-1$ //$NON-NLS-2$
				
				// user readable log
				LogItem li = new LogItem(lang.getString("DXFReader.log.code")+code, LogItem.LEVEL_ERROR); //$NON-NLS-1$
				li.put(lang.getString("DXFReader.log.Line"), new Integer(in.getLineNumber())); //$NON-NLS-1$
				userLog.addItem(li);
				
				code = WRONG_GROUP_CODE;
			}	
		} 	catch (NumberFormatException e) {return WRONG_VALUE_FORMAT;}
		
		// 

		return code;
	}
	
	public void pushBack() {
		doNothing = true;
	}
	
	public String getValuePurposeDescription() {
		return purposeDescriptions[purpose];
	}
	
	public String getValueTypeDescription() {
		return typeDescriptions[type];
	}
	
	public String getValuePurposeDescription(int c) {
		recognizeCode(c);
		return purposeDescriptions[purpose];
	}
	
	public String getValueTypeDescription(int c) {
		recognizeCode(c);
		return typeDescriptions[type];
	}
	
	/**
	 * @return current line number
	 * @since 1.1
	 */
	public int getLineNumber() {
		return in.getLineNumber();
	}
}