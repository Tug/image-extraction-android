package sg.edu.nyp.fis;

import android.graphics.Point;
import android.graphics.Rect;

public class ZoomContext {
	
	private Rect bounds;
	private double width, height;
	private int centerX, centerY;
	private double zoomFactor, ratio;
	private int originalWidth, originalHeight, originalCenterX, originalCenterY;
	
	public ZoomContext(int originalWidth, int originalHeight, 
					 int originalCenterX, int originalCenterY) {
		this.originalWidth = originalWidth;
		this.originalHeight = originalHeight;
		this.originalCenterX = originalCenterX;
		this.originalCenterY = originalCenterY;
		this.ratio = originalWidth/(double)originalHeight;
		this.zoomFactor = 1.0;
		reset();
		this.bounds = new Rect();
		refreshBounds();
	}
	
	public void reset() {
		this.centerX = originalCenterX;
		this.centerY = originalCenterY;
		this.width = originalWidth;
		this.height = originalHeight;
	}
	
	public void setBounds(Rect bounds) {
		this.bounds = bounds;
	}
	
	public Rect getBounds() {
		return bounds;
	}
	
	public double getZoomFactor() {
		return zoomFactor;
	}
	
	public void refreshZoom() {
		zoomFactor = (bounds.right - bounds.left) / (double) originalWidth;
	}
	
	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
		refreshBounds();
	}
	
	public void setCenterPosition(int x, int y) {
		centerX = x;
		centerY = y;
		refreshBounds();
	}
	
	public void refreshBounds() {
		bounds.left = (int) (centerX - width/2);
		bounds.top = (int) (centerY - height/2);
		bounds.right = (int) (centerX + width/2);
		bounds.bottom = (int) (centerY + height/2);
		refreshZoom();
	}
	
	public void zoom(double d) {
		if(zoomFactor > 2 && d > 0) return;
		if(zoomFactor < 0.2 && d < 0) return;
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
	
	public Point translateCoordinates(Point p) {
		refreshZoom();
		int x = (int) ((p.x - bounds.left) / zoomFactor);
		int y = (int) ((p.y - bounds.top) / zoomFactor);
		return new Point(x, y);
	}
	
}
