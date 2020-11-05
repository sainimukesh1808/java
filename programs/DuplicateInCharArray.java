package Pragrames;

import java.util.HashMap;

public class DuplicateInCharArray {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		char array[] = {'a','b','c','a','b','c','a','b','c','1','1'};
		System.out.println(array);
		duplicate(array);
	}
	public static void duplicate(char s[]) {
		HashMap<Character,Integer> dup = new HashMap<Character, Integer>();
		for(char word: s) {
			if(dup.containsKey(word)) {
				dup.put(word,dup.get(word)+1 );
			}else {
				dup.put(word,1);
			}
		}
			
		for(char str:dup.keySet()) {
			System.out.println("Key: " + str + " Value: " + dup.get(str));
		}
		
		
	

	}}
