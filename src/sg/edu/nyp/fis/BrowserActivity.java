package sg.edu.nyp.fis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class BrowserActivity extends Activity {
	
	private WebView webview;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.webview = new WebView(this);
    	setContentView(webview);
    	Bundle extras = getIntent().getExtras();
        String htmlfile = extras != null ? extras.getString("file") : null;
        String url = extras != null ? extras.getString("url") : null;
        if(htmlfile != null) {
			try {
				
				FileInputStream fis = openFileInput(htmlfile);
				InputStreamReader inputreader = new InputStreamReader(fis);
	            BufferedReader buffreader = new BufferedReader(inputreader);
	            String line;
	            StringBuffer contentBuffer = new StringBuffer();
	            while ((line = buffreader.readLine()) != null) {
	            	contentBuffer.append(line);
	            }
	            fis.close();
	            webview.loadDataWithBaseURL(null, contentBuffer.toString(), "text/html", "utf-8", null);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if(url != null) {
        	webview.loadUrl(url);
        }
    }
	
}
