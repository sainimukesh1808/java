package com.qa.java;

import java.util.ArrayList;
import java.util.Arrays;

public class Anagrams {
	public static String[] sorted_array(String sortArray[]){
        int size = sortArray.length;
        for(int i = 0; i<size-1; i++) {
         for (int j = i+1; j<size; j++) {
            if(sortArray[i].compareTo(sortArray[j])>0) {
               String temp = sortArray[i];
               sortArray[i] = sortArray[j];
               sortArray[j] = temp;
            }
         }
        }
        return sortArray;
        
    }
    
    public static ArrayList<String> allAnagrams(String anagramsArray[]){
        ArrayList<String> anagramsListArray = new ArrayList<String>();
        int len = 6;
        for(String ele:anagramsArray){
            if(ele.length()==len){
                anagramsListArray.add(ele);
            }
        }
        return anagramsListArray;
    }
   public static void main(String args[]) {
      String[] myArray = {"JavaFX", "HBase", "OpenCV", "Java", "Hadoop", "Neo4j"};
      String sortArray[] = sorted_array(myArray);
      ArrayList<String> anagramsListArray = allAnagrams(sortArray);
      System.out.println(Arrays.toString(sortArray));
      System.out.println(Arrays.toString(anagramsListArray.toArray()));
   }


}
