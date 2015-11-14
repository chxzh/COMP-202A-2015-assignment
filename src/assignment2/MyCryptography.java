package assignment2;

import java.util.Random;

/*
 * this is my personal solution of assignment 2, COMP202A on 2015
 * confidence will make this into a test case reference
 * */
public class MyCryptography {
	public static void main(String[] args){
		System.out.println(caesarEncrypt("GO ranger!", 3));
		System.out.println(caesarDecrypt("JR udqjhu!", 3));
		System.out.println("end of main");
		return;
	}
	
	public static String caesarEncrypt(String originalMessage, int shift) {
		if(originalMessage == null)return null;
		StringBuffer result = new StringBuffer();
		shift = shift % 26;
		char currentLetter;
		for (int i = 0; i < originalMessage.length(); i++) {
			currentLetter = originalMessage.charAt(i);
			/* shift only when this is a letter,
			 * however isLetter is not permitted for students
			 * */
			if (Character.isLetter(currentLetter)) {
				char starter = (Character.isUpperCase(currentLetter))?'A':'a';
				int index = currentLetter - starter;
				int shiftedIndex = (index + shift)%26;
				currentLetter = (char)(starter+shiftedIndex);
//				currentLetter += shift;
//				if (! Character.isLetter(currentLetter)){
//					currentLetter -= 26;
//				}
			}
			result.append(currentLetter);
		}
		return result.toString();
	}
	
	public static String caesarDecrypt(String encoded, int shift) {
		return caesarEncrypt(encoded, 26-shift);
	}
	
	public static String crackCipher(String encoded, int numberLetters) {
		String result = "", curDecrypted;
		int maxWordCount = -1, curWordCount;
		for (int i = 0; i < numberLetters; i++) {
			curDecrypted = caesarDecrypt(encoded, i);
			curWordCount = SentenceChecker.countEnglishWords(curDecrypted);
			if (curWordCount > maxWordCount){
				maxWordCount = curWordCount;
				result = curDecrypted;
			}
		}
		return result;
	}
	
	public static void shuffle(char[] array) {
		Random generator = new Random(12345);
		int shuffleTimes = array.length;
		shuffleTimes = shuffleTimes * shuffleTimes * shuffleTimes * shuffleTimes;
		char temp;
		int index1, index2;
		for (int i = 0; i < shuffleTimes; i++) {
			index1 = generator.nextInt(array.length);
			index2 = generator.nextInt(array.length);
			temp = array[index1];
			array[index1] = array[index2];
			array[index2] = temp;
		}
	}
	
	public static char[] generatePermutation() {
		char[] result = new char[26];
		for (short i = 0; i < 26; i++) {
			result[i] = (char)('A'+i);
		}
		shuffle(result);
		return result;
	}
	
	public static String permuteEncrypt(String input) {
		char[] newAlphabet = generatePermutation();
		StringBuffer result = new StringBuffer();
		char currentLetter, starterLetter;
		int index;
		boolean uppercase;
		for(int i = 0; i < input.length(); i++) {
			currentLetter = input.charAt(i);
			if(Character.isLetter(currentLetter)) {
				if (Character.isUpperCase(currentLetter)) {
					uppercase = true;
					starterLetter = 'A';
				}
				else {
					uppercase = false;
					starterLetter = 'a';
				}
				index = currentLetter - starterLetter;
				currentLetter = newAlphabet[index];
				if (!uppercase) {
					// alphabet is in upper case as default
					currentLetter = Character.toLowerCase(currentLetter);
				}
			}
			result.append(currentLetter);
		}
		return result.toString();
	}
}
