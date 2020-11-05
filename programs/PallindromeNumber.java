package Pragrames;

public class PallindromeNumber {
	
	
	public static void isPallindrome(int num) {
		int rem=0;
		int sum=0;
		int temp=num;
		while(num>0) {
			rem=num%10;
			num=num/10;
			sum=sum*10 + rem;
		}
		if(temp==sum) {
			System.out.println("Number is pallnedrome");
		}else {
			System.out.println("Number is not pallnedrome");
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		isPallindrome(101);

	}

}
