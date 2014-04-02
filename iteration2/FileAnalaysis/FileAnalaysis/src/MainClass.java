import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class MainClass {


public static void main(String[] args) {

	try{
	File file  = new File("cross");
	
	 new FileAnalyser().extractData(file);;
	
	}catch(Exception e){
		e.printStackTrace();
		//System.out.println("error");
	}
	
}

	
}
