import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

//Authors:Jake Lord and Jake Poirier
//Date: Fall 2014
//Email: jakel92@comcast.net, Jake.Poirier55@gmail.com
//Course: CS200
//File Name: PA5
//Last Modification: Fall 2014

public class Graph {

	public int numVertices;
	public int numEdges;
	public ArrayList<Edge> edges;
	public ArrayList<Vertex> vertices;

	public Graph(int n)
	{
		edges = new ArrayList<Edge>(); 
		vertices = new ArrayList<Vertex>();
		numVertices = n;
		numEdges = 0;

	}
	public void writeDotFile(String outputFile){
		
		try {
			
			
			PrintWriter pw = new PrintWriter(new File(outputFile));
			
			pw.println("digraph program5 {");
			for(int i = 0; i < edges.size(); i++){
				
				pw.println("\"" + edges.get(i).from +"\"" + " -> " +"\"" + edges.get(i).to +"\"" + ";");
			}
			pw.println("}");
			pw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public int inDegree(String filename){

		for(int i = 0 ; i<vertices.size(); i++){
			if(vertices.get(i).name.compareTo(filename) ==0){
				return vertices.get(i).degree;
			}
		}
		return 0;
		
		
		
	}


}

