package edu.umkc.project;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button login;
	TextView registerScreen;
	EditText userNameEntered,passwordEntered;
	LoginDataBaseAdapter loginDataBaseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerScreen = (TextView) findViewById(R.id.link_to_register);
        login = (Button) findViewById(R.id.btnLogin);
        //connect to database
        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
	    loginDataBaseAdapter=loginDataBaseAdapter.open();
        //Listening to Login Button
        userNameEntered = (EditText) findViewById(R.id.userNameText);
        passwordEntered = (EditText) findViewById(R.id.passwordNameText);
        login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//connect to database and validate the details
				String userName = userNameEntered.getText().toString();
				String password = passwordEntered.getText().toString();
				//stored password for given userName
				String storedPassword=loginDataBaseAdapter.getSinlgeEntry(userName,"PASSWORD");
				if(password.equals(storedPassword))
				{
					Toast.makeText(MainActivity.this, "Congrats: Login Successfull", Toast.LENGTH_LONG).show();
					//navigate to scan and connect to device
					
					Intent deviceScanPage = new Intent(MainActivity.this,GameSelectionActivity.class);
					deviceScanPage.putExtra("username", userName);
					startActivity(deviceScanPage);
					
					
				}
				else
				{
					Toast.makeText(MainActivity.this, "User Name or Password does not match", Toast.LENGTH_LONG).show();
				}
				
			}
		});
        
        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
 
    }


    @Override
	protected void onDestroy() {
		super.onDestroy();
	    // Close The Database
		loginDataBaseAdapter.close();
	}
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/
    
}
