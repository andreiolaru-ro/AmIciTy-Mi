package util.XML;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import util.XML.XMLTree.XMLNode.XMLAttribute;

/**
 * A class that contains the data that was read from (or that will be written to) an XML file.
 * 
 * The class also contains functions (like cNode(), finishNode() and reset()) and support (see newNode(), newAttribute()) to keep a current position (current
 * node) in the tree. See the JavaDoc for the functions for particular details.
 * 
 * Most functions return the tree itself, so that chained calls can be used. E.g. new
 * XMLTree(root).newNode(node1).newAttribute(someAttribute).finishNode().newNode(nodeSibilingWithNode1);
 * 
 * @author Andrei Olaru
 * 
 */
public class XMLTree
{
	/**
	 * A class that contains the data corresponding to a node (element) in an {@link XMLTree} / XML file.
	 * 
	 * Contains a name, a string / integer value, attributes, and child nodes.
	 * 
	 * Some functions return the node itself, so that chained calls can be used. E.g. new XMLNode(name).setValue("value").addNode("child");
	 * 
	 * @author Andrei Olaru
	 * 
	 */
	public static class XMLNode
	{
		/**
		 * An iterator that iterates only over the child nodes that have a certain name (specified in constructor).
		 * 
		 * @author Andrei Olaru
		 * 
		 */
		public class NodeIterator implements Iterator<XMLNode>
		{
			String	search	= null;
			int		cIndex	= 0;
			
			public NodeIterator(String searchName)
			{
				search = searchName;
			}
			
			@Override
			public boolean hasNext()
			{
				while(cIndex < nodes.size())
				{
					if(nodes.get(cIndex).name.equals(search))
						return true;
					cIndex++;
				}
				return false;
			}
			
			@Override
			public XMLNode next()
			{
				while(cIndex < nodes.size())
				{
					if(nodes.get(cIndex).name.equals(search))
						return nodes.get(cIndex++);
					cIndex++;
				}
				return null;
			}
			
			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		}
		
		/**
		 * 
		 * @author Andrei Olaru
		 * 
		 */
		public static class XMLAttribute
		{
			String	name	= null;
			String	value	= null;
			
			public XMLAttribute(String attrName, String attrValue)
			{
				super();
				this.name = attrName;
				this.value = attrValue;
			}
		}
		
		protected String			name		= null;
		protected String			namespace	= null;
		protected String			fullName	= null;
		
		protected Set<XMLAttribute>	attributes	= new HashSet<XMLAttribute>();
		
		protected String			valueString	= null;
		protected Integer			valueInt	= null;
		protected List<XMLNode>		nodes		= new LinkedList<XMLNode>();
		
		public XMLNode(String fullNodeName)
		{
			parseName(fullNodeName);
		}
		
		protected void parseName(String fullNodeName)
		{
			int columnIndex = fullNodeName.lastIndexOf(':');
			if(columnIndex < 0)
			{ // no prefix
				namespace = null;
				name = fullNodeName;
			}
			else
			{
				namespace = fullNodeName.substring(0, columnIndex);
				name = fullNodeName.substring(columnIndex + 1);
			}
			this.fullName = fullNodeName;
		}
		
		public XMLNode setValue(String string)
		{
			valueString = string;
			return this;
		}
		
		public XMLNode setValue(int integer)
		{
			valueInt = new Integer(integer);
			return this;
		}
		
		public XMLNode addNode(XMLNode node)
		{
			nodes.add(node);
			return this;
		}
		
		public XMLNode addAttribute(XMLAttribute attr)
		{
			attributes.add(attr);
			return this;
		}
		
		public String getName()
		{
			return name;
		}
		
		public Object getValue()
		{
			if(valueString != null)
				return valueString;
			if(valueInt != null)
				return valueInt;
			return null;
		}
		
		public List<XMLNode> getNodes()
		{
			return nodes;
		}
		
		public Set<XMLAttribute> getAttributes()
		{
			return attributes;
		}
		
		public String getAttributeValue(String attributeName)
		{
			for(XMLAttribute attr : attributes)
				if(attr.name.equals(attributeName))
					return attr.value;
			return null;
		}
		
		/**
		 * @return a {@link NodeIterator} for all child nodes
		 */
		public Iterator<XMLNode> getNodeIterator()
		{
			return nodes.iterator();
		}
		
		/**
		 * @param search
		 *            the name of the nodes to iterate over.
		 * @return a {@link NodeIterator} for child nodes with the name given by search.
		 */
		public Iterator<XMLNode> getNodeIterator(String search)
		{
			return new NodeIterator(search);
		}
		
		/**
		 * @return a {@link String} representation of the node's name, value and attributes (one line).
		 */
		@Override
		public String toString()
		{
			String ret = "";
			ret += namespace + ":" + name;
			if(valueString != null)
				ret += "[" + valueString + "]";
			if(valueInt != null)
				ret += "[" + valueInt + "]";
			for(XMLAttribute attr : attributes)
				ret += " [" + attr.name + ":" + attr.value + "]";
			return ret;
		}
		
		/**
		 * @return a {@link String} representation of this node's name value, attributes and children (recursive). Each node has one line.
		 */
		public String toStringDepth()
		{
			return this.toStringDepth("");
		}
		
		/**
		 * @param indent
		 *            to apply to the output.
		 * @return a {@link String} representation of this node's name value, attributes and children (recursive). Each node has one line.
		 */
		public String toStringDepth(String indent)
		{
			String ret = indent + this.toString() + "\n";
			for(XMLNode node : this.nodes)
				ret += node.toStringDepth(indent + "\t");
			// ret += node.toStringDepth(indent + "|\t"); // alternative printout, with verticla lines
			return ret;
		}
	}
	
	XMLNode			root	= null;
	
	/**
	 * Keeps information for finding the current position in the tree (and allowing going up and down in the tree). stack.peek() is the current node.
	 */
	Stack<XMLNode>	stack	= new Stack<XMLNode>();
	
	/**
	 * Creates a new {@link XMLTree}. The current node becomes the root.
	 * 
	 * @param rootNode
	 *            the root {@link XMLNode} of the new tree
	 */
	public XMLTree(XMLNode rootNode)
	{
		this.root = rootNode;
		stack.push(rootNode);
	}
	
	public XMLNode getRoot()
	{
		return root;
	}
	
	/**
	 * @return the current node (see {@link XMLTree}).
	 */
	public XMLNode currentNode()
	{
		if(stack.empty())
			return null;
		return stack.peek();
	}
	
	/**
	 * Inserts a new node in the tree, as a child of the current node. The new node becomes current node.
	 * 
	 * @param node
	 *            the {@link XMLNode} to insert.
	 * @return the updated {@link XMLTree}.
	 */
	public XMLTree newNode(XMLNode node)
	{
		stack.peek().addNode(node);
		stack.push(node);
		return this;
	}
	
	/**
	 * Inserts a new attribute in the current node.
	 * 
	 * @param attr
	 *            the {@link XMLAttribute} to insert.
	 * @return the updated {@link XMLTree}.
	 */
	public XMLTree newAttribute(XMLAttribute attr)
	{
		stack.peek().addAttribute(attr);
		return this;
	}
	
	/**
	 * Alias of moveToChild()
	 */
	public XMLTree moveDown(XMLNode node)
	{
		return moveToChild(node);
	}
	
	/**
	 * The specified child of the current node becomes current node.
	 * 
	 * @param node
	 *            {@link XMLNode} to become current node; must be a child of the current node.
	 * @return the {@link XMLTree}.
	 * @throws IllegalArgumentException
	 *             if the specified node is not a child of the current node.
	 */
	public XMLTree moveToChild(XMLNode node)
	{
		if(stack.peek().getNodes().contains(node))
		{
			stack.push(node);
			return this;
		}
		throw new IllegalArgumentException();
	}
	
	/**
	 * Alias of finishNode().
	 */
	public XMLTree moveUp()
	{
		return finishNode();
	}
	
	/**
	 * Goes up one level in the tree: the parent of the current node becomes current node.
	 * 
	 * @return the {@link XMLTree}.
	 */
	public XMLTree finishNode()
	{
		stack.pop();
		return this;
	}
	
	/**
	 * The root of the {@link XMLTree} becomes current node.
	 * 
	 * @return the {@link XMLTree}.
	 */
	public XMLTree reset()
	{
		stack.clear();
		stack.push(root);
		return this;
	}
	
	/**
	 * The function does not affect the current node.
	 * 
	 * @return a {@link String} representation of the tree, one node on each line, correctly indented.
	 */
	@Override
	public String toString()
	{
		return root.toStringDepth();
	}
}