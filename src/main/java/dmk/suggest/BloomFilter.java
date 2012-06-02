package dmk.suggest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.BitSet;

public class BloomFilter{
	final BitSet bitSet;
	final MultiHash multiHash;
	final int numFunc;
	//static String fName = "american.uc";

	///////////////////////////////////////////////////////////////////
	//Takes a size for the bitVector and the number of hash functions to be used.
	//If the size of the bitvecotor is too small or if the number of
	//hash functions is too small then you will get too many false positives.
	///////////////////////////////////////////////////////////////////
	BloomFilter(int filterSize, int numFunc){ 
		this.bitSet = new BitSet(filterSize); 	//creates size number of bits and set them all to false
		this.numFunc = numFunc;
		this.multiHash = new MultiHash(numFunc, 0x8FFFFFFF, filterSize);
		//for(int i = 0; i < bitSet.length(); i++){
		//	System.out.println(i + ":" + bitSet.get(i));
		//}
	}

	///////////////////////////////////
	//
	///////////////////////////////////
	public static void main(String args[]){
		if(args.length != 1){
			System.out.println("Usage: java -cp . BloomFilter <file>");
			System.exit(1);
		}

		BloomFilter bf = new BloomFilter(28 * 1000000, 6);
		try{
		//load bloom filter
		FileInputStream fis = new FileInputStream(new File(args[0]));
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line;
		while((line = br.readLine()) != null){
			bf.addToFilter(line);
		}
		br.close();
		br = null;

		//test filter
		fis = new FileInputStream(new File(args[0]));
		br = new BufferedReader(new InputStreamReader(fis));
		line = null;
		int found = 0;
		int missed = 0;
		while((line = br.readLine()) != null){
			if(bf.isInFilter(line)){
				found++;
			}
			else{
				missed++;
			}
		}
		br.close();
		br = null;
		System.out.println("Correctly found: " + found + " entries. Missed " + missed + " entries");

		//test
		br = new BufferedReader(new InputStreamReader(System.in));
		boolean inFilter;
		System.out.println("Enter words to search for in filter. Enter quitApp to quit");
		System.out.println("Enter word:");
		while( ((line = br.readLine()) != null) && line.compareTo("quitApp") != 0){
			System.out.println(bf.isInFilter(line));
			System.out.println("Enter word:");
		}

		br.close();
		br = null;

		}catch(Exception e){
		 	e.printStackTrace(); 
		}
	}//end main

	////////////////////////////////////
	//add string to filter	
	////////////////////////////////////
	public void addToFilter(String str){ 
		int tmp;
		for(int i = 0; i < numFunc; i++){
			tmp = multiHash.hash(i, str);
			//System.out.println(tmp);
			bitSet.set(tmp, true);
			//System.out.println("Set bit: " + tmp);
		}	
	}

	//////////////////////////
	//returns true if string is probably in the filter
	/////////////////////////
	public boolean isInFilter(String str){ 
		boolean inFilter = false;
		int index = 0;
		for(int i = 0; i < numFunc; i++){
			index = multiHash.hash(i, str);
			inFilter = bitSet.get(index);
		}
		return inFilter; 
	}
}//end class BloomFilter
