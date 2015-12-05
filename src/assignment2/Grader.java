package assignment2;

import java.util.Scanner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class Grader {
	private static int grade = 100;
	private static StringBuffer remark = new StringBuffer();
	private static String signature = "by cxz";
	private static Scanner sc;
	
	@BeforeClass
	public static void prepare() {
		sc = new Scanner(System.in);
		return;
	}
	
	@AfterClass
	public static void finish() {
		Grader.checkGeneral();
		Grader.summarize();
	}
	
	public static void summarize() {
		System.out.println("------");
		System.out.println(Grader.grade);
		System.out.println();
		if(Grader.remark.toString().equals("") && Grader.grade == 100)
			Grader.remark.append("Good.\n");
		System.out.println(Grader.remark);
		System.out.println(Grader.signature);
	}
//	
//	private static void discountWithRemark(double discount, String remark) {
//		Grader.grade = (int)(Grader.grade*(1-discount)) + 1;
//		Grader.remark.append("- ");
//		Grader.remark.append(remark);
//		Grader.remark.append(" (-"+(int)(discount*100)+")");
//		Grader.remark.append("\n");
//	}
	
	private static void deductWithRemark(int deduction, String remark) {
		Grader.grade -= deduction;
		Grader.remark.append("- ");
		Grader.remark.append(remark);
		Grader.remark.append(" (-"+deduction+")");
		Grader.remark.append("\n");
		
	}
	private static void checkString(String expected, String result, int deduction, String failRemark) {
		if(!expected.equals(result)) {
			deductWithRemark(deduction, failRemark);
			System.out.println(expected+" != "+result);
			//Assert.assertTrue(false);
		}
	}
	
	private static void checkGeneral() {
		
	}
	
	private static void checkCaesar(String input, 
			int shift, 
			boolean isEncryption, 
			int deduction, 
			String remark) {
		String expected, result;
		if(isEncryption) {
			expected = MyCryptography.caesarEncrypt(input, shift);
			result = Cryptography.caesarEncrypt(input, shift);
		}
		else {
			expected = MyCryptography.caesarDecrypt(input, shift);
			result = Cryptography.caesarDecrypt(input, shift);
		}
		checkString(expected, result, deduction, remark);
	}
	
	private void manualCheck(String functionName, String action, int deduction) {
		System.out.println("Did "+functionName+" "+action+"?");
		if(!sc.nextLine().equals("")) {
			Grader.deductWithRemark(deduction, functionName+" didn't "+action);
		}
	}
	
	/*
	 * 15 points in total for Encrypt function
	 */
	@Test
	public void testEncrytFunction() {
		Grader.checkCaesar("Nm", 3, true, 
				5, "caesarEncrypt() cannot handle upper/lower case letters encryption");
		Grader.checkCaesar("abxyz", 3, true,
				5, "caesarEncrypt() cannot cycle around alphabet");
		Grader.checkCaesar("n,! m", 3, true,
				5, "caesarEncrypt() cannot handle punctuation");
	}
	
	/*
	 * 10 points in total for Decrypt function
	 */
	@Test
	public void testDecryptFuction() {
		Grader.checkCaesar("Nm", 3, false, 
				4, "caesarDecrypt() cannot handle upper/lower case letters encryption");
		Grader.checkCaesar("abxyz", 3, false,
				3, "caesarDecrypt() cannot cycle around alphabet");
		Grader.checkCaesar("n,! m", 3, false,
				3, "caesarDecrypt() cannot handle punctuation");
	}
	
	@Test
	public void testCrackingFunction() {
		this.manualCheck("crackCipher()", "properly use the countWords method", 1);
		this.manualCheck("crackCipher()", "call the decrypt (or encrypt) method", 4);
		this.manualCheck("crackCipher()", "generalize properly for numLetters", 2);
		this.manualCheck("crackCipher()", "loop to try again and again", 5);
		this.manualCheck("crackCipher()", "take the maximum value", 8);
	}
	
	@Test
	public void testShuffleFunction() {
		this.manualCheck("shuffle()", "create the Random object outside of the loop", 3);
		this.manualCheck("shuffle()", "loop to repeat n^4 times", 3);
		this.manualCheck("shuffle()", "choose TWO random values inside the loop", 4);
		this.manualCheck("shuffle()", "swap accurately (usually by using a temporary 3rd variable)", 3);
		this.manualCheck("shuffle()", "modify the input array (instead of creating a new one)", 2);
		this.manualCheck("shuffle()", "use the random seed of 12345", 5);
		
	}
	
	@Test
	public void testGenerateMappingFunction() {
		this.manualCheck("generatePermutation()", "create an array", 2);
		this.manualCheck("generatePermutation()", "fill the array with A-Z", 4);
		this.manualCheck("generatePermutation()", "call shuffle and then return the shuffled array", 4);
		
	}
	
	@Test
	public void testPermuteFunction() {
		this.manualCheck("permuteEncrypt()", "create random permutation using method from question 5", 3);
		this.manualCheck("permuteEncrypt()", "loop through every char of the string", 5);
		this.manualCheck("permuteEncrypt()", "concatenate strings together", 4);
		this.manualCheck("permuteEncrypt()", "look up correct value in an array", 10);
		this.manualCheck("permuteEncrypt()", "keep punctuation unchanged", 4);
		this.manualCheck("permuteEncrypt()", "map lowercase letters to EITHER uppercase or lowercase returned by generateMapping()", 4);
		this.manualCheck("permuteEncrypt()", "avoid using 26 if-statements", 5);
	}

	@Test
	public void generalTest() {
		this.manualCheck("You", "comment the code", 5);
	}
}
