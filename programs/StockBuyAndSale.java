public class StockBuyAndSale {
  public static int maxProfit(int[] arr){
    //time complexity O(n) --> linear
    //space complexity O(1) --> Constant
    int minSoFar = arr[0];
    int maxProfit = 0;
      for (int j : arr) {
          minSoFar = Math.min(minSoFar, j);
          int profit = j - minSoFar;
          maxProfit = Math.max(maxProfit, profit);
      }
    return maxProfit;
  }

  public static void main(String[] a){
    int[] sharesPriceEachDay = {5,2,6,1,4};
    System.out.println("Max Profit: " + maxProfit(sharesPriceEachDay));
  }
}
