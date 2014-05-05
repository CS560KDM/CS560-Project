package edu.umkc.project;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class GameSelectionActivity extends Activity {
String userName ="tempUser";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_selection);
		Button b = (Button) findViewById(R.id.buttonHealth);
		b.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),ReportActivity.class);
				userName = (String)getIntent().getCharSequenceExtra("username");
				i.putExtra("username", getIntent().getCharSequenceExtra("username"));
				startActivity(i);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_selection, menu);
		return true;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		ImageButton fluffyCow = (ImageButton) findViewById(R.id.fluffyCow);
		fluffyCow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//Intent reportPage = new Intent("com.quchen.flappycow.MainActivity");
				//startService(new Intent(GameSelectionActivity.this,ConnectionService.class));
				String url ="http://10.0.2.2:8080/RESTfulWS/rest/upload/user?username="+userName;
				new SaveUserName().execute(url);
				//Log.i("hello", "here");
				Intent i = new Intent(Intent.ACTION_MAIN);
			    PackageManager managerclock = getPackageManager();
			    i = managerclock.getLaunchIntentForPackage("com.quchen.flappycow");
			    i.addCategory(Intent.CATEGORY_LAUNCHER);
				startActivity(i);
			}
		});
		registerReceiver(receiver, new IntentFilter("myproject"));
	}
	public static Integer numberOfStomps;
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle!=null && bundle.getString("numberOfStomps") !=null) {
				
				numberOfStomps = Integer.parseInt(bundle.getString("numberOfStomps"));
				Log.i("number of stomps", String.valueOf(numberOfStomps));
			}else{
				Log.i("data in main class", "bundle null");
			}
		}

	};

}
class SaveUserName extends AsyncTask<String, String, String>{

	@Override
	protected String doInBackground(String... params) {
		 String urlString=params[0]; // URL to call
		 String resultToDisplay = "";
		 InputStream in = null;
		 try {
		 URL url = new URL(urlString);
		 HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		 BufferedReader br = new BufferedReader(new InputStreamReader(
					(urlConnection.getInputStream())));
		 while((resultToDisplay=br.readLine())!=null){
			 Log.i("url response", resultToDisplay);	 
		 }
		 } catch (Exception e ) {
		 return e.getMessage();
		 }
		 return resultToDisplay; 
	}
	
}
