package edu.umkc.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
 
public class LoginDataBaseAdapter 
{
        static final String DATABASE_NAME = "login.db";
        static final int DATABASE_VERSION = 1;
        public static final int NAME_COLUMN = 1;
        // TODO: Create public field for each column in your table.
        // SQL Statement to create a new database.
        static final String DATABASE_CREATE = "create table "+"LOGIN"+
                                     "( " +"ID integer primary key autoincrement, USERNAME text not null, PASSWORD  text, GENDER text,AGE text, WEIGHT text, HEIGHT text, CALORIES text,BMR text,STOMP text);";
        // Variable to hold the database instance
        public  SQLiteDatabase db;
        // Context of the application using the database.
        private final Context context;
        // Database open/upgrade helper
        private DataBaseHelper dbHelper;
        public  LoginDataBaseAdapter(Context _context) 
        {
            context = _context;
            dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public  LoginDataBaseAdapter open() throws SQLException 
        {
            db = dbHelper.getWritableDatabase();
            return this;
        }
        public void close() 
        {
            db.close();
        }
 
        public  SQLiteDatabase getDatabaseInstance()
        {
            return db;
        }
 
        public void insertEntry(String userName,String password,String gender,String age, String weight, String height, String calories)
        {
           ContentValues newValues = new ContentValues();
            // Assign values for each row.
            newValues.put("USERNAME", userName);
            newValues.put("PASSWORD",password);
            newValues.put("AGE", age);
            newValues.put("WEIGHT", weight);
            newValues.put("HEIGHT", height);
            newValues.put("CALORIES", calories);
            newValues.put("GENDER", gender);
            float bmr;
            int weightInt = Integer.parseInt(weight);
            int heightInt = Integer.parseInt(height);
            int ageInt = Integer.parseInt(age);
            if(gender.equalsIgnoreCase("girl"))
            	bmr = (float) (655 +( 4.35 * weightInt)+(4.7 * heightInt) -(4.7 * ageInt));
             else
            	 bmr = (float)(66 + ( 6.23 * weightInt  ) + ( 12.7 * heightInt ) - ( 6.8 * ageInt));
             newValues.put("BMR",bmr);
             newValues.put("STOMP","0");
            // Insert the row into your table
            db.insert("LOGIN", null, newValues);
            ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
        }
        public int deleteEntry(String UserName)
        {
            //String id=String.valueOf(ID);
            String where="USERNAME=?";
            int numberOFEntriesDeleted= db.delete("LOGIN", where, new String[]{UserName}) ;
           // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
            return numberOFEntriesDeleted;
        }    
        public String getSinlgeEntry(String userName, String column)
        {
        	//"USERNAME","PASSWORD","AGE","WEIGHT","HEIGHT","CALORIES"
        	Cursor cursor=db.query("LOGIN", null, " USERNAME=?", new String[]{userName}, null, null, null);
            if(cursor.getCount()<1) // UserName Not Exist
            {
                cursor.close();
                return "NOT EXIST";
            }
            cursor.moveToFirst();
            String password= cursor.getString(cursor.getColumnIndex(column));
            cursor.close();
            return password;                
        }
        public void  updateEntry(String userName,String columnName, String columnValue)
        {//"USERNAME","PASSWORD","AGE","WEIGHT","HEIGHT","CALORIES"
            // Define the updated row content.
            ContentValues updatedValues = new ContentValues();
            // Assign values for each row.
            updatedValues.put("USERNAME", userName);
            updatedValues.put(columnName,columnValue);
 
            String where="USERNAME = ?";
            db.update("LOGIN",updatedValues, where, new String[]{userName});               
        }
        
 
}