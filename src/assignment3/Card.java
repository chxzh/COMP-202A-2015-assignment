package assignment3;

public class Card {
	public enum Suit {SPADES, HEARTS, CLUBS, DIAMONDS};
	public enum Value {ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING};
	private Suit suit;
	private Value value;
	
	public Card(Suit suit, Value value) {
		this.suit = suit;
		this.value = value;
	}
	
	public Suit getSuit() {
		return this.suit;
	}
	
	public Value getValue() {
		return this.value;
	}
	
	public String toString() {
		return this.value.toString() + " of " + this.suit.toString();
	}
}
