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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


class Schema extends DefaultHandler {
	static class SchemaNode {
		String name;
		Collection<Schema.SchemaNode> children = new ArrayList<Schema.SchemaNode>();
		Map<String, AttributeType> attributes = new HashMap<String, AttributeType>();
		Schema.SchemaNode parent;
		
		@SuppressWarnings("hiding")
		public SchemaNode(String name) {
			this.name = name;
		}
	}
	
	private SchemaNode root;
	private SchemaNode current;
	private String prefix;
	
	public Schema.SchemaNode getRoot() {
		return root;
	}
	
	@Override
	public void startElement(String ns, String localName, String qName, Attributes att) throws SAXException {
		if (qName.equals("root")) {
			assert root == null; // there can be no multiple roots
			current = root = new SchemaNode(att.getValue("name"));
			prefix = null;
		} else if (qName.equals("node")) {
			assert prefix == null; // not inside an attribute definition
			SchemaNode child = new SchemaNode(att.getValue("name"));
			child.parent = current;
			current.children.add(child);
			current = child;
		} else if (qName.equals("attribute")) {
			prefix = prefix == null ? att.getValue("name") : prefix + "." + att.getValue("name");
			if (!att.getValue("type").equals("composite")) {
				current.attributes.put(prefix, AttributeType.parseType(att.getValue("type")));
			}
		} else {
			assert false : qName; // should never reach this
		}
	}

	@Override
	public void endElement(String ns, String localName, String qName) throws SAXException {
		if (qName.equals("attribute")) {
			assert prefix != null; // an attribute should have been started
			if (prefix.contains(".")) {
				prefix = prefix.substring(0, prefix.lastIndexOf("."));
			} else {
				prefix = null;
			}
		} else {
			assert prefix == null : prefix; // all attribute tags should have been properly closed
			current = current.parent;
		}
	}
	
}
