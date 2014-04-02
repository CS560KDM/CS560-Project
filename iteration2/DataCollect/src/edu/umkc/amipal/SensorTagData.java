package edu.umkc.amipal;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import android.bluetooth.BluetoothGattCharacteristic;
import android.graphics.YuvImage;
import android.util.Log;

/**
 * Created by Prakash Reddy Vaka
 */
public class SensorTagData {
	static double previousValue= -1.0f;
	static Boolean found = false;
	static Boolean stomp_inside =false;
	static double stompPeak1 = -1.0;
	static double stompPeak2 = -1.0;
	static double stompPeak3 = -1.0;
	static ArrayList<String> acc = new ArrayList<String>();
	static HashMap< Timestamp, String > acc_values = new HashMap<Timestamp, String>();

	public static double extractHumAmbientTemperature(BluetoothGattCharacteristic c) {
		int rawT = shortSignedAtOffset(c, 0);

		return -46.85 + 175.72/65536 *(double)rawT;
	}

	public static double extractHumidity(BluetoothGattCharacteristic c) {
		int a = shortUnsignedAtOffset(c, 2);
		// bits [1..0] are status bits and need to be cleared
		a = a - (a % 4);

		return ((-6f) + 125f * (a / 65535f));
	}

	public static int[] extractCalibrationCoefficients(BluetoothGattCharacteristic c) {
		int[] coefficients = new int[8];

		coefficients[0] = shortUnsignedAtOffset(c, 0);
		coefficients[1] = shortUnsignedAtOffset(c, 2);
		coefficients[2] = shortUnsignedAtOffset(c, 4);
		coefficients[3] = shortUnsignedAtOffset(c, 6);
		coefficients[4] = shortSignedAtOffset(c, 8);
		coefficients[5] = shortSignedAtOffset(c, 10);
		coefficients[6] = shortSignedAtOffset(c, 12);
		coefficients[7] = shortSignedAtOffset(c, 14);

		return coefficients;
	}

	public static double extractBarTemperature(BluetoothGattCharacteristic characteristic, final int[] c) {
		// c holds the calibration coefficients

		int t_r;	// Temperature raw value from sensor
		double t_a; 	// Temperature actual value in unit centi degrees celsius

		t_r = shortSignedAtOffset(characteristic, 0);

		t_a = (100 * (c[0] * t_r / Math.pow(2,8) + c[1] * Math.pow(2,6))) / Math.pow(2,16);

		return t_a / 100;
	}

	public static double extractBarometer(BluetoothGattCharacteristic characteristic, final int[] c) {
		// c holds the calibration coefficients

		int t_r;	// Temperature raw value from sensor
		int p_r;	// Pressure raw value from sensor
		double S;	// Interim value in calculation
		double O;	// Interim value in calculation
		double p_a; 	// Pressure actual value in unit Pascal.

		t_r = shortSignedAtOffset(characteristic, 0);
		p_r = shortUnsignedAtOffset(characteristic, 2);


		S = c[2] + c[3] * t_r / Math.pow(2,17) + ((c[4] * t_r / Math.pow(2,15)) * t_r) / Math.pow(2,19);
		O = c[5] * Math.pow(2,14) + c[6] * t_r / Math.pow(2,3) + ((c[7] * t_r / Math.pow(2,15)) * t_r) / Math.pow(2,4);
		p_a = (S * p_r + O) / Math.pow(2,14);

		//Convert pascal to in. Hg
		double p_hg = p_a * 0.000296;

		return p_hg;
	}

	/**
	 * Gyroscope, Magnetometer, Barometer, IR temperature
	 * all store 16 bit two's complement values in the awkward format
	 * LSB MSB, which cannot be directly parsed as getIntValue(FORMAT_SINT16, offset)
	 * because the bytes are stored in the "wrong" direction.
	 *
	 * This function extracts these 16 bit two's complement values.
	 * */
	private static Integer shortSignedAtOffset(BluetoothGattCharacteristic c, int offset) {
		Integer lowerByte = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
		Integer upperByte = c.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, offset + 1); // Note: interpret MSB as signed.

		return (upperByte << 8) + lowerByte;
	}

	private static Integer shortUnsignedAtOffset(BluetoothGattCharacteristic c, int offset) {
		Integer lowerByte = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
		Integer upperByte = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 1); // Note: interpret MSB as unsigned.

		return (upperByte << 8) + lowerByte;
	}
	public static String extractAccelerometerReading(final BluetoothGattCharacteristic c, int offset){
		/*
		 * The accelerometer has the range [-2g, 2g] with unit (1/64)g.
		 *
		 * To convert from unit (1/64)g to unit g we divide by 64.
		 *
		 * (g = 9.81 m/s^2)
		 *
		 * The z value is multiplied with -1 to coincide
		 * with how we have arbitrarily defined the positive y direction.
		 * (illustrated by the apps accelerometer image)
		 * */
		Integer x = c.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, offset);
		Integer y = c.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, offset+1);
		Integer z = c.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, offset+2) * -1;

		double scaledX = x / 64.0;
		double scaledY = y / 64.0;
		double scaledZ = z / 64.0;

		java.util.Date date= new java.util.Date();
		Timestamp cur_time = new Timestamp(date.getTime());
		String result = Double.toString(scaledX) +","+ Double.toString(scaledY) +","+ Double.toString(scaledZ);
		acc.add(cur_time+","+result);
		acc_values.put(cur_time, result);
		//Float  isStomp = -2.0f;
		// if (scaledY > -0.5)
		//	isStomp = detectStomp((float)scaledX,(float)scaledY, (float)scaledZ);

		/* if(isStomp!=-2.0f){
    		Log.i("stomp",new Timestamp(date.getTime())+","+ result);
        	Log.i("stomp peak",isStomp.toString());}
    	else
    		Log.i("rest",result);*/

		return result;
	}

	public static String extractGyroscopeReading(final BluetoothGattCharacteristic c, int offset){

		float y = shortSignedAtOffset(c, offset) * (500f / 65536f) * -1;
		float x = shortSignedAtOffset(c, offset+2) * (500f / 65536f);
		float z = shortSignedAtOffset(c, offset+4) * (500f / 65536f);
		String result = Float.toString(x)+","+Float.toString(y)+","+Float.toString(z);
		return result;
	}
	//This method detects a stomp
	/*static float previousValue= -1.0f;
	static Boolean found = false;
	static float stompPeak = -2.0f;*/
	/*public static Float detectStomp(float xvalue,float value, float zvalue){
    	java.util.Date date= new java.util.Date(); 	     	
    	if(xvalue <=0.20 && value >= -0.3 && zvalue >= -0.20&& !found ){
    	//	Log.i("value ", Float.toString(value));
    	//	if(value > previousValue){
    		//	previousValue=value;}
    	//	else if(value <= previousValue){
    			found  = true;
    			Log.i("start stomp",new Timestamp(date.getTime())+","+ Float.toString(xvalue)+","+Float.toString(value)+","+Float.toString(zvalue));
    		//	stompPeak = previousValue;
    		//	Log.i("stomp peak",Float.toString(stompPeak));
    		//	previousValue =-2.0f;
    		//	}
    		}else if (found){

    			if (value<=-0.3) {
    				found =false;
    				Log.i("end stomp",new Timestamp(date.getTime())+","+ Float.toString(xvalue)+","+Float.toString(value)+","+Float.toString(zvalue));
    			//	stompPeak = -2.0f;
    			}
    			else
    				Log.i("current_stomp",new Timestamp(date.getTime())+","+ Float.toString(xvalue)+","+Float.toString(value)+","+Float.toString(zvalue));
    		}
    	return stompPeak;
    }*/

/*	public static void detectStomp(ArrayList<String> acc){
		ArrayList<String> time = new ArrayList<String>();
		ArrayList<Double> x = new ArrayList<Double>();
		ArrayList<Double> y = new ArrayList<Double>();
		ArrayList<Double> z = new ArrayList<Double>();
		ArrayList<Double> sqrt = new ArrayList<Double>();
		Double root ;
		Double xvalue;
		Double yvalue;
		Double zvalue;
		String timestamp_1;
		String timestamp_2;

		for (int i = 0; i < acc.size(); i++) {
			String[] values = acc.get(i).split(",");
			time.add(i,values[0]);
			xvalue =Double.parseDouble(values[1]);
			x.add(i,xvalue);
			yvalue = Double.parseDouble(values[2]);
			y.add(i,yvalue);
			zvalue = Double.parseDouble(values[3]);
			z.add(i,zvalue);
			root = Math.sqrt((xvalue* xvalue)+ (yvalue * yvalue)+ (zvalue*zvalue));
			sqrt.add(i,root);
		}

		double previous =1;
		for (int i = 0; i < sqrt.size(); i++) {
			root = sqrt.get(i);
			if(root > 1 && !found)
				found = true;
			if(stompPeak1 == -1){
				while( previous < root && found){
					previous = root;
					stompPeak1 = previous;
					timestamp_1 = time.get(i);
				}
			}
			if(stompPeak2 == -1){
				while( previous > root && found){
					previous = root;
					stompPeak2 = previous;
				}
			}
			if(stompPeak3 == -1)
			{
				while( previous < root && found){
					previous = root;
					stompPeak3 = previous;
				}
			}
			if( stompPeak1 != -1 && stompPeak2 != -1 && stompPeak3 != -1){
			Log.i("values", )	
			}
			
		}

}
*/

}
