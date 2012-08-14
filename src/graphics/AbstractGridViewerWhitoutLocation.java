package graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import agent.AbstractAgent;
import agent.LocationAgent;
import base.Environment;

public abstract class AbstractGridViewerWhitoutLocation<ENVIRONMENT extends Environment<?,?>>  extends AbstractViewer2D<ENVIRONMENT> implements MouseListener {

	int w;
	int h;
	protected AbstractGridViewerWhitoutLocation(ENVIRONMENT cm, Object data)
	{
		super(cm, data);
		setSize(120, 150);
		addMouseListener(this);
	}
	
	protected AbstractGridViewerWhitoutLocation(ENVIRONMENT cm)
	{
		super(cm,null);
	}

	public abstract Color getColor(AbstractAgent cell);	

	@Override
	protected void draw(Graphics2D g)
	{
		// TODO Auto-generated method stub
		w = getWidth();
		h = getHeight();
		double dw = 0.7 * w / Math.sqrt(cm.getAgents().size());
		double dh = 0.7 * h / Math.sqrt(cm.getAgents().size());
		
		g.setBackground(Color.white);
		g.clearRect(0, 0, w, h);
		int nbRow=0;
		int nbCol=0;
		for (AbstractAgent cell : cm.getAgents()) {
			if((nbRow+1/5)*cm.getAgents().size()<cell.getId().id.intValue())
			{
				nbRow++;
				nbCol=0;
			}
				double x = (nbRow - cm.x) * w / cm.width;
				double y = (nbCol - cm.y) * h / cm.height;
				System.out.println(x+" "+y);
				nbCol++;
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
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
		int mx = e.getX();
		int my = e.getY();
		double x = cm.x + mx * cm.width / w;
		double y = cm.y + my * cm.height / h;
//		System.out.println(i + " " + j);
		if(x >= 0 && x < cm.width && y >= 0 && y < cm.height)
			cm.cellAt(x, y).toggleSelected();
		
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseExited(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mousePressed(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

}
