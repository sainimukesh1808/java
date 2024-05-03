public class Parent {
  public Parent(){
//    System.out.println("I am from Parent");
  }

  public static void show(){
    System.out.println("I am show method from Parent class. I am static.");
  }

  public void display(){
    System.out.println("I am display method from Parent class. I am not static.");
  }

  public void parentMethod(){
    System.out.println("parentMethod");
  }
}
