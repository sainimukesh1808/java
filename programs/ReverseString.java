import java.util.ArrayList;
import java.util.List;

public class ReverseString {
  public static String reverseWordsOfStringWithoutReversingCharOfWords(String s) throws Exception {
    if(s.isEmpty()){
      throw new Exception("String is empty");
    }
    String revers = "";
    String[] words = s.split(" ");
    for(int i = words.length -1; i >= 0; i--){
      revers += words[i] + " ";
    }
    return revers.trim();
  }

  public static String reverseString(String s) throws Exception {
    if(s.isEmpty()){
      throw new Exception("String is empty");
    }
    String reverse = "";
    for(int i = s.length() - 1; i >= 0; i--){
      reverse += s.charAt(i);
    }
    return reverse;
  }

  public static String reverseStringByKeepingSpacesAtForwardPosition(String s) throws Exception {
    if(s.isEmpty()){
      throw new Exception("String is empty");
    }
    String revers = "";
    List<Integer> spaces = new ArrayList<>();
    for(int i = 0; i < s.length(); i++){
      if(s.charAt(i) == ' '){
        spaces.add(i);
      }
    }

    for(int i = s.length() - 1; i >= 0; i--){
      if(s.charAt(i) != ' '){
        if(spaces.contains(revers.length())){
          revers += " ";
        }
        revers += s.charAt(i);
      }

    }
    return revers;
  }
  public static void main(String[] a) throws Exception {
    String str = "Mukesh Kumar Saini";
    System.out.println("Input: " + str);
    System.out.println("reverseString: " + reverseString(str));
    System.out.println("reverseWordsOfStringWithoutReversingCharOfWords: " + reverseWordsOfStringWithoutReversingCharOfWords(str));
    System.out.println("reverseStringByKeepingSpacesAtForwardPosition: " + reverseStringByKeepingSpacesAtForwardPosition(str));
  }
}
