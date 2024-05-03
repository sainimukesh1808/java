import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SumOfNumbersPresentInString {
  public static int sumOfNumbersWithoutRegex(String s) throws Exception{
    if(s.isEmpty()){
      throw new Exception("String is empty");
    }
    int sum = 0;
    String temp = "0";
    for(char c : s.toCharArray()){//1000001//aa5aa
      if(!Character.isDigit(c)){
        sum = sum + Integer.parseInt(temp);
        temp = "0";
      }else{
        temp = temp + c;
      }
    }
    sum = sum + Integer.parseInt(temp);// to handle a case where complete string is number like "1234"
    return sum;
  }

  public static int sumOfNumbersWithRegex(String s){
    Pattern pattern = Pattern.compile("\\d+");
    Matcher matcher = pattern.matcher(s);

    int sum = 0;

    // Iterate through the matches and calculate the sum
    while (matcher.find()) {
      String match = matcher.group();
      int number = Integer.parseInt(match);
      sum += number;
    }

    return sum;
  }

  public static void main(String[] a) throws Exception {
    System.out.println("Sum of numbers: " + sumOfNumbersWithRegex("m11u0k11r1r1r0r"));
    System.out.println("Sum of numbers: " + sumOfNumbersWithRegex("Mukesh"));
    System.out.println("Sum of numbers: " + sumOfNumbersWithRegex("100001"));
    System.out.println("Sum of numbers: " + sumOfNumbersWithRegex("100Muk100"));
    System.out.println("Sum of numbers: " + sumOfNumbersWithRegex(" 100 "));

    System.out.println("**********************************************");

    System.out.println("Sum of numbers: " + sumOfNumbersWithoutRegex("m11u0k11r1r1r0r"));
    System.out.println("Sum of numbers: " + sumOfNumbersWithoutRegex("Mukesh"));
    System.out.println("Sum of numbers: " + sumOfNumbersWithoutRegex("100001"));
    System.out.println("Sum of numbers: " + sumOfNumbersWithoutRegex("100Muk100"));
    System.out.println("Sum of numbers: " + sumOfNumbersWithoutRegex(" 100 "));
  }
}
