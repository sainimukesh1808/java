import java.util.HashMap;

public class Recursion {
  static int max(int[] arr, int i){
    if(i == 0){
      return arr[0];
    }
    return Math.max(max(arr, i - 1),arr[i]);
  }

  static int sumOfArray(int[] arr, int i){
    if(i == 0){
      return arr[0];
    }
    return sumOfArray(arr, i-1) + arr[i];
  }

  public static void main(String[] a){
    int[] arr = {1,2,3,4};
    int len = arr.length;
    System.out.println(sumOfArray(arr, len - 1));
    System.out.println(max(arr, len - 1));

  }
}
