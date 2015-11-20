package assignment3;

import java.util.Random;
public class UtilityCode
{
  private static Random r = new Random();
  
  public static void shuffle(Card[] cards, int numCards) {
    for (int i = 0; i < Math.pow(numCards, 4); i++) {
     int j = r.nextInt(numCards);
     int k = r.nextInt(numCards);
     //now swap jth position with kth position
     Card temp = cards[j];
     cards[j] = cards[k];
     cards[k] = temp;
    }
  }
  
  public static void fisherYatesShuffle(Card[] cards, int numCards) {
	  for (int i = 0; i < numCards; i++) {
		  int target = i + r.nextInt(numCards - i);
		  Card temp = cards[i];
		  cards[i] = cards[target];
		  cards[target] = temp;
	  }
  }
}
