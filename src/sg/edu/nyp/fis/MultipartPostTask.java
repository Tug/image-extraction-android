package sg.edu.nyp.fis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class MultipartPostTask extends AsyncTask<Void, Integer, String>
{
	private HttpContext httpContext;
	private ProgressDialog pd;
	private MultipartEntity entity;
	private String url;
	private long fileSize = 0;
	
	public MultipartPostTask(ProgressDialog pd, HttpContext httpContext) {
		this.pd = pd;
		this.httpContext = httpContext;
		this.entity = new CustomMultipartEntity(new ProgressListener() {
			public void transferred(long num)
			{
				if(fileSize > 0)
					publishProgress((int) ((num / (double) fileSize) * 100));
			}
		});
	}
	
	public MultipartPostTask(ProgressDialog pd) {
		this(pd, new BasicHttpContext());
	}
	
	public MultipartPostTask(HttpContext httpContext) {
		this(null, httpContext);
	}
	
	public MultipartPostTask() {
		this(null, new BasicHttpContext());
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public MultipartEntity getEntity() {
		return entity;
	}
	
	@Override
	protected void onPreExecute()
	{
		if(pd != null) {
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			//pd.setCancelable(false);
			pd.show();
		}
	}

	public String sendFile(String httpUrl, MultipartEntity entity) throws Exception {
		fileSize = entity.getContentLength();
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
		HttpConnectionParams.setSoTimeout(httpParameters, 35000);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpPost httpPost = new HttpPost(httpUrl);
		httpPost.setEntity(entity);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		return httpClient.execute(httpPost, responseHandler, httpContext);
    }

	@Override
	protected String doInBackground(Void... params) {
		try {
			return sendFile(url, entity);
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress)
	{
		if(pd != null)
			pd.setProgress((int) (progress[0]));
	}

	@Override
	protected void onPostExecute(String responseText)
	{
		if(pd != null)
			pd.dismiss();
	}
	
	public interface ProgressListener
	{
		void transferred(long num);
	}
 
	public class CountingOutputStream extends FilterOutputStream
	{
		private final ProgressListener listener;
		private long transferred;
 
		public CountingOutputStream(final OutputStream out, final ProgressListener listener)
		{
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}
 
		public void write(byte[] b, int off, int len) throws IOException
		{
			out.write(b, off, len);
			this.transferred += len;
			this.listener.transferred(this.transferred);
		}
 
		public void write(int b) throws IOException
		{
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred);
		}
	}
	
	public class CustomMultipartEntity extends MultipartEntity
	{
		private final ProgressListener listener;
	 
		public CustomMultipartEntity(final ProgressListener listener)
		{
			super();
			this.listener = listener;
		}
	 
		public CustomMultipartEntity(final HttpMultipartMode mode, final ProgressListener listener)
		{
			super(mode);
			this.listener = listener;
		}
	 
		public CustomMultipartEntity(HttpMultipartMode mode, final String boundary, final Charset charset, final ProgressListener listener)
		{
			super(mode, boundary, charset);
			this.listener = listener;
		}
	 
		@Override
		public void writeTo(final OutputStream outstream) throws IOException
		{
			super.writeTo(new CountingOutputStream(outstream, this.listener));
		}
		
	}
}

