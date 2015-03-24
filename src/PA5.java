import java.text.DecimalFormat;

//Authors:Jake Lord and Jake Poirier
//Date: 9/21/14
//Email: jakel92@comcast.net, Jake.Poirier55@gmail.com
//Course: CS200
//File Name: PA5
//Last Modification: 12/3/14
public class PA5 {

	public static void main (String args[]){

		WebPages X = new WebPages();
		X.addPage(args[0]);

		for(int i = 0; i < X.deleteWords.size(); i++){
			X.pruneStopWords(X.deleteWords.get(i));
		}

		X.docSpecific= new double[X.files.size()];

		X.setupSpecifics();


		for(int i = 0; i < X.queries.size(); i++){
			X.bestPages(X.queries.get(i));
			
			X.simValReturn();


		}
		
		X.graph.writeDotFile(X.outputName+".dot");

	}

}
