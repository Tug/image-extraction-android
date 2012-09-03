package sg.edu.nyp.fis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class HomeActivity extends Activity {
	
	private Intent nextActivity;
	private int LOAD_PICTURE_REQUEST_CODE = 1;
	private int TAKE_PICTURE_REQUEST_CODE = 2;
	private File pictureFile;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.home);
    	this.pictureFile = new File(getExternalCacheDir(), getString(R.string.original_img));
    	
    	Button loadPictureButton = (Button) findViewById(R.id.loadPictureButton);
    	Button takePictureButton = (Button) findViewById(R.id.takePictureButton);
    	loadPictureButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
				startActivityForResult(intent, LOAD_PICTURE_REQUEST_CODE);
		    	//startActivityForResult(Intent.createChooser(intent,"Select Picture"), LOAD_PICTURE_REQUEST_CODE);
			}
		});
    	takePictureButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile));
			    //intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
				startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE);
			}
		});
    	
    	Button quickTestButton = (Button) findViewById(R.id.quickTestButton);
    	quickTestButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//nextActivity("content://media/external/images/media/14");
				File quickTestFile = new File(getExternalCacheDir(), getString(R.string.test_img));
				if(!quickTestFile.exists()) {
					try {
						FileOutputStream out = new FileOutputStream(quickTestFile);
						Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.blue);
						Bitmap scaledImg = rescaleBitmap(img, new Point(640,480));
						scaledImg.compress(Bitmap.CompressFormat.PNG, 90, out);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				nextActivity(quickTestFile.getPath());
			}
		});
    	
    	this.nextActivity = new Intent(this, DrawerActivity.class);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == LOAD_PICTURE_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
            	String selectedFile = getPath(intent.getData());
				nextActivity(selectedFile);
            }
        } else if (requestCode == TAKE_PICTURE_REQUEST_CODE) {
        	if(resultCode == Activity.RESULT_OK) {
        		if(pictureFile != null)
        			nextActivity(pictureFile.getPath());
        	}
    	}
    }
   
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    
	public void nextActivity(String filePath) {
		String pictureFilePath = pictureFile.getPath();
		//if(!filePath.equals(pictureFilePath)) {
			Bitmap src;
			try {
				src = BitmapFactory.decodeFile(filePath);
				if(src == null) throw new FileNotFoundException();
				Bitmap res = rescaleBitmap(src, new Point(640,480));
				FileOutputStream out = new FileOutputStream(pictureFile);
				res.compress(Bitmap.CompressFormat.PNG, 90, out);
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		//}
		nextActivity.putExtra("picture_file", pictureFilePath);
    	startActivity(nextActivity);
	}
	
	public Bitmap rescaleBitmap(Bitmap src, Point maxDim) {
		int width = src.getWidth();
        int height = src.getHeight();
        
		float scaleWidth = ((float) maxDim.x) / width;
        float scaleHeight = ((float) maxDim.y) / height;
        
        if(scaleWidth == 1 && scaleHeight == 1)
        	return src;
        
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(src, 0, 0, width, height, matrix, true); 
        
        return resizedBitmap;
	}
	
}
