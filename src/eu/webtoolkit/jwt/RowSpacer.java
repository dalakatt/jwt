/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt;

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

class RowSpacer extends WWebWidget {
	private static Logger logger = LoggerFactory.getLogger(RowSpacer.class);

	public RowSpacer(WTreeViewNode node, int height) {
		super();
		this.node_ = node;
		this.height_ = 0;
		this.resize(WLength.Auto, new WLength(0));
		this.setInline(false);
		this.setStyleClass("Wt-spacer");
	}

	public void setRows(int height, boolean force) {
		if (height == 0) {
			if (this != null)
				this.remove();
		} else {
			if (force || height != this.height_) {
				this.height_ = height;
				this.resize(WLength.Auto, WLength.multiply(this.node_.getView()
						.getRowHeight(), height));
			}
		}
	}

	public final void setRows(int height) {
		setRows(height, false);
	}

	public int getRows() {
		return this.height_;
	}

	public WTreeViewNode getNode() {
		return this.node_;
	}

	public int renderedRow(int lowerBound, int upperBound) {
		WTreeViewNode n = this.getNode();
		int result = 0;
		if (this == n.bottomSpacer()) {
			result = n.getChildrenHeight() - n.getBottomSpacerHeight();
		}
		if (result > upperBound) {
			return result;
		} else {
			return result
					+ n.renderedRow(lowerBound - result, upperBound - result);
		}
	}

	public final int renderedRow() {
		return renderedRow(0, Integer.MAX_VALUE);
	}

	public final int renderedRow(int lowerBound) {
		return renderedRow(lowerBound, Integer.MAX_VALUE);
	}

	DomElementType getDomElementType() {
		return DomElementType.DomElement_DIV;
	}

	private WTreeViewNode node_;
	private int height_;
}
