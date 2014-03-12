import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class FileAnalyser {
ArrayList<SensorData> data;
ArrayList<String> yvalues;
ArrayList<String> zvalues;
ArrayList<String> timeStomps;	
public FileAnalyser() {
data = new ArrayList<SensorData>();
}
/**
 * This method takes the file and input splis and calls the sequence to methods to prepare the sensor data array list
 * @param file
 * @throws IOException
 */
public void extractData(File file) throws IOException {
	// TODO Auto-generated method stub
	BufferedReader bufferReader = new BufferedReader(new FileReader(file));
	String data;
	String[] splitdata;
	String[] tempdata;
	String[] finaldata = null;
	String temptimeStompString = null;
	String[] timestomp = null;
	while ((data=bufferReader.readLine())!=null) {
		//split data and get time and x,y,z accelerations
		splitdata = data.split("\\,",2);
		//get acceleration readings second string in split
		finaldata = splitdata;
		//get timing data
		timestomp = splitdata[0].split("\\ ",4);
		//sending data for storage and further analysis
		prepareData(finaldata,timestomp);
	}
	
	
	//after extracting data we should call feature extractor extractFeatures method
	
	new FeatureExtractor().extractFeatures(this.data);
	
	
	//to check if data is proper in the sensor data arraylist
	//printData();
	
}

/**
 * This method collects the string data from the file and converts the data to a arraly list of sensorData
 * @param finaldata
 * @param timestomp
 */

public void prepareData(String[] finaldata,String[] timestomp) {
	
	String[] values; 
	SensorData tempData = new SensorData();
	String valuesString;
		valuesString = finaldata[1];
	//split the data and add to arraylist of sensor data object
		values = valuesString.split("\\,",3);
		tempData.setX(Double.parseDouble(values[0]));
		tempData.setY(Double.parseDouble(values[1]));
		tempData.setZ(Double.parseDouble(values[2]));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
	    Date parsedDate;
		try {
			parsedDate = dateFormat.parse(timestomp[3]);
			Timestamp timestamp = new Timestamp(parsedDate.getTime());
			tempData.setTime(timestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    data.add(tempData);
	
}

//to test the data is proper in the sensor data array list

void printData(){
	for (int i = 0; i < data.size(); i++) {
	
		System.out.println("time" + data.get(i).getTime().toString() + "x :" + data.get(i).getX().toString() + "y: " + data.get(i).getY().toString() + "z: " + data.get(i).getZ().toString());
	}
}

	
}
