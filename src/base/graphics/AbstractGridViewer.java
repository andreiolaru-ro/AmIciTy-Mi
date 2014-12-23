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

import base.Environment;
import base.agent.LocationAgent;

public abstract class AbstractGridViewer<ENVIRONMENT extends Environment<?, ? extends LocationAgent>, AGENT extends LocationAgent>
		extends AbstractViewer2D<ENVIRONMENT> implements MouseListener {
	int	w;
	int	h;

	protected AbstractGridViewer(ENVIRONMENT cm, Object data) {
		super(cm, data);
		setSize(120, 150);
		addMouseListener(this);
	}

	protected AbstractGridViewer(ENVIRONMENT cm) {
		this(cm, null);
	}

	public abstract Color getColor(AGENT cell);

	@Override
	public void draw(Graphics2D g) {
		w = getWidth();
		h = getHeight();
		double dw = 0.7 * w / Math.sqrt(cm.getAgents().size());
		double dh = 0.7 * h / Math.sqrt(cm.getAgents().size());

		g.setBackground(Color.white);
		g.clearRect(0, 0, w, h);
		for (LocationAgent cell : cm.getAgents()) {
			double x = (cell.getLocation().getX() - cm.x) * w / cm.width;
			double y = (cell.getLocation().getY() - cm.y) * h / cm.height;

			Color color = getColor((AGENT) cell);
			g.setColor(color);
			g.fillRect((int) x, (int) y, (int) dw, (int) dh);
			// g.fill(new Rectangle2D.Double(x, y, dw, dh));
			// g.fill(new Ellipse2D.Double(x, y, dw, dh));
			if (cell.isSelected()) {
				g.setColor(new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color
						.getBlue()));
				// g.fill(new Ellipse2D.Double(x, y, 2, 2));
				g.fillRect((int) x, (int) y, 2, 2);
				// g.fill(new Rectangle2D.Double(x, y, 2, 2));
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
	public void mouseEntered(MouseEvent e) {
		//
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//
	}

}
