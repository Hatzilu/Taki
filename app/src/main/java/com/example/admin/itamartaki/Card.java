package com.example.admin.itamartaki;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * The base class for all the cards in the game, each card has 2 important variables:
 * Number - The number of the card. also used to identify special cards in the GameManager class.
 * Color - The color of the card. also used to identifiy special non color-specific cards in the GameManager class.
 */
public class Card {
    private int number;
    //colors: 0 = red, 1 = yellow, 2 = blue, 3 = green, 4 = WithoutColor
    private int color;
    private float cardX, cardY, cardWidth, cardHeight;
    private Bitmap bitmap;
    public Card(float cardX, float cardY, float screenWidth, int number, int color, Bitmap cardBitmap)
    {
        this.number = number;
        this.color = color;
        this.cardX = cardX;
        this.cardY = cardY;
        this.cardWidth = screenWidth/6;
        this.cardHeight = screenWidth/6 * 1.5f;
        this.bitmap = cardBitmap;
    }

    /**
     * This function draws the card on the canvas.
     * @param canvas
     */
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(getBitmap(),(int)cardWidth,(int)cardHeight,true);
        canvas.drawBitmap(resizedBitmap,this.cardX,this.cardY,paint);
    }

    /**
     * This function checks if the X and Y coordinates given in the paramater are inside the card.
     * @param eventX
     * @param eventY
     * @return
     */
    public boolean contains(float eventX, float eventY)
    {
        return eventX > this.cardX &&
                eventY > this.cardY &&
                eventY < this.cardY + cardHeight &&
                eventX < this.cardX + cardWidth;
    }


    private float getCardX() {
        return cardX;
    }

    public void setCardX(float cardX) {
        this.cardX = cardX;
    }

    private float getCardY() {
        return cardY;
    }

    public void setCardY(float cardY) {
        this.cardY = cardY;
    }

    public float getCardWidth() {
        return cardWidth;
    }


    public void setCardWidth(float cardWidth) {
        this.cardWidth = cardWidth;
    }

    private float getCardHeight() {
        return cardHeight;
    }

    public void setCardHeight(float cardHeight) {
        this.cardHeight = cardHeight;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString()
    {
        return "Card{" +
                "num=" + this.number +
                ", color='" + this.color + '\'' +
                ", Height='" + this.getCardHeight() + '\'' +
                ", Width='" + this.getCardWidth() + '\'' +
                ", X='" + this.getCardX() + '\'' +
                ", Y='" + this.getCardY()+ '\'' +
                '}';
    }
}