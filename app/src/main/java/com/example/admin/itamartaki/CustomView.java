package com.example.admin.itamartaki;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;


//                                                                                                          \\
//          .----------------.  .----------------.  .----------------.  .----------------.                  ||
//         | .--------------. || .--------------. || .--------------. || .--------------. |                 ||
//         | |  _________   | || |      __      | || |  ___  ____   | || |     _____    | |                 ||
//         | | |  _   _  |  | || |     /  \     | || | |_  ||_  _|  | || |    |_   _|   | |                 ||
//         | | |_/ | | \_|  | || |    / /\ \    | || |   | |_/ /    | || |      | |     | |                 ||
//         | |     | |      | || |   / ____ \   | || |   |  __'.    | || |      | |     | |                 ||
//         | |    _| |_     | || | _/ /    \ \_ | || |  _| |  \ \_  | || |     _| |_    | |                 ||
//         | |   |_____|    | || ||____|  |____|| || | |____||____| | || |    |_____|   | |                 ||
//         | |              | || |              | || |              | || |              | |                 ||
//         | '--------------' || '--------------' || '--------------' || '--------------' |                 ||
//         '----------------'  '----------------'  '----------------'  '----------------'                   ||
//        ______ __   __     _____  _____   ___  ___  ___  ___  ______      _   _   ___  ___  ___ _____     ||
//        | ___ \\ \ / /    |_   _||_   _| / _ \ |  \/  | / _ \ | ___ \    | | | | / _ \ |  \/  ||  _  |    ||
//        | |_/ / \ V /       | |    | |  / /_\ \| .  . |/ /_\ \| |_/ /    | |_| |/ /_\ \| .  . || | | |    ||
//        | ___ \  \ /        | |    | |  |  _  || |\/| ||  _  ||    /     |  _  ||  _  || |\/| || | | |    ||
//        | |_/ /  | |       _| |_   | |  | | | || |  | || | | || |\ \     | | | || | | || |  | |\ \_/ /    ||
//        \____/   \_/       \___/   \_/  \_| |_/\_|  |_/\_| |_/\_| \_|    \_| |_/\_| |_/\_|  |_/ \___/     //

/**
 * How to enable debug mode to win faster:
 * go to PlayerHand and change debugMode to true
 * go to CustomView and uncomment the debug mode line for setcard in constructor
 */


public class CustomView extends View {
    private static Context context;
    private final Card setCard;
    private final Card[] pickedColors;
    private final Card[] takiColors;
    private final Koopa koopa;
    private final GameManager gameManager;
    private Bitmap allCardBitmaps[][];
    private final Bitmap[] colorChangeBitmap;
    private Bitmap takiBitmap[];
    private float screenWidth, screenHeight;
    private final PlayerHand playerHand;
    private final PlayerHand otherPlayerHand;
    private boolean isTurnOver;
    private boolean isTurnButtonClickable;
    private int playerWon = 0; //0 == nobody won yet, 1 == player 1 won, 2 == player 2 won
    private final Paint paint;

    public CustomView(Context context) {
        super(context);
        CustomView.context = context;
        colorChangeBitmap = new Bitmap[4];
        takiBitmap = new Bitmap[4];
        allCardBitmaps = initializeCardBitmaps();
        takiBitmap = initializeTakiBitmaps();

        isTurnOver = false;
        boolean isPlayer1Turn = true;
        isTurnButtonClickable = true;

        Bitmap koopaBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.koopa);
        koopa = new Koopa(koopaBitmap, allCardBitmaps);

//        setCard = koopa.drawCard(); //Normal mode for setCard
        setCard = koopa.drawCardDebugMode(); //Debug mode for setCard

        playerHand = new PlayerHand();
        playerHand.initiatePlayerHand(koopa);
        otherPlayerHand = new PlayerHand();
        otherPlayerHand.initiatePlayerHand(koopa);

        pickedColors = new Card[4];
        takiColors = new Card[4];
        initializePickedColors(pickedColors);
        initializeTakiColors(takiColors);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(45);

        gameManager = new GameManager(isPlayer1Turn, koopa, setCard);
        gameManager.unfreezeGame();
        gameManager.setTakiActive(false);
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setCard.draw(canvas);

        if (gameManager.isPlayer1Turn() && !isTurnOver)
        {
            paint.setColor(Color.WHITE);
            canvas.drawText("Turn: Player 1", screenWidth / 6, screenHeight / 5, paint);
        }
        else if (!gameManager.isPlayer1Turn() && !isTurnOver)
        {
            paint.setColor(Color.WHITE);
            canvas.drawText("Turn: Player 2", screenWidth / 6, screenHeight / 5, paint);
        }

        if (gameManager.isPlayer1Turn()) {
            playerHand.draw(canvas);
        } else {
            otherPlayerHand.draw(canvas);
        }

        if (koopa.getNumberOfCardsInKoopa() > 0) {
            koopa.draw(canvas);
        }

        if (gameManager.isChooseStateActive())
        {
            if (setCard.getNumber()==0)
            {
                takiColors[0].draw(canvas);
                takiColors[1].draw(canvas);
                takiColors[2].draw(canvas);
                takiColors[3].draw(canvas);
            }
            else
            {
                pickedColors[0].draw(canvas);
                pickedColors[1].draw(canvas);
                pickedColors[2].draw(canvas);
                pickedColors[3].draw(canvas);
            }
        }
        if (isTurnOver) {
            paint.setColor(Color.BLACK);
            canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
            paint.setColor(Color.WHITE);
            if (gameManager.isPlayer1Turn()) {
                canvas.drawText("It is player 1's turn, click anywhere to continue.", screenWidth / 16, screenHeight / 2, paint);
            } else {
                canvas.drawText("It is player 2's turn, click anywhere to continue.", screenWidth / 16, screenHeight / 2, paint);
            }

        } else {
            isTurnButtonClickable = true;
            paint.setColor(Color.WHITE);
            canvas.drawText("End turn", screenWidth/20, screenHeight / 2 + screenHeight / 20, paint);
        }

        if (playerHand.getCardListLength() == 0) {
            paint.setColor(Color.BLACK);
            canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText("Player 1 wins!", screenWidth / 8, screenHeight / 2, paint);
            playerWon = 1;

        } else if (otherPlayerHand.getCardListLength() == 0) {
            paint.setColor(Color.BLACK);
            canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText("Player 2 wins!", screenWidth / 8, screenHeight / 2, paint);
            playerWon = 2;
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        this.screenWidth = width;
        this.screenHeight = height;

        setSetCardPosition();
        koopa.setKoopaPosition(screenWidth, screenHeight);
        setCard.setCardHeight((screenWidth / 6) * 1.5f);
        setCard.setCardWidth((screenWidth / 6));
        playerHand.arrangeCards(screenWidth, screenHeight);
        otherPlayerHand.arrangeCards(screenWidth, screenHeight);

        setPickedColorsParameters(pickedColors);
        setPickedColorsParameters(takiColors);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (playerWon == 0) {
                isTurnOver = false;
                if (gameManager.wasTurnButtonClicked(eventX, eventY, screenWidth, screenHeight) && isTurnButtonClickable && gameManager.isGameFrozen()) {
                    gameManager.unfreezeGame();
                    gameManager.changeTurn();
                    if (gameManager.isChooseStateActive())
                    {
                        gameManager.setChooseStateActive(false);
                    }
                    if (gameManager.isTakiActive()) {
                        gameManager.setTakiActive(false);
                    }
                    isTurnOver = true;
                    isTurnButtonClickable = false;
                }

                if (gameManager.isPlayer1Turn() && !gameManager.isGameFrozen()) {
                    gameManager.checkKoopaTouchEvent(eventX, eventY, gameManager.isTakiActive(), playerHand, setCard);
                    gameManager.checkPlayerHandTouchEvent(eventX, eventY, playerHand, otherPlayerHand, pickedColors, takiColors);

                } else if (!gameManager.isPlayer1Turn() && !gameManager.isGameFrozen()) {
                    gameManager.checkKoopaTouchEvent(eventX, eventY, gameManager.isTakiActive(), otherPlayerHand, setCard);
                    gameManager.checkPlayerHandTouchEvent(eventX, eventY, otherPlayerHand, playerHand, pickedColors, takiColors);
                }

           } else if (playerWon == 1 || playerWon == 2)
           {
                Intent winnerIntent = new Intent(context, MainActivity.class);
                winnerIntent.putExtra("playerwon", playerWon);
                context.startActivity(winnerIntent);
            }
        }
        playerHand.arrangeCards(screenWidth, screenHeight);
        otherPlayerHand.arrangeCards(screenWidth, screenHeight);
        invalidate();
        return true;
    }

    /**
     * This function sets the position of the pile
     */
    private void setSetCardPosition()
    {
        setCard.setCardX(screenWidth /2 - (screenWidth/16));
        setCard.setCardY(screenHeight/2-((screenWidth/8)*1.5f)*2);
    }


    /**
     * This function creates all of the images for the cards in the game.
     * @return
     */
    private Bitmap[][]  initializeCardBitmaps() {
        allCardBitmaps = new Bitmap[5][12];
        allCardBitmaps[0][0] = BitmapFactory.decodeResource(getResources(),R.drawable.r0);
        allCardBitmaps[0][1] = BitmapFactory.decodeResource(getResources(),R.drawable.r1);
        allCardBitmaps[0][2] = BitmapFactory.decodeResource(getResources(),R.drawable.r2);
        allCardBitmaps[0][3] = BitmapFactory.decodeResource(getResources(),R.drawable.r3);
        allCardBitmaps[0][4] = BitmapFactory.decodeResource(getResources(),R.drawable.r4);
        allCardBitmaps[0][5] = BitmapFactory.decodeResource(getResources(),R.drawable.r5);
        allCardBitmaps[0][6] = BitmapFactory.decodeResource(getResources(),R.drawable.r6);
        allCardBitmaps[0][7] = BitmapFactory.decodeResource(getResources(),R.drawable.r7);
        allCardBitmaps[0][8] = BitmapFactory.decodeResource(getResources(),R.drawable.r8);
        allCardBitmaps[0][9] = BitmapFactory.decodeResource(getResources(),R.drawable.r9);
        allCardBitmaps[0][10] = BitmapFactory.decodeResource(getResources(),R.drawable.r10);
        allCardBitmaps[0][11] = BitmapFactory.decodeResource(getResources(),R.drawable.r11);
        allCardBitmaps[1][0] = BitmapFactory.decodeResource(getResources(),R.drawable.y0);
        allCardBitmaps[1][1] = BitmapFactory.decodeResource(getResources(),R.drawable.y1);
        allCardBitmaps[1][2] = BitmapFactory.decodeResource(getResources(),R.drawable.y2);
        allCardBitmaps[1][3] = BitmapFactory.decodeResource(getResources(),R.drawable.y3);
        allCardBitmaps[1][4] = BitmapFactory.decodeResource(getResources(),R.drawable.y4);
        allCardBitmaps[1][5] = BitmapFactory.decodeResource(getResources(),R.drawable.y5);
        allCardBitmaps[1][6] = BitmapFactory.decodeResource(getResources(),R.drawable.y6);
        allCardBitmaps[1][7] = BitmapFactory.decodeResource(getResources(),R.drawable.y7);
        allCardBitmaps[1][8] = BitmapFactory.decodeResource(getResources(),R.drawable.y8);
        allCardBitmaps[1][9] = BitmapFactory.decodeResource(getResources(),R.drawable.y9);
        allCardBitmaps[1][10] = BitmapFactory.decodeResource(getResources(),R.drawable.y10);
        allCardBitmaps[1][11] = BitmapFactory.decodeResource(getResources(),R.drawable.y11);
        allCardBitmaps[2][0] = BitmapFactory.decodeResource(getResources(),R.drawable.b0);
        allCardBitmaps[2][1] = BitmapFactory.decodeResource(getResources(),R.drawable.b1);
        allCardBitmaps[2][2] = BitmapFactory.decodeResource(getResources(),R.drawable.b2);
        allCardBitmaps[2][3] = BitmapFactory.decodeResource(getResources(),R.drawable.b3);
        allCardBitmaps[2][4] = BitmapFactory.decodeResource(getResources(),R.drawable.b4);
        allCardBitmaps[2][5] = BitmapFactory.decodeResource(getResources(),R.drawable.b5);
        allCardBitmaps[2][6] = BitmapFactory.decodeResource(getResources(),R.drawable.b6);
        allCardBitmaps[2][7] = BitmapFactory.decodeResource(getResources(),R.drawable.b7);
        allCardBitmaps[2][8] = BitmapFactory.decodeResource(getResources(),R.drawable.b8);
        allCardBitmaps[2][9] = BitmapFactory.decodeResource(getResources(),R.drawable.b9);
        allCardBitmaps[2][10] = BitmapFactory.decodeResource(getResources(),R.drawable.b10);
        allCardBitmaps[2][11] = BitmapFactory.decodeResource(getResources(),R.drawable.b11);
        allCardBitmaps[3][0] = BitmapFactory.decodeResource(getResources(),R.drawable.g0);
        allCardBitmaps[3][1] = BitmapFactory.decodeResource(getResources(),R.drawable.g1);
        allCardBitmaps[3][2] = BitmapFactory.decodeResource(getResources(),R.drawable.g2);
        allCardBitmaps[3][3] = BitmapFactory.decodeResource(getResources(),R.drawable.g3);
        allCardBitmaps[3][4] = BitmapFactory.decodeResource(getResources(),R.drawable.g4);
        allCardBitmaps[3][5] = BitmapFactory.decodeResource(getResources(),R.drawable.g5);
        allCardBitmaps[3][6] = BitmapFactory.decodeResource(getResources(),R.drawable.g6);
        allCardBitmaps[3][7] = BitmapFactory.decodeResource(getResources(),R.drawable.g7);
        allCardBitmaps[3][8] = BitmapFactory.decodeResource(getResources(),R.drawable.g8);
        allCardBitmaps[3][9] = BitmapFactory.decodeResource(getResources(),R.drawable.g9);
        allCardBitmaps[3][10] = BitmapFactory.decodeResource(getResources(),R.drawable.g10);
        allCardBitmaps[3][11] = BitmapFactory.decodeResource(getResources(),R.drawable.g11);
        allCardBitmaps[4][0] = BitmapFactory.decodeResource(getResources(),R.drawable.s0);
        allCardBitmaps[4][1] = BitmapFactory.decodeResource(getResources(),R.drawable.s1);

        return allCardBitmaps;
    }

    /**
     * This function creates the images for the cards that appear on screen when a color-change card is placed in the pile.
     * @return
     */
    private Bitmap[] initializeColorChangeBitmaps() {
        colorChangeBitmap[0] = BitmapFactory.decodeResource(getResources(),R.drawable.r13);
        colorChangeBitmap[1] = BitmapFactory.decodeResource(getResources(),R.drawable.y13);
        colorChangeBitmap[2] = BitmapFactory.decodeResource(getResources(),R.drawable.b13);
        colorChangeBitmap[3] = BitmapFactory.decodeResource(getResources(),R.drawable.g13);
        return colorChangeBitmap;
    }

    /**
     * This function creates the images for the cards that appear on screen when a "Super Taki" card is placed in the pile.
     * @return
     */
    private Bitmap[] initializeTakiBitmaps() {
        takiBitmap[0] = BitmapFactory.decodeResource(getResources(),R.drawable.r0);
        takiBitmap[1] = BitmapFactory.decodeResource(getResources(),R.drawable.y0);
        takiBitmap[2] = BitmapFactory.decodeResource(getResources(),R.drawable.b0);
        takiBitmap[3] = BitmapFactory.decodeResource(getResources(),R.drawable.g0);
        return takiBitmap;
    }

    /**
     * This function creates cards that appear on screen when a color-change card is placed in the pile.
     * @return
     */
    private void initializePickedColors(Card[] pickedColors)
    {
        pickedColors[0] = new Card(0,0,0, 20,0,colorChangeBitmap[0]);
        pickedColors[1] = new Card(0,0,0, 20,1,colorChangeBitmap[1]);
        pickedColors[2] = new Card(0,0,0, 20,2,colorChangeBitmap[2]);
        pickedColors[3] = new Card(0,0,0, 20,3,colorChangeBitmap[3]);

    }

    /**
     * This function creates the cards that appear on screen when a "Super Taki" card is placed in the pile.
     * @return
     */
    private void initializeTakiColors(Card[] takiColors)
    {
        takiColors[0] = new Card(0,0,0, 30,0,takiBitmap[0]);
        takiColors[1] = new Card(0,0,0, 30,1,takiBitmap[1]);
        takiColors[2] = new Card(0,0,0, 30,2,takiBitmap[2]);
        takiColors[3] = new Card(0,0,0, 30,3,takiBitmap[3]);

    }

    /**
     * This function sets the positions for the cards that appear on screen when a "Super Taki" or color-change card is placed in the pile.
     * @return
     */
    private void setPickedColorsParameters(Card[] pickedColors)
    {
        pickedColors[0].setCardX(screenWidth/6);
        pickedColors[1].setCardX(screenWidth/6);
        pickedColors[2].setCardX(screenWidth/2+screenWidth/3);
        pickedColors[3].setCardX(screenWidth/2+screenWidth/3);

        pickedColors[0].setCardY(screenHeight/6);
        pickedColors[1].setCardY(screenHeight/2+screenHeight/16);
        pickedColors[2].setCardY(screenHeight/6);
        pickedColors[3].setCardY(screenHeight/2+screenHeight/16);

        for (Card pickedColor : pickedColors) {
            pickedColor.setCardWidth(screenWidth / 6);
            pickedColor.setCardHeight(screenWidth / 6 * 1.5f);
        }
    }
}