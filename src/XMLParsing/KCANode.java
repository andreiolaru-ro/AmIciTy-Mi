package XMLParsing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import XMLParsing.XMLTree.XMLNode;

/**
 * {@link KCANode} is a {@link XMLNode} with a set of parameters. These
 * parameters could generate random values.
 * 
 * @author Alexandre Hocquard
 * 
 */
public class KCANode extends XMLNode {

	KCAParameters	parameters;

	public KCANode(XMLNode node) {
		// TODO Auto-generated constructor stub
		super(node);
		parameters = new KCAParameters(this);
	}

	public KCAParameters getParameters() {
		return parameters;
	}

	public static class KCAParameters {
		private boolean		allValueNull	= true;
		private String		value			= null;
		private String		list			= null;
		private Double		mean			= null;
		private Double		stdev			= null;
		private Double		min				= null;
		private Double		max				= null;
		private Double		first			= null;
		private Double		last			= null;
		private Integer		count			= null;
		private Integer		select			= null;
		private Double		step			= null;
		ArrayList<String>	foreach			= new ArrayList<String>();

		public KCAParameters(XMLNode node) {
			// either value or parameters to generate random values
			if (node.getFirstNode("value") != null)
				value = (String) node.getFirstNode("value").getValue();
			else{
				if (node.getFirstNode("list") != null)
					list = (String) node.getFirstNode("list").getValue();
				if (node.getFirstNode("mean") != null)
					mean = (Double) node.getFirstNode("mean").getValue();
				if (node.getFirstNode("stdev") != null)
					stdev = (Double) node.getFirstNode("stdev").getValue();
				if (node.getFirstNode("min") != null)
					min = (Double) node.getFirstNode("min").getValue();
				if (node.getFirstNode("max") != null)
					max = (Double) node.getFirstNode("max").getValue();
				if (node.getFirstNode("first") != null)
					first = (Double) node.getFirstNode("first").getValue();
				if (node.getFirstNode("last") != null)
					last = (Double) node.getFirstNode("last").getValue();
				if (node.getFirstNode("count") != null)
					count = new Integer(((Double) node.getFirstNode("count").getValue()).intValue());
				if (node.getFirstNode("select") != null)
					select = new Integer(((Double) node.getFirstNode("select").getValue()).intValue());

				if (node.getFirstNode("step") != null)
					step = (Double) node.getFirstNode("step").getValue();

				Iterator<XMLNode> foreachIterator = node.getNodeIterator("foreach");
				while (foreachIterator.hasNext()) {
					foreach.add(foreachIterator.next().getValue().toString());
					allValueNull = false;
				}
			}

			if (value != null || list != null || mean != null || stdev != null || min != null
					|| max != null || first != null || last != null || count != null
					|| select != null || step != null
					|| foreach.size() > 0)
				allValueNull = false;
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
							double val = mean.doubleValue() + KCAScenario.rand().nextGaussian()
									* stdev.doubleValue();
							if (max.doubleValue() <= val && val <= min.doubleValue()) {
								res.add(new Double(val).toString());
								break;
							}
						}
					} else {
						assert mean == null && stdev == null;
						res.add((new Double(min.doubleValue()
								+ (max.doubleValue() - min.doubleValue())
								* KCAScenario.rand().nextDouble())).toString());
					}
				}
			} else {
				if (step != null && count != null) {
					assert first == null || last == null;
					if (first == null) {
						assert last != null;
						first = new Double(last.doubleValue() - (count.intValue() - 1)
								* step.doubleValue());
					}
					if (last == null) {
						assert first != null;
						last = new Double(first.doubleValue() + (count.intValue() - 1)
								* step.doubleValue());
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
					step = new Double((stop - start)
							/ (count.intValue() - (first != null ? 0.5 : 0.0) - (last != null ? 0.5
									: 0.0)));
					val = first != null ? start : start + step.intValue() / 2;
				}
				if (count == null) {
					assert first == null || last == null;
					count = new Integer(1 + (int) Math.floor((stop - start) / step.doubleValue()));
					val = first != null ? start
							: start
							+ step.doubleValue()
							* (last != null ? stop - (count.intValue() - 1)
									* step.doubleValue() : (stop + start - (count
											.intValue() - 1) * step.doubleValue()) / 2);
				}
				for (int i = 0; i < count.intValue(); i++) {
					if (stdev != null) {
						while (true) {
							double v = val + KCAScenario.rand().nextDouble() * stdev.doubleValue();
							boolean ok1 = i == 0 && v >= min.doubleValue()
									|| v >= val - stdev.doubleValue();
									boolean ok2 = i == count.intValue() - 1 && v <= max.doubleValue()
											|| v <= val + stdev.doubleValue();
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
						res.remove(KCAScenario.rand().nextInt(res.size()));
					}
				}
			}
			assert res.size() > 0;
			return res;
		}

		public boolean isAllValueNull() {
			return allValueNull;
		}

		public ArrayList<String> getForeach() {
			return foreach;
		}

		public Integer getSelect() {
			return select;
		}

		@Override
		public String toString() {
			return "Parameters [list=" + list + ", mean=" + mean + ", stdev=" + stdev + ", min="
					+ min + ", max=" + max + ", first=" + first + ", last=" + last + ", count="
					+ count + ", select=" + select + ", step=" + step + ", foreach=" + foreach
					+ "]";
		}

	}

}