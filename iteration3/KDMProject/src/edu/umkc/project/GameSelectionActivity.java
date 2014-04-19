package edu.umkc.project;

import com.quchen.flappycow.MainActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class GameSelectionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_selection);
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
				Intent reportPage = new Intent(GameSelectionActivity.this,MainActivity.class);
				startActivity(reportPage);
			}
		});
		registerReceiver(receiver, new IntentFilter("myproject"));
	}
	public static Integer numberOfStomps;
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle!=null) {
				numberOfStomps = Integer.parseInt(bundle.getString("numberOfStomps"));
				Log.i("number of stomps", String.valueOf(numberOfStomps));
			}else{
				Log.i("data in main class", "bundle null");
			}
		}

		
	};

}
