package sg.edu.nyp.fis;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

public class ZoomableImage extends View
{
	private Drawable image;
	private Rect bounds;
	private int centerX, centerY;
	private double width, height;
	private double ratio;
	
	public ZoomableImage(Context context, Drawable image)
	{
		super(context);
		this.image = image;
		this.width = image.getIntrinsicWidth();
		this.height = image.getIntrinsicHeight();
		this.ratio = width/height;
		setFocusable(true);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if(oldw == -1 && oldh == -1) {
			this.centerX = w/2;
			this.centerY = h/2;
			if(width == -1)
				width = w;
			if(height == -1)
				height = h;
		}
		refreshBounds();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		image.setBounds(bounds);
		image.draw(canvas);
	}
	
	public void zoom(double d) {
		width += d;
		height += d/ratio;
		if(width < 10 || height < 10) {
			width = 10;
			height = 10/ratio;
		}
		if(width > 5000 || height > 5000) {
			width = 5000;
			height = 5000/ratio;
		}
		refreshBounds();
	}
	
	public void move(int dx, int dy) {
		centerX += dx;
		centerY += dy;
		refreshBounds();
		System.out.println("image pos x="+bounds.left+" y="+bounds.top);
	}
	
	public void refreshBounds() {
		bounds.left = (int) (centerX - width/2);
		bounds.top = (int) (centerY - height/2);
		bounds.right = (int) (centerX + width/2);
		bounds.bottom = (int) (centerY + height/2);
	}
	
	public void setBounds(Rect bounds) {
		this.bounds = bounds;
		refreshBounds();
	}
	
	public Rect getBounds() {
		return bounds;
	}
	
}
