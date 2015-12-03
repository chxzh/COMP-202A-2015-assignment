package assignment2;


import java.util.*;


public class Cryptography {
	public static void main(String[] args) {
		System.out.println(caesarEncrypt(" name", 5));

	}

	public static String caesarEncrypt(String originalMessage, int shift) {

		String stri = "";
		for (int i = 0; i < originalMessage.length(); i++) {
			char a = originalMessage.charAt(i);
			if (((int) 'A' <= (int) a && (int) a <= (int) 'Z') || ((int) 'a' <= (int) a && (int) a <= (int) 'z')) {
				// to ensure numbers, punctuation, etc remain the same.
				if (shift > 25) {
					shift = shift % 26;
				} // to cycle over the alphabet
				if ((int) 'a' <= (int) a && (int) a <= (int) 'z') {// if it's
																	// lower
																	// case
					if (((int) a + shift) > 122) {
						shift = shift - 122 + 96;
					} // on the ASCII table, "a" refers to 97;"z" refers to 122.
				}
				if ((int) 'A' <= (int) a && (int) a <= (int) 'Z') {// if it's
																	// upper
																	// case
					if (((int) a + shift) > 90) {
						shift = shift - 90 + 64;
					} // on ASCII,"A" refers to 65,'Z'refers to 90
				}
				char afterShiftChar = (char) (a + shift);

				System.out.print(afterShiftChar);// to check if it comes up the
													// right char after shifting
				System.out.println((int) afterShiftChar);
				stri = (String) (stri + afterShiftChar);
			} else {
				stri = (String) (stri + a);
			}
		}
		return stri;
	}

	public static String caesarDecrypt(String encoded, int shift) {
		String d = caesarEncrypt(encoded, 26 - (shift % 26));
		System.out.println(26 - (shift % 26));
		return d;
	}// question 2 done

	public static String crackCipher(String encoded, int numberLetters) {
		int maxSoFar = 0;
		int numbOfEngWords = 0;
		String result = "";
		for (int i = 0; i < numberLetters; i++) {
			String EncodedResult = caesarDecrypt(encoded, i);
			numbOfEngWords = SentenceChecker.countEnglishWords(EncodedResult);
			if (maxSoFar < numbOfEngWords) {
				maxSoFar = numbOfEngWords;
				result = EncodedResult;
			}
		}
		return result;
	}

	public static void shuffle(char[] r) {
		Random generator = new Random(12345);
		int n = r.length;
		for (int i = 0; i < (n ^ 4); i++) {
			int x = generator.nextInt(r.length);
			int y = generator.nextInt(r.length);
			char temp = r[x];
			r[x] = r[y];
			r[y] = temp;

		}
	}// question 4 done

	public static char[] generatePermutation() {
		// char[] random=new char[26];

		char[] letter = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
				'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		shuffle(letter);

		System.out.println(letter);
		return letter;
	}// question 5 done

	public static String permuteEncrypt(String input) {

		char[] alphabet = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
				'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		char[] shuffledAlphabet = generatePermutation();
		char[] inputChars = input.toUpperCase().toCharArray();

		for (int i = 0; i < inputChars.length; i++) {
			char currentChar = inputChars[i];
			int index = -1;
			for (int j = 0; j < alphabet.length; j++) {
				if (currentChar == alphabet[j]) {
					index = j;
					break;
				}
			}
			if (index == -1) {
				continue;
			}
			inputChars[i] = shuffledAlphabet[index];
		}

		String output = new String(inputChars);

		return output;

	}
}
