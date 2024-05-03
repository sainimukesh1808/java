import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class TestCode {
  int id;
  String name;
  public TestCode(int id, String name){
    this.id = id;
    this.name = name;
  }
  public int getId(){
    return id;
  }

  public String getName(){
    return name;
  }

  public static void main(String[] a){
    Set<String> treeSet = new TreeSet<>();
    treeSet.add("apple");
    treeSet.add("aaaaa");
    treeSet.add("apxle");
    System.out.println("treeset: " + treeSet);
    HashMap<Integer, TestCode> map = new HashMap<>();
    TestCode obj1 = new TestCode(1,"Mukesh");
    TestCode obj2 = new TestCode(2,"Saini");
    TestCode obj3 = new TestCode(3,"Kumar");

    map.put(obj1.getId(), obj1);
    map.put(obj2.getId(), obj2);
    map.put(obj3.getId(), obj3);

    for (Integer id : map.keySet()){
      System.out.println(id + "      "  + map.get(id).getName());
    }
  }
}
