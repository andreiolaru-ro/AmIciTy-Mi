/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru. See the AUTHORS file for more information.
 * 
 * This file is part of AmIciTy-Mi.
 * 
 * AmIciTy-Mi is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * AmIciTy-Mi is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with AmIciTy-Mi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package base.graphics;

public abstract class ViewerFactory {
	protected static double	balanceMinimum	= 0.4;

	public enum Type {
		CONTROL,

		// AGENT_DETAILS,

		// SELECTED_AGENT_DETAILS,

		AGENT_SELECTION_GRID,

		SPECIALTY_GRID,

		PRESSURE_GRID,

		FACTS_GRID,

		DOMAIN_INTEREST_GRID,

		PAUSE_GRID,

		// DATA_GRID,

		PRESSURE_SURFACE,

		DATA_FACTS_SURFACE,

		LOG_VIEWER,

		GLOBAL_FACT_NUMBER_GRAPH,

		GLOBAL_GOAL_NUMBER_GRAPH,

		GLOBAL_PRESSURE_GRAPH,

		MAX_PRESSURE_GRAPH,

		MESSAGE_AVG_GRAPH,

		USELESS_FACTS_AVG_GRAF,

		AGENT_BALANCE,

		AGENT_BALANCE_AVG_GRAF,

		ITEM_GRAF,

		ITEM_WANTED_GRAF,

		ITEM_LOCATION_GRAF
	}

	public static class WindowLayout {
		public class Row {
			int					commonH;
			int					commonW;
			Type				commonType;
			Object				specificParam;
			int					lastX;

			int					y;
			int					nWindows;
			WindowParameters	windows[]	= new WindowParameters[20]; // viewers
																		// in
																		// this
																		// row

			protected Row(int x, int y, int commonH, int commonW, Type commonType, int optimizeFor,
					Object specific) {
				if (commonH <= 0)
					commonH = rowH;
				this.commonH = commonH;
				if (commonW <= 0)
					commonW = (totalW * (100 - mainPercent)) / 100 / optimizeFor;
				this.commonW = commonW;
				this.commonType = commonType;
				lastX = x;
				this.y = y;
				this.specificParam = specific;
			}

			public void add(int W, Type type, Object data, boolean makesquare) {
				int H = commonH;
				if (nWindows >= 20)
					return;
				if (W <= 0)
					W = commonW;
				if (type == null)
					type = commonType;
				if (makesquare)
					H = W = Math.min(W, H);
				windows[nWindows++] = new WindowParameters(type, lastX, y, W, H, data,
						specificParam);
				lastX += W;

				// System.out.println("new window " + nWindows + " (" + type +
				// "):" + lastX + ":" + y + " | " + W + ":" + H);
			}

			public void add(WindowParameters win) {
				if (nWindows >= 20)
					return;
				if (win.getX() < 0)
					win.setX(lastX);
				if (win.getY() < 0)
					win.setY(y);
				if (win.getWidth() <= 0)
					win.setWidth(commonW);
				if (win.getHeight() <= 0)
					win.setHeight(commonH);
				if (generalMakeSquare && (win.getWidth() == commonW || win.getHeight() == commonH))
					win.setWidth(win.setHeight(Math.min(win.getWidth(), win.getHeight())));
				windows[nWindows++] = win;
				lastX += win.getWidth();

				// System.out.println("new window " + nWindows + " (" + win.type
				// + "):" + win.x + ":" + win.y + " | " + win.width + ":" +
				// win.height);
			}

			public void add(Object data) {
				add(0, null, data, generalMakeSquare);
			}
		}

		Row					rows[]				= new Row[10];				// rows
																			// of
																			// system
																			// viewers
		WindowParameters	mains[]				= new WindowParameters[5];	// main
																			// windows
																			// (control,
																			// events,
																			// etc)
		int					x, y;
		int					totalW, totalH;								// total
																			// area
																			// occupied
																			// by
																			// the
																			// viewers
		int					mainPercent;									// percent
																			// of
																			// total
																			// width
																			// taken
																			// by
																			// control
																			// window
																			// and
																			// other
																			// mains
		int					nrows				= 0, nmains = 0;
		int					optimizeRowsFor		= 1;
		int					optimizeMainsFor	= 1;
		int					rowH				= 100;
		boolean				generalMakeSquare	= false;
		int					controlH			= 100;
		int					lastY				= 0, lastMainY = 0;

		public WindowLayout(int x, int y, int W, int H, int mainPercent, int nMainsIndication,
				int rowH, boolean isRowCount, boolean makeSquare) {
			totalW = W;
			totalH = H;
			this.x = x;
			this.y = y;
			this.mainPercent = mainPercent;
			optimizeMainsFor = nMainsIndication;
			optimizeRowsFor = 1;
			if (isRowCount)
				optimizeRowsFor = rowH;
			else
				this.rowH = rowH;
			generalMakeSquare = makeSquare;
		}

		public void addMain(WindowParameters window) {
			if (nmains >= 5)
				return;
			if (window.getX() < 0)
				window.setX(x);
			if (window.getY() < 0)
				window.setY(lastMainY);
			if (window.getHeight() <= 0) {
				if (window.getType() == Type.CONTROL)
					window.setHeight(controlH);
				else
					window.setHeight((totalH - controlH) / optimizeMainsFor);
			}
			lastMainY += window.getHeight();
			if (window.getWidth() <= 0)
				window.setWidth(mainPercent * totalW / 100);
			mains[nmains++] = window;
		}

		public Row addRow(int H, int W, Type type, int windowCountIndication, Object specific) {
			if (nrows >= 10)
				return null;
			if (H == 0)
				H = totalH / optimizeRowsFor;
			rows[nrows++] = new Row(x + (mainPercent * totalW / 100), lastY, H, W, type,
					windowCountIndication, specific);
			lastY += H;
			return rows[nrows - 1];
		}

		public Row addRow(Type type, int windowCountIndication) {
			return addRow(0, 0, type, windowCountIndication, null);
		}

		public Row addRow(Type type, int windowCountIndication, Object specific) {
			return addRow(0, 0, type, windowCountIndication, specific);
		}

		// this is sooooooooo C++
		public WindowParameters[] toCollection() {
			// count
			int count = nmains;
			int i, j;
			for (i = 0; i < nrows; i++)
				count += rows[i].nWindows;
			WindowParameters[] ret = new WindowParameters[count];

			// set
			count = 0;
			for (i = 0; i < nmains; i++)
				ret[count++] = mains[i];
			for (i = 0; i < nrows; i++)
				for (j = 0; j < rows[i].nWindows; j++)
					ret[count++] = rows[i].windows[j];

			return ret;
		}
	}

	public static class WindowParameters {
		private Type	type;
		private int		x;
		private int		y;
		private int		width;
		private int		height;
		private Object	data;
		private Object	specific;

		public WindowParameters(Type type, int x, int y, int width, int height, Object data,
				Object specific) {
			this.setType(type);
			this.setX(x);
			this.setY(y);
			this.setWidth(width);
			this.setHeight(height);
			this.setData(data);
			this.setSpecific(specific);
		}

		public WindowParameters(Type type, int x, int y, int width, int height) {
			this(type, x, y, width, height, null, null);
		}

		public WindowParameters(Type type, int x, int y, Object data) {
			this(type, x, y, 0, 0, data, null);
		}

		public WindowParameters(Type type, int x, int y) {
			this(type, x, y, 0, 0, null, null);
		}

		public WindowParameters(Type type) {
			this(type, -1, -1, 0, 0, null, null);
		}

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}

		public Object getSpecific() {
			return specific;
		}

		public void setSpecific(Object specific) {
			this.specific = specific;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getHeight() {
			return height;
		}

		public int setHeight(int height) {
			this.height = height;
			return height;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}
	}

}
