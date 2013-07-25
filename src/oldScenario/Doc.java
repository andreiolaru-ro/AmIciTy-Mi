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
package oldScenario;


import oldScenario.Schema.SchemaNode;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



class Doc extends DefaultHandler {
	SchemaNode sroot;
	SchemaNode scurrent;
	DocNode droot;
	DocNode dcurrent;
	
	public Doc(Schema schema) {
		sroot = scurrent = schema.getRoot();
	}
	
	@Override
	public void startElement(String ns, String localName, String qName, Attributes att) throws SAXException {
		String prefix;
		if (dcurrent != null && qName.startsWith(dcurrent.name)) {
			prefix = qName.substring(1 + qName.indexOf('.')) + ".";
		} else {
			prefix = "";
			DocNode node = new DocNode(qName);
			
			
			if (qName.equals(sroot.name)) {
				assert droot == null && dcurrent == null; // should not have two roots
				droot = dcurrent = node;
				if (att.getValue("seed") != null) {
					Scenario.initRandom(Long.parseLong(att.getValue("seed")));
				} else {
					Scenario.initRandom();
					dcurrent.attributes.put("seed", Scenario.getSeed().toString());
					dcurrent.attributes.put("seed.type", AttributeType.INTEGER.toString());
					dcurrent.order.add("seed");
				}
			} else {
				assert droot != null; // root should be defined first
				for (Schema.SchemaNode n : scurrent.children) {
					if (qName.equals(n.name)) {
						scurrent = n;
						node.parent = dcurrent;
						dcurrent.children.add(node);
						dcurrent = node;
						break;
					}
				}
			}
		}
		for (int i = 0; i < att.getLength(); i++) {
			String name = prefix + att.getQName(i);
			dcurrent.attributes.put(name, att.getValue(i));
			if (scurrent.attributes.get(name) != null) {
				dcurrent.attributes.put(name + ".type", scurrent.attributes.get(name).toString());
			} else {
				String bname = name.substring(0, name.lastIndexOf('.')); // base name
				if (scurrent.attributes.get(bname) != null) {
					dcurrent.attributes.put(bname + ".type", scurrent.attributes.get(bname).toString());
				} else {
					assert false : name + " - " + bname;
				}
			}
			for (String key : scurrent.attributes.keySet()) {
				if (name.startsWith(key) && !dcurrent.order.contains(key)) {
					dcurrent.order.add(key);
					break;
				}
			}
		}
	}

	@Override
	public void endElement(String ns, String localName, String qName) throws SAXException {
		if (qName.equals(dcurrent.name)) {
			dcurrent = dcurrent.parent;
			scurrent = scurrent.parent;
		}
	}
	
	@Override
	public String toString() {
		return droot.toString();
	}
	
}
