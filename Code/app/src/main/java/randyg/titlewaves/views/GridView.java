package randyg.titlewaves.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Scroller;

import androidx.core.view.ViewCompat;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;

import randyg.titlewaves.music.SongBlueprint;

public class GridView extends View
{
    // Preferences
    public boolean drawGrid = true;
    public boolean textGlow = true;
    public int backgroundTint;
    public int cursorTint;
    public int gridTint;
    public int textColor;
    public int overrideColor = 0xffD9363D;
    public int cellSizeDpi = 24;
    public int textSizeDpi = 18;
    public int gridBorderSizePx = 1;

    int N_ROWS = 30;
    int N_COLS = 20;

    private EventListener eventListener;

    private Paint backgroundFillPaint;
    private Paint debugTextPaint;
    private Paint gridLetterPaint;
    private Paint tilePaint;
    private Paint gridPaint;

    private Scroller scroller;
    private GestureDetector gestureDetector;
    private char charBuf[] = new char[1];

    private float cellWidth = 0;
    private float cellHeight = 0;

    private int maxScrollX = 0;
    private int maxScrollY = 0;

    private GridViewModel viewModel;

    public ArrayList<int[]> highlightedCells = new ArrayList<>();

    public GridView(Context context)
    {
        super(context);
    }

    public GridView(Context context, AttributeSet attr)
    {
        super(context, attr);
    }

    public void setEventListener_(EventListener listener)
    {
        eventListener = listener;
    }

    public void recomputeScrollBounds()
    {
        maxScrollY = viewModel.lines.size() + 10;

        maxScrollX = 0;

        for (StringBuffer line : viewModel.lines)
        {
            if (maxScrollX < line.length())
                maxScrollX = line.length();
        }

        maxScrollX += 4;
    }

    public void handleTextChanged(int ch)
    {
        if (eventListener != null)
            eventListener.onTextChanged(ch);

        recomputeScrollBounds();
    }

    public int[] getRowCol()
    {
        return new int[] { viewModel.currentCellY, viewModel.currentCellX };
    }

    public void addHighlight(int x, int y, int color)
    {
        highlightedCells.add(new int[] { x, y, color });

        panIntoView(x, y);

        invalidate();
    }

    public int getHighlight(int x, int y)
    {
        for (int[] data : highlightedCells)
        {
            if (x == data[0] && y == data[1])
                return data[2];
        }

        return 0;
    }

    public void clearHighlights()
    {
        highlightedCells.clear();

        invalidate();
    }

    public void clear()
    {
        viewModel.clear();

        handleTextChanged(0);

        setScrollX(0);
        setScrollY(0);

        invalidate();
    }

    public void clearOverrides()
    {
        viewModel.songBlueprint.getCellOverride().clear();
        viewModel.songBlueprint.getHorizontalOverride().clear();
    }

    public void setText(String text)
    {
        clear();
        
        viewModel.setText(text);

        handleTextChanged(0);

        invalidate();
    }

    public String getText()
    {
        return viewModel.toString();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        N_COLS = (int)(w / cellWidth);
        N_ROWS = (int)(h / cellHeight);

        invalidate();
    }

    public int[] cellWorldPos(int x, int y)
    {
        return new int[] {
            (int)(x * cellWidth),
            (int)(y * cellHeight)
        };
    }

    public int[] screenToCellPos(int x, int y)
    {
        int cellX = (int) ((getScrollX() + x) / cellWidth);
        int cellY = (int) ((getScrollY() + y) / cellHeight);
        return new int[] {cellX, cellY};
    }

    public void panIntoView(int x, int y)
    {
        int[] cellPos = cellWorldPos(x, y);

        int cw = (int)cellWidth;
        int ch = (int)cellHeight;

        int c_x0 = cellPos[0];
        int c_y0 = cellPos[1];
        int c_x1 = c_x0 + cw;
        int c_y1 = c_y0 + ch;

        int vp_x0 = getScrollX();
        int vp_y0 = getScrollY();
        int vp_x1 = vp_x0 + getWidth();
        int vp_y1 = vp_y0 + getHeight();

        int scrollX = vp_x0;
        int scrollY = vp_y0;

        int dx = 0;
        int dy = 0;

        if (c_x0 < vp_x0) {
            dx = c_x0 - vp_x0;
        } else if (c_x1 > vp_x1) {
            dx = c_x1 - vp_x1;
        }

        if (c_y0 < vp_y0) {
            dy = c_y0 - vp_y0;
        } else if (c_y1 > vp_y1) {
            dy = c_y1 - vp_y1;
        }

        if (dx != 0 || dy != 0)
        {
            scroller.forceFinished(true);
            scroller.startScroll(scrollX, scrollY, dx, dy, 200);
        }
    }

    public void rereadPreferences()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        drawGrid = prefs.getBoolean("show_grid", true);
        textGlow = prefs.getBoolean("text_glow", true);
        gridTint = prefs.getInt("grid_color", 0xFF151515); // 0x38EAECED
        textColor = prefs.getInt("text_color", 0xFFEBE5E4);
        backgroundTint = prefs.getInt("tile_color", 0x00222222) & 0x00FFFFFF;
        cursorTint = prefs.getInt("cursor_color", 0xFF0CA597) | 0xFF000000;

        String dpi = prefs.getString("tile_size_dpi", "24");
        cellSizeDpi = Integer.valueOf(dpi);

        //------------------------------------------------------------------------

        cellWidth = getPixelsFromDPs(cellSizeDpi);
        cellHeight = cellWidth;

        backgroundFillPaint = new Paint();
        backgroundFillPaint.setColor(Color.WHITE);
        backgroundFillPaint.setStyle(Paint.Style.FILL);

        tilePaint = new Paint();

        gridPaint = new Paint();
        gridPaint.setColor(gridTint);
        gridPaint.setStrokeWidth(gridBorderSizePx);
        //gridPaint.setAntiAlias(true);

        gridLetterPaint = new Paint();
        gridLetterPaint.setColor(textColor);
        if (textGlow) {
            gridLetterPaint.setShadowLayer(16, 0, 0, textColor);
        }
        //setLayerType(LAYER_TYPE_SOFTWARE, gridLetterPaint);
        gridLetterPaint.setTextAlign(Paint.Align.CENTER);
        gridLetterPaint.setTextSize(getPixelsFromDPs(textSizeDpi));
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),"fonts/SpaceMono-Regular.ttf");
        gridLetterPaint.setTypeface(font);
        gridLetterPaint.setAntiAlias(true);

        debugTextPaint = new Paint();
        debugTextPaint.setColor(Color.RED);;
        debugTextPaint.setTextSize(24);

        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    public void handleActionUp(MotionEvent e)
    {
        GridViewModel model = GridView.this.viewModel;

        if (!scroller.isFinished()) {
            scroller.abortAnimation();
            //scroller.forceFinished(true);
        } else {
            int x = (int) e.getX();
            int y = (int) e.getY();

            int[] cursorXY = screenToCellPos(x, y);
            int cursorX = cursorXY[0];
            int cursorY = cursorXY[1];

            viewModel.setCursor(cursorX, cursorY);

            if (eventListener != null)
            {
                int codePoint = model.charAt(cursorX, cursorY);
                eventListener.onTileTouched(cursorX, cursorY, codePoint);
            }
        }

        // Aborts any active scroll animations and invalidates
        scroller.forceFinished(true);
    }

    public void init(Context context, GridViewModel viewModel)
    {
        this.viewModel = viewModel;

        rereadPreferences();

        setFocusable(true);
        setFocusableInTouchMode(true);

        scroller = new Scroller(context, null, true);
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener () {
            @Override
            public void onLongPress(MotionEvent e)
            {
                if (eventListener != null)
                {
                    int x = (int)e.getX();
                    int y = (int)e.getY();
                    int[] cellXY = screenToCellPos(x, y);
                    x = cellXY[0];
                    y = cellXY[1];

                    eventListener.onLongPress(x, y, viewModel.charAt(x, y));
                }
            }

            @Override
            public boolean onDoubleTap(MotionEvent e)
            {
                showKeyboard();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                scrollBy((int)distanceX, (int)distanceY);

                int maxX = Math.max(0, (int)(maxScrollX * cellWidth) - getWidth());
                int maxY = Math.max(0, (int)(maxScrollY * cellHeight) - getHeight());

                if (getScrollX() < 0)
                    setScrollX(0);
                else if (getScrollX() > maxX)
                    setScrollX(maxX);
                if (getScrollY() < 0)
                    setScrollY(0);
                else if (getScrollY() > maxY)
                    setScrollY(maxY);

                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                int maxX = Math.max(0, (int)(maxScrollX * cellWidth) - getWidth());
                int maxY = Math.max(0, (int)(maxScrollY * cellHeight) - getHeight());

                // Before flinging, aborts the current animation
                scroller.forceFinished(true);

                scroller.fling(getScrollX(), getScrollY(),
                        -(int)(velocityX / 2f),
                        -(int)(velocityY / 2f),
                        0, maxX,
                        0, maxY);

                // Invalidates to trigger computeScroll()
                ViewCompat.postInvalidateOnAnimation(GridView.this);

                return true;
            }
        });

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                GridViewModel model = GridView.this.viewModel;

                if (event.getAction() != KeyEvent.ACTION_UP)
                   return true;

                if (!highlightedCells.isEmpty())
                    return true;

                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DEL:
                    {
                        model.handleBackspace();
                        handleTextChanged(0);
                        break;
                    }

                    default:
                    {
                        int cx = model.currentCellX;
                        int cy = model.currentCellY;
                        int uniChar = event.getUnicodeChar();
                        model.handleCharInput(uniChar);
                        if (uniChar != 0)
                        {
                            handleTextChanged(uniChar);

                            if (eventListener != null)
                            {
                                eventListener.onCharAdded(cx, cy, uniChar);
                            }
                        }
                        break;
                    }
                }

                panIntoView(viewModel.currentCellX, viewModel.currentCellY);
                invalidate();

                return true; // handled
            }
        });
    }

    @Override
    public void computeScroll()
    {
        super.computeScroll();

        if (scroller.computeScrollOffset())
        {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean detectedUp = event.getAction() == MotionEvent.ACTION_UP;
        if (!gestureDetector.onTouchEvent(event) && detectedUp)
            handleActionUp(event);

        invalidate();

        return true;
    }

    public static int fastHash(int x) {
        x = ((x >>> 16) ^ x) * 0x45d9f3b;
        x = ((x >>> 16) ^ x) * 0x45d9f3b;
        x = (x >>> 16) ^ x;
        return x;
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        HashMap<Integer, SongBlueprint.LineOverride> rowOverrides =
                viewModel.songBlueprint.getHorizontalOverride();

        HashMap<Integer, SongBlueprint.LineOverride> columnOverrides =
                viewModel.songBlueprint.getVerticalOverride();

        HashMap<Integer, SongBlueprint.LineOverride> cellOverrides =
                viewModel.songBlueprint.getCellOverride();

        int width = getWidth();
        int height = getHeight();

        int scrollX = getScrollX();
        int scrollY = getScrollY();

        float textXOffset = cellWidth / 2f;
        float textYOffset = (cellHeight / 2f) - ((gridLetterPaint.descent() + gridLetterPaint.ascent()) / 2f);

        final int startCellX = (int)(scrollX / cellWidth);
        final int endCellX = startCellX + N_COLS + 2;
        final int xOffset = scrollX - (int)(scrollX % cellWidth);

        final int startCellY = (int)(scrollY / cellHeight);
        final int endCellY = startCellY + N_ROWS + 2;
        final int yOffset = scrollY - (int)(scrollY % cellHeight);

        ArrayList<SongBlueprint.LineOverride> columnOverridesLUT = new ArrayList<>();
        for (int i = startCellX; i < endCellX; i++)
        {
            columnOverridesLUT.add(columnOverrides.get(i));
        }

        canvas.drawRect(scrollX, scrollY, scrollX+width, scrollY+height, backgroundFillPaint);

        for (int cellY = startCellY, py = yOffset; cellY < endCellY; cellY++, py += cellHeight)
        {
            StringBuffer line = viewModel.getLine(cellY);
            int lineSize = line != null ? line.length() : 0;

            int horizontalBackgroundTint = backgroundTint;

            for (int cellX = startCellX, px = xOffset; cellX < endCellX; cellX++, px += cellWidth)
            {
                int finalBackgroundTint = horizontalBackgroundTint;

                // cell override
                int cellOverrideKey = SongBlueprint.cellOverrideKey(cellX, cellY);
                SongBlueprint.LineOverride cellOverride = cellOverrides.get(cellOverrideKey);

                int ch = 0;
                if (line != null && cellX >= 0 && cellX < lineSize) {
                    ch = line.charAt(cellX);
                }

                int cellID = (cellY << 12) | cellX;
                int highlightColor = getHighlight(cellX, cellY);

                if (highlightColor != 0)
                {
                    finalBackgroundTint = cursorTint;
                }
                else if (highlightedCells.isEmpty() && viewModel.currentCellX == cellX && viewModel.currentCellY == cellY)
                {
                    finalBackgroundTint = cursorTint;
                }
                //else
                {
                    int R = (fastHash(cellID) & 31) % 24;
                    int alpha = 255 - R;

                    // The components are stored as follows: (alpha << 24) | (red << 16) | (green << 8) | blue
                    int color = (alpha << 24) | finalBackgroundTint;

                    tilePaint.setColor(color);
                }

                canvas.drawRect(px, py, px + cellWidth, py + cellHeight, tilePaint);

                if (cellOverride != null)
                {
                    tilePaint.setColor(overrideColor);
                    canvas.drawRect(px, py + cellHeight - 4, px + cellWidth, py + cellHeight, tilePaint);
                }

                if (ch != 0 && ch != ' ')
                {
                    charBuf[0] = (char)ch;
                    canvas.drawText(charBuf, 0, 1,
                            px + textXOffset,
                            py + textYOffset, gridLetterPaint);
                }
            }
        }

        for (int cellY = startCellY, py = yOffset; cellY < endCellY; cellY++, py += cellHeight)
        {
            SongBlueprint.LineOverride rowOverride = rowOverrides.get(cellY);
            if (rowOverride != null) {
                tilePaint.setColor(overrideColor);
                canvas.drawRect(scrollX, py, scrollX + 4, py + cellHeight, tilePaint);
            }
        }

        for (int cellX = startCellX, px = xOffset; cellX < endCellX; cellX++, px += cellWidth)
        {
            SongBlueprint.LineOverride columnOverride = columnOverrides.get(cellX);
            if (columnOverride != null) {
                tilePaint.setColor(overrideColor);
                canvas.drawRect(px, scrollY, px + cellWidth, scrollY + 4, tilePaint);
            }
        }

        if (drawGrid) {
            for (int y = 0; y < N_ROWS + 2; y++) {
                float ly = yOffset + y * cellHeight;
                canvas.drawLine(xOffset, ly, xOffset+width+cellWidth, ly, gridPaint);
            }

            for (int x = 0; x < N_COLS + 2; x++) {
                float lx = xOffset + x * cellWidth;
                canvas.drawLine(lx, yOffset, lx, yOffset+height + cellHeight, gridPaint);
            }
        }

        if (randyg.titlewaves.BuildConfig.DEBUG)
        {
            canvas.drawText("Scroll: " + scrollX + ", " + scrollY,
                    scrollX + 50, scrollY + height - 85, debugTextPaint);
            canvas.drawText("Cursor: " + viewModel.currentCellX + ", " + viewModel.currentCellY,
                    scrollX + 50, scrollY + height - 50, debugTextPaint);
        }
    }

    public int getPixelsFromDPs(int dps) {
        Resources r = getContext().getResources();
        int px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()
        ));
        return px;
    }

    private void showKeyboard() {
        requestFocus();
        //if (requestFocus())
        {
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    public interface EventListener
    {
        void onTileTouched(int x, int y, int codePoint);

        void onLongPress(int x, int y, int codePoint);

        void onCharAdded(int x, int y, int ch);

        void onTextChanged(int ch);
    }
};

