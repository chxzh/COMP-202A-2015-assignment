package assignment3;

public class CardPile {
	private Card[] cards;
	private int numCards;
	
	public CardPile() {
		this.cards = new Card[52];
		this.numCards = 0;
	}
	
	public void addToBottom(Card card) {
		this.cards[this.numCards] = card;
		this.numCards ++;
	}
	
	public boolean isEmpty() {
		return this.numCards == 0;
	}
	
	public Card get(int i) {
		return ( i < this.numCards && i >= 0 )?
				this.cards[i]:
				null;
	}
	
	public Card remove(int i) {
		if (i >= this.numCards || i < 0) return null;
		Card removedCard = this.cards[i];
		this.numCards --;
		for (int j = i; j < this.numCards; j++) {
			this.cards[j] = this.cards[j+1];
		}
		this.cards[this.numCards] = null;
		return removedCard;
	}
	
	public int find(Suit s, Value v) {
		int result = -1;
		for (int i = 0; i < this.numCards; i ++) {
			Card card = this.cards[i];
			if(card.getSuit() == s && card.getValue() == v) {
				result = i;
				break;
			}
		}
		return result;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < this.numCards; i++) {
			result.append(", "+ i +". "+this.cards[i].toString());
		}
		result.delete(0, 2);
		return result.toString();
	}
	
	public static CardPile makeFullDeck(){
		CardPile fullDeck = new CardPile();
		for(Suit s: Suit.values())
			for(Value v: Value.values())
				fullDeck.addToBottom(new Card(s,v));
		UtilityCode.fisherYatesShuffle(fullDeck.cards, fullDeck.numCards);
		return fullDeck;
	}
	
	public static void main(String[] args) {
		StringBuffer result = new StringBuffer("123456");
		result.delete(0, 2);
		System.out.println(result.toString());
	}
	
}
