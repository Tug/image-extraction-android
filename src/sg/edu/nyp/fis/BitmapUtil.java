package sg.edu.nyp.fis;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class BitmapUtil {

	public static Bitmap grayscale8ToAlpha32(Bitmap grayscaleBitmap) {
		Bitmap grayscaleBitmap32 = convertTo32(grayscaleBitmap);
		return grayscale32ToAlpha32(grayscaleBitmap32);
	}
	
	public static Bitmap grayscale32ToAlpha32(Bitmap grayscaleBitmap) {
		Bitmap alphaBitmap = Bitmap.createBitmap(grayscaleBitmap.getWidth(),
												 grayscaleBitmap.getHeight(),
												 Bitmap.Config.ARGB_8888);
		float[] matrix = new float[] {
		        0, 0, 0, 0, 0,
		        0, 0, 0, 0, 0,
		        0, 0, 0, 0, 0,
		        1, 0, 0, 0, 0};
		Paint grayToAlpha = new Paint();
		grayToAlpha.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(matrix)));
		Canvas alphaCanvas = new Canvas(alphaBitmap);
		alphaCanvas.setDensity(Bitmap.DENSITY_NONE);
		alphaCanvas.drawBitmap(grayscaleBitmap, 0, 0, grayToAlpha);
		return alphaBitmap;
	}
	
	public static Bitmap convertTo32(Bitmap grayscaleBitmap) {
		if(grayscaleBitmap.getConfig() == Config.ARGB_8888)
			return grayscaleBitmap;
		return grayscaleBitmap.copy(Config.ARGB_8888, true);
	}
	
}
