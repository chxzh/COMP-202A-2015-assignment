package utils;

import java.util.Scanner;

import org.junit.AfterClass;
import org.junit.BeforeClass;


public class Grader {
	private static final int FULLMARK = 100;
	private static int grade = FULLMARK;
	private static final String signature = "by cxz";
	private static StringBuffer remark;
//	private static Scanner sc;
	
	@BeforeClass
	public static void prepare() {
		sc = new Scanner(System.in);
		return;
	}
	
	@AfterClass
	public static void finish() {
		Grader.checkOverall();
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

	private static void checkOverall() {
		return;
	}
	
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

//	private void manualCheck(String functionName, String action, int deduction) {
//		System.out.println("Did "+functionName+" "+action+"?");
//		if(!sc.nextLine().equals("")) {
//			Grader.deductWithRemark(deduction, functionName+" didn't "+action);
//		}
//	}
}
