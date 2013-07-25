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

import java.awt.Component;

import javax.swing.JFrame;

import base.Environment;

public abstract class AbstractViewer<ENVIRONMENT extends Environment<?, ?>> implements
		ControllableView<ENVIRONMENT>, UpdateListener {
	protected ENVIRONMENT	cm;
	protected Object		data;

	protected JFrame		frame;

	protected AbstractViewer(ENVIRONMENT cm, Object data) {
		this.data = data;
		frame = new JFrame();
		relink(cm);
	}

	protected void addDrawer(Component drawer) {
		frame.add(drawer);
	}

	@Override
	public void setLocation(int x, int y) {
		frame.setLocation(x, y);
	}

	@Override
	public void setSize(int width, int height) {
		frame.setSize(width, height);
	}

	@Override
	public void relink(ENVIRONMENT cm) {
		if (this.cm != cm) {
			this.cm = cm;
			cm.addUpdateListener(this);
		}
	}

	@Override
	public void show() {
		frame.setVisible(true);
		update();
	}

	@Override
	public void hide() {
		cm.removeUpdateListener(this);
		frame.setVisible(false);
	}

	public AbstractViewer<ENVIRONMENT> setTitle(String title) {
		frame.setTitle(title);
		return this;
	}
}
