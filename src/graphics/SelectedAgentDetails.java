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
package graphics;

import KCAAgent.EnvironmentKCA;

public class SelectedAgentDetails extends AgentDetails {
	@SuppressWarnings("hiding")
	public SelectedAgentDetails(EnvironmentKCA cm) {
		super(cm);
	}

	// @Override
	// protected void draw(Graphics2D g) {
	// int w = getWidth();
	// int h = getHeight();
	// g.clearRect(0, 0, w, h);
	// int n = cm.getSelected().size();
	// if (n == 0) {
	// return;
	// }
	// int k = 1 + (int) Math.floor(Math.sqrt(n - 1));
	// int padding = 5;
	// int dw = (w - padding) / k - padding;
	// int dh = (h - padding) / k - padding;
	// int x = (w - k * dw - (k - 1) * padding) / 2;
	// int y = (h - k * dh - (k - 1) * padding) / 2;
	// for (int i = 0; i < n; i++) {
	// drawCell(g, cm.getSelected().get(i), x + i % k * (dw + padding), y + i /
	// k * (dh + padding), dw, dh);
	// }
	// }
}
