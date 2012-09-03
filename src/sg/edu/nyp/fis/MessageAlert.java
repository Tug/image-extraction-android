package sg.edu.nyp.fis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MessageAlert implements Runnable {

	private Context context;
	private String title;
	private String message;
	
	public MessageAlert(Context context, String title, String message) {
		this.context = context;
		this.title = title;
		this.message = message;
	}
	
	public void run() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
           }
        });
		AlertDialog alert = builder.create();
	}

}
