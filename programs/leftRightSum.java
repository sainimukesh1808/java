/*
print the number/numbers who has/have equal sum of right side numbers and left side numbers.
I/P: 2,3,1,2,1,5
	2+3+1 = 6
	1+5 = 6
O/P: 2
*/
package com.qa.java;
public class leftRightSum {
		static void leftRightsum(int[] arr) {
        //1,0,2,2,0,0,2,2
        int totalSum = 0;
        for(int num : arr){
            totalSum += num;
        }
        
        int leftSum = 0;
        int rightSum = 0;
        for(int i =0; i < arr.length;i++){
            
            rightSum = totalSum-leftSum-arr[i];
           
            if(leftSum == rightSum){
                System.out.print(i);
            }
            leftSum = leftSum + arr[i];
        }
        
    }

    public static void main(String[] args) {
        int[] a = {2,3,1,2,1,5};
        leftRightsum(a);
        
    }
	}

