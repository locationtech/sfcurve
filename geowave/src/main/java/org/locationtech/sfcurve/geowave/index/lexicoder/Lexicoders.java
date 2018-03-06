/*******************************************************************************
 * Copyright (c) 2013-2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License,
 * Version 2.0 which accompanies this distribution and is available at
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 ******************************************************************************/
package org.locationtech.sfcurve.geowave.index.lexicoder;

/**
 * A class containing instances of lexicoders.
 *
 */
public class Lexicoders
{
	public static final ShortLexicoder SHORT = new ShortLexicoder();
	public static final IntegerLexicoder INT = new IntegerLexicoder();
	public static final LongLexicoder LONG = new LongLexicoder();
	public static final DoubleLexicoder DOUBLE = new DoubleLexicoder();
}
