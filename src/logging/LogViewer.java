package logging;

import graphics.AbstractViewer;

import java.awt.TextArea;


import base.Environment;

public class LogViewer extends AbstractViewer {
	Logger logger;
	TextArea text;
	
	@SuppressWarnings("hiding")
	public LogViewer(Environment cm) {
		super(cm, null);
		this.logger = cm.getLogger();
		text = new TextArea();
		addDrawer(text);
		setSize(600, 600);
	}
	
	@Override
	public void update() {
//		text.setText(logger.getEntries());
		text.setText(logger.printEntries());
		text.append(".");	// should make it always scroll down. 
	}
}
