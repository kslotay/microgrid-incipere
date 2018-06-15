package testing.t2;

import java.util.Arrays;
import java.util.List;

import sgs.util.SortedLinkedListSet;
import testing.Out_;

public class TestSortedLinkedListSet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SortedLinkedListSet<Integer> list = new SortedLinkedListSet<Integer>();
		
		list.add(1);
		list.add(1);
		list.put(99);
		list.put(1);
		list.add(3);
		list.add(1);
		list.add(2);
		list.add(1);
		list.add(4);
		list.add(1);
		list.add(1);
		list.add(100);
		list.put(1);
		list.add(0);
		list.put(1);
		list.add(2);
		list.put(5);
		list.put(6);
		Out_.pl(list);
		
		list.removeFirst();
		list.removeFirst();
		list.removeLast();
		list.remove((Integer)1);
		list.remove((Integer)4);
		Out_.pl(list);
		
		Integer[] a1 = new Integer[] {1,2,3,4,5,6,7,8,9,7,3,1,0};
		List<Integer> l1 = Arrays.asList(a1);
		//LinkedList<Integer> l2 = new LinkedList<Integer>( l1 );
		list.addAll(l1);
		Out_.pl(list);
	}

}
