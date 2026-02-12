package com.restassured.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator;

public class ComparatorAndMapSortingDemo {
	
	
	
	public static void getOccuranceOfChars(String s) {
		Map<Character, Integer> map = new HashMap<>();
		for(char c : s.toCharArray()) {
			
			if(!map.containsKey(c)) {
				map.put(c, 1);
			}else {
				map.put(c, map.get(c) + 1);
			}
		}
		
		List<Map.Entry<Character, Integer>> sortedOnValues = map.entrySet().stream().sorted(Map.Entry.<Character,Integer>comparingByValue().reversed()).collect(Collectors.toList());
		for(int i =0; i<3;i++) {
			System.out.println(sortedOnValues.get(i).getKey());
		}
		
		System.out.println(map.toString());
	}
	
	public static void main(String a[]) {
		ComparatorAndMapSortingDemo.getOccuranceOfChars("mukeshsaini");
		Map<String, Integer> map = new HashMap<>();
		map.put("Zebra", 5);
		map.put("Apple", 3);
		map.put("Mango", 8);
		
		//Sorting Map using Java -8
		List<Map.Entry<String, Integer>> sortedEntries = map.entrySet()
	            .stream()
	            .sorted(Map.Entry.comparingByKey())
	            .collect(Collectors.toList());
		
		//Reversed Sorting Map using Java -8
		List<Map.Entry<String, Integer>> reversedSortedEntries = map.entrySet()
	            .stream()
	            .sorted(Map.Entry.<String,Integer>comparingByKey().reversed())
	            .collect(Collectors.toList());
		
		sortedEntries.forEach(entry -> System.out.println(entry.getKey()));
		
		for(Map.Entry<String,Integer> entry:sortedEntries) {
			System.out.println(entry.getKey() + "   " + entry.getValue());
		}
		
		
		Employee emp1 = new Employee("Mukesh", "bold", 31);
		Employee emp2 = new Employee("Mukesh", "Adobe", 30);
		Employee emp3 = new Employee("Mukesh", "DS", 25);
		
		List<Employee> emps = new ArrayList<Employee>();
		emps.add(emp1);
		emps.add(emp2);
		emps.add(emp3);
		
		//sorting with Comparator
		emps.sort(java.util.Comparator.comparing(Employee::getCompany));
		emps.stream().forEach(e->System.out.println(e.getCompany()));
		//sorting with Collections
		Collections.sort(emps, java.util.Comparator.comparingInt(Employee::getAge));
		emps.stream().forEach(e->System.out.println(e.getAge()));
	}

}
