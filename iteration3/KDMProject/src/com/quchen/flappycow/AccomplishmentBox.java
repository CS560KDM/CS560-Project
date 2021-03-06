package com.quchen.flappycow;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.android.gms.games.GamesClient;

import edu.umkc.project.R;

public class AccomplishmentBox{
	public static final String SAVE_NAME = "ACCOMBLISHMENTS";
	
	public static final String ONLINE_STATUS_KEY = "online_status";
	
	public static final String KEY_POINTS = "points";
	public static final String ACHIEVEMENT_KEY_SURVIVE_5_MINUTES = "achievement_survive_5_minutes";
	public static final String ACHIEVEMENT_KEY_TOASTIFICATION = "achievement_toastification";
	public static final String ACHIEVEMENT_KEY_BRONZE = "achievement_bronze";
	public static final String ACHIEVEMENT_KEY_SILVER = "achievement_silver";
	public static final String ACHIEVEMENT_KEY_GOLD = "achievement_gold";
	
	int points;
	boolean achievement_survive_5_minutes;
	boolean achievement_toastification;
	boolean achievement_bronze;
	boolean achievement_silver;
	boolean achievement_gold;
	
	/**
	 * Stores the score and achievements locally.
	 * 
	 * The accomblishments will be saved local via SharedPreferences.
	 * This makes it very easy to cheat.
	 * 
	 * @param outbox Data that should be saved
	 * @param activity activity that is needed for shared preferences
	 */
	public void saveLocal(Activity activity){
		SharedPreferences saves = activity.getSharedPreferences(SAVE_NAME, 0);
		SharedPreferences.Editor editor = saves.edit();
		
		if(points > saves.getInt(KEY_POINTS, 0)){
			editor.putInt(KEY_POINTS, points);
		}
		if(achievement_survive_5_minutes){
			editor.putBoolean(ACHIEVEMENT_KEY_SURVIVE_5_MINUTES, true);
		}
		if(achievement_toastification){
			editor.putBoolean(ACHIEVEMENT_KEY_TOASTIFICATION, true);
		}
		if(achievement_bronze){
			editor.putBoolean(ACHIEVEMENT_KEY_BRONZE, true);
		}
		if(achievement_silver){
			editor.putBoolean(ACHIEVEMENT_KEY_SILVER, true);
		}
		if(achievement_gold){
			editor.putBoolean(ACHIEVEMENT_KEY_GOLD, true);
		}
		
		editor.commit();
	}
	
	/**
	 * Uploads accomplishments to Google Play Services
	 * @param activity
	 * @param gamesClient
	 */
	public void submitScore(Activity activity, GamesClient gamesClient){
		gamesClient.submitScore(activity.getResources().getString(R.string.leaderboard_highscore), this.points);
		
		if(this.achievement_survive_5_minutes){
			gamesClient.unlockAchievement(activity.getResources().getString(R.string.achievement_survive_5_minutes));
		}
		if(this.achievement_toastification){
			gamesClient.unlockAchievement(activity.getResources().getString(R.string.achievement_toastification));
		}
		if(this.achievement_bronze){
			gamesClient.unlockAchievement(activity.getResources().getString(R.string.achievement_bronze));
		}
		if(this.achievement_silver){
			gamesClient.unlockAchievement(activity.getResources().getString(R.string.achievement_silver));
		}
		if(this.achievement_gold){
			gamesClient.unlockAchievement(activity.getResources().getString(R.string.achievement_gold));
		}
		
		AccomplishmentBox.savesAreOnline(activity);
		
		Toast.makeText(activity.getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * reads the local stored data
	 * @param activity activity that is needed for shared preferences
	 * @return local stored score and achievements
	 */
	public static AccomplishmentBox getLocal(Activity activity){
		AccomplishmentBox box = new AccomplishmentBox();
		SharedPreferences saves = activity.getSharedPreferences(SAVE_NAME, 0);
		
		box.points = saves.getInt(KEY_POINTS, 0);
		box.achievement_survive_5_minutes = saves.getBoolean(ACHIEVEMENT_KEY_SURVIVE_5_MINUTES, false);
		box.achievement_toastification = saves.getBoolean(ACHIEVEMENT_KEY_TOASTIFICATION, false);
		box.achievement_bronze = saves.getBoolean(ACHIEVEMENT_KEY_BRONZE, false);
		box.achievement_silver = saves.getBoolean(ACHIEVEMENT_KEY_SILVER, false);
		box.achievement_gold = saves.getBoolean(ACHIEVEMENT_KEY_GOLD, false);
		
		return box;
	}
	
	/**
	 * marks the data as online
	 * @param activity activity that is needed for shared preferences
	 */
	public static void savesAreOnline(Activity activity){
		SharedPreferences saves = activity.getSharedPreferences(SAVE_NAME, 0);
		SharedPreferences.Editor editor = saves.edit();
		editor.putBoolean(ONLINE_STATUS_KEY, true);
		editor.commit();
	}
	
	/**
	 * marks the data as offline
	 * @param activity activity that is needed for shared preferences
	 */
	public static void savesAreOffline(Activity activity){
		SharedPreferences saves = activity.getSharedPreferences(SAVE_NAME, 0);
		SharedPreferences.Editor editor = saves.edit();
		editor.putBoolean(ONLINE_STATUS_KEY, false);
		editor.commit();
	}
	
	/**
	 * checks if the last data is already uploaded
	 * @param activity activity that is needed for shared preferences
	 * @return wheater the last data is already uploaded
	 */
	public static boolean isOnline(Activity activity){
		return activity.getSharedPreferences(SAVE_NAME, 0).getBoolean(ONLINE_STATUS_KEY, true);
	}
}