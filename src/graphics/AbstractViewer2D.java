package graphics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;

import base.Environment;

public abstract class AbstractViewer2D extends AbstractViewer {
	private Canvas canvas;
	private BufferStrategy strategy;
	
	@SuppressWarnings("hiding")
	protected AbstractViewer2D(Environment cm, Object data) {
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
