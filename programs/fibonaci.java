package Pragrames;

import java.util.Scanner;

public class fibonaci {
	public static void primenumber(){
		int number=7;
		for(int i=3;i<=number/2;i++) {
			if(number%i == 0){
				System.out.println("prime not");
				break;
					}
		}
		}

	public static void main(String[] args) {
		fibonaci.primenumber();
		Scanner n=new Scanner(System.in);
		System.out.println("enter vale of n: ");
		String s=n.nextLine();
		int num=Integer.parseInt(s);
		int a=1;
		int b=2;
		int c;
		System.out.println(a);
		System.out.println(b);
		for(int i=1;i<=num;i++) {
			
			c=a+b;
			a=b;
			b=c;
			System.out.println(c);
			
		}
	}

}
