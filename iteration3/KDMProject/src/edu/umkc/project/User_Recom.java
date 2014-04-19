package edu.umkc.project;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class User_Recom extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user__recom);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user__recom, menu);
		return true;
	}

}
