package XMLParsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import scenario.Scenario;

import XMLParsing.XMLTree.XMLNode;

import com.sun.xml.internal.xsom.impl.scd.Iterators.Map;

public class KCAParameters
{
	private String		value	= null;
	private String		list	= null;
	private Double		mean	= null;
	private Double		stdev	= null;
	private Double		min		= null;
	private Double		max		= null;
	private Double		first	= null;
	private Double		last	= null;
	private Integer		count	= null;
	private Integer		select	= null;
	private Double		step	= null;
	ArrayList<String>	foreach	= new ArrayList<String>();

	public KCAParameters(XMLNode node)
	{
		// only value or parameters to generate random values
		if (node.getNodeIterator("value").hasNext())
		{
			value = node.getNodeIterator("value").next().getValue().toString();
		} else
		{
			if (node.getNodeIterator("list").hasNext())
				list = (String) node.getNodeIterator("list").next().getValue();
			if (node.getNodeIterator("mean").hasNext())
				mean = (Double) node.getNodeIterator("mean").next().getValue();
			if (node.getNodeIterator("stdev").hasNext())
				stdev = (Double) node.getNodeIterator("stdev").next().getValue();
			if (node.getNodeIterator("min").hasNext())
				min = (Double) node.getNodeIterator("min").next().getValue();
			if (node.getNodeIterator("max").hasNext())
				max = (Double) node.getNodeIterator("max").next().getValue();
			if (node.getNodeIterator("first").hasNext())
				first = (Double) node.getNodeIterator("first").next().getValue();
			if (node.getNodeIterator("last").hasNext())
				last = (Double) node.getNodeIterator("last").next().getValue();
			if (node.getNodeIterator("count").hasNext())
				count = new Integer(((Double) node.getNodeIterator("count").next().getValue()).intValue());
			if (node.getNodeIterator("select").hasNext())
				select = new Integer(((Double) node.getNodeIterator("select").next().getValue()).intValue());
			if (node.getNodeIterator("step").hasNext())
				step = (Double) node.getNodeIterator("step").next().getValue();
			
			Iterator<XMLNode> foreachIterator = node.getNodeIterator("foreach");
			while (foreachIterator.hasNext())
			{
				foreach.add(foreachIterator.next().getValue().toString());
			}
		}
	}


	List<String> getValues() {
		List<String> res = new ArrayList<String>();			
		if (value != null) {
			res.add(value);
		} else if (list != null) {
			for (String item : list.split(",")) {
				res.add(item);
			}
		} else if (count == null && step == null) {
			assert min != null && max != null;
			select = new Integer(select == null ? 1 : select.intValue());
			for (int i = 0; i < select.intValue(); i++) {
				if (mean != null && stdev != null) {
					while (true) {
						double val = mean.doubleValue() + Scenario.rand().nextGaussian() * stdev.doubleValue();
						if (max.doubleValue() <= val && val <= min.doubleValue()) {
							res.add(new Double(val).toString());
							break;
						}
					}						
				} else {
					assert mean == null && stdev == null;
					res.add((new Double(min.doubleValue() + (max.doubleValue() - min.doubleValue()) * Scenario.rand().nextDouble())).toString());
				}
			}
		} else {
			if (step != null && count != null) {
				assert first == null || last == null;
				if (first == null) {
					assert last != null;
					first = new Double(last.doubleValue() - (count.intValue() - 1) * step.doubleValue());
				}
				if (last == null) {
					assert first != null;
					last = new Double(first.doubleValue() + (count.intValue() - 1) * step.doubleValue());
				}
			}
			if (min == null) {
				assert first != null;
				min = first;
			}
			if (max == null) {
				assert last != null;
				max = last;
			}
			double start = first != null ? first.doubleValue() : min.doubleValue();
			double stop = last != null ? last.doubleValue() : max.doubleValue();
			double val = 0.0;
			if (step == null) {
				assert count.intValue() >= (first != null ? 1 : 0) + (last != null ? 1 : 0);
				step = new Double((stop - start) / (count.intValue() - (first != null ? 0.5 : 0.0) - (last != null ? 0.5 : 0.0)));
				val = first != null ? start : start + step.intValue() / 2;
			} 
			if (count == null) {
				assert first == null || last == null;
				count = new Integer(1 + (int) Math.floor((stop - start) / step.doubleValue()));
				val = first != null 
						? start 
								: start + step.doubleValue() * (last != null 
								? stop - (count.intValue() - 1) * step.doubleValue() 
										: (stop + start - (count.intValue() - 1) * step.doubleValue()) / 2);
			}
			for (int i = 0; i < count.intValue(); i++) {
				if (stdev != null) {
					while (true) {
						double v = val + Scenario.rand().nextDouble() * stdev.doubleValue();
						boolean ok1 = i == 0 && v >= min.doubleValue() || v >= val - stdev.doubleValue();
						boolean ok2 = i == count.intValue() - 1 && v <= max.doubleValue() || v <= val + stdev.doubleValue();
						if (ok1 && ok2) {
							res.add(new Double(v).toString());
							break;
						}
					}
				} else {
					res.add(new Double(val).toString());
				}
				val += step.doubleValue();
			}
			if (select != null) {
				for (int i = 0; i < count.intValue() - select.intValue(); i++) {
					res.remove(Scenario.rand().nextInt(res.size()));
				}
			}
		}
		assert res.size() > 0;
		return res;
	}

	@Override
	public String toString()
	{
		return "Parameters [list=" + list + ", mean=" + mean + ", stdev=" + stdev + ", min=" + min
				+ ", max=" + max + ", first=" + first + ", last=" + last + ", count=" + count
				+ ", select=" + select + ", step=" + step + ", foreach=" + foreach + "]";
	}

}
