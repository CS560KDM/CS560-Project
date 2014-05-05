package com.quchen.flappycow;


import android.util.Log;



public class DetectMotion {
	
	Boolean found = false;
	int tempCount =0;
	float previousValue= -1.0f;
	float stompPeak = 0.0f;
	String stompType;	
	float kickPeak = 0.0f;
	Boolean kickFound = false;
/*public synchronized Float peakValue(float value){
	detectStomp(value);
	return stompPeak;
}*/
	
	
//This method detects a stomp
public synchronized  Float detectStomp(float xvalue,float value, float zvalue){
	stompPeak=-2.0f;
	if(value >= -0.8 && !found ){
		//Log.i("value ", Float.toString(value));
		if(value > previousValue){
			previousValue=value;
		}else if(value <= previousValue){
			found  = true;
			stompPeak = previousValue;
			previousValue =-2.0f;
			}
		}else if (found){
			if (value<=-0.8) {
				found =false;
			}
		}
	return stompPeak;
}
Float kickValue = -2.0f;
public Float detectRightKick(float xaxis, float yaxis, float zaxis) {
	kickValue = -2.0f;
	if(xaxis >= 0.15 && yaxis >= -0.9 && yaxis <= 0.7 && zaxis >= -0.50 && !kickFound  ){
		//Log.i("Found side Kick ", "yes");
		kickFound = true;
		kickValue = (float) Math.sqrt(xaxis*xaxis + zaxis*zaxis);
	}else if(kickFound){
		//Log.i("waiting for next kick  ", Integer.toString(tempCount));
		tempCount++;
		if(xaxis <= 0.10 && zaxis >= -0.15 && tempCount >=7 ){
			kickFound =false;
			tempCount=0;
		}
	}
	return kickValue ;
}

}
