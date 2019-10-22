package com.wtz.tools.view.custom_view_study.dashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

import java.io.InputStream;

/**
 * http://download.csdn.net/download/lioulei007/9861393
 */
public final class Thermometer extends View {

	private static final String TAG = Thermometer.class.getSimpleName();
	
	private Paint compassPaint;
	private Bitmap compass;
	private Matrix compassMatrix;
	private float compassScale;
	
	private Paint handPaint;
	private Path handPath;
	private Paint handScrewPaint;
	
	private Paint backgroundPaint;
	private Bitmap background; // holds the cached static part

	// 转动手柄的位置等的初始化
	private boolean handInitialized = false;
	public  float handTarget = 0;
	private float handPosition = handTarget;
	private float handVelocity = 0.0f;
	private float handAcceleration = 0.0f;
	private long lastHandMoveTime = -1L;
	private int bitmap;
	
	public Thermometer(Context context, int _bitmap, float _handTarget) {
		super(context);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		bitmap = _bitmap;
		handTarget = _handTarget;
		handPosition = handTarget;
		initDrawingTools();
	}
	
	private Bitmap readBitmap(Context context, int id){
	     BitmapFactory.Options opt = new BitmapFactory.Options();
	     opt.inPreferredConfig= Bitmap.Config.RGB_565;//表示16位位图 565代表对应三原色占的位数
	     opt.inInputShareable=true;
	     opt.inPurgeable=true;//设置图片可以被回收
	     InputStream is = context.getResources().openRawResource(id);
	     return BitmapFactory.decodeStream(is, null, opt);
	}    
	
	//画图工具的初始化
	private void initDrawingTools() {

		//设置compass的属性
		compassPaint = new Paint();
		compassPaint.setFilterBitmap(true);
		compass = BitmapFactory.decodeResource(getContext().getResources(),bitmap);
		compass = readBitmap(getContext(), bitmap);
		compassMatrix = new Matrix();
		compassScale = (1.0f / compass.getWidth())*0.9f;
		compassMatrix.setScale(compassScale, compassScale);
		
		//设置指针的属性
		handPaint = new Paint();
		handPaint.setAntiAlias(true);
		handPaint.setColor(Color.RED);
	    handPaint.setShadowLayer(0.01f, -0.005f, -0.005f, 0x7f000000);	
		handPaint.setStyle(Paint.Style.FILL);
		
		//设置指针的画法
		handPath = new Path();
		handPath.moveTo(0.5f, 0.5f + 0.15f);
		handPath.lineTo(0.5f - 0.010f, 0.5f + 0.15f - 0.007f);
		handPath.lineTo(0.5f - 0.002f, 0.5f - 0.32f);
		handPath.lineTo(0.5f + 0.002f, 0.5f - 0.32f);
		handPath.lineTo(0.5f + 0.010f, 0.5f + 0.15f - 0.007f);
		handPath.lineTo(0.5f, 0.5f + 0.15f);
		handPath.addCircle(0.5f, 0.5f, 0.025f, Path.Direction.CW);
	
		
		
		handScrewPaint = new Paint();
		handScrewPaint.setAntiAlias(true);
		handScrewPaint.setColor(0xff493f3c);
		handScrewPaint.setStyle(Paint.Style.FILL);
		
		backgroundPaint = new Paint();
		backgroundPaint.setFilterBitmap(true);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);
		
		int chosenDimension = Math.min(chosenWidth, chosenHeight);
		
		setMeasuredDimension(chosenDimension, chosenDimension);
	}
	
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		} 
	}
	
	// in case there is no size specified
	private int getPreferredSize() {
		return 200;
	}

	private float degreeToAngle(float degree) {
		return degree;
	}

	private void drawCompass(Canvas canvas) {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		initDrawingTools();
		canvas.translate(0.5f - compass.getWidth() * compassScale / 2.0f, 
						 0.5f - compass.getHeight() * compassScale / 2.0f);	

		canvas.drawBitmap(compass, compassMatrix, compassPaint);
		if(compass != null)
		{
			compass.recycle();
			compass = null;
		}
		canvas.restore();		
	}

	private void drawHand(Canvas canvas) {
		if (!handInitialized) {
			float handAngle = degreeToAngle(handPosition);
			canvas.save(Canvas.MATRIX_SAVE_FLAG);
			canvas.rotate(handAngle, 0.5f, 0.5f);
			canvas.drawPath(handPath, handPaint);
			canvas.restore();
			
			canvas.drawCircle(0.5f, 0.5f, 0.01f, handScrewPaint);
		}
	}

	private void drawBackground(Canvas canvas) {
		if (background == null) {
			Log.i(TAG, "Background not created");
		} else {
			canvas.drawBitmap(background, 0, 0, backgroundPaint);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		drawBackground(canvas);

		float scale = (float) getWidth();		
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);

		drawCompass(canvas);
		drawHand(canvas);
		
		canvas.restore();
	
		if (handNeedsToMove()) {
			moveHand();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		regenerateBackground();
	}
	
	private void regenerateBackground() {
		// free the old bitmap
		if (background != null) {
			background.recycle();
		}
		
		background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas backgroundCanvas = new Canvas(background);
		float scale = (float)  getWidth();		
		backgroundCanvas.scale(scale, scale);
		
	}

	private boolean handNeedsToMove() {
		return Math.abs(handPosition - handTarget) > 0.01f;
	}
	
	private void moveHand() {
		if (! handNeedsToMove()) {
			return;
		}
		
		if (lastHandMoveTime != -1L) {
			long currentTime = System.currentTimeMillis();
			float delta = (currentTime - lastHandMoveTime) / 1000.0f;

			float direction = Math.signum(handVelocity);
			if (Math.abs(handVelocity) < 90.0f) {
//				float s = 360/75;
				handAcceleration = 5 * (handTarget - handPosition);
			} else {
				handAcceleration = 0.0f;
			}
			handPosition += handVelocity * delta;
			handVelocity += handAcceleration * delta;
//			if(handTarget>360){
//				handTarget = handTarget-360;
//			}
			if ((handTarget - handPosition) * direction < 0.01f * direction) {
				handPosition = handTarget;
				handVelocity = 0.0f;
				handAcceleration = 0.0f;
				lastHandMoveTime = -1L;
			} else {
				lastHandMoveTime = System.currentTimeMillis();
			}
			invalidate();
		} else {
			lastHandMoveTime = System.currentTimeMillis();
			moveHand();
		}
	}
}
