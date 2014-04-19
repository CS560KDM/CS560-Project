package edu.umkc.project;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ReportActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);

		ImageButton report = (ImageButton)(findViewById(R.id.report));
		ImageButton recomm = (ImageButton)(findViewById(R.id.recomm));

		report.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i= new Intent(getApplicationContext(),User_Report.class );
				i.putExtra("username", getIntent().getCharSequenceExtra("username"));
				startActivity(i);

			}
		});

		recomm.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i= new Intent(getApplicationContext(),User_Recom.class );
				i.putExtra("username", getIntent().getCharSequenceExtra("username"));
				startActivity(i);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report, menu);
		return true;
	}


}
