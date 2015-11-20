package daiyiming.unique.com.beamtextview.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import daiyiming.unique.com.beamtextview.R;

/**
 * Created by daiyiming on 2015/11/20.
 */
public class BeamTextView extends View {

    public final static int PLAY_DURATION_HIGH = 15;
    public final static int PLAY_DURATION_NORMAL = 25;
    public final static int PLAY_DURATION_LOW = 35;

    private Bitmap textBitmap = null;
    private Bitmap beamBitmap = null;

    private Paint textPaint = null;
    private Paint beamPaint = null;
    private Paint mixPaint = null;

    private String text = "代一鸣O(∩_∩)O~~";
    private int textColor = Color.BLACK;
    private int textSize = 0;

    private int lightColor = Color.WHITE;
    private int beamRadius = 0;

    private boolean isInit = false;
    private Rect textRect = null;
    private int beamX = 0;
    private int maxBeamX = 0, minBeamX = 0;

    private int playSpeed = PLAY_DURATION_NORMAL;
    private Handler handler = new Handler();
    private Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            beamX += 4;
            if (beamX > maxBeamX) {
                beamX = minBeamX;
            }
            BeamTextView.this.invalidate();
            handler.postDelayed(playRunnable, playSpeed);
        }
    };

    public BeamTextView(Context context) {
        super(context);
        init();
    }

    public BeamTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取资源
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BeamTextView);
        text = typedArray.getString(R.styleable.BeamTextView_text);
        if (text == null) {
            text = "代一鸣O(∩_∩)O~~";
        }
        textColor = typedArray.getColor(R.styleable.BeamTextView_textColor, Color.BLACK);
        textSize = typedArray.getDimensionPixelSize(R.styleable.BeamTextView_textSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 22, getResources().getDisplayMetrics()));
        lightColor = typedArray.getColor(R.styleable.BeamTextView_lightColor, Color.WHITE);
        beamRadius = typedArray.getDimensionPixelSize(R.styleable.BeamTextView_beamRadius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
        switch (typedArray.getInt(R.styleable.BeamTextView_speed, PLAY_DURATION_NORMAL)) {
            case 0: {
                playSpeed = PLAY_DURATION_LOW;
            } break;
            case 1: {
                playSpeed = PLAY_DURATION_NORMAL;
            } break;
            case 2: {
                playSpeed = PLAY_DURATION_HIGH;
            } break;
        }

        init();
    }

    private void init() {
        textPaint = new Paint();
        textPaint.setColor(textColor);
        if (textSize == 0) {
            textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 22, getResources().getDisplayMetrics());
        }
        textPaint.setTextSize(textSize);
        textPaint.setDither(true);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);

        textRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textRect);

        beamPaint = new Paint();
        beamPaint.setDither(true);
        beamPaint.setShader(new RadialGradient(beamRadius, beamRadius, beamRadius, new int[]{lightColor, Color.TRANSPARENT}, null, Shader.TileMode.REPEAT));
        beamPaint.setAntiAlias(true);
        beamPaint.setStyle(Paint.Style.FILL);

        mixPaint = new Paint();
        mixPaint.setFilterBitmap(false);
        mixPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (!isInit) {
            isInit = true;

            minBeamX = (getWidth() - textRect.width()) / 2 - 2 * beamRadius;
            maxBeamX = (getWidth() - textRect.width()) / 2 + textRect.width();
            beamX = minBeamX;
            handler.post(playRunnable);
        }

        //创建文本Bitmap
        createTextBitmap();
        //创建光线Bitmap
        createBeamBitmap();
        //将光线绘制上去
        Canvas textCanvas = new Canvas(textBitmap);
        textCanvas.drawBitmap(beamBitmap, beamX, (getHeight() - beamBitmap.getHeight()) / 2, mixPaint);
        //绘图
        canvas.drawBitmap(textBitmap, 0, 0, null);
    }

    private void createBeamBitmap() {
        beamBitmap = Bitmap.createBitmap(beamRadius * 2, beamRadius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(beamBitmap);
        canvas.drawCircle(beamBitmap.getWidth() / 2, beamBitmap.getHeight() / 2, beamRadius, beamPaint);
    }

    private void createTextBitmap() {
        textBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(textBitmap);
        //绘制文本
        canvas.drawText(text, (getWidth() - textRect.width()) / 2, (getHeight() + textRect.height()) / 2, textPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacks(playRunnable);
        super.onDetachedFromWindow();
    }

    public void setText(String text) {
        this.text = text;
        if (isInit) { //如果已经初始化了则重新刷新数据
            textPaint.getTextBounds(text, 0, text.length(), textRect);
            minBeamX = (getWidth() - textRect.width()) / 2 - 2 * beamRadius;
            maxBeamX = (getWidth() - textRect.width()) / 2 + textRect.width();
            beamX = minBeamX;
        }
    }

    public void setTextColor(int color) {
        this.textColor = color;
        textPaint.setColor(color);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
    }

    public void setLightColor(int lightColor) {
        this.lightColor = lightColor;
        beamPaint.setShader(new RadialGradient(beamRadius, beamRadius, beamRadius, new int[]{lightColor, Color.TRANSPARENT}, null, Shader.TileMode.REPEAT));
    }

    public void setBeamRadius(int beamRadius) {
        this.beamRadius = beamRadius;
        if (isInit) { //如果已经初始化了则重新刷新数据
            textPaint.getTextBounds(text, 0, text.length(), textRect);
            minBeamX = (getWidth() - textRect.width()) / 2 - 2 * beamRadius;
            maxBeamX = (getWidth() - textRect.width()) / 2 + textRect.width();
            beamX = minBeamX;
        }
    }

}



























