
import java.sql.Timestamp;
import java.util.ArrayList;


public class FeatureExtractor {

	ArrayList<FeatureData> featureData;
	Boolean detectedPattern = false;
	Double sumOfNorm=0.0;
	Double avgNorm=0.0;
	Integer numberOfPeaks=0;
	ArrayList<PatternTiming> patternTiming=null;
	public FeatureExtractor() {
		featureData = new ArrayList<FeatureData>();
		patternTiming = new ArrayList<PatternTiming>();
	}
	
	
	
	
	
	
	/**
	 * this method calls all the required methods and populates the FeatureData arraylist
	 * @param sensorDatas
	 */
	public void extractFeatures(ArrayList<SensorData> sensorDatas){
		
			Double tempDouble;
			Timestamp startTime;
			Timestamp endTime;
			Boolean detectPeak = false;
			PatternTiming tempPatternTiming = null;
			SensorData tempSensorData;
			
			for (int i = 0; i < sensorDatas.size(); i++) {
				tempSensorData = sensorDatas.get(i);
				tempDouble = extractNorm(sensorDatas.get(i));
				System.out.println(tempDouble+"\n");
				
				 if((tempDouble >= 1.2) && !detectedPattern){
					 startTime = sensorDatas.get(i).getTime();
					 detectedPattern = true;
					 tempPatternTiming = new PatternTiming();
					 tempPatternTiming.setStart(i);
					 System.out.println("Start :"+startTime);
				 }
				 if (detectedPattern) {
					 //logic to detect end of pattern
					 if(i>=2){
						 if( (extractNorm(sensorDatas.get(i)) >=1 && extractNorm(sensorDatas.get(i)) <=1.1) && (extractNorm(sensorDatas.get(i-1)) >=1 && extractNorm(sensorDatas.get(i-1)) <=1.1) && (extractNorm(sensorDatas.get(i-2)) >=1 && extractNorm(sensorDatas.get(i-2)) <=1.1) ){
							
							 endTime = tempSensorData.getTime();
							 detectedPattern = false;
							 tempPatternTiming.setEnd(i);
							 patternTiming.add(tempPatternTiming);
							 tempPatternTiming = null;
							 System.out.println("End : "+endTime);
						 }
						 
					 }
					 
				 }
				
			}
		
		extractFeaturesFromPatterns(sensorDatas);	
		
		printFeatureData();
		//sumOfNorm(sensorDatas);
		
		//printpatterTimining();
		
		
	}
	
//print features
	
	private void printFeatureData(){
		System.out.println("Sum of Norm  : \t" + "distance travelled : \t" + "no of peaks : \t" + "pattern time ");
		for (int i = 0; i < featureData.size(); i++) {
			FeatureData data = featureData.get(i);
			System.out.println(data.getSumOfNorm() + "\t" + data.getDistanceTravelled() + "\t" + data.getNumberOfPeaks() + "\t" + data.getPatternTime());
		}
	}
	
	private void extractFeaturesFromPatterns(ArrayList<SensorData> sensorDatas) {
		FeatureData tempFeatureData;
		for (int i = 0; i < patternTiming.size(); i++) {
			
			PatternTiming pt  = patternTiming.get(i);
			Double sum = sumOfNorm(sensorDatas, pt.getStart() , pt.getEnd());
			Double distance = distanceTravelled(sensorDatas, pt.getStart() , pt.getEnd());
			Integer peaks= detectPeak(sensorDatas, pt.getStart(), pt.getEnd());
			if (peaks>0) {
				tempFeatureData = new FeatureData();
				tempFeatureData.setDistanceTravelled(distance);
				tempFeatureData.setSumOfNorm(sum);
				tempFeatureData.setPatternTime(totalTimeinMilli(sensorDatas, pt.start, pt.end));
				tempFeatureData.setNumberOfPeaks(peaks);
				featureData.add(tempFeatureData);
			}
		}
		
	}






	// is only for debugprint patterntiming array
	public void printpatterTimining()
	{
		for (int i = 0; i < patternTiming.size(); i++) {
			System.out.println(patternTiming.get(i).getStart());
			System.out.println(patternTiming.get(i).getEnd());
		}
	}
	
	/**
	 * calculate the sum of norms
	 * @param sensorDatas 
	 */
	public Double sumOfNorm(ArrayList<SensorData> sensorDatas,Integer start, Integer end){
		
		Double sum=0.0;
			Integer lengthOfList = end - start;
			
			for (int j = 0; j < lengthOfList; j++) {
				sum +=extractNorm(sensorDatas.get(start+j));	
			}
			
		return sum;
	}
		
	
	
	
	/**
	 * This method returns the norm value as output
	 * @param sensorData
	 * @return
	 */
	public Double extractNorm(SensorData sensorData){
		
		return Math.sqrt(sensorData.getX()*sensorData.getX() + sensorData.getY()*sensorData.getY() + sensorData.getZ()*sensorData.getZ() );
		
	}
	
	
	public Integer detectPeak(ArrayList<SensorData> sensorDatas,Integer start, Integer end){
		
		Integer lengthOfList = end - start;
		Integer peakCount=0;
		for (int j = 0; j < lengthOfList; j++) {
			if (j>=1 && j<lengthOfList) {
				if ((extractNorm(sensorDatas.get(j-1)) > extractNorm(sensorDatas.get(j)) && extractNorm(sensorDatas.get(j)) > extractNorm(sensorDatas.get(j+1))) || (extractNorm(sensorDatas.get(j-1)) > extractNorm(sensorDatas.get(j)) && extractNorm(sensorDatas.get(j)) < extractNorm(sensorDatas.get(j+1))) ) {
					peakCount++;
				}
				
			}

		}
		//System.out.println(peakCount);
		return peakCount;
			
	}
	public Double distanceTravelled(ArrayList<SensorData> sensorDatas,Integer start, Integer end){
		Double distance = 0.0;
		long time=0;
		Integer lengthOfList = end - start;
		time = totalTimeinMilli(sensorDatas, start, end);
		for (int j = 0; j < lengthOfList; j++) {
			distance += extractNorm(sensorDatas.get(start+j));	
		}
		//average norm
		distance = distance/lengthOfList;
		//calculating distance
		distance = 0.5 * distance * time* time ;
		return distance;
		
	}
	
	public long totalTimeinMilli(ArrayList<SensorData> sensorDatas,Integer start, Integer end){
	
		return sensorDatas.get(end).getTime().getTime()-sensorDatas.get(start).getTime().getTime();
	}
	
}
