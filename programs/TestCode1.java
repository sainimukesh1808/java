import java.util.HashMap;
import java.util.Map;

public class TestCode1 {
  public static void main(String[] a){
    String str = " Rama  Rama Rama Rama HareKrishna Hare Krishna Hare KrishnaHareKrishna Krishna HareHare!!";
    //stringbuilder
    "Hare" -->stringbuilder. contains()
              count++
    stringbuilder.delete("Hare")
        map.put("Hare", count)
            count = 1

    Map<String,Integer> map = new HashMap<>();
    String[] arr = str.split(" ");

    for(int i = 0; i < arr.length; i++){
      if(!map.containsKey(arr[i])){
        map.put(arr[i], 1);
      }else{
        map.put(arr[i], map.get(arr[i]) + 1);
      }
    }

    for(String word : map.keySet()){
      System.out.println("Word: " + word + "      " + map.get(word));
    }

  }

}
