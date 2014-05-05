package edu.umkc.project;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class User_Recom extends Activity {
	LoginDataBaseAdapter loginDataBaseAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user__recom);
		loginDataBaseAdapter=new LoginDataBaseAdapter(this);
	    loginDataBaseAdapter=loginDataBaseAdapter.open();
	    TextView reco = (TextView)findViewById(R.id.reco);
	   // ImageView result = (ImageView) findViewById(R.id.img);
		
	    String username = (String) getIntent().getCharSequenceExtra("username");
	    String heightValue=loginDataBaseAdapter.getSinlgeEntry(username,"HEIGHT");
		String weightValue=loginDataBaseAdapter.getSinlgeEntry(username,"WEIGHT");
		String calorieValue=loginDataBaseAdapter.getSinlgeEntry(username,"CALORIES");
		String BMR = loginDataBaseAdapter.getSinlgeEntry(username,"BMR");
		String Stomp = loginDataBaseAdapter.getSinlgeEntry(username,"STOMP");
	
	
		int stomp_count = Integer.parseInt(Stomp);
		Float cal_burnt =(float) 0.0;
		Float bmrValue = Float.parseFloat(BMR);
	if ( stomp_count < 20){
		cal_burnt = (float) (bmrValue * 1.2);
		String reco_text = " Food - yes \n Proteins \n Vitamins \n\n"+
		"Food - No \n Cheese \n Carbohydrate\n\n"+
				"Action - yes \n Outdoor activies\n Tennis,Racquetball,Basket Ball,Cricket with friends"+
					"\n\nAction - No \n Dont stay in front of tv or computer without any action for a long time\n"+
				"Dont sit idle.";
		reco.setText(reco_text);
		//result.setImageResource(R.drawable.vlittle);
	}
	else if ( (stomp_count >= 20) && (stomp_count < 50)){
		cal_burnt = (float) (bmrValue * 1.375);
		String reco_text = " Food - yes \n Proteins\n cereals \n Vitamins \n\n"+
				" Food - No \nJunk Foods \n Chesse \n\n" +
				" Action - yes\n Jogging,Running \n Try to go to gym regularly \n\n" +
				" Action - No \n Sleep less and work more \n Try using cycles for shorter distance\n";
		reco.setText(reco_text);
		//result.setImageResource(R.drawable.little);
	}
	else if ( (stomp_count >= 50) && (stomp_count < 90)){
		cal_burnt = (float) (bmrValue * 1.55);
		String reco_text = "Food - yes \n Milk \n Proteins\n\n" +
				" Food - No \nCarbohydrates \n junk foods \n\n" +
				" Action - yes\n Start walking for atleast 15 mins a day\n get more involved in sports \n\n" +
				" Action - No \n Dont sit for a long time\n ";
		reco.setText(reco_text);
		//result.setImageResource(R.drawable.avg);
	}
	else if ( (stomp_count >= 90) && (stomp_count < 150)){
		cal_burnt = (float) (bmrValue * 1.725);
		String reco_text = " COntinue the same intake and the gaming activity. \n You seem to be amazing in maintaing your health!";
		reco.setText(reco_text);
		//result.setImageResource(R.drawable.good);
	}
	else {
		cal_burnt = (float) (bmrValue * 1.9);
		String reco_text = " Food - yes \n Dont worry about eating cheese. \n Eat healthy, Live healthy \n\n" +
				" Food - No\n Eat whatever you want, no restrictions..!\n Try avoiding junk foods \n\n" +
				" Action - yes \n You are already working out too much..! Relax!!\n\n" +
				" Action - No \n Try to reduce your workout, your intake is less.\n";
		reco.setText(reco_text);
		//result.setImageResource(R.drawable.excell);
	}
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user__recom, menu);
		return true;
	}

}



//food to eat
//food not to eat
//activity to do
// activity not to do




