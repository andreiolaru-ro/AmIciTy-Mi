package graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import KCAAgent.EnvironmentKCA;
import KCAAgent.KCAAgent;

public abstract class AbstractGridViewer extends AbstractViewer2D implements MouseListener {
	int w;
	int h;
	
	@SuppressWarnings("hiding")
	protected AbstractGridViewer(EnvironmentKCA cm, Object data) {
		super(cm, data);
		setSize(120, 150);
		addMouseListener(this);
	}
	
	@SuppressWarnings("hiding")
	protected AbstractGridViewer(EnvironmentKCA cm) {
		this(cm, null);
	}
	
	public abstract Color getColor(KCAAgent cell);

	@Override
	public void draw(Graphics2D g) {
		w = getWidth();
		h = getHeight();
		double dw = 0.7 * w / Math.sqrt(cm.getAgents().size());
		double dh = 0.7 * h / Math.sqrt(cm.getAgents().size());
		
		g.setBackground(Color.white);
		g.clearRect(0, 0, w, h);
		for (KCAAgent cell : cm.getAgents()) {
			double x = (cell.getLocation().getX() - cm.x) * w / cm.width;
			double y = (cell.getLocation().getY() - cm.y) * h / cm.height;
			
			Color color = getColor(cell);
			g.setColor(color);
			g.fillRect((int)x, (int)y, (int)dw, (int)dh);
//			g.fill(new Rectangle2D.Double(x, y, dw, dh));
//			g.fill(new Ellipse2D.Double(x, y, dw, dh));
			if (cell.isSelected()) {
				g.setColor(new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue()));
//				g.fill(new Ellipse2D.Double(x, y, 2, 2));
				g.fillRect((int)x, (int)y, 2, 2);
//				g.fill(new Rectangle2D.Double(x, y, 2, 2));
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		double x = cm.x + mx * cm.width / w;
		double y = cm.y + my * cm.height / h;
//		System.out.println(i + " " + j);
		if(x >= 0 && x < cm.width && y >= 0 && y < cm.height)
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
