public class RainWaterTrapping {
  public static  int rainWaterTrapping(int[] arr){
    int n = arr.length;
    if(arr.length < 3){
      return 0;
    }
    int[] leftMax = new int[n];
    leftMax[0]  = arr[0];
    int[] rightMax = new int[n];
    rightMax[n-1] = arr[n-1];

    //Adding left max of current element to leftMax array
    for(int i=1; i<n; i++){
      leftMax[i] = Math.max(leftMax[i-1], arr[i]);
    }

    //Adding right max of current element to rightMax array
    for(int j=n-2;j>=0;j--){
      rightMax[j] = Math.max(rightMax[j+1], arr[j]);
    }

    int trapWaterBlocks = 0;
    for(int i = 0; i < n; i++){
      trapWaterBlocks = trapWaterBlocks + (Math.min(leftMax[i], rightMax[i]) - arr[i]);
    }
    return trapWaterBlocks;
  }

  public static void main(String a[]){
    int[] heights = {0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
    int trappedWater = rainWaterTrapping(heights);
    System.out.println("trappedWater: "  + trappedWater);
  }
}
