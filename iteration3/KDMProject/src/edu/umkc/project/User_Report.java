package edu.umkc.project;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class User_Report extends Activity {
	LoginDataBaseAdapter loginDataBaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user__report);
		TextView user =(TextView)findViewById(R.id.usenameValue);
		TextView age =(TextView)findViewById(R.id.ageValue);
		TextView height =(TextView)findViewById(R.id.heightValue);
		TextView weight =(TextView)findViewById(R.id.weightValue);
		TextView calorie =(TextView)findViewById(R.id.calorieValue);
		loginDataBaseAdapter=new LoginDataBaseAdapter(this);
	    loginDataBaseAdapter=loginDataBaseAdapter.open();
	    
		String username = (String) getIntent().getCharSequenceExtra("username");
		System.out.println(username);
		String ageValue=loginDataBaseAdapter.getSinlgeEntry(username,"AGE");
		String heightValue=loginDataBaseAdapter.getSinlgeEntry(username,"HEIGHT");
		String weightValue=loginDataBaseAdapter.getSinlgeEntry(username,"WEIGHT");
		String calorieValue=loginDataBaseAdapter.getSinlgeEntry(username,"CALORIES");
		String BMR = loginDataBaseAdapter.getSinlgeEntry(username,"BMR");
		user.setText(username);
		age.setText(ageValue);
		height.setText(heightValue);
		weight.setText(weightValue);
		calorie.setText(calorieValue);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user__report, menu);
		return true;
	}

}
