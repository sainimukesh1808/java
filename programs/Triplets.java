import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Triplets {
  public static List<List<Integer>> getTriplets(int[] arr, int target) throws Exception {
    if(arr.length == 0){
      throw new Exception("Please enter a valid array");
    }
    List<List<Integer>> tripletsList = new ArrayList<>();
    Arrays.sort(arr);
    for(int i=0; i< arr.length-2;i++){
      if(i>0 && arr[i] == arr[i-1]){
        continue;
      }
      int j = i+1;
      int k = arr.length - 1;
      while (j<k){
        if(arr[i] + arr[j] + arr[k] == target){
          tripletsList.add(Arrays.asList(arr[i],arr[j],arr[k]));
          while (j<k && arr[j] == arr[j+1]){
            j++;
          }
          while (j<k && arr[k] == arr[k-1]){
            k--;
          }
          j++;
          k--;
        }
        else if (arr[i] + arr[j] + arr[k] < target) {
          j++;
        }
        else if (arr[i] + arr[j] + arr[k] > target) {
          k--;
        }
      }
    }
    return tripletsList;
  }

  public static List<List<Integer>> getDoubles(int[] arr, int target) throws Exception {
    if(arr.length == 0){
      throw new Exception("Please enter a valid array");
    }
    List<List<Integer>> doubles = new ArrayList<>();
    Arrays.sort(arr);
    int i = 0;
    int j = arr.length-1;
    while (i<j){
//      if(i>0 && arr[i] == arr[i-1]){
//        continue;
//      }
      if(arr[i] + arr[j] == target){
        doubles.add(Arrays.asList(arr[i], arr[j]));
        while (i<j && arr[i] == arr[i+1]){
          i++;
        }
        while (i<j && arr[j] == arr[j-1]){
          j--;
        }
        i++;
        j--;
      }else if(arr[i] + arr[j] < target){
        i++;
      }else if(arr[i] + arr[j] > target){
        j--;
      }
    }

    return doubles;
  }

  public static Map<Character, Integer> getOccurrence(String str) throws Exception {
    if(str.isEmpty()){
      throw new Exception("String is empty");
    }
    Map<Character, Integer> map = new HashMap<>();
    for(Character c : str.toLowerCase().toCharArray()){
      if(!Character.isAlphabetic(c)){
        continue;
      }
      if(!map.containsKey(c)){
        map.put(c,1);
      }else {
        map.put(c,map.get(c) + 1);
      }
    }
    return map;
  }

  public static void main(String a[]) throws Exception {
//    int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
//    int target = 15;
    int[] nums = {0,0,0,2,2,2,2,-1,-1,-1,-2,-2,-2};
    int target = 0;
    List<List<Integer>> triplets = getTriplets(nums, target);
    for (List<Integer> triple : triplets){
      System.out.println(triple);
    }

    List<List<Integer>> doubles = getDoubles(nums, target);
    for (List<Integer> d : doubles){
      System.out.println(d);
    }


    String str = "Mukesh Kumar Saini";
    Map<Character,Integer> map = getOccurrence(str);
    for (char c : map.keySet()){
      System.out.println(c + "    " + map.get(c));
    }
  }
}
