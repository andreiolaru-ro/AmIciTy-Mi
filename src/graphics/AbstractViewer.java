package graphics;

import java.awt.Component;

import javax.swing.JFrame;

import base.Environment;


public abstract class AbstractViewer<ENVIRONMENT extends Environment<?,?>> implements ControllableView<ENVIRONMENT>, UpdateListener {
	protected ENVIRONMENT cm;
	protected Object data;
	
	protected JFrame frame;

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
	public void relink(ENVIRONMENT cm)
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
	
	public AbstractViewer<ENVIRONMENT> setTitle(String title) {
		frame.setTitle(title);
		return this;
	}
}
