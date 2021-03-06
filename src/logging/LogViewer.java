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
package logging;

import java.awt.TextArea;

import base.Environment;
import base.graphics.AbstractViewer;

public class LogViewer<ENVIRONMENT extends Environment<?, ?>> extends AbstractViewer<ENVIRONMENT> {
	Logger		logger;
	TextArea	text;

	public LogViewer(ENVIRONMENT cm) {
		super(cm, null);
		this.logger = cm.getLogger();
		text = new TextArea();
		addDrawer(text);
		setSize(600, 600);
	}

	@Override
	public void update() {
		// text.setText(logger.getEntries());
		text.setText(logger.printEntries());
		text.append("."); // should make it always scroll down.
	}
}
