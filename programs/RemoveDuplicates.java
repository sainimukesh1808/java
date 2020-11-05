package Pragrames;

import java.util.HashSet;
import java.util.Set;

public class RemoveDuplicates {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int i =123156145;
		String s = String.valueOf(i);
//		System.out.println(s);
		int n=s.length();
		String s1="";
		char c[] = s.toCharArray();
		Set<Character> newset = new HashSet<Character>();
		for(char d:c) {
			newset.add(d);
		}
		for(char e:newset) {
			s1 = s1+e;
			
		}
		System.out.println(s1);
		rmDuplicates(c,n);
	}
	
	public static void rmDuplicates(char c1[], int size) {
		String s="";
		for(int i=0; i<size; i++) {
			for(int j=0;j<i;j++) {
				if (c1[j]==c1[i]) {
					break;
					
				}
				
			}
		}
		System.out.println(s);	
	}

}
