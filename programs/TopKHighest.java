package com.programe;

import java.util.*;

public class TopKHighest {

    public static List<Integer> topK(int[] arr, int k) {

        if (arr == null || arr.length == 0 || k <= 0) {
            return Collections.emptyList();
        }

        k = Math.min(k, arr.length);

        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : arr) {
            minHeap.offer(num);

            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
//        Step-by-step:
//        	1️⃣ Insert number into minHeap
//        	2️⃣ If heap size exceeds k → remove smallest
//        	3️⃣ At the end → heap contains only top k largest numbers
//        	Min heap always keeps smallest element at top.
//
//        	So when heap size becomes k+1:
//        	minHeap.poll();
//        	removes the smallest element.
//        	🧠 Example
//        	arr = [1, 23, 12, 9, 30, 2, 50]
//        	k = 3
//
//        	Iteration:
//        	Heap after inserting 1 → [1]
//        	Heap after inserting 23 → [1, 23]
//        	Heap after inserting 12 → [1, 23, 12]
//        	Heap after inserting 9 → [1, 9, 12, 23]
//        	Size > 3 → remove 1
//        	Heap → [9, 23, 12]
//        	Continue…

        List<Integer> result = new ArrayList<>(minHeap);

        // Manual descending sort (without using reverseOrder)
        Collections.sort(result, Collections.reverseOrder());
//        for (int i = 0; i < result.size() - 1; i++) {
//            for (int j = i + 1; j < result.size(); j++) {
//                if (result.get(i) < result.get(j)) {
//                    int temp = result.get(i);
//                    result.set(i, result.get(j));
//                    result.set(j, temp);
//                }
//            }
//        }

        return result;
    }

    public static void main(String[] args) {

        int[] arr1 = {1, 23, 12, 9, 30, 2, 50};
        System.out.println(topK(arr1, 3));  // [50, 30, 23]

        int[] arr2 = {11, 5, 12, 9, 44, 17, 2};
        System.out.println(topK(arr2, 2));  // [44, 17]

        int[] arr3 = {-10, -3, -20, -5, -1};
        System.out.println(topK(arr3, 2));  // [-1, -3]

        int[] arr4 = {5, 1, 2};
        System.out.println(topK(arr4, 10)); // [5, 2, 1]
    }
}
