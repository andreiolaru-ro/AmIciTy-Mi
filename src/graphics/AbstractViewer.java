package graphics;

import java.awt.Component;

import javax.swing.JFrame;

import KCAAgent.EnvironmentKCA;

public abstract class AbstractViewer implements ControllableView, UpdateListener {
	protected EnvironmentKCA cm;
	protected Object data;
	
	protected JFrame frame;
	
	@SuppressWarnings("hiding")
	protected AbstractViewer(EnvironmentKCA cm, Object data) {
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
	
	@SuppressWarnings("hiding")
	@Override
	public void relink(EnvironmentKCA cm)
	{
		if(this.cm != cm)
		{
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
	
	public AbstractViewer setTitle(String title) {
		frame.setTitle(title);
		return this;
	}
}
