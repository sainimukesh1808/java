package Pragrames;

public class PrimeNumbers {
	public static boolean isPrimeNumber(int num) {
		//2is the lowest prime number
		if(num<=1) {
			return false;
		}
		for(int i=2; i<num;i++) {
			if(num%i==0) {
				return false;
				
			}
		}
		return true;
		
	}
	public static int getPrimeNumbers(int num) {
		int count=0;
		for(int i=2;i<=num;i++) {
			if(isPrimeNumber(i)) {
				System.out.println(i);
				count++;
			}
		}
		return count;
	
	}
	public static void main(String[] args) {
		System.out.println(getPrimeNumbers(7));
		

	}

}
