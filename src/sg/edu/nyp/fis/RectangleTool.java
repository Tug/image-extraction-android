package sg.edu.nyp.fis;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class RectangleTool implements DrawTool
{
	private Rect rect;
	private Paint paint;
	private Canvas canvas;
	
	public RectangleTool(int strokeWidth) {
		this.paint = new Paint();
		this.rect = new Rect(0,0,0,0);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(strokeWidth);
		paint.setAntiAlias(false);
	}
	
	public RectangleTool() {
		this(2);
	}
	
	public void init(Canvas c) {
		this.canvas = c;
	}

	public void onDoubleTouch(int x1, int y1, int x2, int y2) {
		int topLeftX = x1;
		int topLeftY = y1;
		int width = x2 - x1;
		int height = y2 - y1;
		if(width < 0) {
			topLeftX = x2;
			width *= -1;
		}
		if(height < 0) {
			topLeftY = y2;
			height *= -1;
		}
		rect.left = topLeftX;
		rect.top = topLeftY;
		rect.right = topLeftX+width;
		rect.bottom = topLeftY+height;
	}

	public void onSimpleTouch(int x, int y) {
		
	}

	public void onSimpleTouchRealeased() {
		
	}
	
	public void onDoubleTouchRealeased() {
		
	}

	public void draw() {
		//canvas.drawColor(Color.TRANSPARENT);
		if(rect.width() > 0 && rect.height() > 0)
			canvas.drawRect(rect, paint);
	}

	public void reset() {
		rect.left = 0;
		rect.top = 0;
		rect.right = 0;
		rect.bottom = 0;
	}
	
	public Rect getRect() {
		return rect;
	}
}