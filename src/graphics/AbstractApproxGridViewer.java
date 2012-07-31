package graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Vector;

import KCAAgent.KCAAgent;
import base.Environment;

public abstract class AbstractApproxGridViewer extends AbstractGridViewer
{
	int rezX, rezY;
	
	@SuppressWarnings("hiding")
	protected AbstractApproxGridViewer(Environment cm, Object data, int resolutionX, int resolutionY)
	{
		super(cm, data);
		rezX = resolutionX;
		rezY = resolutionY;
	}

	@Override
	public void draw(Graphics2D g)
	{
		w = getWidth();
		h = getHeight();
		g.setBackground(Color.black);
		g.clearRect(0, 0, w, h);
		
		int cw = w / rezX;
		int ch = h / rezY;
		
		int offsetx = (w - rezX * cw) / 2;
		int offsety = (h - rezY * ch) / 2;
		
		@SuppressWarnings("unchecked")
		List<Color> grid[][] = new List[rezX][rezY];
		boolean sel[][] = new boolean[rezX][rezY];
		for(int i = 0; i < rezX; i++)
			for(int j = 0; j < rezY; j++)
				grid[i][j] = new Vector<Color>();
		
		for (KCAAgent cell : cm.getAgents())
		{
			int x = (int)((cell.location.getX() - cm.x) * rezX / cm.width);
			int y = (int)((cell.location.getY() - cm.y) * rezY / cm.height);
			
			Color color = getColor(cell);
			grid[x][y].add(color);
			
			if(cell.isSelected())
				sel[x][y] = true;
		}
		
		for(int i = 0; i < rezX; i++)
			for(int j = 0; j < rezY; j++)
			{
				int R = 0, G = 0, B = 0;
				int s = grid[i][j].size();
				if(s > 0)
				{
					for(Color color : grid[i][j])
					{
						R += color.getRed();
						G += color.getGreen();
						B += color.getBlue();
					}
					R /= s;
					G /= s;
					B /= s;
				}
				
//				if(R + G + B == 0)
//				{
//					R = 255;
//					G = 255;
//					B = 255;
//				}
				
				g.setColor(new Color(R, G, B));
				g.fillRect(offsetx + i * cw, offsety + j * ch, cw, ch);
				if(sel[i][j])
				{
					g.setColor(new Color(255 - R, 255 - G, 255 - B));
					g.fillRect(offsetx + i * cw, offsety + j * ch, 2, 2);
				}
			}
	}
}
