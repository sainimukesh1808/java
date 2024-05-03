import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringProgram {
  public static void permutations(char[] arr, int fi){
    if(fi == arr.length - 1){
      System.out.println(arr);
      return;
    }

    for(int i = fi; i < arr.length; i++){
      swap(arr, i, fi);
      permutations(arr, fi+1);
      swap(arr, i, fi);
    }
  }
  public static void swap(char[] arr, int i, int j){
    char temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
  }

  public static boolean isAnagrams(String s1, String s2){
    if(s1.length() != s2.length()){
      return false;
    }
    List<Character> list = new ArrayList<>();
    for(char c : s1.toCharArray()){
      list.add(c);
    }
    for(int i = 0 ; i < s1.length(); i++){
      char c = s2.charAt(i);
      if(!(list.contains(c))){
        return false;
      }else{
        list.remove(Character.valueOf(c));
      }
    }
    return true;
  }

  public static void swapWithoutThirdVariable(String str1, String str2){
    str1 = str1 + str2;//MukeshSaini
    str2 = str1.substring(0, str1.length() - str2.length());//11-5=6//Mukesh
    str1 = str1.substring(str2.length());//Saini
  }
  public static void main(String[] a){
    String str = "ABC";
    System.out.println("Given String: " + str);
    permutations(str.toCharArray(), 0);

    System.out.println("is given staring are anagrams? " + isAnagrams("anbb","aanb"));
  }
}
