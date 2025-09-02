//Anagrams are words or phrases formed by rearranging the letters of another word or phrase, using all the original letters exactly once.
//Listen → Silent
//Elbow → Below

import java.util.Arrays;

public class Anagrams {

    public static boolean isAnagram(String s1, String s2) {
        // If lengths differ, not an anagram
        if (s1.length() != s2.length()) {
            return false;
        }

        // Convert to char arrays
        char[] arr1 = s1.toCharArray();
        char[] arr2 = s2.toCharArray();

        // Sort both arrays
        Arrays.sort(arr1);
        Arrays.sort(arr2);

        // Compare sorted arrays
        return Arrays.equals(arr1, arr2);
    }

    public static void main(String[] args) {
        System.out.println(isAnagram("listen", "silent")); // true
        System.out.println(isAnagram("triangle", "integral")); // true
        System.out.println(isAnagram("hello", "world")); // false
    }
}
