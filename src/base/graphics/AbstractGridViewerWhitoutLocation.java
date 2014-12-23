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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import P2PAgent.P2PAgent;
import base.Environment;
import base.agent.AbstractAgent;
import base.agent.Location;

public abstract class AbstractGridViewerWhitoutLocation<ENVIRONMENT extends Environment<?, ?>>
		extends AbstractViewer2D<ENVIRONMENT> implements MouseListener {

	int											w;
	int											h;
	private static Map<AbstractAgent, Location>	LocationOnTheGrid;

	protected AbstractGridViewerWhitoutLocation(ENVIRONMENT cm, Object data) {
		super(cm, data);
		setSize(120, 150);
		LocationOnTheGrid = new HashMap<AbstractAgent, Location>();
		addMouseListener(this);

	}

	protected AbstractGridViewerWhitoutLocation(ENVIRONMENT cm) {
		this(cm, null);
	}

	public abstract Color getColor(AbstractAgent cell);

	@Override
	protected void draw(Graphics2D g) {
		w = getWidth();
		h = getHeight();
		g.setBackground(Color.white);
		g.clearRect(0, 0, w, h);
		double nbRow = 0;
		double nbCol = 0;
		double number = ((int) Math.sqrt(cm.getAgents().size()));
		int idAgent = 0;
		P2PAgent cell = null;
		while (idAgent < cm.getAgents().size()) {
			cell = (P2PAgent) cm.getAgent(idAgent);
			if ((cell.getId().getId().doubleValue() / number) == 1) {
				nbRow++;
				nbCol = 0;
				number = number + (int) Math.sqrt(cm.getAgents().size());
			}
			double x = (nbRow - cm.x) * w / cm.width;
			double y = (nbCol - cm.y) * h / cm.height;
			// System.out.println(x+" "+y);
			LocationOnTheGrid.put(cell, new Location(nbRow, nbCol));
			nbCol++;
			idAgent++;
			Color color = getColor(cell);
			g.setColor(color);
			// g.fillRect((int)x, (int)y, (int)dw, (int)dh);
			// g.fill(new Rectangle2D.Double(x, y, dw, dh));
			// g.fill(new Ellipse2D.Double(x, y, dw, dh));
			if (cell.isSelected()) {
				g.setColor(Color.RED);
				// g.fill(new Ellipse2D.Double(x, y, 2, 2));
				g.fillRect((int) x, (int) y, 2, 2);
				// g.fill(new Rectangle2D.Double(x, y, 2, 2));
			} else {
				g.setColor(Color.BLACK);
				// g.fill(new Ellipse2D.Double(x, y, 2, 2));
				g.fillRect((int) x, (int) y, 2, 2);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		double x = cm.x + mx * cm.width / w;
		double y = cm.y + my * cm.height / h;
		// System.out.println(i + " " + j);
		if (x >= 0 && x < cm.width && y >= 0 && y < cm.height)
			cm.cellAt(x, y).toggleSelected();

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}

	public static Map<AbstractAgent, Location> getLocationOnTheGrid() {
		return LocationOnTheGrid;
	}

}
