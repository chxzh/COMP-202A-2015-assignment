package assignment4;

import java.util.ArrayList;
import java.util.Collections;

import assignment3.Card;
import assignment3.Suit;
import assignment3.Value;

public class CardPile {
	private ArrayList<Card> cards;
	
	public CardPile() {
		this.cards = new ArrayList<Card>();
	}
	
	public void addToBottom(Card card) {
		this.cards.add(card);
	}
	
	public boolean isEmpty() {
		return this.cards.isEmpty();
	}
	
	public Card get(int i) {
		try{
			return this.cards.get(i);
		} catch(IndexOutOfBoundsException e){
			return null;
		}
	}
	
	public Card remove(int i) {
		try{
			return this.cards.remove(i);
		} catch(IndexOutOfBoundsException e){
			return null;
		}
	}
	
	public int find(Suit s, Value v) {
		int counter = 0;
		for (Card card: this.cards) {
			if(card.getSuit() == s && card.getValue() == v) {
				return counter;
			}
			counter ++;
		}
		return -1;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		int index = 1;
		for (Card card: this.cards) {
			result.append(", "+ index +". "+card);
			index ++;
		}
		result.delete(0, 2);
		return result.toString();
	}
	
	public String toStringHide1st() {
		String unHiden = this.toString();
		int firstComma = unHiden.indexOf(',');
		String hidden = "A Hidden Card";
		return (firstComma == -1)? hidden: hidden + unHiden.substring(firstComma);
	}
	
	public int getNumCards(){
		return this.cards.size();
	}
	
	
	public static CardPile makeFullDeck(int n) {
		CardPile fullDeck = new CardPile();
		for (int i = 0; i < n; i++) {
			for(Suit s: Suit.values())
				for(Value v: Value.values())
					fullDeck.addToBottom(new Card(s,v));
		}
		Collections.shuffle(fullDeck.cards);
		return fullDeck;
	}
	
	public static CardPile makeFullDeck() {
		return makeFullDeck(1);
	}
	public static void main(String[] args){
		Card c1 = new Card(Suit.SPADES, Value.ACE);
		Card c2 = new Card(Suit.SPADES, Value.ACE);
		System.out.println(c1.equals(c2));
	}
}
