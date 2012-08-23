package graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import P2PAgent.P2PAgent;
import agent.AbstractAgent;
import base.Environment;
import agent.Location;

public abstract class AbstractGridViewerWhitoutLocation<ENVIRONMENT extends Environment<?,?>>  extends AbstractViewer2D<ENVIRONMENT> implements MouseListener {

	int w;
	int h;
	private static Map<AbstractAgent, Location> LocationOnTheGrid;

	protected AbstractGridViewerWhitoutLocation(ENVIRONMENT cm, Object data)
	{
		super(cm, data);
		setSize(120, 150);
		LocationOnTheGrid = new HashMap<AbstractAgent, Location>();
		addMouseListener(this);
		
	}
	
	protected AbstractGridViewerWhitoutLocation(ENVIRONMENT cm)
	{
		this(cm,null);
	}

	public abstract Color getColor(AbstractAgent cell);	

	@Override
	protected void draw(Graphics2D g)
	{
		// TODO Auto-generated method stub
		w = getWidth();
		h = getHeight();
		g.setBackground(Color.white);
		g.clearRect(0, 0, w, h);
		double nbRow=0;
		double nbCol=0;
		double number= (double)((int) Math.sqrt(cm.getAgents().size()));
		int idAgent=0;
		P2PAgent cell = null;
		while(idAgent<cm.getAgents().size()) {
			cell = (P2PAgent) cm.getAgent(idAgent);
			if((cell.getId().getId().doubleValue()/number)==1)
			{
				nbRow++;
				nbCol=0;
				number=number+(int) Math.sqrt(cm.getAgents().size());
			}
			double x = (nbRow - cm.x) * w / cm.width;
			double y = (nbCol - cm.y) * h / cm.height;
				//System.out.println(x+" "+y);
			LocationOnTheGrid.put(cell, new Location(nbRow, nbCol));
			nbCol++;
			idAgent++;
			Color color = getColor(cell);
			g.setColor(color);
			//g.fillRect((int)x, (int)y, (int)dw, (int)dh);
//			g.fill(new Rectangle2D.Double(x, y, dw, dh));
//			g.fill(new Ellipse2D.Double(x, y, dw, dh));
			if (cell.isSelected()) {
				g.setColor(Color.RED);
//				g.fill(new Ellipse2D.Double(x, y, 2, 2));
				g.fillRect((int)x, (int)y, 2, 2);
//				g.fill(new Rectangle2D.Double(x, y, 2, 2));
			}
			else
			{
				g.setColor(Color.BLACK);
//				g.fill(new Ellipse2D.Double(x, y, 2, 2));
				g.fillRect((int)x, (int)y, 2, 2);
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
	
	public static Map<AbstractAgent, Location> getLocationOnTheGrid()
	{
		return LocationOnTheGrid;
	}

}
