/*
print the number/numbers who has/have equal sum of right side numbers and left side numbers.
I/P: 2,3,1,2,1,5
	2+3+1 = 6
	1+5 = 6
O/P: 2
*/
package com.qa.java;
public class leftRightSum {
		public static void main (String[] args) {
	    int arr[] = {2,3,1,2,1,5};
	    int arr_length = arr.length;
			for(int ele=1;ele<arr_length;ele++){
	      int l_sum=0;
	      int r_sum=0;
	      for(int l_sub_ele=ele-1; l_sub_ele>=0; l_sub_ele--){
	        l_sum = l_sum + arr[l_sub_ele];
	      }
	      for(int r_sub_ele=ele+1; r_sub_ele<arr_length;r_sub_ele++){
	        r_sum = r_sum + arr[r_sub_ele];
	      }
	      
	      if(r_sum == l_sum){
	        System.out.println(arr[ele]);
	      }
	    }
		}
	}

