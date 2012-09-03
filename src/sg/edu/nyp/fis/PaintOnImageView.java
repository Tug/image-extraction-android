package sg.edu.nyp.fis;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.view.View;
import android.view.ViewParent;

public class PaintOnImageView extends View
{
	private Bitmap image, imageCopy;
	private ZoomableCanvas zcanvas, zimage, rectcanvas;
	private ZoomContext zcontext;
	private HashMap<String, DrawTool> tools;
	private DrawTool currentTool;
	private int px = -1, py = -1;
	private double prevDist = -1;
	private int left = 0, top = 0;
	
	public PaintOnImageView(Context context, Bitmap image, HashMap<String, DrawTool> tools)
	{
		super(context);
		int w = image.getWidth();
		int h = image.getHeight();
		this.image = image;
		this.imageCopy = image.copy(Bitmap.Config.ARGB_8888, true);
		this.zcontext = new ZoomContext(w, h, 100, 100);
		this.zimage = new ZoomableCanvas(imageCopy);
		this.zimage.setContext(zcontext);
		Bitmap drawableBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		this.zcanvas = new ZoomableCanvas(drawableBitmap);
		this.zcanvas.setContext(zcontext);
		Bitmap rectBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		this.rectcanvas = new ZoomableCanvas(rectBitmap);
		this.rectcanvas.setContext(zcontext);
		this.tools = tools;
		tools.get("brush").init(zcanvas);
		tools.get("rect").init(rectcanvas);
	}
	
	public ZoomableCanvas getZoomableImage() {
		return zimage;
	}
	
	public ZoomableCanvas getZoomableMask() {
		return zcanvas;
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom) {
		ViewParent viewParent = getParent();
		this.left = left;
		this.top = top;
		while(viewParent != null && viewParent instanceof View) {
			View parent = (View) viewParent;
			this.left += parent.getLeft();
			this.top += parent.getTop();
			viewParent = parent.getParent();
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		zimage.draw(canvas);
		clearCanvas(rectcanvas);
		for(Map.Entry<String, DrawTool> entry : tools.entrySet()) {
			DrawTool tool = entry.getValue();
			tool.draw();
		}
		zcanvas.draw(canvas);
		rectcanvas.draw(canvas);
	}
	
	public DrawTool selectTool(String name)
	{
		currentTool = tools.get(name);
		return currentTool;
	}
	
	public void doubleTouch(int x1, int y1, int x2, int y2)
	{
		if(currentTool != null) {
			Point p = new Point(x1, y1);
			p = this.translateCoordinates(p);
			if(p == null) return;
			p = zcontext.translateCoordinates(p);
			if(p == null) return;
			x1 = p.x;
			y1 = p.y;
			p = new Point(x2, y2);
			p = this.translateCoordinates(p);
			if(p == null) return;
			p = zcontext.translateCoordinates(p);
			if(p == null) return;
			x2 = p.x;
			y2 = p.y;
			currentTool.onDoubleTouch(x1, y1, x2, y2);
			invalidate();
		} else {
			int dx = x1 - x2;
			int dy = y1 - y2;
			double dist = Math.sqrt(dx*dx+dy*dy);
			if(prevDist != -1) {
				zcontext.zoom(dist - prevDist);
				invalidate();
			}
			prevDist = dist;
		}
	}
	
	public void simpleTouch(int x, int y)
	{
		if(currentTool != null) {
			Point p = new Point(x, y);
			p = this.translateCoordinates(p);
			if(p == null) return;
			p = zcontext.translateCoordinates(p);
			if(p == null) return;
			x = p.x;
			y = p.y;
			currentTool.onSimpleTouch(x, y);
			invalidate();
		} else {
			int dx = x - px;
			int dy = y - py;
			if(px != -1 && py != -1) {
				zcontext.move(dx, dy);
				invalidate();
			}
			px = x;
			py = y;
		}
	}
	
	public void simpleTouchReleased()
	{
		px = -1;
		py = -1;
		if(currentTool != null)
			currentTool.onSimpleTouchRealeased();
	}
	
	public void doubleTouchReleased()
	{
		prevDist = -1;
		if(currentTool != null)
			currentTool.onDoubleTouchRealeased();
	}
	
	public void getDrawableBitmapPNG(FileOutputStream out) {
		zcanvas.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
	}
	
	public HashMap<String, DrawTool> getTools() {
		return tools;
	}
	
	public void reset() {
		this.imageCopy = image.copy(Bitmap.Config.ARGB_8888, true);
		this.zimage.setBitmap(imageCopy);
		for(Map.Entry<String, DrawTool> entry : tools.entrySet()) {
			DrawTool tool = entry.getValue();
			tool.reset();
		}
		resetMask();
	}

	public void resetMask() {
		clearCanvas(zcanvas);
		tools.get("brush").reset();
	}

	public void clearCanvas(ZoomableCanvas zc) {
		zc.getBitmap().eraseColor(0);
	}
	
	public void undo() {
		
	}
	
	public Point translateCoordinates(Point op) {
		Point p = new Point(op.x-left, op.y-top);
		if(p.x < 0 || p.y < 0 || p.x > getWidth() || p.y > getHeight())
			return null;
		return p;
	}
}
