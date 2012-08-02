package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import KCAAgent.KCAAgent;
import KCAAgent.Specialty;
import base.Environment;

public abstract class AbstractGraphViewer extends AbstractViewer2D
{
	public static class GraphParam
	{
		Color color;
		GraphLink link;
		int traces;
		
		@SuppressWarnings("hiding")
		public GraphParam(Color color, GraphLink link, Integer traces)
		{
			this.link = (link == null) ? new GraphLink() : link;
			this.color = (color == null) ? Color.black : color;
			this.traces = (traces == null) ? 1 : traces.intValue();
		}
	}
	
	public static class GraphLink
	{
		String name = null;
		double maxValue = 0;
		double minValue = 0;
		boolean initialized = false;
		
		public GraphLink()
		{
		}
		
		@SuppressWarnings("hiding")
		public GraphLink(String name)
		{
			this.name = name;
		}
		
		public void update(double value)
		{
			if(!initialized)
			{
				initialized = true;
				minValue = value;
				maxValue = value;
				return;
			}
			if(value > maxValue)
				maxValue = value;
			if(value < minValue)
				minValue = value;
		}
		
		public double getMax()
		{
			return maxValue;
		}
		
		public double getMin()
		{
			return minValue;
		}
		
		public double updateGetMax(double value)
		{
			update(value);
			return getMax();
		}
		
		public double updateGetMin(double value)
		{
			update(value);
			return getMin();
		}
}
	
	public static class GraphPoint
	{
		int		step;
		double	value;
		
		@SuppressWarnings("hiding")
		public GraphPoint(int step, double value)
		{
			this.step = step;
			this.value = value;
		}
	}
	
	List<List<GraphPoint>>	points	= new LinkedList<List<GraphPoint>>();
	boolean				dolines	= true;
	GraphParam param;
	
	@SuppressWarnings("hiding")
	protected AbstractGraphViewer(Environment cm, Object data)
	{
		this(cm, data, new GraphParam(null,null,null));
	}
	
	@SuppressWarnings("hiding")
	protected AbstractGraphViewer(Environment cm, Object data, Color color)
	{
		this(cm, data, new GraphParam(color, null, null));
	}
	
	@SuppressWarnings("hiding")
	protected AbstractGraphViewer(Environment cm, Object data, GraphParam param)
	{
		super(cm, data);

		if((data != null) && (data instanceof Specialty))
			param.color = ((Specialty)data).getColor();
		
		this.param = param;
		
		for(int i = 0; i < param.traces; i++)
			points.add(new LinkedList<GraphPoint>());
	}
	
	protected abstract double getCellValue(KCAAgent cell);
	
	protected abstract double calculateValue();
	
	@SuppressWarnings("unused")
	protected double getValue(int trace)
	{
		return calculateValue();
	}
	
	@SuppressWarnings("static-method")
	protected double stringScale()
	{
		return 1;
	}
	
	protected String makeString(double val, double max)
	{
		double scale = stringScale();
		double valA = val * scale;
		double maxA = max * scale;
		String valS = ((valA >= 10) ? new Double(valA) : new DecimalFormat("0.00").format(valA)).toString();
		String maxS = ((maxA >= 10) ? new Double(maxA) : new DecimalFormat("0.00").format(maxA)).toString();
		
		return maxS + (val < max ? " : " + valS : "");
//		return max + ":" + val;
	}
	
	// we suppose that an update means 1 step
	@Override
	public void update()
	{
		super.update();
		if(!points.isEmpty() && (!points.get(0).isEmpty()) && (points.get(0).get(points.get(0).size() - 1).step == Environment.getStep()))
			return;
		double value = calculateValue();
		for(int i = 0; i < param.traces; i++)
		{
			double traceValue;
			if(param.traces == 1)
				traceValue = value;
			else
				traceValue = getValue(i);
			param.link.update(traceValue);
			points.get(i).add(new GraphPoint(Environment.getStep(), traceValue));
		}
	}
	
	@Override
	protected void draw(Graphics2D g)
	{
		int w = getWidth();
		int h = getHeight();
		g.setBackground(Color.white);
		g.clearRect(0, 0, w, h);
		
		boolean firsttrace = true;
		
		for(List<GraphPoint> trace : points)
		{
			g.setColor(param.color);
			g.setStroke(new BasicStroke(1.0f));
			
			int n = trace.size();
			double step = (double)w / n;
			double yMax = param.link.getMax();
			double yMin = param.link.getMin();
			double ry = (yMax == yMin) ? 1 : (h / (yMax - yMin));
			
			double val = 0;
			double area = 0;
			double dx = 0;
			boolean first = true;
			
			int x = 0;
			int y = 0;
			
			for(GraphPoint point : trace)
			{
				if(first)
				{
					y = (int)Math.round(yMin + ry * (point.value - yMin));
					first = false;
					continue;
				}
				dx += step;
				val = point.value;
				area += val * step;
				if(dx > 1)
				{
					int idx = (int)Math.floor(dx);
					double ival = (area - (dx - idx) * val) / idx;
					int nx = x + idx;
					int ny = (int)Math.round(yMin + ry * (ival - yMin));
					g.drawLine(x, h - y, nx, h - ny);
					area -= ival * idx;
					dx -= idx;
					x = nx;
					y = ny;
				}
			}
			
			if(firsttrace)
				g.drawString(makeString(val, yMax), w / 2 - 30, h - 10);
			
			firsttrace = false;
		}
		
		if(param.link != null && param.link.name != null)
			g.drawString(param.link.name, 2, h - 5);
		
	}
	
}
