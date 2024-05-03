public class SingletonExample {
  private static SingletonExample singletonExample;
  private SingletonExample(){

  }
  public static SingletonExample getSingleton(){
    if(singletonExample == null){
      singletonExample = new SingletonExample();
    }
    return singletonExample;
  }

  public void showMessage(){
    System.out.println("Mukesh");
  }

  public static void main(String[] s){
    SingletonExample singletonExample1 = new SingletonExample();
    singletonExample1.showMessage();
  }

}
