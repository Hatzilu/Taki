package com.example.admin.itamartaki;

import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * This is the hand of the player, at the start of the game each player recieves 8 cards.
 * In the player's turn, the player can pick a card from the hand and place it in the pile.
 */
public class PlayerHand {

    private static boolean debugMode = false;
    private final ArrayList<Card> cardlist;

    public PlayerHand()
    {
        cardlist = new ArrayList<>();
    }

    /**
     * This function adds 8 cards to the hand.
     * This function is called at the beginning of the game.
     * @param koopa
     */
    public void initiatePlayerHand(Koopa koopa)
    {
        if (debugMode)
        {
            cardlist.add(koopa.drawCardDebugMode());
        }
        else {
            for (int i = 0; i < 8; i++) {
                cardlist.add(koopa.drawCard());
            }
        }
    }

    /**
     * This function rearranges the positions of all the cards in the player's hand relative to the amount of cards in the hand.
     * (The more cards there are in the player's hand, the smaller the spaces between them become.)
     * @param screenWidth
     * @param screenHeight
     */
    public void arrangeCards(float screenWidth, float screenHeight)
    {
        float cardX = 0;
        float cardWidth = screenWidth/6;
        float distance = 25 - (cardlist.size()*10);
        for (int i = 0; i < cardlist.size(); i++) {
        if (cardlist.get(i)!=null) {
            cardlist.get(i).setCardX(cardX);
            cardlist.get(i).setCardY(screenHeight / 2 + screenHeight / 4);
            cardlist.get(i).setCardWidth(cardWidth);
            cardlist.get(i).setCardHeight(cardWidth * 1.5f);
            cardX = cardX + cardWidth + distance;
        }
        }
    }

    /**
     * This function draws the player's hand on the canvas.
     * @param canvas
     */
    public void draw(Canvas canvas)
    {

        ArrayList<Card> player1 = this.getCardlist();
        for (int i = 0; i < player1.size(); i++)
        {
            if (player1.get(i)!=null) {
                player1.get(i).draw(canvas);
            }
        }
    }

    /**
     * This function checks which card from the hand was picked, and returns it.
     * @param eventX
     * @param eventY
     * @return
     */
    public Card getSelectedCard(float eventX, float eventY) {
        Card selectedCard = null;
        for(Card card: this.cardlist){
             if (card!=null) {
                 if (card.contains(eventX, eventY)) {
                     selectedCard = card;
                 }
             }
        }
        return selectedCard;
    }

    /**
     * This function returns the number of cards with the same color as the color given in the parameter
     * @param color
     * @return
     */
    public int getNumberOfCardsWithSameColor(int color)
    {
        int  counter = 0;
        for (int i = 0; i < this.getCardListLength(); i++) {
            if (this.getCardlist().get(i).getColor()==color)
            {
                counter++;
            }
        }
        return counter;
    }

    /**
     * This function removes a specific card from the player's hand.
     * @param card
     */
    public void removeCard(Card card) {
        this.cardlist.remove(card);
    }
    /**
     * This function adds a card to the player's hand.
     * @param card
     */
    public void addCard(Card card)
    {
        cardlist.add(card);
    }

    private ArrayList<Card> getCardlist()
    {
        return cardlist;
    }
    public int getCardListLength()
    {
        return cardlist.size();

    }
}