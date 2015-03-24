import java.util.Arrays;

//Authors:Jake Lord and Jake Poirier
//Date: Fall 2014
//Email: jakel92@comcast.net, Jake.Poirier55@gmail.com
//Course: CS200
//File Name: PA5
//Last Modification: Fall 2014

//Hashtable is implemented using quadratic probing.
public class HashTable implements TermIndex {

	public static int size, wordNum;
	public static double trigger;
	Term[] termArray, tempArray;	


	public HashTable(int size){
		this.size = size;
		termArray = new Term[size];
		trigger = 0; //Used to determine hashtable resize
		wordNum = 0; //number of words total in the hashtable

	}

	@Override
	public void add(String filename, String newWord) {

		int key = Math.abs(newWord.hashCode());
		int hk = key % size;

		int count = 0;

		trigger = (double)wordNum / (double)size;

		//Dynamic algorithm used to implement quadratic probing
			while(count < size){
				if(termArray[hk] == null || termArray[hk].getName().equals("RESERVED")){

					termArray[hk] = new Term(newWord);
					wordNum++;

					termArray[hk].incFrequency(filename);
				
					break;

				}else if(termArray[hk].getName().compareTo(newWord) ==0){
					
					
					termArray[hk].incFrequency(filename);
					break;
				}else{

					count++;
					hk = (key + count*count) %size;
				}
			}	
			
			//Trigger rebuild of hashtable at 80% capacity
			if(trigger >= 0.80){
				rebuild();
			}
		}
	

	public void rebuild(){
		

		this.size = (2*this.size)+1;
		
		tempArray = new Term[this.size];

		

		for(int i = 0; i < termArray.length; i++){
			if(termArray[i]!=null){
				if(termArray[i].getName().compareTo("RESERVED")!=0){
					rebuildAdd(termArray[i].getName(),i);
				}
			}

		}

		termArray = tempArray;

	}
	
	public void rebuildAdd(String addWord, int position){
		
		int key = Math.abs(addWord.hashCode());
		int hk = key % size;
		int count = 0;

			while(count < size){
				if(tempArray[hk] == null){

					tempArray[hk] = termArray[position];
			
					break;

		
				}else{

					count++;
					hk = (key + count*count) %size;
				}
			}	
		}
		


	@Override
	public int size() {

			return this.size;

	}

	@Override
	public void delete(String word) {

		int key = Math.abs(word.hashCode());
		int hk = key % size;

		int count = 0;


		while(count < size){
			if(termArray[hk].getName().compareTo(word) == 0 ){
				termArray[hk] = new Term("RESERVED");
				wordNum--;
				break;

			}else if(termArray[hk] == null){
				break;
			}else{
				count++;
				hk = (key + count*count) %size;
			}
		}	




	}

	//Retrieval function of my hashtable
	@Override
	public Term get(String word, Boolean printP) {

		int key = Math.abs(word.hashCode());
		int hk = key % size;

		int count = 0;

		while(count < size){
			if(termArray[hk] == null){
				return null;
		
			}else{
				if(termArray[hk].getName().compareTo(word) == 0 ){
					return termArray[hk];

				}else{
					count++;
					hk = (key + count*count) %size;
				}
			}
		}
		return null;


	}
	
	public static void main (String args[]){
		HashTable t1 = new HashTable(20);

		//    	System.out.println(Math.abs("oatmeal".hashCode()));
		t1.add("Document", "apple");
		t1.add("Document", "air");
		t1.add("Document", "cat");
		t1.add("Document", "bear");
		t1.add("Document", "dog");
		t1.add("Document", "tree");
		t1.add("Document", "snake");
		t1.add("Document", "noobs");
		t1.add("Document", "xan");
		t1.add("Document", "oatmeal");
		for(int i = 0; i < t1.size; i++){
			if(t1.termArray[i] != null){
				System.out.println(t1.termArray[i].getName());

			}else{
				System.out.println("____");
			}

		}
		System.out.println();
		System.out.println();
		System.out.println();
		//    	System.out.println(t1.get("oatmeal", false).getName());
		//
		//    	System.out.println(t1.get("noobs", false).getName());

		t1.delete("dog");

		for(int i = 0; i < t1.size; i++){
			if(t1.termArray[i] != null){
				System.out.println(t1.termArray[i].getName());

			}else{
				System.out.println("____");
			}

		}
		t1.add("Document","dog");
		t1.add("Document","dog");


		System.out.println();
		System.out.println();
		System.out.println();
		for(int i = 0; i < t1.size; i++){
			if(t1.termArray[i] != null){
				System.out.println(t1.termArray[i].getName());

			}else{
				System.out.println("____");
			}

		}

	}

}

