package sg.edu.nyp.fis;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;

public class BrushTool implements DrawTool
{
	private int brushWidth;
	private Paint paint;
	private Path currentPath;
	private boolean firstTouch;
	private Canvas canvas;
	private int px, py;
	
	public BrushTool(int brushWidth) {
		super();
		this.brushWidth = brushWidth;
		this.paint = new Paint();
		paint.setAntiAlias(false);
		foregroundColor();
		paint.setStrokeWidth(brushWidth);
		this.currentPath = new Path();
		this.firstTouch = true;
	}
	
	public void init(Canvas c) {
		this.canvas = c;
	}

	public BrushTool() {
		this(10);
	}
	
	public void onDoubleTouch(int x1, int y1, int x2, int y2) {
		// do nothing
	}

	public void onSimpleTouch(int x, int y) {
		px = x;
		py = y;
		//if(firstTouch) currentPath.moveTo(x, y);
		//currentPath.lineTo(x, y);
		currentPath.addCircle(x, y, brushWidth, Path.Direction.CW);
		//maskCanvas.drawPath(currentPath, paint);
		firstTouch = false;
	}

	public void onSimpleTouchRealeased() {
		System.out.println("Simple touch released");
		//currentPath.lineTo( px, py );
		firstTouch = true;
	}
	
	public void onDoubleTouchRealeased() {
		
	}
	
	public void draw() {
		canvas.drawPath(currentPath, paint);
	}
	
	public void reset() {
		currentPath.reset();
	}
	
	public void foregroundColor() {
		paint.setColor(Color.BLACK);
		this.currentPath = new Path();
	}
	
	public void backgroundColor() {
		paint.setColor(Color.GRAY);
		this.currentPath = new Path();
	}

}