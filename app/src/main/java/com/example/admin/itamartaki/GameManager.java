package com.example.admin.itamartaki;

import android.util.Log;

/**
 * Manage the game with Taki game rules.
 */
public class GameManager {
     private final Koopa koopa;
     private final Card setCard;
     private boolean isPlayer1Turn, isTakiActive, isPlusActive, isStopActive, isGameFrozen;
     private boolean isColorChangeActive, isChooseStateActive;

    public GameManager(Boolean isPlayer1Turn, Koopa koopa, Card setCard)
     {
        this.koopa = koopa;
        this.setCard = setCard;
        this.isPlayer1Turn = isPlayer1Turn;
     }

    /**
     * Check if the move made was valid
     * @param card
     * @param setCard
     * @return
     */
    private boolean isMoveValid(Card card, Card setCard)
    {
        return (setCard.getNumber() == card.getNumber() || setCard.getColor() == card.getColor()) ||
                card.getColor() == 4 || setCard.getColor() == 4
                || (isTakiActive && card.getNumber()==setCard.getNumber()&& card.getColor()==setCard.getColor());
    }

    /**
     * this function will handle what happens if the koopa is pressed during the game. it will add a card to the hand of the player that pressed it if the game allows it.
     * @param eventX
     * @param eventY
     * @param isTakiActive
     * @param playerHand
     * @param setCard
     */
    public void checkKoopaTouchEvent(float eventX, float eventY, boolean isTakiActive, PlayerHand playerHand, Card setCard)
    {
        if (koopa.contains(eventX, eventY) && koopa.getCardStack().size()>0 && !(isTakiActive))
        {
            playerHand.addCard(koopa.drawCard());
            this.freezeGame();
        }
        if (isTakiActive && playerHand.getNumberOfCardsWithSameColor(setCard.getColor())==0)
        {
            this.freezeGame();
        }
    }

    /**
     * This function handles what happens when a card from the player's hand is pressed. if the card can be added to the pile, it will, otherwise, it won't.
     * If the card is a special card, it will go into the "handleSpecialCards" which takes care of the events that happen in the game when a special card is added.
     * @param eventX
     * @param eventY
     * @param playerHand
     * @param otherPlayerHand
     * @param colors
     * @param takicolors
     */
    public void checkPlayerHandTouchEvent(float eventX, float eventY, PlayerHand playerHand,PlayerHand otherPlayerHand, Card[] colors, Card[] takicolors)
    {
        Card card;
        if(isChooseStateActive)
        {
            if (setCard.getNumber()==0)
            {
                card = getColorPicking(eventX, eventY, takicolors);
            }
            else
            {
                card = getColorPicking(eventX, eventY, colors);
            }
        }
        else
        {
            card = playerHand.getSelectedCard(eventX, eventY);
        }
        if (card == null)
        {
            Log.d("selectedCard","Card is null!");
        }
        else if (this.isMoveValid(card, setCard) || isChooseStateActive)
        {
            setCard.setNumber(card.getNumber());
            setCard.setColor(card.getColor());
            setCard.setBitmap(card.getBitmap());

            playerHand.removeCard(card);

            this.handleSpecialCards(card,eventX,eventY,otherPlayerHand,setCard,colors,takicolors);

            if (isTakiActive && playerHand.getNumberOfCardsWithSameColor(setCard.getColor())==0 || card.getColor()!=4 && (!isTakiActive && !isPlusActive && !isStopActive && !isChooseStateActive))
            {
                freezeGame();
            }
        }
    }

    /**
     * this function checks if the "end turn" button was pressed.
     * @param eventX
     * @param eventY
     * @param screenWidth
     * @param screenHeight
     * @return
     */
    public boolean wasTurnButtonClicked(float eventX, float eventY, float screenWidth, float screenHeight)
    {
        return eventY > (screenHeight / 2) && eventY < (screenHeight / 2 + screenHeight / 16) && eventX > 0 && eventX < (screenWidth/6 + screenWidth/30);
    }

    /**
     * This function handles special cards in the game. if a special card is added, it will be checked by this function which will check which card is it and according to that
     * it will change the game.
     * Examples:
     * If a card with the number 2 is added, the game will add 2 cards to the opposing player's hand.
     * If a "Taki" is placed in the pile, the player can add all of the cards of the same color in his hand in the same turn.
     * If a "Plus" card is placed in the pile, the player can add one more card of the same color to the pile or take one card from the koopa before ending his turn.
     * @param card
     * @param eventX
     * @param eventY
     * @param otherPlayerHand
     * @param setCard
     * @param colors
     * @param takicolors
     */
    private void handleSpecialCards(Card card, float eventX, float eventY,
                                    PlayerHand otherPlayerHand, Card setCard, Card[] colors, Card[] takicolors)
    {
        isPlusActive=false;
        isStopActive=false;
        isChooseStateActive=false;
        switch(card.getNumber())
        {
            case 10:
                isPlusActive = true;
                break;
            case 11:
                isStopActive = true;
                break;
            case 2:
                if(card.getColor() < 4)
                {
                    takeTwo(otherPlayerHand);
                }
                break;
            case 1:
                if (card.getColor() == 4)
                {
                    isChooseStateActive = true;
                }
                break;
            case 0:
                if (card.getColor() == 4)
                {
                    isChooseStateActive = true;
                }
                else
                {
                    setTakiActive(true);
                }
                break;

            case 20:
                setCard.setColor(checkColorPicking(eventX,eventY,colors));
                isChooseStateActive=false;
                break;
            case 30:
                setCard.setColor(checkColorPicking(eventX,eventY,takicolors));
                setTakiActive(true);
                isChooseStateActive=false;
                break;
        }
    }

    /**
     * this function makes the other player take 2 cards.
     * this function is called when a card with a number of 2 is added to the pile because in taki putting a card with the number 2 forces the other player to take 2 cards.
     * @param otherPlayerHand
     */
    private void takeTwo (PlayerHand otherPlayerHand)
    {
        if (koopa.getNumberOfCardsInKoopa() > 1)
        {
            otherPlayerHand.addCard(koopa.drawCard());
            otherPlayerHand.addCard(koopa.drawCard());
        }
    }

    /**
     * returns the color of the card that was selected, this function is used in the color selection cards that show up when a color changing card is placed in the pile.
     * @param eventX
     * @param eventY
     * @param card
     * @return
     */
    private int checkColorPicking(float eventX, float eventY, Card[] card)
    {
        int color = 0;
        for (Card aCard : card) {
            if (aCard.contains(eventX, eventY)) {
                color = aCard.getColor();
            }
        }
        setColorChangeActive(false);
        return color;
    }
    /**
     * returns the card that was selected, this function is used in the Taki selection cards that show up when a "Super Taki" card is placed in the pile.
     * @param eventX
     * @param eventY
     * @param card
     * @return
     */
    private Card getColorPicking(float eventX, float eventY, Card[] card)
    {
        for (Card aCard : card) {
            if (aCard.contains(eventX, eventY)) {
                return aCard;
            }
        }

        return null;
    }

    public boolean isTakiActive() {
        return isTakiActive;
    }

    public void setTakiActive(boolean takiActive) {
        isTakiActive = takiActive;
    }

    public boolean isGameFrozen() {
        return this.isGameFrozen;
    }

    private void freezeGame()
    {
        this.isGameFrozen = true;
    }

    public void unfreezeGame()
    {
        this.isGameFrozen = false;
    }


    public boolean isPlayer1Turn() {
        return isPlayer1Turn;
    }

    public boolean isPlusActive() {
        return isPlusActive;
    }

    public boolean isStopActive() {
        return isStopActive;
    }

    public void setChooseStateActive(boolean chooseStateActive) {
        this.isChooseStateActive = chooseStateActive;
    }


    public void changeTurn()
    {
        isPlayer1Turn = !isPlayer1Turn;
    }



    public boolean isChooseStateActive(){
        return isChooseStateActive;
    }

    public boolean isColorChangeActive() {
        return isColorChangeActive;
    }

    private void setColorChangeActive(boolean colorChangeActive) {
        this.isColorChangeActive = colorChangeActive;
    }


    private void setPlusActive(boolean plusActive) {
        isPlusActive = plusActive;
    }

    private void setStopActive(boolean stopActive) {
        isStopActive = stopActive;
    }


}
