/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt.chart;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.lang.ref.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.*;
import javax.servlet.*;
import eu.webtoolkit.jwt.*;
import eu.webtoolkit.jwt.chart.*;
import eu.webtoolkit.jwt.utils.*;
import eu.webtoolkit.jwt.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enumeration that specifies options for the labels.
 * <p>
 * 
 * @see WPieChart#setDisplayLabels(EnumSet options)
 */
public enum LabelOption {
	/**
	 * Do not display labels (default).
	 */
	NoLabels,
	/**
	 * Display labels inside each segment.
	 */
	Inside,
	/**
	 * Display labels outside each segment.
	 */
	Outside,
	/**
	 * Display the label text.
	 */
	TextLabel,
	/**
	 * Display the value (as percentage).
	 */
	TextPercentage;

	/**
	 * Returns the numerical representation of this enum.
	 */
	public int getValue() {
		return ordinal();
	}
}
