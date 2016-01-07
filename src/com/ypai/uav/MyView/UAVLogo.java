package com.ypai.uav.MyView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class UAVLogo extends View  {

	private boolean bCircle=false;
	private boolean bLine=false;
	private boolean bGetPoint=false;
	private float angle=0;
	private final int radias=20;
	private float offset_x=0;
	private float offset_y=0;
	private int height=0;
	private int width=0;
	private Paint mPaint;
	private float mCircle_x=0;
	private float mCircle_y=0;
	private List<PointF> lists;
	private List<PointF> listlinepoint;
	public UAVLogo(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);
		lists=new ArrayList<PointF>();
		listlinepoint=new ArrayList<PointF>();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		drawPoint(canvas);
        drawUavLogo(canvas);
        drawLine(canvas);
       
        
	}

	private void drawLine(Canvas canvas) {
		if (listlinepoint.size()>1) {
        	//Iterator<PointF> iterator=listlinepoint.iterator();
        	 for (int i = 0; i < listlinepoint.size()-1; i++) {
        		 
     			canvas.drawLine(listlinepoint.get(i).x, listlinepoint.get(i).y, listlinepoint.get(i+1).x, listlinepoint.get(i+1).y, mPaint);
     		}
		}
	}

	private void drawUavLogo(Canvas canvas) {
		Path path3 = new Path();
        float dx=(float) (radias*Math.sin((angle*Math.PI)/180));
        float dy=radias-(float) (radias*Math.cos((angle*Math.PI)/180));
        path3.moveTo(dx+width/2+offset_x, -radias+dy+height/2+offset_y);
        path3.lineTo(-radias+dy+width/2+offset_x, -dx+height/2+offset_y);
        path3.lineTo((float) (10*Math.sin((angle*Math.PI)/180))+width/2+offset_x, -radias/2+radias/2-(float) (10*Math.cos((angle*Math.PI)/180))+height/2+offset_y);
        path3.lineTo(radias-dy+width/2+offset_x, dx+height/2+offset_y);
        path3.close();
        // »æÖÆÈý½ÇÐÎ
        canvas.drawPath(path3, mPaint);
	}

	private void drawPoint(Canvas canvas) {
		int i=1;
		for (PointF pointF : lists) {
			
			if (lists.size()>0) {
				canvas.drawCircle(pointF.x, pointF.y, 5, mPaint);
				canvas.drawText(i + "", pointF.x - 10, pointF.y, mPaint);
				i++;
			}
		}
		if (bCircle) {
			canvas.drawCircle(mCircle_x, mCircle_y, 10, mPaint);
		}
	}
	
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);    
	    width = MeasureSpec.getSize(widthMeasureSpec);    
	    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
	}

	public void setLogoParams(float angle,float offset_x,float offset_y){
		this.angle=angle;
		this.offset_x=offset_x;
		this.offset_y=offset_y;
		listlinepoint.add(new PointF(width/2+offset_x,height/2+offset_y));
		invalidate();
		
	}
	public void drawCircle(float x,float y) {
		Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);   
		Canvas c = new Canvas(b);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        c.drawCircle(x, y, 3, paint);
	}
	public void setCircleEnable(boolean bCircle,float cx,float cy) {
		this.bCircle=bCircle;
		this.mCircle_x=cx;
		this.mCircle_y=cy;
	}
	public void setGetPoint(Boolean enable) {
		if (enable) {
			lists.clear();
			bGetPoint=true;
			listlinepoint.clear();
			invalidate();
		} else {
			
			bGetPoint=false;
		}
	}
	public List<PointF> getListPoint() {
		
		return lists;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			if (bGetPoint) {
				PointF pointF = new PointF(event.getX(), event.getY());
				lists.add(pointF);
				invalidate();
			}
			
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	public void clearlist() {
		lists.clear();
		listlinepoint.clear();
		invalidate();
	}
}
