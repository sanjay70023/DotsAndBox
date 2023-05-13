package com.tiptop.dotsandboxes.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.tiptop.dotsandboxes.R;
import com.tiptop.dotsandboxes.event_bus.RxBus;
import com.tiptop.dotsandboxes.event_bus.events.EmitSoundEvent;
import com.tiptop.dotsandboxes.event_bus.events.PlayerMoveEvent;
import com.tiptop.dotsandboxes.game.controllers.Game;
import com.tiptop.dotsandboxes.game.models.Board;
import com.tiptop.dotsandboxes.game.models.Edge;


/**
 * Class responsible for displaying and interacting with the board
 */
public class BoardView extends View {

    private Game game;              // The game which is being played
    private int horizontalOffset;   // Offset at the left and right to display the dots grid
    private int verticalOffset;     // Offset at the top and bottom to display the dots grid
    private int[][] boxesAlpha;     // array containing the alpha value of each box

    private int boxSide;       // default size for the box
    private int snapLength;    // default distance for a touch to "snap" to a line
    private int dotRadius;      // the radius of the dots

    private Paint linePaint;
    private Paint lineTempPaint;
    private Paint dotPaint;
    private Paint boxPaint;

    // the bounds of the touch area that can beused for placing lines
    // it is bound by -snapLength and touchWidth for the orizontal dimension
    // and by -snapLength and touchHeight for the vertical dimension
    private float touchWidth;
    private float touchHeight;

    // colors of the mService boxes
    private Drawable colorPlayer1;
    private Drawable colorPlayer2;

    // temp coordinates for calculations
    private float x1temp, y1temp, x2temp, y2temp;
    private boolean drawTemp = false;
    private boolean touchable = false;

    private boolean shouldMakeASound;
    private boolean isTurnMe;

    public BoardView(Context context) {
        super(context);
        init();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialize all paints required to draw on a {@link Canvas}
     */
    private void init() {

        // setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        // setBackgroundColor(Color.TRANSPARENT);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(10f);
        linePaint.setColor(ContextCompat.getColor(getContext(), R.color.icon_color_white));

       /* lineTempPaint = new Paint();
        lineTempPaint.setAntiAlias(true);
        lineTempPaint.setStrokeWidth(12f);
        lineTempPaint.setColor(ContextCompat.getColor(getContext(), R.color.icon_color_white));*/

        dotPaint = new Paint();
        dotPaint.setColor(ContextCompat.getColor(getContext(), R.color.icon_color_white));
        dotPaint.setAntiAlias(true);


        boxPaint = new Paint();
        boxPaint.setAntiAlias(true);

        boxSide = getResources().getDimensionPixelSize(R.dimen.default_box_size);
        snapLength = getResources().getDimensionPixelSize(R.dimen.default_snap_length);
        dotRadius = getResources().getDimensionPixelSize(R.dimen.dot_size);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        shouldMakeASound = sharedPref.getBoolean(getContext().getString(R.string.pref_key_sound), true);
    }

    /**
     * Draws everything in a specific order
     * 1. boxes
     * 2. lines
     * 3. temp lines
     * 4. dots
     *
     * @param canvas the canvas to draw on
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable selectedDrawable;
        if (game == null)
            return;

        Board board = game.getBoard();

        boolean revalidate = false;
        int x1, y1, x2, y2;
        for (int i = 0; i < board.getRows(); i++)
            for (int j = 0; j < board.getColumns(); j++) {
                Board.Box box = board.getBoxAt(i, j);

                // draw the box
                if (!isTurnMe) {
                    if (box.player == Game.Player.PLAYER2)
                        selectedDrawable = colorPlayer1;
                    else
                        selectedDrawable = colorPlayer2;
                } else {
                    if (box.player == Game.Player.PLAYER2)
                        selectedDrawable = colorPlayer2;
                    else
                        selectedDrawable = colorPlayer1;
                }
                if (box.left && box.right && box.bottom && box.top) {

                    // update the box alpha
                    if (boxesAlpha[i][j] < 255) {
                        revalidate = true;
                        boxesAlpha[i][j] += 5;
                    }

                    boxPaint.setAlpha(boxesAlpha[i][j]);

                    x1 = horizontalOffset + j * boxSide;
                    y1 = verticalOffset + i * boxSide;
                    x2 = horizontalOffset + (j + 1) * boxSide;
                    y2 = verticalOffset + (i + 1) * boxSide;

                    /*Drawable d = getResources().getDrawable(R.drawable.fillblue, null);*/
                    selectedDrawable.setBounds(x1, y1, x2, y2);
                    selectedDrawable.draw(canvas);
                  /*  Bitmap bmp = drawableToBitmap(getResources().getDrawable(R.drawable.vs_icon));
                    canvas.drawBitmap(bmp, x1, y1, null);*/
                    // canvas.drawRect(x1, y1, x2, y2, boxPaint);
                }

                if (box.top) {
                    x1 = horizontalOffset + j * boxSide;
                    x2 = horizontalOffset + (j + 1) * boxSide;
                    y1 = verticalOffset + i * boxSide;
                    y2 = verticalOffset + i * boxSide;

                    canvas.drawLine(x1, y1, x2, y2, linePaint);
                }

                if (box.left) {
                    x1 = horizontalOffset + j * boxSide;
                    x2 = horizontalOffset + j * boxSide;
                    y1 = verticalOffset + i * boxSide;
                    y2 = verticalOffset + (i + 1) * boxSide;

                    canvas.drawLine(x1, y1, x2, y2, linePaint);
                }

                if (box.right && j == board.getColumns() - 1) {
                    x1 = horizontalOffset + (j + 1) * boxSide;
                    x2 = horizontalOffset + (j + 1) * boxSide;
                    y1 = verticalOffset + i * boxSide;
                    y2 = verticalOffset + (i + 1) * boxSide;

                    canvas.drawLine(x1, y1, x2, y2, linePaint);
                }

                if (box.bottom && i == board.getRows() - 1) {
                    x1 = horizontalOffset + j * boxSide;
                    x2 = horizontalOffset + (j + 1) * boxSide;
                    y1 = verticalOffset + (i + 1) * boxSide;
                    y2 = verticalOffset + (i + 1) * boxSide;

                    canvas.drawLine(x1, y1, x2, y2, linePaint);
                }
            }

        // draw the temp line
       /* if (drawTemp && (x1temp > 0 && x1temp <= board.getColumns() &&
                        x2temp > 0 && x2temp <= board.getColumns() &&
                        y1temp > 0 && y1temp <= board.getRows() &&
                        y2temp > 0 && y2temp <= board.getRows())) {

            canvas.drawLine(x1temp*boxSide + horizontalOffset,
                            y1temp*boxSide + verticalOffset,
                            x2temp*boxSide + horizontalOffset,
                            y2temp*boxSide + verticalOffset, lineTempPaint);
        }*/

        // draw the dots
        for (int i = 0; i <= board.getRows(); i++)
            for (int j = 0; j <= board.getColumns(); j++) {
                x1 = horizontalOffset + j * boxSide;
                y1 = verticalOffset + i * boxSide;

                canvas.drawCircle(x1, y1, dotRadius, dotPaint);
            }

        if (revalidate)
            this.postInvalidate();
    }
  /*  public Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap=((BitmapDrawable)drawable).getBitmap();
            return bitmap;
        }

        Bitmap bitmap = Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888);
        return bitmap;
    }*/

    /**
     * Sets the game object and calculates the width and height of the touch area
     *
     * @param game Game object representing the developing game. In its nature
     *             it is a state machine.
     */
    public void setGame(Game game) {
        this.game = game;
        this.boxesAlpha = new int[game.getBoard().getRows()][game.getBoard().getColumns()];
        requestLayout();
    }

    /**
     * Overriden in order to maintain a square aspect ratio of the view.
     * Also used to calculate board specific measures
     *
     * @param widthMeasureSpec  dimension specifying the width
     * @param heightMeasureSpec dimension specifying the height
     */
    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 300;
        int desiredHeight = 300;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        // Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.max(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        // Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.max(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        // MUST CALL THIS!
        setMeasuredDimension(width, height);

        int size;
        if (width < height) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            size = width - getPaddingLeft() - getPaddingRight();
        } else {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
            size = height - getPaddingTop() - getPaddingBottom();
        }

        if (game == null)
            return;

        if (game.getBoard().getColumns() < game.getBoard().getRows()) {
            boxSide = size / game.getBoard().getRows();
        } else {
            boxSide = size / game.getBoard().getColumns();
        }

        this.horizontalOffset = (width - size) / 2;
        this.verticalOffset = (height - size) / 2;

        this.touchWidth = game.getBoard().getColumns() * boxSide + snapLength;
        this.touchHeight = game.getBoard().getRows() * boxSide + snapLength;
    }

    /**
     * Using the native {@link View} method to service the touch events
     *
     * @param motionEvent the motion event to be processed
     * @return returns whether the MotionEvent was consumed
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // block user interaction
        if (!touchable)
            return false;

        // don't proceed without a game object present
        if (game == null)
            return false;

        Board board = game.getBoard();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // send an event to make a sound

            case MotionEvent.ACTION_MOVE: {
                // calculate where on the view did the motion event occur
                float touchX = motionEvent.getX() - horizontalOffset;
                float touchY = motionEvent.getY() - verticalOffset;

                // check if the touch event was within the touch area
                if (touchX < -snapLength ||
                        touchX > touchWidth ||
                        touchY < -snapLength ||
                        touchY > touchHeight) {
                    return false;
                }

                // calculate on which box did the touch happen
                float rowY = Math.abs(touchY) / boxSide;
                float columnX = Math.abs(touchX) / boxSide;

                double deltaXLeft, deltaYDown, deltaXRight, deltaYUp, deltaX, deltaY;

                // get the part after the decimal point
                deltaXLeft = columnX - Math.floor(columnX);
                deltaXRight = Math.ceil(columnX) - columnX;
                deltaYDown = rowY - Math.floor(rowY);
                deltaYUp = Math.ceil(rowY) - rowY;

                // determine whether the touch event is closer to a vertical or a horizontal line
                deltaX = deltaXLeft > deltaXRight ? deltaXRight : deltaXLeft;
                deltaY = deltaYDown > deltaYUp ? deltaYUp : deltaYDown;

                // horizontal line
                if (deltaX < deltaY) {
                    if (deltaX < snapLength) {
                        x1temp = (float) Math.floor(columnX);
                        x2temp = x1temp;
                        y1temp = (float) Math.floor(rowY);
                        y2temp = y1temp + 1;

                        if (deltaX == deltaXRight) {
                            x1temp += 1;
                            x2temp += 1;
                        }
                        drawTemp = true;
                    } else {
                        x1temp = 0;
                        x2temp = 0;
                        y1temp = 0;
                        y2temp = 0;
                        drawTemp = false;
                    }
                }
                // vertical line
                else if (deltaX >= deltaY) {
                    if (deltaY < snapLength) {
                        x1temp = (float) Math.floor(columnX);
                        x2temp = x1temp + 1;
                        y1temp = (float) Math.floor(rowY);
                        y2temp = y1temp;

                        if (deltaY == deltaYUp) {
                            y1temp += 1;
                            y2temp += 1;
                        }
                        drawTemp = true;
                    } else {
                        x1temp = 0;
                        x2temp = 0;
                        y1temp = 0;
                        y2temp = 0;
                        drawTemp = false;
                    }
                }

                invalidate();
                return true;
            }

            case MotionEvent.ACTION_UP:
                int numberDotStart = ((int) y1temp) * (board.getColumns() + 1) + (int) x1temp;
                int numberDotEnd = ((int) y2temp) * (board.getColumns() + 1) + (int) x2temp;

                int totalDots = (board.getColumns() + 1) * (board.getColumns() + 1);

                for (int i = (board.getColumns() + 1); i < totalDots; i += (board.getColumns() + 1)) {
                    if (numberDotEnd == i) {
                        if (numberDotStart == i - 1) {
                            return false;
                        }
                    }
                }

                if (numberDotEnd >= totalDots) return false;
                // send a move event only if the edge is not present in the game tree
                if (!game.getGameTree().hasEdge(numberDotStart, numberDotEnd)) {
                    RxBus.getInstance().send(new PlayerMoveEvent(new Edge(numberDotStart, numberDotEnd)));

                    // produce a sound if a line is about to be drawn
                    if (shouldMakeASound) {
                        RxBus.getInstance().send(new EmitSoundEvent());
                    }
                }

                invalidate();

            case MotionEvent.ACTION_CANCEL: {
                drawTemp = false;
                x1temp = 0;
                x2temp = 0;
                y1temp = 0;
                y2temp = 0;
                invalidate();
                return true;
            }
        }
        return false;
    }

    public boolean isTurnMe() {
        return isTurnMe;
    }

    public void setTurnMe(boolean turnMe) {
        isTurnMe = turnMe;
        if (isTurnMe) {

            colorPlayer1 = getResources().getDrawable(R.drawable.fillblue, null);
            colorPlayer2 = getResources().getDrawable(R.drawable.fillgreen, null);
        } else {
            colorPlayer1 = getResources().getDrawable(R.drawable.fillgreen, null);
            colorPlayer2 = getResources().getDrawable(R.drawable.fillblue, null);
        }
    }

    public void enableInteraction() {
        touchable = true;
    }

    public void disableInteraction() {
        touchable = false;
    }
}
