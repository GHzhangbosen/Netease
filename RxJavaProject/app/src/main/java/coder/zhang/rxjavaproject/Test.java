package coder.zhang.rxjavaproject;

import java.util.HashMap;

public class Test {

    public static void main(String[] args) {

    }

    public int removeDuplicates(int[] nums) {
        int count = nums.length;
        for (int i = 0, size = nums.length; i < size; i++) {
            if (i - 1 < 0) continue;
            if (nums[i - 1] == nums[i]) count--;
        }
        return count;
    }
}
