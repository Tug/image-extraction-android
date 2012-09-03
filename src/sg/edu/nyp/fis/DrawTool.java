package sg.edu.nyp.fis;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface DrawTool {
	void init(Canvas c);
	void onDoubleTouch(int x1, int y1, int x2, int y2);
	void onSimpleTouch(int x, int y);
	void onSimpleTouchRealeased();
	void onDoubleTouchRealeased();
	void draw();
	void reset();
}