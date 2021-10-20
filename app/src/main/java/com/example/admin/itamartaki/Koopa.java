package com.example.admin.itamartaki;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

/**
 * The koopa contains all the cards in the game. The koopa is used in the game to get new cards.
 * At the beginning of the game, 17 cards are drawn from the koopa: 8 for player 1, 8 for player 2, and 1 card to be placed in the pile.
 * when the koopa is pressed in the game, the player will randomly recieve
 */
public class Koopa {
    private ArrayList<Card> cardStack;
    private float koopaX, koopaY, koopaWidth, koopaHeight;
    private Random random;
    private final Bitmap bitmap;
    private final Bitmap[][] allCardBitmaps;

    public Koopa(Bitmap bitmap, Bitmap[][] cardBitmaps) {
        cardStack = new ArrayList<>();
        random = new Random();
        this.bitmap = bitmap;
        this.allCardBitmaps = cardBitmaps;
        for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
            for (int numberIndex = 0; numberIndex < 12; numberIndex++) {
                cardStack.add(new Card(0, 0, 0,
                        numberIndex, colorIndex, cardBitmaps[colorIndex][numberIndex]));
            }
        }
        for (int i = 0; i < 2; i++) //special non color cards
        {
            cardStack.add(new Card(0, 0, 0,
                    i, 4, cardBitmaps[4][i]));
        }
    }

    /**
     * This function sets the position of the koopa on the screen.
     *
     * @param screenWidth
     * @param screenHeight
     */
    public void setKoopaPosition(float screenWidth, float screenHeight) {
        koopaX = screenWidth / 2 + screenWidth / 8;
        koopaY = screenHeight / 4;
        koopaWidth = screenWidth / 6;
        koopaHeight = koopaWidth * 1.5f;
    }

    /**
     * This function creates all the cards. This function is called when the koopa runs out of cards.
     *
     * @param cardBitmaps
     */
    private void createKoopa(Bitmap[][] cardBitmaps) {
        cardStack = new ArrayList<>();
        random = new Random();
        for (int colorIndex = 0; colorIndex < 4; colorIndex++) //creation of all colored cards
        {
            for (int numberIndex = 0; numberIndex < 12; numberIndex++) {
                cardStack.add(new Card(0, 0, 0,
                        numberIndex, colorIndex, cardBitmaps[colorIndex][numberIndex]));
            }
        }
        for (int i = 0; i < 2; i++) //creation of special non color-specific cards
        {
            cardStack.add(new Card(0, 0, 0,
                    i, 4, cardBitmaps[4][i]));
        }
    }

    /**
     * This function returns a random card from the koopa.
     * If the koopa is empty, it will recreate all the cards.
     *
     * @return
     */
    public Card drawCard() {

        if (cardStack.size() > 2) {
            return cardStack.remove(random.nextInt(cardStack.size() - 1));
        } else {
            createKoopa(allCardBitmaps);
        }
        return cardStack.remove(0);
    }

    /**
     * This function draws the koopa on the canvas.
     *
     * @param canvas
     */
    public void draw(Canvas canvas) {
        Bitmap koopabitmap = Bitmap.createScaledBitmap(bitmap, (int) getKoopaWidth(), (int) getKoopaHeight(), true);
        if (getCardStack() != null && getNumberOfCardsInKoopa() > 0) {
            Paint paint = new Paint();
            canvas.drawBitmap(koopabitmap, getKoopaX(), getKoopaY(), paint);
        }
    }

    /**
     * This function checks if the X and Y coordinates given in the paramater are inside the koopa.
     *
     * @param eventX
     * @param eventY
     * @return
     */
    public boolean contains(float eventX, float eventY) {
        return eventX > this.koopaX && eventY > this.koopaY && eventY < this.koopaY + this.koopaHeight && eventX < this.koopaX + this.koopaWidth;
    }

    /**
     * This function returns the number of cards inside the koopa.
     *
     * @return
     */
    public int getNumberOfCardsInKoopa() {
        return cardStack.size();
    }

    public ArrayList<Card> getCardStack() {
        return cardStack;
    }

    private float getKoopaX() {
        return koopaX;
    }

    private float getKoopaY() {
        return koopaY;
    }

    private float getKoopaWidth() {
        return koopaWidth;
    }

    private float getKoopaHeight() {
        return koopaHeight;
    }

    public Card drawCardDebugMode() {
        return cardStack.remove(0);
    }
}


