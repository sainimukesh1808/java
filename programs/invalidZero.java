/*
Consider 0 as an invalid number. if two consecutive numbers are same then convert left number to zero and right to double of it's value. 
In the end, shifts all zeros to right.
I/P: 2,4,5,0,0,5,5,4,8,6,0,6,8,8
	2,4,0,0,0,10,5,4,8,6,0,6,8,8
	2,4,0,0,0,10,5,4,8,0,0,12,8,8
	2,4,0,0,0,10,5,4,8,0,0,12,0,16
O/P: 2,4,10,5,4,8,12,16,0,0,0,0,0,0
*/

package com.qa.java;

import java.util.Arrays;

public class invalidZero {
		public static void main (String[] args) {
	    int arr[] = {2,4,5,0,0,5,5,4,8,6,0,6,8,8};
	    for(int ele=0;ele<arr.length-1;ele++){
	      for(int j=ele+1; j<arr.length; j++){
	        if(arr[j]==0){
	          continue;
	        }
	        if(arr[ele] == arr[j]){
	          arr[ele] = 0;
	          arr[j] = 2*arr[j];
	        }
	        break; 
	      }
	    }
	    int temp;
	    for(int ele=0;ele<arr.length-1;ele++){
	      for(int j=ele+1; j<arr.length; j++){
	        if(arr[ele]==0){
	          temp = arr[ele];
	          arr[ele] = arr[j];
	          arr[j] = temp;
	        }}
	        
	    }
	    System.out.println("New Araay: " + Arrays.toString(arr));
		
		}
	}

/*
O/P: New Array: [2, 4, 10, 5, 4, 8, 12, 16, 0, 0, 0, 0, 0, 0]
*/
