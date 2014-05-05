package edu.umkc.project;

import org.w3c.dom.Text;

import android.media.Image;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ImageView;
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
		TextView bmr = (TextView)findViewById(R.id.bmrValue);
		TextView stomp = (TextView)findViewById(R.id.stompValue);
		TextView burnt = (TextView) findViewById(R.id.calburnValue);
		TextView result = (TextView)findViewById(R.id.result);
		loginDataBaseAdapter=new LoginDataBaseAdapter(this);
	    loginDataBaseAdapter=loginDataBaseAdapter.open();
	    
		String username = (String) getIntent().getCharSequenceExtra("username");
		System.out.println(username);
		String ageValue=loginDataBaseAdapter.getSinlgeEntry(username,"AGE");
		String heightValue=loginDataBaseAdapter.getSinlgeEntry(username,"HEIGHT");
		String weightValue=loginDataBaseAdapter.getSinlgeEntry(username,"WEIGHT");
		String calorieValue=loginDataBaseAdapter.getSinlgeEntry(username,"CALORIES");
		String BMR = loginDataBaseAdapter.getSinlgeEntry(username,"BMR");
		loginDataBaseAdapter.updateEntry(username, "STOMP", loginDataBaseAdapter.getSinlgeEntry("stomp","STOMP"));
		String Stomp = loginDataBaseAdapter.getSinlgeEntry("stomp","STOMP");
		System.out.println("STOMP HERE---"+Stomp);
		
		
		user.setText(username);
		age.setText(ageValue);
		height.setText(heightValue);
		weight.setText(weightValue);
		calorie.setText(calorieValue);
		
		stomp.setText(Stomp);
/* If you are sedentary (little or no exercise) : Calorie-Calculation = BMR x 1.2
If you are lightly active (light exercise/sports 1-3 days/week) : Calorie-Calculation = BMR x 1.375
If you are moderatetely active (moderate exercise/sports 3-5 days/week) : Calorie-Calculation = BMR x 1.55
If you are very active (hard exercise/sports 6-7 days a week) : Calorie-Calculation = BMR x 1.725
If you are extra active (very hard exercise/sports & physical job or 2x training) : Calorie-Calculation = BMR x 1.9
*/
		int stomp_count = Integer.parseInt(Stomp);
		Float cal_burnt =(float) 0.0;
		Float bmrValue = Float.parseFloat(BMR);
		bmr.setText(String.format("%.2f", bmrValue));
	if ( stomp_count < 20){
		cal_burnt = (float) (bmrValue * 1.2);
		burnt.setText(cal_burnt.toString());
		result.setText("Very little Workout");
	}
	else if ( (stomp_count >= 20) && (stomp_count < 50)){
		cal_burnt = (float) (bmrValue * 1.375);
		burnt.setText(cal_burnt.toString());
		result.setText("little workout");
	}
	else if ( (stomp_count >= 50) && (stomp_count < 90)){
		cal_burnt = (float) (bmrValue * 1.55);
		burnt.setText(cal_burnt.toString());
		result.setText("Average Workout");
	}
	else if ( (stomp_count >= 90) && (stomp_count < 150)){
		cal_burnt = (float) (bmrValue * 1.725);
		burnt.setText(cal_burnt.toString());
		result.setText("Good workout");
	}
	else {
		cal_burnt = (float) (bmrValue * 1.9);
		burnt.setText(cal_burnt.toString());
		result.setText("Excellent Workout");
	}
	
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user__report, menu);
		return true;
	}

}
