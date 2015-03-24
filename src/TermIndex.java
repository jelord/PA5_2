//File Name: TermIndex
//Jake Poirier and Jake Lord
//Email: Jake.Poirier55@gmail.com, jakel92@comcast.net
//Date: Fall 2014

public interface TermIndex {

     public void add(String filename, String newWord);

     public int size();

     public void delete(String word);

     public Term get(String word, Boolean printP);

}

