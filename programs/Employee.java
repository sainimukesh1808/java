package com.restassured.test;

public class Employee {
	String name;
	String company;
	int age;
	public Employee(String name, String company, int age) {
		this.name = name;
		this.company = company;
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

}
