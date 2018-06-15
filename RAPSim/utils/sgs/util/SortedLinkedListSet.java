package sgs.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

/**
 * Sorted Set based on LinkedList.
 * 
 * @author Kristofer Schweiger
 * @param <E> - type used in list
 */
public class SortedLinkedListSet<E> implements Set<E> {

	private Comparator<E> comparator;
	private LinkedList<E> list = new LinkedList<E>();
	
	/**
	 * Constructor: With comparator used for this Set (determines equality and order)
	 * @param comparator
	 */
	public SortedLinkedListSet(Comparator<E> comparator){
		this.comparator = comparator;
	}
	/**
	 * Use ObjectComparator based on hash value!!!
	 */
	public SortedLinkedListSet(){
		this.comparator = new ObjectComparator<E>();
	}
	
	@Override
	public boolean add(E e1){
		return add(e1, false);
	}
	/**
	 * Add element, also replace "equal" elements.
	 * @see #add(E)
	 * @param e1
	 * @return true if element was added (should always be true)
	 */
	public boolean put(E e1){
		return add(e1, true);
	}
	
	/**
	 * @see #add(E)
	 * @param e1
	 * @param replace - replace existing element if true
	 * @return true if element was added
	 */
	private boolean add(E e1, boolean replace){
		ListIterator<E> it = list.listIterator();
		
		while(it.hasNext()){
			E e2=it.next();
			int comp = comparator.compare(e1, e2);
			
			if(comp > 0){
				// continue
			}
			else if(comp < 0){			// add new element in Sorted Set, return true.
				if(it.hasPrevious())	// -
					it.previous();		// -
				it.add(e1);				// -
				return true;			// -
			}
			else {	// comp == 0
				if(replace){			// replace element, return true
					it.remove();		// -
					it.add(e1);			// -
					return true;		// -
				}
				return false;			// do not replace element, return false
			}
		}
		
		it.add(e1);		// added at last index, return true
		return true;	// -
	}
	
	/**
	 * @return ArrayList with all elements from this set
	 */
	public ArrayList<E> getArrayList(){
		return new ArrayList<E>(list);
	}
	/**
	 * @return LinkedList with all elements from this set
	 */
	public LinkedList<E> getLinkedList(){
		return new LinkedList<E>(list);
	}

	// -------------------------------------------------------------
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		
		boolean changed = false;
		for(E e1 : c){
			changed = add(e1) || changed;
		}
		return changed;
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	// ------------------------------------------------------------

	/**
	 * @see LinkedList#descendingIterator
	 */
	public Iterator<E> descendingIterator() {
		return list.descendingIterator();
	}

	/**
	 * @see LinkedList#getFirst
	 */
	public E getFirst() {
		return list.getFirst();
	}

	/**
	 * @see LinkedList#getLast
	 */
	public E getLast() {
		return list.getLast();
	}

	/**
	 * @see LinkedList#indexOf(Object)
	 */
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	/**
	 * @see LinkedList#peek
	 */
	public E peek() {
		return list.peek();
	}

	/**
	 * @see LinkedList#peekFirst
	 */
	public E peekFirst() {
		return list.peekFirst();
	}

	/**
	 * @see LinkedList#peekLast
	 */
	public E peekLast() {
		return list.peekLast();
	}

	/**
	 * @see LinkedList#poll
	 */
	public E poll() {
		return list.poll();
	}

	/**
	 * @see LinkedList#pollFirst
	 */
	public E pollFirst() {
		return list.pollFirst();
	}

	/**
	 * @see LinkedList#pollLast
	 */
	public E pollLast() {
		return list.pollLast();
	}

	/**
	 * @see LinkedList#pop
	 */
	public E pop() {
		return list.pop();
	}

	/**
	 * @see LinkedList#remove
	 */
	public E remove() {
		return list.remove();
	}

	/**
	 * @see LinkedList#remove(int)
	 */
	public E remove(int index) {
		return list.remove(index);
	}

	/**
	 * @see LinkedList#removeFirst
	 */
	public E removeFirst() {
		return list.removeFirst();
	}

	/**
	 * @see LinkedList#removeLast
	 */
	public E removeLast() {
		return list.removeLast();
	}

	/**
	 * Do not use iterator to add elements to the list
	 * @see LinkedList#listIterator
	 */
	public ListIterator<E> listIterator() {
		return list.listIterator();
	}
	
	/**
	 * Do not use iterator to add elements to the list
	 * @see LinkedList#listIterator(int)
	 */
	public ListIterator<E> listIterator(int index) {
		return list.listIterator(index);
	}

//	/**
//	 * @see LinkedList#subList(int,int)
//	 */
//	public List<E> subList(int fromIndex, int toIndex) {
//		return list.subList(fromIndex, toIndex);
//	}

	/**
	 * @see LinkedList#toString
	 */
	@Override
	public String toString() {
		return list.toString();
	}
	
	// --- SortedSet --- ----------------------------------
	
//	@Override
//	public Comparator<? super E> comparator() {
//		return this.comparator;
//	}
//	@Override
//	public E first() {
//		return list.getFirst();
//	}
//	@Override
//	public SortedSet<E> headSet(E arg0) {
//		return list.;
//	}
//	@Override
//	public E last() {
//		return null;
//	}
//	@Override
//	public SortedSet<E> subSet(E arg0, E arg1) {
//		return null;
//	}
//	@Override
//	public SortedSet<E> tailSet(E arg0) {
//		return null;
//	}
	
}


class ObjectComparator<E> implements Comparator<E>{
	@Override
	public int compare(E a, E b) {
		if( a.hashCode() > b.hashCode() )
			return  1;
		if( a.hashCode() < b.hashCode() )
			return -1;
		return 0;
	}
}