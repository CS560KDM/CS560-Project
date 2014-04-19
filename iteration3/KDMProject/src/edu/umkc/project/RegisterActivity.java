package edu.umkc.project;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	EditText userNameText, passwordText, ageText, weightText, heightText, caloriesText;
	RadioGroup radioSexGroup;
	RadioButton radioSexButton;
	LoginDataBaseAdapter loginDataBaseAdapter;
	Button registerButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		// get Instance  of Database Adapter
        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();
        userNameText = (EditText) findViewById(R.id.userNameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        ageText = (EditText) findViewById(R.id.ageText);
        weightText = (EditText) findViewById(R.id.weightText);
        heightText = (EditText) findViewById(R.id.heightText);
        caloriesText = (EditText) findViewById(R.id.caloriesText);
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
        radioSexButton = (RadioButton) findViewById(radioSexGroup.getCheckedRadioButtonId());
        //genderText = (EditText) findViewById(R.id.genderText);
        registerButton = (Button) findViewById(R.id.registerButton);
        //button listener
        
        registerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//validate and register the user in the database
				String userName=userNameText.getText().toString();
	            String password=passwordText.getText().toString();
	            String age  = ageText.getText().toString();
	            String weight = weightText.getText().toString();
	            String height = heightText.getText().toString();
	            String calories = caloriesText.getText().toString();
	            String gender = (String) radioSexButton.getText();
	            Log.i("gender",gender);
	            //validation and saving in database
	         // check if any of the fields are vaccant
	            if(userName.equals("")||password.equals("") || gender.equals("") || age.equals("") || weight.equals("") || height.equals("") || calories.equals(""))
	            {
	                    Toast.makeText(getApplicationContext(), "Field Vaccant", Toast.LENGTH_LONG).show();
	                    return;
	            }
	            
	            else
	            {
	                // Save the Data in Database
	                loginDataBaseAdapter.insertEntry(userName, password,gender,age,weight,height,calories);
	                Toast.makeText(getApplicationContext(), "Account Successfully Created ", Toast.LENGTH_LONG).show();
	                finish();
	            }
	            
				
			}
		});
        
	}
	
	@Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
 
        loginDataBaseAdapter.close();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

}
