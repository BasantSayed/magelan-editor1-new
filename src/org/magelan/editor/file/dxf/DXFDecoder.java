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
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.magelan.commons.Lang;
import org.magelan.core.CoreModel;
import org.magelan.core.DefaultEntity;
import org.magelan.core.Entity;
import org.magelan.core.db.DataSource;
import org.magelan.core.db.DefaultDataSource;
import org.magelan.core.entity.*;
import org.magelan.core.style.DefaultLineStyle;
import org.magelan.core.style.Layer;
import org.magelan.core.style.LineStyle;
import org.magelan.editor.Editor;


/**
 * Reads a DXF file into a <code>CoreModel</code> instance.
 *
 * @author Assen Antov
 * @version 2.2, 06/2006
 */
public class DXFDecoder implements DXFConst {

	private static Logger log = Logger.getLogger(DXFDecoder.class);
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private static org.magelan.commons.ui.log.Logger userLog = new org.magelan.commons.ui.log.Logger.Default();

	private static final String DS_HANDLES = "DXF_Handles";
	private static final String DS_VARS = "DXF_Variables";
	
	/**
	 * This method will read the data from the input stream and will create and
	 * add the corresponding entities to the available  drawing instance. 
	 * 
	 * @param in input stream to read DXF data from
	 * @param d drawing to add entities to <p>
	 *
	 * @throws IOException
	 */
	public static void decode(InputStream in, CoreModel d) throws IOException {
		log.setLevel(Level.ERROR);
		
		userLog.clear();
		userLog.info(lang.getString("DXFDecoder.log.started")); //$NON-NLS-1$
		
		// create a DXFReader
		DXFReader r = new DXFReader(new InputStreamReader(new BufferedInputStream(in, 1048576)), userLog);

		// group code
		int c;

		// create a datasource for entity handles
		DataSource handles = new DefaultDataSource(DS_HANDLES, new String[] {"Entity", "Handle"}, new Class[] {DefaultEntity.class, String.class});
		d.addFeature(handles);
		
		DataSource vars = new DefaultDataSource(DS_VARS, new String[] {"Name", "Value"}, new Class[] {String.class, String.class});
		d.addFeature(vars);
		
		// loops through the file
		c = r.read();
		while (!r.sval.equals("EOF")) { //$NON-NLS-1$

			// check if section (should be)
			if (r.sval.equals("SECTION")) { //$NON-NLS-1$

				// read section name
				if (r.read() == 2) {

					/*
					 * Is this a header section?
					 * Header variables are saved to the CoreModel with a 'DXF.' prefix
					 */
					if (r.sval.equals("HEADER")) { //$NON-NLS-1$
						c = r.read();
						while (!r.sval.equals("ENDSEC")) { //$NON-NLS-1$
							if (c == 9) {
								// store variable value here
								Object varVal = null;

								// get variable name
								String varName = r.sval; //$NON-NLS-1$

								c = r.read();
								if (((c >= 0) && (c <= 9)) || (c == 390)) {
									varVal = r.sval; // !!
								} else if ((c == 40) || (c == 50)) {
									varVal = new Double(r.dval);
								} else if ((c == 70) || (c == 62)) {
									varVal = new Integer(r.ival);
								} else if ((c == 370) || (c == 380)) {
									varVal = new Integer(r.ival);
								} else if (c == 10) {
									PointEntity p = new PointEntity();
									p.setX(r.dval);
									if (r.read() == 20) {
										p.setY(-r.dval);
									} else { /*error*/
									}

									if (r.read() == 30) {
									} else {
										r.pushBack();
									}

									varVal = p;
								}

								// read DXF variables

								vars.append(new Object[] {varName, ""+varVal});

							}

							c = r.read();
						}
					}

					/*
					 * Skip the classes section, as it is only for the benefit
					 * of AutoCAD.
					 */
					else if (r.sval.equals("CLASSES")) { //$NON-NLS-1$
						c = r.read();
						while (!r.sval.equals("ENDSEC")) { //$NON-NLS-1$
							c = r.read();
						}
					}
					
					/*
					 * Skip the symbols section.
					 */
					else if (r.sval.equals("SYMBOL")) { //$NON-NLS-1$
						c = r.read();
						while (!r.sval.equals("ENDSEC")) { //$NON-NLS-1$
							c = r.read();
						}
					}

					/*
					 * Skip the blocks section.
					 * TODO: read blocks info!
					 */
					else if (r.sval.equals("BLOCKS")) { //$NON-NLS-1$
						c = r.read();
						while (!r.sval.equals("ENDSEC")) { //$NON-NLS-1$
							c = r.read();
						}
					}

					/*
					 * Read all AutoCAD entities and add the corresponding
					 * Magelan entities to the CoreModel.
					 */
					else if (r.sval.equals("ENTITIES")) { //$NON-NLS-1$
						c = r.read();
						while (c == 0) {
							// is it a line entity?
							if (r.sval.equals("LINE")) { //$NON-NLS-1$

								LineEntity o = new LineEntity();

								while ((c = r.read()) != 0) {
									switch (c) {
										case 100: {
											if (r.sval.equals("AcDbLine")) { //$NON-NLS-1$
											}
											 //$NON-NLS-1$

											break;
										}
										case 10:
											o.setX1(r.dval);
											break;
										case 20:
											o.setY1(-r.dval);
											break;
										case 30:
											break; // ignore Z
										case 11:
											o.setX2(r.dval);
											break;
										case 21:
											o.setY2(-r.dval);
											break;
										case 31:
											break; // ignore Z
										case 39:
											o.setThickness((float) r.dval);
											break;

										/*  210, 220, 230 */
										default:
											entityCommon(r, o, d); /* or error unrecognized code */
									}
								}

								d.add(o);
								if (c == 0) {
									r.pushBack(); // next entity
								}
							}
							// TODO: fill the solid 
							else if (r.sval.equals("SOLID")) { //$NON-NLS-1$

								PolyLineEntity o = new PolyLineEntity();

								// collect the coords
								double[] x = new double[4];
								double[] y = new double[4];

								while ((c = r.read()) != 0) {
									switch (c) {
										case 100: {
											if (r.sval.equals("AcDbTrace")) { //$NON-NLS-1$
											}
											 //$NON-NLS-1$

											break;
										}
										case 10:
											x[0] = r.dval;
											break;
										case 20:
											y[0] = -r.dval;
											break;
										case 30:
											break;
										case 11:
											x[1] = r.dval;
											break;
										case 21:
											y[1] = -r.dval;
											break;
										case 31:
											break;
										case 12:
											x[3] = r.dval;
											break;
										case 22:
											y[3] = -r.dval;
											break;
										case 32:
											break;
										case 13:
											x[2] = r.dval;
											break;
										case 23:
											y[2] = -r.dval;
											break;
										case 33:
											break;
										case 39:
											o.setThickness((float) r.dval);
											break;

										/*  210, 220, 230 */
										default:
											entityCommon(r, o, d); /* or error unrecognized code */
									}
								}

								// add the points to the polyline
								o.setPoints(x, y);
								o.setClosed(true);

								d.add(o);
								if (c == 0) {
									r.pushBack(); // next entity
								}
							}
							// translates to a simple line, as there is no 
							// equivalent of the XLINE in Magelan
							else if (r.sval.equals("XLINE")) { //$NON-NLS-1$

								LineEntity o = new LineEntity();

								while ((c = r.read()) != 0) {
									switch (c) {
										case 100: {
											if (r.sval.equals("AcDbXline")) { //$NON-NLS-1$
											}
											 //$NON-NLS-1$

											break;
										}
										case 10:
											o.setX1(r.dval);
											break;
										case 20:
											o.setY1(-r.dval);
											break;
										case 30:
											break;
										case 11:
											o.setX2(r.dval);
											break;
										case 21:
											o.setY2(-r.dval);
											break;
										case 31:
											break;
										default:
											entityCommon(r, o, d); /* or error unrecognized code */
									}
								}

								d.add(o);
								if (c == 0) {
									r.pushBack(); // next entity
								}
							}
							// is this a circle?
							else if (r.sval.equals("CIRCLE")) { //$NON-NLS-1$

								CircleEntity o = new CircleEntity();

								while ((c = r.read()) != 0) {
									switch (c) {
										case 100: {
											if (r.sval.equals("AcDbCircle")) { //$NON-NLS-1$
											}
											 //$NON-NLS-1$

											break;
										}
										case 10:
											o.setX(r.dval);
											break;
										case 20:
											o.setY(-r.dval);
											break;
										case 30:
											break;
										case 40:
											o.setRadius(r.dval);
											break;
										case 39:
											o.setThickness((float) r.dval);
											break;

										/*  210, 220, 230 */
										default:
											entityCommon(r, o, d); /* or error unrecognized code */
									}
								}

								d.add(o);
								if (c == 0) {
									r.pushBack(); // next entity
								}
							}
							// is this a point entity?
							else if (r.sval.equals("POINT")) { //$NON-NLS-1$

								PointEntity o = new PointEntity();

								while ((c = r.read()) != 0) {
									switch (c) {
										case 100: {
											if (r.sval.equals("AcDbPoint")) { //$NON-NLS-1$
											}
											 //$NON-NLS-1$

											break;
										}
										case 10:
											o.setX(r.dval);
											break;
										case 20:
											o.setY(-r.dval);
											break;
										case 30:
											break;
										case 39:
											o.setThickness((float) r.dval);
											break;

										/*  50, 210, 220, 230 */
										default:
											entityCommon(r, o, d); /* or error unrecognized code */
									}
								}

								d.add(o);
								if (c == 0) {
									r.pushBack(); // next entity
								}
							}
							// simplest one line text entity
							else if (r.sval.equals("RTEXT")) { //$NON-NLS-1$

								TextEntity o = new TextEntity();

								while ((c = r.read()) != 0) {
									switch (c) {
										case 100: {
											if (r.sval.equals("AcDbRText")) { //$NON-NLS-1$
											}
											 //$NON-NLS-1$

											break;
										}
										case 10:
											o.setX(r.dval);
											break;
										case 20:
											o.setY(-r.dval);
											break;
										case 30:
											break;
										case 50:
											o.setRotation((float) r.dval);
											break;
										case 40:
											o.setSize((float) r.dval);
											break;
										case 7:

											// TODO: add TextStyle support to CoreModel

											/* o.style = d.getTextStyle(r.sval);*/
											break;
										case 70: /*o.typeFlags = r.ival;*/
											break;
										case 1:
											o.setText(r.sval);
											break; // or importfilename

										/*210, 220, 230 */
										default:
											entityCommon(r, o, d); /* or error unrecognized code */
									}
								}

								d.add(o);
								if (c == 0) {
									r.pushBack(); // next entity
								}
							}
							// one line text; translates to Text
							else if (r.sval.equals("TEXT")) { //$NON-NLS-1$

								TextEntity o = new TextEntity();

								while ((c = r.read()) != 0) {
									switch (c) {
										case 100: {
											if (r.sval.equals("AcDbText")) { //$NON-NLS-1$
											}
											 //$NON-NLS-1$

											break;
										}
										case 10:
											o.setX(r.dval);
											break;
										case 20:
											o.setY(-r.dval);
											break;
										case 30:
											break;
										case 39:
											o.setThickness((float) r.dval);
											break;
										case 50:
											o.setRotation((float) -r.dval);
											break;
										case 40:
											o.setSize((float) r.dval);
											break;
										case 7:

											// TODO: add TextStyle support to CoreModel

											/* o.style = d.getTextStyle(r.sval);*/
											break;
										case 41: /*o.relXScale = r.dval;*/
											break;
										case 51:

											// TODO: using the shearing property

											/*o.obliqueAngle = r.dval; */
											break;
										case 1:
											o.setText(r.sval);
											break;
										case 71: /*o.textGenerationFlags = r.ival;*/
											break;
										case 72: /*o.hJustification = r.ival;*/
											break;
										case 11: /*o.jx = r.dval;*/
											break; // justification point
										case 21: /*o.jy = r.dval;*/
											break;
										case 31: /*o.jz = r.dval;*/
											break;
										case 73: /*o.vJustification = r.ival;*/
											break;

										/*210, 220, 230 */
										default:
											entityCommon(r, o, d); /* or error unrecognized code */
									}
								}

								d.add(o);
								if (c == 0) {
									r.pushBack(); // next entity
								}
							}
							// multiline text; translates to TEXT
							// TODO: separate lines
							else if (r.sval.equals("MTEXT")) { //$NON-NLS-1$

								TextEntity o = new TextEntity();

								while ((c = r.read()) != 0) {
									switch (c) {
										case 100: {
											if (r.sval.equals("AcDbMText")) { //$NON-NLS-1$
											}
											 //$NON-NLS-1$

											break;
										}
										case 10:
											o.setX(r.dval);
											break;
										case 20:
											o.setY(-r.dval);
											break;
										case 30:
											break;
										case 50:
											o.setRotation((float) r.dval);
											break; // *** !!! ***
										case 40:
											o.setSize((float) r.dval);
											break;
										case 41: /*o.rectWidth = r.dval;*/
											break;
										case 71: /*o.attachmentPoint = r.ival;*/
											break;
										case 72: /*o.drawingDirection = r.ival;*/
											break;
										case 1:
											o.setText(r.sval);
											break;
										case 3:
											o.setText(o.getText() + "\n" + //$NON-NLS-1$
													  r.sval);
											break; //$NON-NLS-1$
										case 7:

											// TODO: add TextStyle support to CoreModel

											/* o.style = d.getTextStyle(r.sval);*/
											break;
										case 11: /*o.dx = r.dval;*/
											break; // direction vector 
										case 21: /*o.dy = r.dval;*/
											break;
										case 31: /*o.dz = r.dval;*/
											break;
										case 42:
											break; // text width
										case 43:
											break; // entity height
										case 73: /*o.spacingStyle = r.ival;*/
											break;
										case 44: /*o.spacingFactor = r.dval;*/
											break;

										/*210, 220, 230 */
										default:
											entityCommon(r, o, d); /* or error unrecognized code */
									}
								}

								d.add(o);
								if (c == 0) {
									r.pushBack(); // next entity
								}
							}

							// TODO: currently ignores thickness changes - fix!
							else if (r.sval.equals("LWPOLYLINE")) { //$NON-NLS-1$

								PolyLineEntity o = new PolyLineEntity();
								int vert = -1; // vertex index
								int nVertices = 0;
								double[] x = null;
								double[] y = null;

								while ((c = r.read()) != 0) {
									switch (c) {
										case 100: {
											if (r.sval.equals("AcDbPolyline")) { //$NON-NLS-1$
											}
											 //$NON-NLS-1$

											break;
										}
										case 90:
											nVertices = (int) r.i32val; // feature - cannot read more than maxint vertices
											x = new double[nVertices];
											y = new double[nVertices];

											/*
											o.startWidth = new double[o.nVertices];
											o.endWidth = new double[o.nVertices];
											o.bulge = new double[o.nVertices];
											*/
											break;
										case 70:
											int polyLineFlag = r.ival;
											if ((polyLineFlag | 1) == polyLineFlag) {
												o.setClosed(true);
											}
											break;
										case 43: /*o.constantWidth = r.dval;*/
											break;
										case 39:
											o.setThickness((float) r.dval);
											break;
										case 38: /*o.elevation = r.dval;*/
											break;
										case 10:
											x[++vert] = r.dval;
											break;
										case 20:
											y[vert] = -r.dval;
											break;
										case 40: /*o.startWidth[vert] = r.dval;*/
											break;
										case 41: /*o.endWidth[vert] = r.dval;*/
											break;
										case 42: /*o.bulge[vert] = r.dval;*/
											break;

										/*  210, 220, 230 */
										default:
											entityCommon(r, o, d); /* or error unrecognized code */
									}
								}

								// add the points to the polyline
								int size = Math.min(x.length, y.length);

								for (int i = 0; i < size; i++)
									o.add(new PointEntity(x[i], y[i]));

								d.add(o);
								if (c == 0) {
									r.pushBack(); // next entity
								}
							}
							

							/*
							 * 
							 */
							else if (r.sval.equals("POLYLINE")) { //$NON-NLS-1$
								PolyLineEntity o = new PolyLineEntity();

								while ((c = r.read()) != 0) {
									switch (c) {
										case 100: break;
										case 10: break; // dummy
										case 20: break; // dummy
										case 30: break; // elevation
										case 39:
											o.setThickness((float) r.dval);
											break;
										case 70: // flag
											if ((r.ival | 1) == r.ival) {
												o.setClosed(true);
											}
											break;
										case 40: break;
										case 41: break;
										case 43: /*o.constantWidth = r.dval;*/
											break;
										case 71: break;
										case 72: break;
										case 73: break;
										case 74: break;
										case 75: break; // smoothness
										case 210: break;
										case 220: break;
										case 230: break;
										default:
											entityCommon(r, o, d); /* or error unrecognized code */
									}
								}
								
								// iterate vertices
								while (r.sval.equals("VERTEX")) { //$NON-NLS-1$
									// vertex coordinates
									double x = 0, y = 0;
									
									while ((c = r.read()) != 0) {
										switch (c) {
											case 100: break;
											case 10:
												x = r.dval;
												break;
											case 20:
												y = -r.dval;
												break;
											case 30: break; // z
											case 40: break; // start width 
											case 41: break; // end width
											case 42: break; // bulge
											case 70: break; // flag
											case 50: break;
											case 71: break;
											case 72: break;
											case 73: break;
											case 74: break;
											default:
												entityCommon(r, o, d); /* or error unrecognized code */
										}
									}
									
									// add the vertex to the pline
									o.add(x, y);
								}

								// add the polyline
								d.add(o);
								
								// ignore seqend definition
								if (c == 0 && r.sval.equals("SEQEND")) { //$NON-NLS-1$
									while ((c = r.read()) != 0) {}
								}
								else {
									log("SEQEND expected", r.getLineNumber()); //$NON-NLS-1$
								}
								
								if (c == 0) {
									r.pushBack(); // next entity
								}
								else {
									log("???", r.getLineNumber()); //$NON-NLS-1$
								}
							}
							
							// insert a block; currently blocks are not supported!
							// TODO: add blocks support!
							else if (r.sval.equals("INSERT")) { //$NON-NLS-1$

								//Insert o = new Insert();
								while ((c = r.read()) != 0) {
									/*
									switch (c) {
									  case 100: {
									    if (r.sval.equals("AcDbInsert")) {}
									    break;
									  }
									  case 66: o.attribFollowFlag = r.ival; break;
									  case 2: o.blockName = r.sval; break;
									  case 10: o.x = r.dval; break;
									  case 20: o.y = r.dval; break;
									  case 30: o.z = r.dval; break;
									  case 41: o.scaleX = r.dval; break;
									  case 42: o.scaleY = r.dval; break;
									  case 43: o.scaleZ = r.dval; break;
									  case 50: o.rotation = r.dval; break;
									  case 70: o.columnCount = r.ival; break;
									  case 71: o.rowCount = r.ival; break;
									  case 44: o.columnSpacing = r.dval; break;
									  case 45: o.rowSpacing = r.dval; break;
									  //   210, 220, 230
									  default: entityCommon(r, o, d);  // or error unrecognized code
									}
									*/
								}

								//d.add(o);
								if (c == 0) {
									r.pushBack(); // next entity
								}
							}
							// end of section reached?
							else if (r.sval.equals("ENDSEC")) { //$NON-NLS-1$
								break;
							}
							else {
								// skip unsupported entities!
								while ((c = r.read()) != 0) {
									log("Unsupported entity " + r.sval, r.getLineNumber()); //$NON-NLS-1$
								}

								r.pushBack();
							}

							c = r.read();
						}
					}

					// skip objects section
					else if (r.sval.equals("OBJECTS")) { //$NON-NLS-1$
						c = r.read();
						while (!r.sval.equals("ENDSEC")) { //$NON-NLS-1$
							c = r.read();
						}
					}

					/*
					 * The tables section contains layer, linestyle and other
					 * definitions.
					 */
					else if (r.sval.equals("TABLES")) { //$NON-NLS-1$
						
						// read table definitions
						c = r.read();
						while (r.sval.equals("TABLE")) { //$NON-NLS-1$

							String tableName = ""; //$NON-NLS-1$
							
							while ((c = r.read()) != 0) {
								switch (c) {
								  case -1: break;
								  case 2: 
								  	tableName = r.sval;
								  	break;
								  case 5: break;
								  case 102: break;
								  case 360: break;
								  case 330: break;
								  case 100: break;
								  case 70: break;
								  default: // error unrecognized code
								}
							}
							//r.pushBack();
							
							/*
							 * Layer table definition
							 */
							if (tableName.equals("LAYER")) { //$NON-NLS-1$

								while (r.sval.equals("LAYER")) { //$NON-NLS-1$
									
									Layer l = new Layer();
									
									while ((c = r.read()) != 0) {
										switch (c) {
											case 100: break;
											case 2: // name
												l.setName(r.sval);
												break;
											case 70: break;
											case 62: // color
												l.setColor(toColor(r.ival));
												break;
											case 6: // linetype
												LineStyle ls = (LineStyle) d.getFeature(LineStyle.class, r.sval);
												if (ls != null) {
													l.setLineStyle(ls);
												}
												break;
											case 290: break; // do not plot if 0
											case 370: 
												l.setThickness((float) r.dval);
												break;
											case 390: break;
											default: // error unrecognized code
										}
									}
									
									if (!l.getName().equals("0")) { //$NON-NLS-1$
										d.addFeature(l);
									}
								}
							}
							
							/*
							 * Skip the other tables
							 */
							else {
								while (!r.sval.equals("TABLE") && !r.sval.equals("ENDSEC")) { c = r.read(); } //$NON-NLS-1$ //$NON-NLS-2$
							}
						}
						
					}	
				} else {
					log("Section name expected", r.getLineNumber()); //$NON-NLS-1$
				}
			} else { 
				log("Section expected", r.getLineNumber()); //$NON-NLS-1$
			}

			// !! 
			c = r.read();
		}
		
		userLog.info(lang.getString("DXFDecoder.log.completed")); //$NON-NLS-1$
		
		//AbstractDataSource.dump(handles, System.out);
	}

	/**
	 * Decodes groupcodes common for all entities.
	 */
	private static void entityCommon(DXFReader r, Entity o, CoreModel d) {
		switch (r.groupCode) {
			case 5:
				if (r.sval == null && r.sval.equals("")) break;
				
				DataSource handles = (DataSource) d.getFeature(DataSource.class, DS_HANDLES);
				handles.append(new Object[] {o, r.sval});

				break;
			case 330: /*o.owner = r.sval;*/
				break;
			case 102: /* skip 'em */
				break;
			case 100: {
				if (r.sval.equals("AcDbEntity")) { //$NON-NLS-1$
				}
				 //$NON-NLS-1$

				break;
			}
			case 67: /*o.entityPlace = r.ival;*/
				break;
			case 410: /*o.layoutName = r.sval;*/
				break;
			case 8:

				/*
				 * create a default layer with the same name,
				 * if cannot find the real one
				 */
				Layer l = (Layer) d.getFeature(Layer.class, r.sval);

				if (l == null) {
					l = new Layer(r.sval);
				}

				o.setLayer(l);
				break;
			case 6:

				/*
				 * create a default linestyle with the same name,
				 * if cannot find the real one
				 */
				List styles = d.getFeatures(LineStyle.class);
				Iterator iter = styles.iterator();
				while (iter.hasNext()) {
					LineStyle ls = (LineStyle) iter.next();
					if (ls.getName().equalsIgnoreCase(r.sval)) {
						o.setLineStyle(ls);
					}
				}
				if (o.getLineStyle() == null) {
					o.setLineStyle(new DefaultLineStyle(r.sval));
				}
				break;
			case 62:
				o.setColor(toColor(r.ival));
				break;
			case 48:

				// TODO: add support in the Entity interface

				/*o.lineTypeScale = r.dval;*/
				break;
			case 60: /*o.visible = r.ival;*/
				break;

			/* -1  360  92 310 */
		}
	}
	
	/**
	 * Converts an AutoCAD color value in the range 1 - 255 to its RGB equivalent.
	 * 
	 * @param ival color value 1 - 255
	 * @return a <code>Color</code> instance
	 */
	public static Color toColor(int ival) {
		// if < 1 or > 255 do not throw an exception but return white
		if (ival < 1 || ival > 255) return Color.white;
		
		// basic AutoCAD colors
		if (ival < 9) {
			return COLOR_MAP[ival];
		}
		
		// basic gray colors
		if (ival > 249) {
			return COLOR_MAP[ival-240];
		}
		
		// 240 palette colors
		int factor[] = new int[] { 255, 165, 127, 76, 38 };
		float r0[] = new float[] { 1, 1, 1, 1, 1, 0.75f, 0.5f, 0.25f, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.25f, 0.5f, 0.75f, 1, 1, 1, 1 };
		float g0[] = new float[] { 0, 0.25f, 0.5f, 0.75f, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0.75f, 0.5f, 0.25f, 0, 0, 0, 0, 0, 0, 0, 0 };
		float b0[] = new float[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.25f, 0.5f, 0.75f, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0.75f, 0.5f, 0.25f };		
		int r, g, b;
		int i = ival - 10;
		int col = i / 24;
		int f = factor[(i % 10)/2];
		if (i % 2 == 0) {
			r = (int) (r0[col] * f);
			g = (int) (g0[col] * f);
			b = (int) (b0[col] * f);
		}
		else {
			r = (int) ((r0[col]/2 + 0.5) * f);
			g = (int) ((g0[col]/2 + 0.5) * f);
			b = (int) ((b0[col]/2 + 0.5) * f);
		}
		return new Color(r, g, b);
	}
	
	protected static void log(String msg, int ln) {
		log.debug(msg + lang.getString("DXFDecoder.log.line") + ln); //$NON-NLS-1$
		
		// user log
		//LogItem li = new LogItem(msg, LogItem.LEVEL_ERROR);
		//li.put(lang.getString("DXFDecoder.log.Line"), new Integer(ln)); //$NON-NLS-1$
		//userLog.addItem(li);
	}
}