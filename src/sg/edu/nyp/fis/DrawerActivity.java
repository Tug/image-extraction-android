package sg.edu.nyp.fis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.nyp.fis.R.id;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.os.Handler;

public class DrawerActivity extends Activity implements OnClickListener {
	
	private PaintOnImageView poicanvas;
	private HttpContext localContext;
	private boolean oneFinger = false, twoFingers = false;
	private int counter = 0;
	private String currentAlgo = "grabcut";
	private String defaultClassifier = "svc1";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.drawer);
    	FrameLayout preview = (FrameLayout) findViewById(R.id.preview);
    	
    	Bundle extras = getIntent().getExtras();
        String filename = extras != null ? extras.getString("picture_file") : null;
        if(filename == null) {
        	filename = getExternalCacheDir() + "/" + getString(R.string.original_img);
        }
        Bitmap image = null;
		InputStream is;
		try {
			is = new FileInputStream(filename);
			//Uri fileUri = Uri.parse(filename);
			//is = getContentResolver().openInputStream(fileUri);
			//is = openFileInput(filename);
			image = BitmapFactory.decodeStream(is);
			HashMap<String, DrawTool> tools = new HashMap<String, DrawTool>();
			tools.put("brush", new BrushTool());
			tools.put("rect", new RectangleTool());
			this.poicanvas = new PaintOnImageView(this, image, tools);
			poicanvas.selectTool("move");
    		preview.addView(poicanvas);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			finish();
		}
    	findViewById(R.id.moveButton).setOnClickListener(this);
    	findViewById(R.id.rectangleButton).setOnClickListener(this);
    	findViewById(R.id.backgroundButton).setOnClickListener(this);
    	findViewById(R.id.foregroundButton).setOnClickListener(this);
    	findViewById(R.id.undoButton).setOnClickListener(this);
    	findViewById(R.id.resetButton).setOnClickListener(this);
    	
    	this.localContext = new BasicHttpContext();
    	localContext.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());
    	sendImage(filename);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawer_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.segmentationItem:
            	segementation();
            	break;
            case R.id.identificationItem:
            	identification();
            	break;
        }
        return true;
    }
    
    public void sendImage(String filename) {
    	ProgressDialog pd = new ProgressDialog(this);
    	pd.setTitle("Please wait");
    	pd.setMessage("Sending image to the server...");
    	MultipartPostTask sendImageTask = new MultipartPostTask(pd, localContext){
    		@Override
    		protected void onPostExecute(String responseText)
    		{
    			super.onPostExecute(responseText);
    			AlertDialog.Builder dialog = new AlertDialog.Builder( DrawerActivity.this );
    			dialog.setTitle("Status");
    			try {
    				if(responseText == null || responseText.equals(""))
    					throw new JSONException("Empty response");
    				JSONObject obj = new JSONObject(responseText);
					obj.getString("imgUrl");
					dialog.setMessage("Success");
				} catch(JSONException e) {
					dialog.setMessage("Server Error : " + responseText);
				}
				dialog.setPositiveButton("OK", null);
    			dialog.show();
    		}
    	};
    	sendImageTask.setUrl("http://"+this.getString(R.string.server_host)+"/upload");
    	MultipartEntity entity = sendImageTask.getEntity();
		entity.addPart("file", new FileBody(new File(filename)));
		//entity.addPart("canvas-size[width]", new StringBody(""+image.getIntrinsicWidth()));
		//entity.addPart("canvas-size[height]", new StringBody(""+image.getIntrinsicHeight()));
    	sendImageTask.execute();
    }
    
    public void segementation() {
    	ProgressDialog pd = new ProgressDialog(this);
    	pd.setTitle("Please wait");
    	pd.setMessage("Segmentation...");
    	MultipartPostTask sendImageTask = new MultipartPostTask(pd, localContext) {
    		@Override
    		protected void onPostExecute(String responseText)
    		{
    			super.onPostExecute(responseText);
    			AlertDialog.Builder dialog = new AlertDialog.Builder( DrawerActivity.this );
    			dialog.setTitle( "Status" );
    			try {
    				if(responseText == null || responseText.equals(""))
    					throw new JSONException("Empty response");
    				JSONObject obj = new JSONObject(responseText);
					String imgStr = obj.getString("img");
					if(imgStr == null || imgStr.equals(""))
						throw new Exception("Empty response");
					String maskb64str = imgStr.substring(imgStr.indexOf(";base64,")+8,
														 imgStr.length()-1);
					if(maskb64str == null || maskb64str.equals(""))
						throw new Exception("Response not valid");
					dialog.setMessage("Success");
					byte[] maskArr = Base64.decode(maskb64str, Base64.DEFAULT);
					Bitmap mask = BitmapFactory.decodeByteArray(maskArr, 0, maskArr.length);
					Bitmap mask32 = BitmapUtil.grayscale8ToAlpha32(mask);
					Paint alphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
					alphaPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
					poicanvas.resetMask();
					poicanvas.getZoomableImage().drawBitmap(mask32, 0, 0, alphaPaint);
					poicanvas.invalidate();
				} catch(Exception e) {
					dialog.setMessage("Server Error : " + responseText);
				}
				dialog.setPositiveButton("OK", null);
    			dialog.show();
    		}
    	};
    	sendImageTask.setUrl("http://"+this.getString(R.string.server_host)+"/segmentation/"+currentAlgo);
    	MultipartEntity entity = sendImageTask.getEntity();
    	try {
    		File tempMask = new File(getExternalCacheDir(), getString(R.string.mask_img));
    		FileOutputStream out = new FileOutputStream(tempMask);
    		poicanvas.getDrawableBitmapPNG(out);
			entity.addPart("mask", new FileBody(tempMask));
			DrawTool tool = poicanvas.getTools().get("rect");
			if(tool != null) {
				RectangleTool rectTool = (RectangleTool) tool;
				Rect rectangle = rectTool.getRect();
				if(rectangle.width() != 0 && rectangle.height() != 0) {
					entity.addPart("rectangle[x]", new StringBody(""+rectangle.left));
					entity.addPart("rectangle[y]", new StringBody(""+rectangle.top));
					entity.addPart("rectangle[width]", new StringBody(""+rectangle.width()));
					entity.addPart("rectangle[height]", new StringBody(""+rectangle.height()));
				}
			}
	    	sendImageTask.execute();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
    
    public void identification() {
    	ProgressDialog pd = new ProgressDialog(this);
    	pd.setTitle("Please wait");
    	pd.setMessage("Identification...");
    	DownloadFilesTask downloadResultTask = new DownloadFilesTask(pd, localContext) {
    		@Override
    		protected void onPostExecute(String result)
    		{
    			super.onPostExecute(result);
    			if(result == null) return;
				String filename = "result.html";
				OutputStreamWriter out = null;
				try {
					out = new OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE));
					out.write(result);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if(out != null) {
						try {
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				Intent nextActivity = new Intent(DrawerActivity.this, BrowserActivity.class);
				nextActivity.putExtra("file", filename);
				startActivity(nextActivity);
    		}
    	};
    	String url = "http://"+this.getString(R.string.server_host)+"/learning/predict/"+defaultClassifier;
    	downloadResultTask.execute(url);
    }
    
    public void identificationJSON() {
    	ProgressDialog pd = new ProgressDialog(this);
    	pd.setTitle("Please wait");
    	pd.setMessage("Identification...");
    	DownloadFilesTask downloadResultTask = new DownloadFilesTask(pd, localContext) {
    		@Override
    		protected void onPostExecute(String result)
    		{
    			super.onPostExecute(result);
    			String str = "";
				try {
					JSONArray obj = new JSONArray(result);
					if(obj != null) {
	    				for(int i=0; i<obj.length(); i++) {
	    					JSONObject obj2 = obj.getJSONObject(i);
	    					if(obj2 != null) {
	    						String flowername = obj2.getString("name");
	    						String proba = obj2.getString("proba");
	    						str += "" + (i+1) + " - " + flowername + " - " + proba + "%\n";
	    					}
	    				}
	    			}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(str.equals("")) {
					str = "Error";
				}
				//DrawerActivity.this.runOnUiThread(
				//		new MessageAlert(DrawerActivity.this, "Identification result",str));
				ProgressDialog pd = getProgressDialog();
				pd.setTitle("Identification result");
		    	pd.setMessage(str);
		    	pd.setCancelable(true);
		    	pd.setProgress(10000);
		    	pd.show();
    		}
    	};
    	String url = "http://"+this.getString(R.string.server_host)+"/learning/predict_json/"+defaultClassifier;
    	downloadResultTask.execute(url);
    }
	
	public void resetServer() {
		ProgressDialog pd = new ProgressDialog(this);
    	pd.setTitle("Please wait");
    	pd.setMessage("Resetting...");
		DownloadFilesTask downloadResultTask = new DownloadFilesTask(pd, localContext);
    	String url = "http://"+this.getString(R.string.server_host)+"/segmentation/reset"; 
    	downloadResultTask.execute(url);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		super.dispatchTouchEvent(event);
		int pointersCount = event.getPointerCount();
		int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
		if (pointersCount == 1) {
			int actionType = event.getAction();
			if(actionType != MotionEvent.ACTION_CANCEL
			&& actionType != MotionEvent.ACTION_UP) {
				x1 = (int) event.getX();
				y1 = (int) event.getY();
				x2 = -1;
				y2 = -1;
			}
		}
		if (pointersCount == 2) {
			int id = event.getPointerId(0);
			int actionType = event.getAction() & MotionEvent.ACTION_MASK;
			if(actionType != MotionEvent.ACTION_CANCEL
			&& actionType != MotionEvent.ACTION_UP) {
				x1 = (int) event.getX(id);
				y1 = (int) event.getY(id);
			}
			
			id = event.getPointerId(1);
			actionType = (event.getAction() >> MotionEvent.ACTION_POINTER_INDEX_SHIFT) & MotionEvent.ACTION_MASK;
			if(actionType != MotionEvent.ACTION_CANCEL
			&& actionType != MotionEvent.ACTION_UP) {
				x2 = (int) event.getX(id);
				y2 = (int) event.getY(id);
			}
		}

		setPress(x1, y1, x2, y2);

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setPress(int x1, int y1, int x2, int y2) {
		if(x1 == -1 && y1 == -1 && x2 == -1 && y2 == -1) {
			if(twoFingers) {
				poicanvas.doubleTouchReleased();
				twoFingers = false;
			}
			if(oneFinger) {
				poicanvas.simpleTouchReleased();
				oneFinger = false;
			}
		} else if(x1 != -1 && y1 != -1 && x2 == -1 && y2 == -1) {
			if(oneFinger && counter > 0) {
				poicanvas.simpleTouch(x1, y1);
				poicanvas.invalidate();
			}
			oneFinger = true;
			if(twoFingers) {
				poicanvas.doubleTouchReleased();
				twoFingers = false;
			}
			counter++;
		} else {	// two fingers, zoom picture
			if(twoFingers) {
				poicanvas.doubleTouch(x1, y1, x2, y2);
				poicanvas.invalidate();
				counter = 0;
			}
			twoFingers = true;
			if(oneFinger) {
				poicanvas.simpleTouchReleased();
				oneFinger = false;
			}
		}
	}

	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.moveButton:
			poicanvas.selectTool("move");
			break;
		case R.id.rectangleButton:
			poicanvas.selectTool("rect");
			break;
		case R.id.backgroundButton:
			BrushTool btool = (BrushTool) poicanvas.selectTool("brush");
			btool.backgroundColor();
			break;
		case R.id.foregroundButton:
			BrushTool btool2 = (BrushTool) poicanvas.selectTool("brush");
			btool2.foregroundColor();
			break;
		case R.id.undoButton:
			poicanvas.undo();
			break;
		case R.id.resetButton:
			resetServer();
			poicanvas.reset();
			poicanvas.invalidate();
		}
	}

}