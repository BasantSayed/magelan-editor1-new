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

import java.awt.Color;


/**
 * Defines common DXF constants.
 *
 * @author Assen Antov
 * @version 1.1, 03/2004
 */
public interface DXFConst {

  /**
   * Version info about the implementation.
   */
  public static final String IMPL_VERSION = "1.1, 03/2004"; //$NON-NLS-1$
  
  /**
   * Copyright string.
   */
  public static final String COPYRIGHT = "Copyright 2003 (c) The Magelan Project"; //$NON-NLS-1$
  
  /**
   * Default color.
   */
  public static final int COLOR_BYLAYER = 256;
  
  public static final int COLOR_RED = 1;
  public static final int COLOR_YELLOW = 2;
  public static final int COLOR_GREEN = 3;
  public static final int COLOR_CYAN = 4;
  public static final int COLOR_BLUE = 5;
  public static final int COLOR_MAGENTA = 6;
  public static final int COLOR_WHITE = 7;
  public static final int COLOR_DARK_GRAY = 8;
  public static final int COLOR_BLACK = 250;
  public static final int COLOR_ORANGE = 21;
  
  /**
   * Default linestyle.
   */
  public static final String BYLAYER = "BYLAYER"; //$NON-NLS-1$
  
  /**
   * Color map
   */
  public static final Color[] COLOR_MAP = new Color[] {
  		Color.white, 
		Color.red,	Color.yellow,	Color.green,	Color.cyan, // 1-5
		Color.blue,	Color.magenta,	Color.white,	new Color(128, 128, 128), // 5-8
		new Color(242, 242, 242), // 9
		
		Color.black, new Color(51, 51, 51), new Color(102, 102, 102), 
		new Color(153, 153, 153), new Color(244, 244, 244), Color.white // 250 - 255
  };
}