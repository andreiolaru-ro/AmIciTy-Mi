package graphics;

import graphics.AbstractGraphViewer.GraphParam;

import java.awt.Color;
import java.util.Collection;

import logging.LogViewer;
import KCAAgent.DataContent;
import KCAAgent.EnvironmentKCA;
import KCAAgent.Fact;
import KCAAgent.Goal;
import KCAAgent.SimulationKCA;
import KCAAgent.Goal.GoalType;
import KCAAgent.KCAAgent;
import KCAAgent.Logix.Domain;
import KCAAgent.Specialty;
import agent.MeasureName;

public class ViewerFactory
{
	private static double balanceMinimum = 0.4;
	public enum Type {
		CONTROL,

//		AGENT_DETAILS,

//		SELECTED_AGENT_DETAILS,
		
		AGENT_SELECTION_GRID,

		SPECIALTY_GRID,

		PRESSURE_GRID,

		FACTS_GRID,
		
		DOMAIN_INTEREST_GRID,

//		DATA_GRID,

		PRESSURE_SURFACE,

		DATA_FACTS_SURFACE,

		LOG_VIEWER,

		GLOBAL_FACT_NUMBER_GRAPH,

		GLOBAL_GOAL_NUMBER_GRAPH,

		GLOBAL_PRESSURE_GRAPH,

		MAX_PRESSURE_GRAPH,

		MESSAGE_AVG_GRAPH,
		
		USELESS_FACTS_AVG_GRAF,
		
		AGENT_BALANCE,
		
		AGENT_BALANCE_AVG_GRAF
	}
	
	public static class WindowLayout
	{
		public class Row
		{
			int					commonH;
			int					commonW;
			Type				commonType;
			Object				specificParam;
			int					lastX;
			@SuppressWarnings("hiding")
			int					y;
			int					nWindows;
			WindowParameters	windows[]	= new WindowParameters[20]; // viewers in this row
																		
			@SuppressWarnings("hiding")
			protected Row(int x, int y, int commonH, int commonW, Type commonType, int optimizeFor, Object specific)
			{
				if(commonH <= 0)
					commonH = rowH;
				this.commonH = commonH;
				if(commonW <= 0)
					commonW = (totalW * (100 - mainPercent)) / 100 / optimizeFor;
				this.commonW = commonW;
				this.commonType = commonType;
				lastX = x;
				this.y = y;
				this.specificParam = specific;
			}
			
			public void add(int W, Type type, Object data, boolean makesquare)
			{
				int H = commonH;
				if(nWindows >= 20)
					return;
				if(W <= 0)
					W = commonW;
				if(type == null)
					type = commonType;
				if(makesquare)
					H = W = Math.min(W, H);
				windows[nWindows++] = new WindowParameters(type, lastX, y, W, H, data, specificParam);
				lastX += W;
				
//				System.out.println("new window " + nWindows + " (" + type + "):" + lastX + ":" + y + " | " + W + ":" + H);
			}
			
			public void add(WindowParameters win)
			{
				if(nWindows >= 20)
					return;
				if(win.x < 0)
					win.x = lastX;
				if(win.y < 0)
					win.y = y;
				if(win.width <= 0)
					win.width = commonW;
				if(win.height <= 0)
					win.height = commonH;
				if(generalMakeSquare && (win.width == commonW || win.height == commonH))
					win.width = win.height = Math.min(win.width, win.height);
				windows[nWindows++] = win;
				lastX += win.width;
				
//				System.out.println("new window " + nWindows + " (" + win.type + "):" + win.x + ":" + win.y + " | " + win.width + ":" + win.height);
			}
			
			public void add(Object data)
			{
				add(0, null, data, generalMakeSquare);
			}
		}
		
		Row					rows[]				= new Row[10];				// rows of system viewers
		WindowParameters	mains[]				= new WindowParameters[5];	// main windows (control, events, etc)
		int					x, y;
		int					totalW, totalH;								// total area occupied by the viewers
		int					mainPercent;									// percent of total width taken by control window and other mains
		int					nrows				= 0, nmains = 0;
		int					optimizeRowsFor		= 1;
		int					optimizeMainsFor	= 1;
		int					rowH				= 100;
		boolean				generalMakeSquare	= false;
		int					controlH			= 100;
		int					lastY				= 0, lastMainY = 0;
		
		public WindowLayout(int x, int y, int W, int H, int mainPercent, int nMainsIndication, int rowH, boolean isRowCount, boolean makeSquare)
		{
			totalW = W;
			totalH = H;
			this.x = x;
			this.y = y;
			this.mainPercent = mainPercent;
			optimizeMainsFor = nMainsIndication;
			optimizeRowsFor = 1;
			if(isRowCount)
				optimizeRowsFor = rowH;
			else
				this.rowH = rowH;
			generalMakeSquare = makeSquare;
		}
		
		public void addMain(WindowParameters window)
		{
			if(nmains >= 5)
				return;
			if(window.x < 0)
				window.x = x;
			if(window.y < 0)
				window.y = lastMainY;
			if(window.height <= 0)
			{
				if(window.type == Type.CONTROL)
					window.height = controlH;
				else
					window.height = (totalH - controlH) / optimizeMainsFor;
			}
			lastMainY += window.height;
			if(window.width <= 0)
				window.width = mainPercent * totalW / 100;
			mains[nmains++] = window;
		}
		
		public Row addRow(int H, int W, Type type, int windowCountIndication, Object specific)
		{
			if(nrows >= 10)
				return null;
			if(H == 0)
				H = totalH / optimizeRowsFor;
			rows[nrows++] = new Row(x + (mainPercent * totalW / 100), lastY, H, W, type, windowCountIndication, specific);
			lastY += H;
			return rows[nrows - 1];
		}
		
		public Row addRow(Type type, int windowCountIndication)
		{
			return addRow(0, 0, type, windowCountIndication, null);
		}
		
		public Row addRow(Type type, int windowCountIndication, Object specific)
		{
			return addRow(0, 0, type, windowCountIndication, specific);
		}
		
		// this is sooooooooo C++
		public WindowParameters[] toCollection()
		{
			// count
			int count = nmains;
			int i, j;
			for(i = 0; i < nrows; i++)
				count += rows[i].nWindows;
			WindowParameters[] ret = new WindowParameters[count];
			
			// set
			count = 0;
			for(i = 0; i < nmains; i++)
				ret[count++] = mains[i];
			for(i = 0; i < nrows; i++)
				for(j = 0; j < rows[i].nWindows; j++)
					ret[count++] = rows[i].windows[j];
			
			return ret;
		}
	}
	
	public static class WindowParameters
	{
		private Type	type;
		private int		x;
		private int		y;
		private int		width;
		private int		height;
		private Object	data;
		private Object	specific;
		
		public WindowParameters(Type type, int x, int y, int width, int height, Object data, Object specific)
		{
			this.type = type;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.data = data;
			this.specific = specific;
		}
		
		public WindowParameters(Type type, int x, int y, int width, int height)
		{
			this(type, x, y, width, height, null, null);
		}
		
		public WindowParameters(Type type, int x, int y, Object data)
		{
			this(type, x, y, 0, 0, data, null);
		}
		
		public WindowParameters(Type type, int x, int y)
		{
			this(type, x, y, 0, 0, null, null);
		}
		
		public WindowParameters(Type type)
		{
			this(type, -1, -1, 0, 0, null, null);
		}
	}
	
	public static ControllableView[] createViewers(EnvironmentKCA cm, WindowParameters params[])
	{
		ControllableView[] viewers = new ControllableView[params.length];
		WindowParameters control = null;
		for(int i = 0; i < params.length && control == null; i++)
			if(params[i].type == Type.CONTROL)
				control = params[i];
		
		SimulationKCA kca = (SimulationKCA)(control.data);
		kca.createMainWindow(control.x, control.y, control.width, control.height);
		
		for(int i = 0; i < params.length; i++)
		{
			if(params[i].type != Type.CONTROL)
				viewers[i] = createViewer(cm, params[i]);
		}
		
		return viewers;
	}
	
	public static ControllableView createViewer(EnvironmentKCA cm, WindowParameters params)
	{
		ControllableView viewer = createViewerSub(cm, params.type, params.data, params.specific);
		viewer.setLocation(params.x, params.y);
		if(params.width != 0 && params.height != 0)
		{
//			System.out.println("setting dim: " + params.width + ", " + params.height);
			viewer.setSize(params.width, params.height);
//			viewer.setSize(100, 100);
		}
		viewer.show();
		return viewer;
	}
	
	private static ControllableView createViewerSub(EnvironmentKCA cm, Type type, Object data, Object specific)
	{
		switch(type)
		{
//		case SELECTED_AGENT_DETAILS:
//			return new SelectedAgentDetails(cm).setTitle("Selected Agent Details");
//		case AGENT_DETAILS:
//			return new AgentDetails(cm).setTitle("Agent Details");
		case AGENT_SELECTION_GRID:
			return new AbstractGridViewer(cm) {
				@Override
				public Color getColor(KCAAgent cell)
				{
					return Color.GREEN;
				}
				
			}.setTitle("Selection Grid");
		case SPECIALTY_GRID:
			return new AbstractApproxGridViewer(cm, null, 30, 30) {
				@Override
				public Color getColor(KCAAgent cell)
				{
					return ((Specialty)cell.getMeasure(MeasureName.SPECIALTY)).getColor();
				}
			}.setTitle("Interest Grid - all");
		case PRESSURE_GRID:
			return new AbstractGridViewer(cm) {
				@Override
				public Color getColor(KCAAgent cell)
				{
					double pressure = ((Float) cell.getMeasure(MeasureName.AGENT_PRESSURE).getValue()).doubleValue();
					if(pressure >= 0)
					{
						double pr = 1 - Math.pow(1 - pressure, 2); // make low pressure look slightly higher
						double c = 1 - pr; // complement
						return new Color(1.0f, (float)c, (float)c);
					}
					else
					{
						double pr = 1 - Math.pow(1 + pressure, 2); // make low pressure look slightly higher
						double c = 1 - pr; // complement
						return new Color((float)c, (float)c, 1.0f);
					}
				}
			}.setTitle("Pressure Grid");
		case DOMAIN_INTEREST_GRID:
			return new AbstractGridViewer(cm, data) {
				@Override
				public Color getColor(KCAAgent cell)
				{
					Domain domain = (Domain)data;
					float val = (float)(double)((Specialty)cell.getMeasure(MeasureName.SPECIALTY)).getValue(domain);
					float a = 1;
					float b = 1 - val;
					switch(domain)
					{
					case A:
						return new Color(a, b, b);
					case B:
						return new Color(b, a, b);
					case C:
						return new Color(b, b, a);
					default:
						return null;
					}
				}
			}.setTitle("Interest Grid - " + data);
//		case DATA_GRID:
//			return new AbstractGridViewer(cm, data) {
//				@Override
//				public Color getColor(Agent cell)
//				{
//					Data d = (Data)data;
//					return cell.getData().contains(d) ? d.getSpecialty().getColor() : Color.white;
//				}
//			}.setTitle("Data Grid - " + data);
		case FACTS_GRID:
			return new AbstractGridViewer(cm, data) {
				@Override
				public Color getColor(KCAAgent cell)
				{
					DataContent d = (DataContent)data;
					Collection<Fact> facts = cell.getFacts(false);
					Color agentColor = Color.white;
					for(Fact fact : facts)
					{
						if(fact.getAbstractContentRecursive().equals(d))
						{
							if(fact.getAgentFactBalance() >= balanceMinimum)
							{
								agentColor = Color.red;		// good
								break;
							}
							else
								agentColor = Color.green;		// bad
							/*if(Logix.similarity(cell.getSpecialty(), fact.getSpecialty())* cell.gradeFactHistory(fact.getSpecialty(), fact.firstStep) >= balanceMinimum)
							//if(Logix.similarity(cell.specialtyHistory[cell.currentSpecialty-1], fact.getSpecialty()) >= balanceMinimum)
							{
								agentColor = Color.red;
								break;
							}
							else
								agentColor = Color.green;*/
						}
					}
					return agentColor;
				}
			}.setTitle("Data Facts Grid - " + data);
//		case PRESSURE_SURFACE:
//			return new AbstractSurfaceViewer(cm) {
//				@Override
//				protected boolean isHighlighted(int x, int y)
//				{
//					return cm.cellAt(y, x).isSelected();
//				}
//				
//				@Override
//				protected double getHeight(int x, int y)
//				{
//					return cm.cellAt(y, x).getPressure();
//				}
//			}.setTitle("Pressure Surface");
//		case DATA_FACTS_SURFACE:
//			return new AbstractSurfaceViewer(cm, data) {
//				@Override
//				protected boolean isHighlighted(int x, int y)
//				{
//					return cm.cellAt(y, x).isSelected();
//				}
//				
//				@Override
//				protected double getHeight(int x, int y)
//				{
//					return Fact.filterCollectionOnData(cm.cellAt(x, y).getFacts(false), (Data)data).size() / 100.0;
//				}
//			}.setTitle("Data Facts Surface - " + data);
		case GLOBAL_FACT_NUMBER_GRAPH:
			return new AbstractAggregGraphViewer(cm, data, (GraphParam)specific) {
				@Override
				public double getCellValue(KCAAgent cell)
				{
					if(data!=null)
						return Fact.filterCollectionOnAbstractContent(cell.getFacts(false), (DataContent)data).size();
					else
						return cell.getFacts(false).size();
				}
//			}.setTitle(data!=null ? "Data Facts Number - " + data : "Total Facts Number");
			}.setTitle(data!=null ? data + " - Facts Number" : "Total Facts Number");
		case GLOBAL_GOAL_NUMBER_GRAPH:	// ca receive in data the GoalType to filter goals by
			return new AbstractAggregGraphViewer(cm, data) {
				@Override
				public double getCellValue(KCAAgent cell)
				{
					if((data != null) && (data instanceof GoalType))
						return Goal.filterCollectionOfType(cell.getGoals(), (GoalType)data).size();
					else
						return cell.getGoals().size();
				}
			}.setTitle("Goal Number" + (((data != null) && (data instanceof GoalType)) ? (" - " + ((GoalType)data).toString()) : ""));
		case GLOBAL_PRESSURE_GRAPH:
			return new AbstractAggregGraphViewer(cm, data, Color.red) {
				@Override
				public double getCellValue(KCAAgent cell)
				{
					return (Float) cell.getMeasure(MeasureName.AGENT_PRESSURE).getValue();
				}
				
				@Override
				protected double stringScale()
				{
					return 1.0 / (cm.getAgents().size()) * 100;
				};
				@Override
				protected String makeString(double val, double max)
				{
					return (int)val + " : " + super.makeString(val, max);
				};
			}.setTitle("Pressure Average Graph");
		case MAX_PRESSURE_GRAPH:
			return new AbstractMaxGraphViewer(cm, data, Color.red) {
				@Override
				public double getCellValue(KCAAgent cell)
				{
					return (Float) cell.getMeasure(MeasureName.AGENT_PRESSURE).getValue();
				}
				
				@Override
				public double stringScale()
				{
					return 100;
				}
			}.setTitle("Max Agent Pressure");
		case MESSAGE_AVG_GRAPH:
			return new AbstractAvgGraphViewer(cm, data, Color.blue) {
				@Override
				public double getCellValue(KCAAgent cell)
				{
					return cell.getInbox().size();
				}
				
				@Override
				public double stringScale()
				{
					return 100;
				}
			}.setTitle("Message Number");
		case USELESS_FACTS_AVG_GRAF:
			return new AbstractAvgGraphViewer(cm, data, Color.red) {
				@Override
				public double getCellValue(KCAAgent cell)
				{	// mean percentage
					
					/*Collection<Fact> facts = cell.getFacts(false);
					double n = facts.size();
					double uselessFacts = 0;
					for(Fact fact : facts)
					{
						if(Logix.similarity(cell.getSpecialty(), fact.getSpecialty()) * cell.gradeFactHistory(fact.getSpecialty(), fact.firstStep) < balanceMinimum)
							uselessFacts++;
					}
					if(n > 0)
						return uselessFacts / n;
					return 0;*/
					return (Double) cell.getMeasure(MeasureName.AGENT_USELESS_FACT).getValue();
				}
				
				@Override
				public double stringScale()
				{
					return 100;
				}
			}.setTitle("Useless Facts Number");
		case AGENT_BALANCE:
			return new AbstractGridViewer(cm) {
				@Override
				public Color getColor(KCAAgent cell)
				{	// more red -> higher balance value -> good
					double sim = ((Double) cell.getMeasure(MeasureName.AGENT_BALANCE).getValue()).doubleValue();
					return new Color(1.0f, 1.0f - (float)sim, 1.0f - (float)sim);
				}
			}.setTitle("Agent Balance");
		case AGENT_BALANCE_AVG_GRAF:
			return new AbstractAvgGraphViewer(cm, data, Color.blue) {
				@Override
				public double getCellValue(KCAAgent cell)
				{	// mean balance, in percentage
					return (Double) cell.getMeasure(MeasureName.AGENT_BALANCE).getValue();
				}
				
				@Override
				public double stringScale()
				{
					return 100;
				}
			}.setTitle("Agent Balance");
		case LOG_VIEWER:
			return new LogViewer(cm).setTitle("Event Log");
		case CONTROL:
			return null;
		default:
			return null;
		}
	}
}
