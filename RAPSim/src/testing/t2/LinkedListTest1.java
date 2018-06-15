package testing.t2;

import java.util.LinkedList;

public class LinkedListTest1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		A a = new A();
		//LinkedList<Integer> x = a.getList();
		
		System.out.print(a.getList().remove());
		System.out.print(a.getList().removeFirst());
		System.out.print(a.getList().removeLast());
		
		System.out.print(a.getList().peekFirst());
		System.out.print(a.getList().peekLast());
		
		System.out.print(a.getList().pop());
	}

}

class A{
	LinkedList<Integer> getList(){
		
		LinkedList<Integer> list = new LinkedList<>();
		System.out.print("|");
		
		for(int i=0; i<5; i++)
			list.add(i+1);
		
		return list;
	}
}