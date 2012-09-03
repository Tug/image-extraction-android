package sg.edu.nyp.fis;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class DownloadFilesTask extends AsyncTask<String, Integer, String> {
	
	private ProgressDialog pd;
	private HttpContext localContext;
	
	public DownloadFilesTask(ProgressDialog pd, HttpContext localContext) {
		this.pd = pd;
		this.localContext = localContext;
	}
	
	@Override
	protected void onPreExecute()
	{
		if(pd != null) {
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.show();
		}
	}
	
    protected String doInBackground(String... urls) {
        String url = urls[0];
        String fileStream = null;
        try {
        	fileStream = downloadFile(url);
		} catch (IOException e) {
			e.printStackTrace();
        }
        return fileStream;
    }
    
    private String downloadFile(String url) throws IOException {
	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet(url);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
		return httpClient.execute(httpGet, responseHandler, localContext);
	}

    protected void onProgressUpdate(Integer... progress) {
    	if(pd != null)
			pd.setProgress((int) (progress[0]));
    }

    protected void onPostExecute(String result) {
    	if(pd != null)
			pd.dismiss();
    }
    
    public ProgressDialog getProgressDialog() {
    	return pd;
    }
}