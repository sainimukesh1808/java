package Pragrames;

import java.util.HashMap;

public class DuplicateInString {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		duplicate("java java java is a mukesh a mukesh java");
	}
	public static void duplicate(String s) {
		String s1[]=s.split(" ");
		HashMap<String,Integer> dup = new HashMap<String, Integer>();
		for(String word: s1) {
			if(dup.containsKey(word)) {
				dup.put(word,dup.get(word)+1 );
			}else {
				dup.put(word,1);
			}
		}
			
		for(String str:dup.keySet()) {
			System.out.println(str + " \t" + dup.get(str));
		}
	}}
