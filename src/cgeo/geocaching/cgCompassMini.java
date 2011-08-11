package cgeo.geocaching;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;

public class cgCompassMini extends View {
	private int arrowSkin = R.drawable.compass_arrow_mini_white;
	private Context context = null;
	private Double cacheLat = null;
	private Double cacheLon = null;
	private Bitmap compassArrow = null;
	private Double azimuth = Double.valueOf(0);
	private Double heading = Double.valueOf(0);
	private PaintFlagsDrawFilter setfil = null;
	private PaintFlagsDrawFilter remfil = null;

	public cgCompassMini(Context contextIn) {
		super(contextIn);
		context = contextIn;
	}

	public cgCompassMini(Context contextIn, AttributeSet attrs) {
		super(contextIn, attrs);
		context = contextIn;

		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.cgCompassMini);
		int usedSkin = attributes.getInt(R.styleable.cgCompassMini_skin, 0);
		if (usedSkin == 1) arrowSkin = R.drawable.compass_arrow_mini_black;
		else arrowSkin = R.drawable.compass_arrow_mini_white;
	}

	@Override
	public void onAttachedToWindow() {
		compassArrow = BitmapFactory.decodeResource(context.getResources(), arrowSkin);

		setfil = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
		remfil = new PaintFlagsDrawFilter(Paint.FILTER_BITMAP_FLAG, 0);
	}

	@Override
	public void onDetachedFromWindow() {
		if (compassArrow != null) {
			compassArrow.recycle();
			compassArrow = null;
		}
	}

	public void setContent(Double cacheLatIn, Double cacheLonIn) {
		cacheLat = cacheLatIn;
		cacheLon = cacheLonIn;
	}

	protected void updateAzimuth(Double azimuthIn) {
		azimuth = azimuthIn;

		updateDirection();
	}

	protected void updateHeading(Double headingIn) {
		heading = headingIn;

		updateDirection();
	}

	protected void updateCoords(Double latitudeIn, Double longitudeIn) {
		if (latitudeIn == null || longitudeIn == null || cacheLat == null || cacheLon == null) {
			return;
		}

		heading = cgBase.getHeading(latitudeIn, longitudeIn, cacheLat, cacheLon);

		updateDirection();
	}

	protected void updateDirection() {
		if (compassArrow == null || compassArrow.isRecycled() == true) {
			return;
		}

        // compass margins
        int compassRoseWidth = compassArrow.getWidth();
        int compassRoseHeight = compassArrow.getWidth();
        int marginLeft = (getWidth() - compassRoseWidth) / 2;
        int marginTop = (getHeight() - compassRoseHeight) / 2;

        invalidate(marginLeft, marginTop, (marginLeft + compassRoseWidth), (marginTop + compassRoseHeight));
	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);

		Double azimuthRelative = azimuth - heading;
		if (azimuthRelative < 0) {
			azimuthRelative = azimuthRelative + 360;
		} else if (azimuthRelative >= 360) {
			azimuthRelative = azimuthRelative - 360;
		}

		// compass margins
		canvas.setDrawFilter(setfil);

		int marginLeft = 0;
		int marginTop = 0;

		int compassArrowWidth = compassArrow.getWidth();
		int compassArrowHeight = compassArrow.getWidth();

		int canvasCenterX = (compassArrowWidth / 2) + ((getWidth() - compassArrowWidth) / 2);
		int canvasCenterY = (compassArrowHeight / 2) + ((getHeight() - compassArrowHeight) / 2);

		marginLeft = (getWidth() - compassArrowWidth) / 2;
		marginTop = (getHeight() - compassArrowHeight) / 2;

		canvas.rotate(-(azimuthRelative.floatValue()), canvasCenterX, canvasCenterY);
		canvas.drawBitmap(compassArrow, marginLeft, marginTop, null);
		canvas.rotate(azimuthRelative.floatValue(), canvasCenterX, canvasCenterY);

		canvas.setDrawFilter(remfil);
	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 21 + getPaddingLeft() + getPaddingRight();

            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 21 + getPaddingTop() + getPaddingBottom();

            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }
}