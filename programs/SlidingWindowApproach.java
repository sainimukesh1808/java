public class SlidingWindowApproach {
  public static int sumOfMaxSubArray(int[] arr, int k){
    int wSum = 0;

    //Finding sum of window of length k
    for(int i=0; i<k;i++){
      wSum = wSum + arr[i];
    }

    int maxSum = wSum;

    //sliding window by removing first element and adding current element and so on...
    for(int i=k; i < arr.length; i++){
      wSum = wSum + arr[i] - arr[i-k];
      maxSum = Math.max(maxSum, wSum);
    }


    return maxSum;
  }
  public static void main(String[] a){
    int[] arr = {200,9,31,-4,21,1000};
    int lengthOfSubArray = 3;
    System.out.println("Sum of max sub array of length 3: " + sumOfMaxSubArray(arr, lengthOfSubArray));
  }
}
