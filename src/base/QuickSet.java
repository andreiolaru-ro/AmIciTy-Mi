package base;

import java.util.AbstractList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;

/**
 * @author Andrei Olaru
 * 
 *         A set of T objects that has the following important features:
 * 
 *         is sorted (O(n) insertion).
 * 
 *         has quick updating - considers that objects' compared measures dont't update by large amounts, so it is better to 'move' the object more to the left or to the right, rather than re-inserting it.
 * 
 * @param <T>
 */
public class QuickSet<T> extends AbstractList<T> implements SortedSet<T>
{
	public interface QuickUpdatable
	{
		boolean update();
	}
	
	public interface QuickFadable
	{
		void quickFade(float rate);
	}
	
	public interface QuickForgetConfirmer<T>
	{
		boolean confirmForget(T element);
	}
	
	protected class QuickIterator implements Iterator<T>
	{
		QNode<T>	current		= null;
		QNode<T>	lastVisited	= null;
		boolean		removable	= true;
		int			frozenChg	= nChanges;
		boolean		backwards	= false;
		
		public QuickIterator()
		{
			current = Qfirst.next;
			lastVisited = Qfirst;
		}
		
		@Override
		public boolean hasNext()
		{
			if(nChanges != frozenChg)
				throw new ConcurrentModificationException();
			return current != Qlast;
		}
		
		public boolean hasPrev()
		{
			if(nChanges != frozenChg)
				throw new ConcurrentModificationException();
			return current != Qfirst;
		}
		
		protected T currentElement()
		{
			return current.content;
		}
		
		@Override
		public T next()
		{
			if(nChanges != frozenChg)
				throw new ConcurrentModificationException();
			lastVisited = current;
			current = current.next;
			removable = true;
			backwards = false;
			return lastVisited.content;
		}
		
		public T prev()
		{
			if(nChanges != frozenChg)
				throw new ConcurrentModificationException();
			lastVisited = current;
			current = current.prev;
			removable = true;
			backwards = true;
			return lastVisited.content;
		}
		
		@Override
		public void remove()
		{
			if(nChanges != frozenChg)
				throw new ConcurrentModificationException();
			if(!removable)
				throw new IllegalStateException("cannot remove before next/prev call");
			if((lastVisited == Qfirst) || (lastVisited == Qlast))
				throw new IllegalStateException("cannot remove");
			lastVisited.getRemoved();
			frozenChg++;
			removable = false;
			lastVisited = null;
		}
		
		/**
		 * evaluates if it should be right to insert the element in the set by a subsequent insertHere operation
		 */
		protected boolean evaluateHere(T element)
		{
			if(!removable)
				throw new IllegalStateException("bad state while evaluating position for " + element);
			if(!backwards)
				return QuickSet.this.evaluateHere(lastVisited.content, element, current.content);
			return QuickSet.this.evaluateHere(current.content, element, lastVisited.content);
		}
		
		protected int evaluateDir(T element)
		{
			if(!removable)
				throw new IllegalStateException("bad state while evaluating direction for " + element);
			if(evaluate((!backwards ? current : lastVisited).content, element) < 0)
				// element should be moved forward
				return -1;
			if(evaluate(element, (!backwards ? lastVisited : current).content) < 0)
				// element should be moved backward
				return +1;
			// element should just be inserted here
			return 0;
		}
		
		/**
		 * inserts a new element between the element that was last visited and the current element
		 */
		protected void insertHere(T element)
		{
			if(nChanges != frozenChg)
				throw new ConcurrentModificationException();
			if(!removable)
				throw new IllegalStateException("bad state while inserting " + element);
			if(!backwards)
			{
				@SuppressWarnings("unused")
				QNode<T> newnode = new QNode<T>(lastVisited, element, current);
				frozenChg++;
				return;
			}
			@SuppressWarnings("unused")
			QNode<T> newnode = new QNode<T>(current, element, lastVisited);
			frozenChg++;
			return;
		}
		
		protected void resync()
		{
			if(!backwards)
			{
				prev();
				next();
			}
			else
			{
				next();
				prev();
			}
		}
		
		protected void ffw()
		{
			current = Qlast.prev;
			lastVisited = Qlast;
			backwards = true;
			removable = true;
		}
	}
	
	protected static class QNode<T>
	{
		protected QNode<T>		prev, next;
		protected T				content;
		protected QuickSet<T>	container;
		
		// for interior nodes
		protected QNode(@SuppressWarnings("hiding") QNode<T> prev, T element, @SuppressWarnings("hiding") QNode<T> next)
		{
			this.prev = prev;
			this.next = next;
			this.content = element;
			this.container = prev.container;
			prev.next = this;
			next.prev = this;
			
			this.container.sizeup();
		}
		
		// for end nodes
		protected QNode(QuickSet<T> list, @SuppressWarnings("hiding") QNode<T> prev, T element, @SuppressWarnings("hiding") QNode<T> next)
		{
			this.prev = prev;
			this.next = next;
			this.content = element;
			this.container = list;
			
			this.container.sizeup();
		}
		
		protected T getRemoved()
		{
			if(next == null || prev == null)
				return null;
			next.prev = prev;
			prev.next = next;
			
			this.container.sizedown();
			return content;
			// poof!
		}
	}
	
	protected Comparator<T>	comparator	= null;
	protected QNode<T>		Qfirst		= new QNode<T>(this, null, null, null);
	protected QNode<T>		Qlast		= new QNode<T>(this, null, null, null);
	protected int			size		= 0;
	protected int			nChanges	= 0;
	
	public QuickSet(Comparator<T> c)
	{
		comparator = c;
		Qfirst.next = Qlast;
		Qlast.prev = Qfirst;
	}
	
	public QuickSet()
	{
		this(null);
	}
	
	protected void sizeup()
	{
		size++;
		nChanges++;
	}
	
	protected void sizedown()
	{
		size--;
		nChanges++;
	}
	
	// returns el1 - el2
	@SuppressWarnings("unchecked")
	protected int evaluate(T el1, T el2)
	{
		if((el1 == null) || (el2 == null))
			return 0;
		if(comparator != null)
			return comparator.compare(el1, el2);
		return ((Comparable<T>)el1).compareTo(el2);
		
	}
	
	protected boolean evaluateHere(T elBefore, T elNew, T elAfter)
	{
		// System.out.print(elBefore + ":" + elNew + ":" + elAfter);
		if((evaluate(elBefore, elNew) <= 0) && (evaluate(elNew, elAfter) <= 0))
		{
			// System.out.println("here");
			return true;
		}
		// System.out.println("not here");
		return false;
	}
	
	@Override
	public boolean add(T element)
	{
		if(element == null)
			return false;
		QuickIterator it = internalIterator();
		while(it.hasNext())
		{
			if(it.evaluateHere(element))
			{
				it.insertHere(element);
				// System.out.println("inserted");
				return true;
			}
			it.next();
		}
		
		it.insertHere(element);
		// System.out.println("inserted last");
		return true;
	}
	
	public boolean update(T element)
	{
		QuickIterator it = internalIterator();
		T el = it.next();
		while(it.hasNext() && !el.equals(element))
			el = it.next();
		if(!it.hasNext() && !el.equals(element))
			// not found
			return false;
		
		it.remove();
		it.resync();
		// find direction
		if(it.evaluateDir(element) < 0)
		{ // must move forward
			// search
			while(it.hasNext() && !it.evaluateHere(element))
				it.next();
			// reinsert
			it.insertHere(element);
			// System.out.println("inserted (fw)");
			return true;
		}
		else if(it.evaluateDir(element) > 0)
		{ // must move backward
			// search
			while(it.hasPrev() && !it.evaluateHere(element))
				it.prev();
			// reinsert
			it.insertHere(element);
			// System.out.println("inserted (bk)");
			return true;
		}
		else
		{ // no need to move
			it.insertHere(element);
			// System.out.println("inserted (same)");
			return true;
		}
	}
	
	public void updateAll()
	{
		// to check and to not use for the moment, maybe
		Vector<T> toUpdate = new Vector<T>(this);
		for(T el : toUpdate)
		{
			if(el instanceof QuickUpdatable)
				((QuickUpdatable)el).update();
			update(el);
		}
	}
	
	public void fadeAll(float rate)
	{
		for(T el : this)
			((QuickFadable)el).quickFade(rate);
	}
	
	public void forgetTail(int nElements)
	{
		forgetTail(nElements, null);
	}
	
	// -1 will mean erase everything
	public void forgetTail(int nElements, QuickForgetConfirmer<T> confirmer)
	{
		int nbElements= nElements;
		QuickIterator it = internalIterator();
		it.ffw();
		while(nbElements != 0 && it.hasPrev())
		{
			T element = it.prev();
			if(confirmer != null)
			{
				if(confirmer.confirmForget(element))
				{
					it.remove();
					nbElements--;
				}
			}
			else
			{
				it.remove();
				nbElements--;
			}
			
		}
		
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "null" })
	public boolean remove(Object element)
	{
		T nel = (T)element;
		for(QuickIterator it = (QuickIterator)iterator(); it.hasNext();)
		{
			T el = it.next();
			if((nel == null && el == null) || (nel.equals(el)))
			{
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	protected QuickIterator internalIterator()
	{
		return new QuickIterator();
	}
	
	@Override
	public Iterator<T> iterator()
	{
		return internalIterator();
	}
	
	public T containsReturn(T search)
	{
		for(T el : this)
			if(el.equals(search))
				return el;
		return null;
	}
	
	@Override
	public T get(int arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int size()
	{
		return size;
	}
	
	@Override
	public Comparator<? super T> comparator()
	{
		return comparator;
	}
	
	@Override
	public T first()
	{
		return Qfirst.next.content;
	}
	
	@Override
	public T last()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public SortedSet<T> headSet(T toElement)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public SortedSet<T> subSet(T fromElement, T toElement)
	{
		return null;
	}
	
	@Override
	public SortedSet<T> tailSet(T fromElement)
	{
		return null;
	}
	
	public static void testQS()
	{
		class HI implements Comparable<HI>
		{
			Integer	x;
			
			public HI(int a)
			{
				x = new Integer(a);
			}
			
			public Integer get()
			{
				return x;
			}
			
			public void set(@SuppressWarnings("hiding") Integer x)
			{
				this.x=x;
			}
			
			@Override
			public String toString()
			{
				return x.toString();
			}
			
			@Override
			public int compareTo(HI o)
			{
				return x.compareTo(o.get());
			}
		}
		QuickSet<HI> qs = new QuickSet<HI>(null);
		HI tomod;
		qs.add(new HI(5));
		qs.add(tomod = new HI(2));
		qs.add(new HI(-10));
		qs.add(new HI(11));
		qs.add(new HI(3));
		
		for(HI x : qs)
			System.out.print(x + " ");
		System.out.println(" : " + qs.size());
		
		for(Iterator<HI> it = qs.iterator(); it.hasNext();)
			if(it.next().get().intValue() == 5)
				it.remove();
		
		for(HI x : qs)
			System.out.print(x + " ");
		System.out.println(" : " + qs.size());
		
		tomod.set(new Integer(7));
		qs.update(tomod);
		
		for(HI x : qs)
			System.out.print(x + " ");
		System.out.println(" : " + qs.size());
		
		tomod.set(new Integer(70));
		qs.update(tomod);
		
		for(HI x : qs)
			System.out.print(x + " ");
		System.out.println(" : " + qs.size());
		
		tomod.set(new Integer(-70));
		qs.update(tomod);
		
		for(HI x : qs)
			System.out.print(x + " ");
		System.out.println(" : " + qs.size());
		
		tomod.set(new Integer(7));
		qs.update(tomod);
		
		for(HI x : qs)
			System.out.print(x + " ");
		System.out.println(" : " + qs.size());
		
		System.out.println("00000000000000000000000000000000000000000000000000000000000000");
		
	}
	
}
