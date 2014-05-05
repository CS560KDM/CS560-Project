package edu.umkc.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;


public class ConnectionService extends IntentService implements BluetoothAdapter.LeScanCallback{
//variables declaration
	public String data;
	WebServiceTask service = new WebServiceTask();
	LoginDataBaseAdapter loginDataBaseAdapter;

	private static final String TAG = "BluetoothGattActivity";

    private static final String DEVICE_NAME = "SensorTag";

    /* Humidity Service */
    private static final UUID HUMIDITY_SERVICE = UUID.fromString("f000aa20-0451-4000-b000-000000000000");
    private static final UUID HUMIDITY_DATA_CHAR = UUID.fromString("f000aa21-0451-4000-b000-000000000000");
    private static final UUID HUMIDITY_CONFIG_CHAR = UUID.fromString("f000aa22-0451-4000-b000-000000000000");
    /* Barometric Pressure Service */
    private static final UUID PRESSURE_SERVICE = UUID.fromString("f000aa40-0451-4000-b000-000000000000");
    private static final UUID PRESSURE_DATA_CHAR = UUID.fromString("f000aa41-0451-4000-b000-000000000000");
    private static final UUID PRESSURE_CONFIG_CHAR = UUID.fromString("f000aa42-0451-4000-b000-000000000000");
    private static final UUID PRESSURE_CAL_CHAR = UUID.fromString("f000aa43-0451-4000-b000-000000000000");
    /* Acceleromter configuration servcie */
    private static final UUID ACCELEROMETER_SERVICE = UUID.fromString("f000aa10-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_DATA_CHAR = UUID.fromString("f000aa11-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_CONFIG_CHAR = UUID.fromString("f000aa12-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_PERIOD_CHAR = UUID.fromString("f000aa13-0451-4000-b000-000000000000");

    /* Gyroscope Configuration service */
    private static final UUID GYROSCOPE_SERVICE = UUID.fromString("f000aa50-0451-4000-b000-000000000000");
    private static final UUID GYROSCOPE_DATA_CHAR = UUID.fromString("f000aa51-0451-4000-b000-000000000000");
    private static final UUID GYROSCOPE_CONFIG_CHAR = UUID.fromString("f000aa52-0451-4000-b000-000000000000");
    /* Client Configuration Descriptor */
    private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    private BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;

    private BluetoothGatt mConnectedGatt;

    private TextView mTemperature, mHumidity, mPressure,accValue,gyroValue;

    private ProgressDialog mProgress;


	
// variable declaration end
	
	public ConnectionService() {
		super("ConnectionService");
	}

	@Override
		public void onCreate() {
			// TODO Auto-generated method stub
			super.onCreate();
			loginDataBaseAdapter=new LoginDataBaseAdapter(this);
			 loginDataBaseAdapter=loginDataBaseAdapter.open();
			BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
	        mBluetoothAdapter = manager.getAdapter();
	        mDevices = new SparseArray<BluetoothDevice>();
	        /*
	         * A progress dialog will be needed while the connection process is
	         * taking place
	         */
	        mProgress = new ProgressDialog(this);
	        mProgress.setIndeterminate(true);
	        mProgress.setCancelable(false);
		}
	
	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("ok", "hanldeIntent");
		startScan();
		
	}

	private void broadCast() {
		// TODO Auto-generated method stub
		Intent intent = new Intent("com.quchen.flappycow");
		intent.putExtra("data", "3");
		sendBroadcast(intent);		
	}

	@Override
	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
		// TODO Auto-generated method stub
		Log.i("scan", "in onlescan method");
		Log.i(TAG, "New LE Device: " + device.getName() + " @ " + rssi);
        /*
         * We are looking for SensorTag devices only, so validate the name
         * that each device reports before adding it to our collection
         */
        if (DEVICE_NAME.equals(device.getName())) {
            mDevices.put(device.hashCode(), device);
            mConnectedGatt = device.connectGatt(this, false, mGattCallback);
            mBluetoothAdapter.stopLeScan(this);
            //Update the overflow menu
            //invalidateOptionsMenu();
        }
	}

    private void startScan() {
    	Log.i("start", "scan");
        if (mBluetoothAdapter.startLeScan(this)) {
			Log.i("scan", "started");
		}
        else{
        	Log.i("scan", "not started");
        }
        

        //mHandler.postDelayed(mStopRunnable, 2500);
    }
	/*
     * In this callback, we've created a bit of a state machine to enforce that only
     * one characteristic be read or written at a time until all of our sensors
     * are enabled and we are registered to get notifications.
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /* State Machine Tracking */
        private int mState = 0;

        private void reset() { mState = 0; }

        private void advance() { mState++; }

        /*
         * Send an enable command to each sensor by writing a configuration
         * characteristic.  This is specific to the SensorTag to keep power
         * low by disabling sensors you aren't using.
         */
        private void enableNextSensor(BluetoothGatt gatt) {
        	Log.i("sensor", "enable");
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                /*case 0:
                    Log.d(TAG, "Enabling pressure cal");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_CONFIG_CHAR);
                    characteristic.setValue(new byte[] {0x02});
                    break;
                case 1:
                    Log.d(TAG, "Enabling pressure");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_CONFIG_CHAR);
                    characteristic.setValue(new byte[] {0x01});
                    break;
                case 2:
                    Log.d(TAG, "Enabling humidity");
                    characteristic = gatt.getService(HUMIDITY_SERVICE)
                            .getCharacteristic(HUMIDITY_CONFIG_CHAR);
                    characteristic.setValue(new byte[] {0x01});
                    break;*/
                case 0:
                    Log.d(TAG,"Enabling accelerometer");
                    characteristic= gatt.getService(ACCELEROMETER_SERVICE)
                            .getCharacteristic(ACCELEROMETER_CONFIG_CHAR);
                    characteristic.setValue(new byte[]{0x01});
                    break;
                case 1:
                    Log.d(TAG,"Enabling accelerometer");
                    characteristic= gatt.getService(ACCELEROMETER_SERVICE)
                            .getCharacteristic(ACCELEROMETER_PERIOD_CHAR);
                    characteristic.setValue(new byte[]{(byte)10});
                    break;
                case 2:
                    Log.d(TAG,"Enabling config gyroscope");
                    characteristic= gatt.getService(GYROSCOPE_SERVICE)
                            .getCharacteristic(GYROSCOPE_CONFIG_CHAR);
                    characteristic.setValue(new byte[]{0x07});
                    break;
               /* case 3:
                    Log.d(TAG,"Enabling config gyroscope");
                    characteristic= gatt.getService(GYROSCOPE_SERVICE)
                            .getCharacteristic(GYROSCOPE_CONFIG_CHAR);
                    characteristic.setValue(new byte[]{0x01});
                    break;*/
              /*  case 3:
                    Log.d(TAG,"Enabling gyroscope");
                    characteristic= gatt.getService(GYROSCOPE_SERVICE)
                            .getCharacteristic(GYROSCOPE_DATA_CHAR);
                    characteristic.setValue(new byte[]{0x01});
                    break;*/
                default:
                    //mHandler.sendEmptyMessage(MSG_DISMISS);
                    Log.i(TAG, "All Sensors Enabled 1");
                    return;
            }

            gatt.writeCharacteristic(characteristic);
        }
        
        private void readNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                /*case 0:
                    Log.d(TAG, "Reading pressure cal");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_CAL_CHAR);
                    break;
                case 1:
                    Log.d(TAG, "Reading pressure");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_DATA_CHAR);
                    break;
                case 2:
                    Log.d(TAG, "Reading humidity");
                    characteristic = gatt.getService(HUMIDITY_SERVICE)
                            .getCharacteristic(HUMIDITY_DATA_CHAR);
                    break;*/
                case 0:
                    Log.d(TAG,"Reading accelerometer");
                    characteristic = gatt.getService(ACCELEROMETER_SERVICE)
                            .getCharacteristic(ACCELEROMETER_DATA_CHAR);
                    break;
                case 1:
                    Log.d(TAG,"Reading Gyroscope");
                    characteristic = gatt.getService(GYROSCOPE_SERVICE)
                            .getCharacteristic(GYROSCOPE_DATA_CHAR);
                    break;
               /* case 1:
                    Log.d(TAG,"Reading accelrometer");
                    characteristic = gatt.getService(ACCELEROMETER_SERVICE)
                            .getCharacteristic(ACCELEROMETER_PERIOD_CHAR);
                    break;*/
                /*case 2:
                    Log.d(TAG,"Reading Gyroscope");
                    characteristic = gatt.getService(GYROSCOPE_SERVICE)
                            .getCharacteristic(GYROSCOPE_CONFIG_CHAR);
                    break;*/
                default:
                    //mHandler.sendEmptyMessage(MSG_DISMISS);
                    Log.i(TAG, "All Sensors Enabled 2");
                    return;
            }

            gatt.readCharacteristic(characteristic);
        }
       /* * Enable notification of changes on the data characteristic for each sensor
        * by writing the ENABLE_NOTIFICATION_VALUE flag to that characteristic's
        * configuration descriptor.
        */
       private void setNotifyNextSensor(BluetoothGatt gatt) {
           BluetoothGattCharacteristic characteristic;
           switch (mState) {
               /*case 0:
                   Log.d(TAG, "Set notify pressure cal");
                   characteristic = gatt.getService(PRESSURE_SERVICE)
                           .getCharacteristic(PRESSURE_CAL_CHAR);
                   break;
               case 1:
                   Log.d(TAG, "Set notify pressure");
                   characteristic = gatt.getService(PRESSURE_SERVICE)
                           .getCharacteristic(PRESSURE_DATA_CHAR);
                   break;
               case 2:
                   Log.d(TAG, "Set notify humidity");
                   characteristic = gatt.getService(HUMIDITY_SERVICE)
                           .getCharacteristic(HUMIDITY_DATA_CHAR);
                   break;*/
               case 0:
                   Log.d(TAG,"Set notify accelerometer");
                   characteristic = gatt.getService(ACCELEROMETER_SERVICE)
                           .getCharacteristic(ACCELEROMETER_DATA_CHAR);
                   break;
               /*case 1:
                   Log.d(TAG,"Set config accelerometer");
                   characteristic = gatt.getService(ACCELEROMETER_SERVICE)
                           .getCharacteristic(ACCELEROMETER_CONFIG_CHAR);
                   break;
               case 2:
                   Log.d(TAG,"Set period accelerometer");
                   characteristic = gatt.getService(ACCELEROMETER_SERVICE)
                           .getCharacteristic(ACCELEROMETER_PERIOD_CHAR);
                   break;*/
              /* case 3:
                   Log.d(TAG,"Config gyroscope cal");
                   characteristic = gatt.getService(GYROSCOPE_SERVICE)
                           .getCharacteristic(GYROSCOPE_CONFIG_CHAR);
                   break;*/
               case 1:
                   Log.d(TAG,"Config gyroscope data");
                   characteristic = gatt.getService(GYROSCOPE_SERVICE)
                           .getCharacteristic(GYROSCOPE_DATA_CHAR);
                   break;

               default:
                  // mHandler.sendEmptyMessage(MSG_DISMISS);
                   Log.i(TAG, "All Sensors Enabled 3");
                   return;
           }

           //Enable local notifications
           gatt.setCharacteristicNotification(characteristic, true);
           //Enabled remote notifications
           BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
           desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
           gatt.writeDescriptor(desc);
       }
       @Override
       public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
           Log.d(TAG, "Connection State Change: "+status+" -> "+connectionState(newState));
           if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
               /*
                * Once successfully connected, we must next discover all the services on the
                * device before we can read and write their characteristics.
                */
               gatt.discoverServices();
               //mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Discovering Services..."));
           } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
               /*
                * If at any point we disconnect, send a message to clear the weather values
                * out of the UI
                */
              // mHandler.sendEmptyMessage(MSG_CLEAR);
           } else if (status != BluetoothGatt.GATT_SUCCESS) {
               /*
                * If there is a failure at any stage, simply disconnect
                */
               gatt.disconnect();
           }
       }

       @Override
       public void onServicesDiscovered(BluetoothGatt gatt, int status) {
           Log.d(TAG, "Services Discovered: "+status);
          // mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Enabling Sensors..."));
           /*
            * With services discovered, we are going to reset our state machine and start
            * working through the sensors we need to enable
            */
           reset();
           enableNextSensor(gatt);
       }

       @Override
       public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
           //For each read, pass the data up to the UI thread to update the display
           if (ACCELEROMETER_DATA_CHAR.equals(characteristic.getUuid())) {
        	   Log.i("sensor", "accelerometer");
               //mHandler.sendMessage(Message.obtain(null, MSG_ACCELEROMETER, characteristic));
           }
           if (GYROSCOPE_DATA_CHAR.equals(characteristic.getUuid())) {
               //mHandler.sendMessage(Message.obtain(null, MSG_GYROSCOPE, characteristic));
        	   Log.i("sensor", "gyroscope");
           }
           /*if (HUMIDITY_DATA_CHAR.equals(characteristic.getUuid())) {
               mHandler.sendMessage(Message.obtain(null, MSG_HUMIDITY, characteristic));
           }
           if (PRESSURE_DATA_CHAR.equals(characteristic.getUuid())) {
               mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE, characteristic));
           }
           if (PRESSURE_CAL_CHAR.equals(characteristic.getUuid())) {
               mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE_CAL, characteristic));
           }
*/

           //After reading the initial value, next we enable notifications
           setNotifyNextSensor(gatt);
       }

       @Override
       public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
           //After writing the enable flag, next we read the initial value
           readNextSensor(gatt);
       }

       @Override
       public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
           /*
            * After notifications are enabled, all updates from the device on characteristic
            * value changes will be posted here.  Similar to read, we hand these up to the
            * UI thread to update the display.
            */
        /*   if (HUMIDITY_DATA_CHAR.equals(characteristic.getUuid())) {
               mHandler.sendMessage(Message.obtain(null, MSG_HUMIDITY, characteristic));
           }
           if (PRESSURE_DATA_CHAR.equals(characteristic.getUuid())) {
               mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE, characteristic));
           }
           if (PRESSURE_CAL_CHAR.equals(characteristic.getUuid())) {
               mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE_CAL, characteristic));
           }*/
    	   if (ACCELEROMETER_DATA_CHAR.equals(characteristic.getUuid())) {
               //mHandler.sendMessage(Message.obtain(null, MSG_ACCELEROMETER, characteristic));
        	   //Log.i("sensor", "acceleromter changed");
        	   updateAccelerometerCals(characteristic);
           }
           /*if (GYROSCOPE_DATA_CHAR.equals(characteristic.getUuid())) {
               //mHandler.sendMessage(Message.obtain(null, MSG_GYROSCOPE, characteristic));
        	   //Log.i("sensor", "gyroscope changed");
        	   updateGyroValues(characteristic);
           }*/
          /* if (GYROSCOPE_CONFIG_CHAR.equals(characteristic.getUuid())) {
               mHandler.sendMessage(Message.obtain(null, MSG_GYROSCOPE_CAL, characteristic));
           }*/
       }

       @Override
       public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
           //Once notifications are enabled, we move to the next sensor and start over with enable
           advance();
           enableNextSensor(gatt);
       }

       @Override
       public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
           Log.d(TAG, "Remote RSSI: "+rssi);
       }

       private String connectionState(int status) {
           switch (status) {
               case BluetoothProfile.STATE_CONNECTED:
                   return "Connected";
               case BluetoothProfile.STATE_DISCONNECTED:
                   return "Disconnected";
               case BluetoothProfile.STATE_CONNECTING:
                   return "Connecting";
               case BluetoothProfile.STATE_DISCONNECTING:
                   return "Disconnecting";
               default:
                   return String.valueOf(status);
           }
       }
   };
   private void updateGyroValues(BluetoothGattCharacteristic characteristic){
       String gyroData = SensorTagData.extractGyroscopeReading(characteristic,0);
       
       
		
		//Log.i(TAG,gyroData);
   }
   DetectMotion motion = new DetectMotion();
   Float isStomp=-2.0f;
   public static Integer numberOfStomps=0;
   private void updateAccelerometerCals(BluetoothGattCharacteristic characteristic) {
	   System.out.println("INSIDE UPDATE************************************");
       //if (mPressureCals == null) return;
       //double pressure = SensorTagData.extractBarometer(characteristic, mPressureCals);
       Float[] values = SensorTagData.extractAccelerometerReading(characteristic, 0);
       String data="";
       isStomp = motion.detectStomp(values[0],values[1],values[2]);
       if(isStomp!=-2.0f){
    	numberOfStomps++;
    	loginDataBaseAdapter.updateEntry("stomp", "STOMP", String.valueOf(numberOfStomps));
    	//loginDataBaseAdapter.insertEntry("stomp", "p","girl","24","123","40","4000");
    	//loginDataBaseAdapter.insertEntry("stomp", "p","as","a","b","c","d");
    	JSONObject obj=new JSONObject();
    	try {
			obj.put("id", "stomp");
			obj.put("stomp_s", String.valueOf(numberOfStomps));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	service.execute("http://134.193.136.127:8983/solr/collection1_shard1_replica1/update/json?overwrite=true -H Content-type:application/json --data ["+obj.toString()+"]");
    	
    	System.out.println("should be value==================="+loginDataBaseAdapter.getSinlgeEntry("stomp", "STOMP"));
       Intent intent = new Intent("myproject");
       intent.putExtra("data", "stomp");
       intent.putExtra("numberOfStomps", String.valueOf(numberOfStomps));
       sendBroadcast(intent);
       }

       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
       Date now = new Date();
       String strDate = sdf.format(now);
       data = strDate +","+String.valueOf(values[0]) + "," + String.valueOf(values[1]) +"," + String.valueOf(values[2]);
       writeToFile(data);
       //Log.i(TAG,accelerationData);
       //accValue.setText(accelerationData);
      // mTemperature.setText(String.format("%.1f\u00B0C", temp));
       //mPressure.setText(String.format("%.2f", pressure));
       //mPressure.setText(temp);
   }
   private void writeToFile(String data) {
	    try {
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("config.txt", Context.MODE_PRIVATE));
	        //outputStreamWriter.write(data);
	        outputStreamWriter.append(data);
	        outputStreamWriter.close();
	        service.execute("http://10.0.2.2:8080/RESTfulWS/rest/upload/file");
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}
  
   
}

class WebServiceTask extends AsyncTask<String, String, String> {
	 
    public static final int POST_TASK = 1;

    private static final String TAG = "WebServiceTask";

    // connection timeout, in milliseconds (waiting to connect)
    private static final int CONN_TIMEOUT = 3000;
     
    // socket timeout, in milliseconds (waiting for data)
    private static final int SOCKET_TIMEOUT = 5000;
     
    private int taskType = POST_TASK;
    private Context mContext = null;
    private String processMessage = "Processing...";

    private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

    private ProgressDialog pDlg = null;

        @Override
    protected String doInBackground(String... urls) {

        String url = urls[0];
        String result = "";
        Log.i("in the asynch task", "1");
        HttpResponse response = doResponse(url);

        if (response == null) {
        	Log.i("result null", "null");
            return result;
        } else {
        	Log.i("result not null", " not null");
            try {

                result = getStringFromInputStream(response.getEntity().getContent());
                Log.i("result", result);
            } catch (IllegalStateException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);

            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }

        }

        return result;
    }

    // Establish connection and socket (data retrieval) timeouts
    private HttpParams getHttpParams() {
         
        HttpParams htpp = new BasicHttpParams();
         
        HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
        HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);
         
        return htpp;
    }
     
    private HttpResponse doResponse(String url) {
         
        // Use our connection and data timeouts as parameters for our
        // DefaultHttpClient
        HttpClient httpclient = new DefaultHttpClient(getHttpParams());

        HttpResponse response = null;

        try {
            switch (taskType) {

            case POST_TASK:
                HttpPost httppost = new HttpPost(url);
                // Add parameters
                httppost.setHeader("Content-type", "application/json");
                httppost.setHeader("Accept", "application/json");
                JSONObject obj = new JSONObject();
                obj.put("data", "");
                httppost.setEntity(new StringEntity(obj.toString(), "UTF-8"));
                response = httpclient.execute(httppost);
                break;
            }
        } catch (Exception e) {

            Log.e(TAG, e.getLocalizedMessage(), e);

        }

        return response;
    }
     
 // convert InputStream to String
 	private static String getStringFromInputStream(InputStream is) {
  
 		BufferedReader br = null;
 		StringBuilder sb = new StringBuilder();
  
 		String line;
 		try {
  
 			br = new BufferedReader(new InputStreamReader(is));
 			while ((line = br.readLine()) != null) {
 				sb.append(line);
 			}
  
 		} catch (IOException e) {
 			e.printStackTrace();
 		} finally {
 			if (br != null) {
 				try {
 					br.close();
 				} catch (IOException e) {
 					e.printStackTrace();
 				}
 			}
 		}
  
 		return sb.toString();
  
 	}
}
