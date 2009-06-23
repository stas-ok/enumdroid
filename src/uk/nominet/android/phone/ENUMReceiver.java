package uk.nominet.android.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class ENUMReceiver extends BroadcastReceiver {

	/* Log tag */
	static private final String TAG = "ENUMReceiver";
	
	/* Exported constants */
	static public final String BYPASS_PREFIX = "**";
	
	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		Log.d(TAG, "action " + action + " received");

		/* check connectivity and update notification on every received vent */
		boolean online = ENUMUtil.updateNotification(context);
		
		/* and do this if it's an outgoing call */
		if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			String number = getResultData(); 
			if (number == null) return;
			
			Log.d(TAG, "number = " + number);
			if (online && number.startsWith("+")) {
				setResultData(null);
				
				Intent newIntent = new Intent(context, ENUMList.class);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				newIntent.setData(Uri.fromParts("tel", number, null));
				context.startActivity(newIntent);
			} else {
				if (number.startsWith(BYPASS_PREFIX)) {
					setResultData(number.substring(BYPASS_PREFIX.length()));
				} else {
					setResultData(number);
				}
			}
		}
	}
}
