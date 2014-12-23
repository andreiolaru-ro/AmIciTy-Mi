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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;

import base.Environment;

public abstract class AbstractViewer2D<ENVIRONMENT extends Environment<?, ?>> extends
		AbstractViewer<ENVIRONMENT> {
	private Canvas			canvas;
	private BufferStrategy	strategy;

	protected AbstractViewer2D(ENVIRONMENT cm, Object data) {
		super(cm, data);
		canvas = new Canvas() {
			/**
			 * 
			 */
			private static final long	serialVersionUID	= 9055198544864717871L;

			@Override
			public void paint(Graphics g) {
				draw((Graphics2D) g);
				drawTitle(g);
			}
		};
		canvas.setBackground(Color.black);
		addDrawer(canvas);
	}

	void drawTitle(Graphics g) {
		g.setColor(Color.gray);
		int x = g.getFontMetrics().stringWidth(frame.getTitle()) / 2;
		g.drawString(frame.getTitle(), getWidth() / 2 - x, 15);
	}

	protected abstract void draw(Graphics2D g);

	protected int getWidth() {
		return canvas.getWidth();
	}

	protected int getHeight() {
		return canvas.getHeight();
	}

	protected void addMouseListener(MouseListener listener) {
		canvas.addMouseListener(listener);
	}

	@Override
	public void update() {
		if (strategy == null) {
			canvas.createBufferStrategy(2);
			strategy = canvas.getBufferStrategy();
		}
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		draw(g);
		drawTitle(g);
		strategy.show();
	}
}
