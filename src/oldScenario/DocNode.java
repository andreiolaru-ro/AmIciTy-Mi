/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru. See the AUTHORS file for more information.
 * 
 * This file is part of AmIciTy-Mi.
 * 
 * AmIciTy-Mi is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * AmIciTy-Mi is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with AmIciTy-Mi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/**
 * 
 */
package oldScenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scenario.AbstractScenario;


class DocNode {
	private class Value {
		private AttributeType type;

		private String  value;
		private String  list;
		private Double  mean;
		private Double  stdev;
		private Double  min;
		private Double  max;
		private Double  first;
		private Double  last;
		private Integer count;
		private Integer select;
		private Double  step;
		
		@SuppressWarnings("hiding")
		private Object getVal(String name, AttributeType type) {
			String str = attributes.get(name);
			if (str == null) {
				return  null;
			}
			switch (type) {
			case INTEGER :
				return new Integer(Integer.parseInt(str));
			case REAL :
				return new Double(Double.parseDouble(str));
			case STRING :
				return str;
			default :
				assert false : type;
				return null;
			}
		}
		
		private String getStr(Double number) {
			switch (type) {
			case INTEGER :
				return (new Integer((int) Math.round(number.doubleValue()))).toString();
			case REAL :
				return number.toString();
			default :
				assert false : type; // should be called only for numbers;
				return null;
			}
		}
		
		public Value(String att) {
			assert attributes.get(att + ".type") != null : att;
			type = AttributeType.parseType(attributes.get(att + ".type"));

			value  = (String)  getVal(att             , AttributeType.STRING);
			list   = (String)  getVal(att + ".list"   , AttributeType.STRING);
			mean   = (Double)  getVal(att + ".mean"   , AttributeType.REAL);
			stdev  = (Double)  getVal(att + ".dev"    , AttributeType.REAL);
			min    = (Double)  getVal(att + ".min"    , AttributeType.REAL);
			max    = (Double)  getVal(att + ".max"    , AttributeType.REAL);
			first  = (Double)  getVal(att + ".first"  , AttributeType.REAL);
			last   = (Double)  getVal(att + ".last"   , AttributeType.REAL);
			count  = (Integer) getVal(att + ".count"  , AttributeType.INTEGER);
			select = (Integer) getVal(att + ".select" , AttributeType.INTEGER);
			step   = (Double)  getVal(att + ".step"   , AttributeType.REAL);
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
							double val = mean.doubleValue() + AbstractScenario.rand().nextGaussian() * stdev.doubleValue();
							if (max.doubleValue() <= val && val <= min.doubleValue()) {
								res.add(getStr(new Double(val)));
								break;
							}
						}						
					} else {
						assert mean == null && stdev == null;
						res.add(getStr(new Double(min.doubleValue() + (max.doubleValue() - min.doubleValue()) * AbstractScenario.rand().nextDouble())));
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
							double v = val + AbstractScenario.rand().nextDouble() * stdev.doubleValue();
							boolean ok1 = i == 0 && v >= min.doubleValue() || v >= val - stdev.doubleValue();
							boolean ok2 = i == count.intValue() - 1 && v <= max.doubleValue() || v <= val + stdev.doubleValue();
							if (ok1 && ok2) {
								res.add(getStr(new Double(v)));
								break;
							}
						}
					} else {
						res.add(getStr(new Double(val)));
					}
					val += step.doubleValue();
				}
				if (select != null) {
					for (int i = 0; i < count.intValue() - select.intValue(); i++) {
						res.remove(AbstractScenario.rand().nextInt(res.size()));
					}
				}
			}
			assert res.size() > 0;
			return res;
		}
	}
		
	String name;
	Collection<DocNode> children = new ArrayList<DocNode>();
	Map<String, String> attributes = new HashMap<String, String>();
	List<String> order = new ArrayList<String>();
	DocNode parent;
	
	@SuppressWarnings("hiding")
	public DocNode(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		List<List<String>> attval = new ArrayList<List<String>>();
		Collection<List<Integer>> nodes = new ArrayList<List<Integer>>();
		nodes.add(new ArrayList<Integer>());
		for (int i = 0; i < order.size(); i++) {
			String att = order.get(i);
			String[] foreach = attributes.containsKey(att + ".for-each") ? attributes.get(att + ".for-each").split(",") : new String[0];
			int prod = 1;
			for (int j = 0; j < foreach.length; j++) {
				prod *= attval.get(order.indexOf(foreach[j])).size();
			}
			List<String> vals = new ArrayList<String>();
			for (int j = 0; j < prod; j++) {
				vals.addAll(new Value(att).getValues());
			}
			attval.add(vals);			
			List<List<Integer>> newnodes = new ArrayList<List<Integer>>();
			for (List<Integer> node : nodes) {
				int select = 0;
				if (foreach.length > 0) {
					select = node.get(order.indexOf(foreach[0])).intValue();
					for (int j = 1; j < foreach.length; j++) {
						select = select * attval.get(order.indexOf(foreach[j - 1])).size() + 
							node.get(order.indexOf(foreach[j])).intValue();
					}
				}
				for (int j = 0; j < attval.get(i).size() / prod; j++) {
					List<Integer> newnode = new ArrayList<Integer>();
					newnode.addAll(node);
					newnode.add(new Integer(select + j));
					newnodes.add(newnode);
				}
			}
			nodes = newnodes;
		}
		StringBuffer sb = new StringBuffer();
		for (List<Integer> node : nodes) {
			sb.append("<" + name);
			for (int i = 0; i < order.size(); i++) {
				sb.append(" " + order.get(i) + "=\"" + 
						attval.get(i).get(node.get(i).intValue()) + "\"");
			}
			if (children.isEmpty()) {
				sb.append("/>\n");
			} else {
				sb.append(">\n");
				for (DocNode child : children) {
					sb.append(child.toString());
				}
				sb.append("</" + name + ">\n");						
			}
		}
		return sb.toString();
	}
}
