import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;

//Authors:Jake Lord, Anna Beckman, Jake Poirier
//Date: 9/21/14
//Email: jakel92@comcast.net
//Course: CS200
//Last Modification: 3/15/15
public class WebPages {

	//Declare All Global Variables and Constructors
	public int totalNumDocs = -1, middle;
	public HashTable table;
	public Graph graph;

	public ArrayList<Term>termIndex; 
	public ArrayList<Term>repeats = new ArrayList<Term>();
	public ArrayList<String>files, deleteWords, queryWords;
	public static ArrayList<String>queries;
	public static double[] docSpecific;
	public double[] common;
	public double queryWeights;
	public double[] docs;
	public String max = "";



	private int indexOfRepeats = 0;
	
	private int fileIndex = 0;
	private int CHECKIN = 0;
	String [] fileFoundArray;
	int PRUNEnumber=0;
	public String outputName;

	//Constructor of WebPages
	//Initializes terms,queries,specific queries, and the graph
	public WebPages(){
		this.termIndex = new ArrayList<Term>();
		this.queries = new ArrayList<String>();
		queryWords = new ArrayList<String>();

		this.graph = new Graph(0);
		

	}
	//Method that returns the files
	public String[]whichPages(String word) {
		int counter=-1;
		for(int i=0; i<termIndex.size(); i++) {
			if(word.toLowerCase().equals(termIndex.get(i).getName())) {
				counter=i;
			}	
		}
		if(counter==-1) {
			return null;
		}
		fileFoundArray=new String[termIndex.get(counter).list.size()];
		for (int i=0; i<fileFoundArray.length; i++) {
			fileFoundArray[i]=termIndex.get(counter).list.get(i).docName;
		}		
		return fileFoundArray;
	}
	
	//This method takes in the arguments file and breaks it up into usable "chunks" based on
	//the ordering of the arguments file itself
	//It is highly important that the arguments file is ordered as specs dictates or the program will crash
	public void fileReader (String filename){
		//Local Variables to the method
		CHECKIN = 1;
		files = new ArrayList<String>();
		deleteWords = new ArrayList<String>();
		File file = new File(filename);
		
		//Try/catch block for the scanner. Catches a lack of a file
		try{
			Scanner s = new Scanner(file);
			outputName = s.next();
			//Find arraysize
			HashTable.size = s.nextInt();
			table = new HashTable(HashTable.size);
			//This adds filenames into arraylist and breaks once we hit EOFs
			while (true){
				String holder = s.next();
				//	System.out.println(holder);
				totalNumDocs++;
				if(holder.equals("*EOFs*")) break;
				files.add(holder);
				graph.vertices.add(new Vertex(holder,0));
				
			}

			Collections.sort(files);
			//skip EOFs
			s.nextLine();
			//allocate prunenumber
			
			//Store words that are needed for whichPages method
			while (s.hasNextLine()){
				String line = s.nextLine();
				if(line.equals("*STOPs*")){
					break;
				}
				if (line.length()>0)
					deleteWords.add(line);

			}
			//Take in the queries
			while(s.hasNextLine()){
				String line = s.nextLine();
				queries.add(line);
			}

			for (int i = 0; i<files.size(); i++){
				addPage(files.get(i));
				fileIndex++;
				indexOfRepeats = repeats.size();
			}


		}catch (IOException e){
			System.out.println("Encountered the following error"+e);
		}
	}
	
	//This method is called within fileReader x times, where x is the number of text documents within
	//the arguments document
	public void addPage(String filename){
		if(CHECKIN==0){
			fileReader(filename);
		}else{
			File file = new File(filename);

			try{
				Scanner scan = new Scanner(file);
				String y = "";
				while (scan.hasNextLine()){
				
					y = y + scan.nextLine() + " ";
					
				}
//				System.out.println("File pased in: " + filename);

				getLinks(y, y.length(),filename);
				
					y = y.toLowerCase().replaceAll("[^\\w\\s\\<\\>\\']"," ").replaceAll("\\<.*?\\>", " ");
					
					y = y.replaceAll("[\\<\\>]", " ");
					Scanner z = new Scanner (y);

					while(z.hasNext()){
						String properTerm = z.next();
						//System.out.println(properTerm);

						Term aTerm = new Term(properTerm);
						repeats.add(aTerm);
//						System.out.println(z.toString());
					}
				
				
				scan.close();

			}catch (IOException e){
				System.out.println("Encountered the following error"+e);

			}
			try{
				//allocate();
				allocateHashTable();
			}catch(NoSuchElementException e){
			}
		}	
	



	}
	
	//Creates the links of my graph. Each vertex is to be represented by web pages
	public void getLinks(String a, int length, String filename){
		
		int vertexCheck = 0;
		int count = 0;
		String word = "";
		if(a.contains("http://")){
			
			int num = a.indexOf("http://");
			char i = 34;
			while(a.charAt(num + 7 + count) != i){
				
				word = word + a.charAt(num +7 + count);
				count++;
			}
			for(int j = 0; j < graph.vertices.size(); j++){


				if(graph.vertices.get(j).name.compareTo(word) ==0){
					graph.vertices.get(j).degree++;
					vertexCheck = 1;
				}				
			}			
			if(vertexCheck == 0) {				
				graph.vertices.add(new Vertex (word,1));
			}
			
			
			

			if(word.compareTo("") != 0){

				graph.edges.add(new Edge(filename,word));
			}
			
			String nextString = a.substring(num+7+count,a.length());
			getLinks(nextString,nextString.length(),filename);
		}
	}
	
	

	//Calls my add function of hashtable
	public void allocateHashTable(){

		for(int i = indexOfRepeats; i < repeats.size(); i++){

			table.add(files.get(fileIndex), repeats.get(i).getName());

		}
	}

	//Calls delete function of my hashtable
	public void pruneStopWords(String word){

		table.delete( word);

	}


	//Following functions are the algorithm used to determine relevance of documents to my query
	public double TFIDF(String document, Term t) {
		double TFIDF=0;
		float docFreq=0;
		for (int i=0; i<t.list.size(); i++) {
			if(t.list.get(i).getDocName().equals(document)) {
				docFreq=(float)t.list.get(i).getTermFrequency();
			}
		}
		float totalDoc=(float)t.getDocFrequency();
		TFIDF=docFreq*Math.log((float)totalNumDocs/totalDoc);
		return TFIDF;
	}

	public double QIDF(Term t) {
		double QIDF=0;

		float totalDoc=(float)t.getDocFrequency();
		QIDF=.5*(1+Math.log((float)totalNumDocs/totalDoc));
		return QIDF;
	}

	public void setupSpecifics(){

		
		double tf;
	

		for(int i = 0; i < table.size(); i++){
			if(table.termArray[i] != null ){

				if(table.termArray[i].getName().compareTo("RESERVED")!=0){


					Term foundTerm = table.termArray[i];
					//System.out.println("Getting table terms  "+ foundTerm.getName());
					for (int j = 0; j < foundTerm.list.size(); j++) {

						tf = TFIDF(foundTerm.list.get(j).getDocName(), foundTerm);

						for (int k = 0; k < files.size(); k++) {
							if (files.get(k).compareTo(foundTerm.list.get(j).docName) == 0) {

								docSpecific[k] = docSpecific[k] + tf * tf;
								
							}
						}
					}
				} 
			}
		}
	}
	
	//Binary search algorithm used by the bestPages method
	public int binarySearch(ArrayList<String> words, String processed) {
        int start = 0;
        int mid = 0;
        int end = words.size();

        while (end > start) {
            mid = (start + end) / 2;
            if (processed.compareTo(words.get(mid)) > 0) {
            	start = mid + 1;
            }
            else if (processed.compareTo(words.get(mid)) < 0) {
            	end = mid;
            }
        }
        return end;
    }

	
	public void bestPages(String query){

		common = new double[files.size()];
	
		queryWords = new ArrayList<String>();
		queryWeights = 0;
		double tf;
		double qf;

		

		Scanner scanQuery = new Scanner(query);
		while (scanQuery.hasNext()) {
			String temp = scanQuery.next();
			temp = temp.toLowerCase();

			
			queryWords.add(binarySearch(queryWords, temp), temp);
		}		

		for (int i = 0; i < queryWords.size(); i++) {

			if (table.get(queryWords.get(i), false) != null) {

				Term foundTerm = table.get(queryWords.get(i), false);
				qf = QIDF(foundTerm);
				queryWeights = queryWeights + qf * qf;

				for (int j = 0; j < foundTerm.list.size(); j++) {

					tf = TFIDF(foundTerm.list.get(j).getDocName(), foundTerm);

					for (int k = 0; k < files.size(); k++) {
						if (files.get(k).compareTo(foundTerm.list.get(j).docName) == 0) {

							common[k] = common[k] + tf * qf;
				
						}
					}
				}
			} 
		}
	}

	public void simValReturn(){
		double[] simVal = new double[files.size()];
		for (int d = 0; d < simVal.length; d++) {
			simVal[d] = common[d]
					/ (Math.sqrt(docSpecific[d]) * Math.sqrt(queryWeights));
		}
		int arrayPlace = 0;
		double max = 0;
		for (int j = 0; j < simVal.length; j++) {
			if (simVal[j] > max) {

				arrayPlace =j;
				max = simVal[j]*graph.inDegree(files.get(j));
			
			}
			
		}
		DecimalFormat fmt = new DecimalFormat("###0.00");
		System.out.print("[");
		for(int i = 0; i < queryWords.size();i++){
			System.out.print(queryWords.get(i)+" ");
			
		}if(max == 0){
			System.out.println("] "+"not found");
		}else{
			System.out.println("] in " + files.get(arrayPlace)
					+ ": " + fmt.format(max));
		}
		
		
	}
	

//	int mergeCounter1=0;
//	int mergeCounter2=0;
//
//	public ArrayList<Term> mergeSort(ArrayList<Term> a) {
//		mergeCounter1=0;
//		if (a.size() <= 1) {
//			return a;
//		}
//		ArrayList<Term> firstHalf = new ArrayList<>();
//		ArrayList<Term> secondHalf = new ArrayList<>();
//		for (int i = 0; i < a.size() / 2; i++) {
//			firstHalf.add(a.get(i));
//		}
//		for (int i = a.size() / 2; i < a.size(); i++) {
//			secondHalf.add(a.get(i));
//		}
//		return merge(mergeSort(firstHalf), mergeSort(secondHalf));
//	}
//
//	public ArrayList<Term> merge(ArrayList<Term> l1, ArrayList<Term> l2) {
//
//		if (l1.size() == 0) {
//			return l2;
//		}
//		if (l2.size() == 0) {
//			return l1;
//		}
//		ArrayList<Term> result = new ArrayList<>();
//		Term nextElement;
//		if (l1.get(0).getTotalFrequency() <= l2.get(0).getTotalFrequency()) {
//
//			nextElement = l2.get(0);
//			l2.remove(0);
//		} else {
//			nextElement = l1.get(0);
//			l1.remove(0);
//		}
//		result.add(nextElement);
//		result.addAll(merge(l1,l2));
//		mergeCounter1++;
//		return result;
//	}
//
//	public ArrayList<Term> mergeSortWords(ArrayList<Term> a) {
//		mergeCounter1=0;
//		if (a.size() <= 1) {
//			return a;
//		}
//		ArrayList<Term> firstHalf = new ArrayList<>();
//		ArrayList<Term> secondHalf = new ArrayList<>();
//		for (int i = 0; i < a.size() / 2; i++) {
//			firstHalf.add(a.get(i));
//		}
//		for (int i = a.size() / 2; i < a.size(); i++) {
//			secondHalf.add(a.get(i));
//		}
//		return mergeWords(mergeSortWords(firstHalf), mergeSortWords(secondHalf));
//	}
//
//	public ArrayList<Term> mergeWords(ArrayList<Term> l1, ArrayList<Term> l2) {
//
//		if (l1.size() == 0) {
//			return l2;
//		}
//		if (l2.size() == 0) {
//			return l1;
//		}
//		ArrayList<Term> result = new ArrayList<>();
//		Term nextElement;
//		if (l1.get(0).getName().compareTo(l2.get(0).getName()) > 0) {
//			mergeCounter1++;
//			nextElement = l2.get(0);
//			l2.remove(0);
//
//		} else {
//			nextElement = l1.get(0);
//			l1.remove(0);
//
//		}
//		result.add(nextElement);
//		result.addAll(mergeWords(l1,l2));
//
//		return result;
//
//	}
//	//END OF CODE TAKEN FROM softbase.ipfw.edu



}




