package edu.uwm.infost325;

import java.util.Arrays;

public class Hash {
	String[] arr1;
	int arrSize;
	int arrItems;

	public static void main(String[] args) {
		Hash a = new Hash(30);
		String[] ElemAddtery = { "1", "5", "17", "21", "26" };
		String[] ElemAddtery2 = { "100", "510", "170", "214", "268", "398", "235", "802", "900", "723", "699", "1",
				"16", "999", "890", "725", "998", "978", "988", "990", "889", "984", "320", "321", "400", "415", "450",
				"50", "660", "624" };

		a.hashFunctionC(ElemAddtery2, a.arr1);

		a.findKey("510");

		a.displayStack();
	}

	public void hashFunctionC(String[] input1, String[] arr1) {
		// for (int i = 0; i < input1.length; i++) {
		// String newVal = stringsForArray[i];
		// arr1[Integer.parseInt(newVal)] = newVal;
		// }

		for (int i = 0; i < input1.length; i++) {
			String newEle = input1[i];
			int arrIndex = Integer.parseInt(newEle) % 29;

			System.out.println("Modulus Index= " + arrIndex + " for value " + newEle);

			while (arr1[arrIndex] != "-1") {
				++arrIndex;
				System.out.println("Try This " + arrIndex + " instead");
				arrIndex %= arrSize;
			}
			arr1[arrIndex] = newEle;
		}
	}

	public String findKey(String key) {
		int arrIndexHash = Integer.parseInt(key) % 29;
		while (arr1[arrIndexHash] != "-1") {
			if (arr1[arrIndexHash] == key) {
				System.out.println(key + " was found in Index " + arrIndexHash);
				return arr1[arrIndexHash];
			}
			++arrIndexHash;
			arrIndexHash %= arrSize;
		}
		return null;
	}

	Hash(int size) {
		arrSize = size;
		arr1 = new String[size];
		Arrays.fill(arr1, "-1");
	}

	public void displayStack() {
		int incr = 0;
		for (int t = 0; t < 3; t++) {

		}
	}

}
