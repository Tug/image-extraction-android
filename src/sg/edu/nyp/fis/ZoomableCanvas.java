package sg.edu.nyp.fis;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class ZoomableCanvas extends Canvas {

	private Bitmap bitmap;
	private BitmapDrawable bitmapDrawable;
	private ZoomContext context;
	
	public ZoomableCanvas(Bitmap bitmap) {
		super(bitmap);
		setBitmap(bitmap);
	}
	
	public void setBitmap(Bitmap bitmap) {
		super.setBitmap(bitmap);
		this.bitmap = bitmap;
		this.bitmapDrawable = new BitmapDrawable(bitmap);
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public BitmapDrawable getBitmapDrawable() {
		return bitmapDrawable;
	}
	
	public void draw(Canvas canvas) {
		bitmapDrawable.setBounds(context.getBounds());
		bitmapDrawable.draw(canvas);
	}
	
	public void setContext(ZoomContext context) {
		this.context = context;
	}
	
	public ZoomContext getContext() {
		return context;
	}
	
	/*
	@Override
	public void drawRect(Rect rect, Paint paint) {
		Rect rect2 = new Rect();
		rect2.left = (int) (zoomFactor * rect.left);
		rect2.top = (int) (zoomFactor * rect.top);
		rect2.right = (int) (zoomFactor * rect.right);
		rect2.bottom = (int) (zoomFactor * rect.bottom);
		super.drawRect(rect2, paint);
	}
	*/
	/*
	@Override
	public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
		context.refreshZoom();
		double zoom = context.getZoomFactor();
		left *= zoom;
		top *= zoom;
		Matrix matrix = new Matrix();
        matrix.postScale((float) zoom, (float) zoom);
        Bitmap resizedBitmap = Bitmap.createBitmap(	bitmap, 0, 0,
													bitmap.getWidth(), bitmap.getHeight(),
													matrix, true );
		super.drawBitmap(bitmap, left, top, paint);
	}
	*/
	
	
}
