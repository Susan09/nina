package edu.nd.nina.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.nd.nina.graph.TypedEdge;
import edu.nd.nina.graph.TypedSimpleGraph;
import edu.nd.nina.structs.Pair;
import edu.nd.nina.types.dblp.Author;
import edu.nd.nina.types.dblp.Paper;
import edu.nd.nina.types.dblp.Venue;
import edu.nd.nina.types.dblp.Year;

public class DBLP {

	/**
	 * . Within this archive, you will find one plain text file. Each line
	 * begins with an identifier for the data found on that line, as is
	 * described at the Arnetminer dataset website:
	 * 
	 * #* --- paperTitle 

	 * #@ --- Authors
	 * 
	 * #year ---- Year
	 * 
	 * #conf --- publication venue
	 * 
	 * #citation --- number of citations for this paper
	 * 
	 * #index ---- index id of this paper
	 * 
	 * #% ---- the id of references of this paper (there are multiple lines,
	 * with each indicating a reference)
	 * 
	 * @param dblpGraphFile
	 */
	public static void loadDBLPGraphFromFile(InputStream is, TypedSimpleGraph tsg){

		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = "";

		
		Map<Integer, Paper> idxToPaper = new TreeMap<Integer, Paper>();
		List<Pair<Integer, Integer>> refs = new ArrayList<Pair<Integer, Integer>>();
		
		Paper paper = null;
		List<Author> authors = new ArrayList<Author>();
		Year year = null;
		Venue v = null;		
		
		int i=0;
		try {
			while((line = br.readLine() ) != null){
				
				if(line.trim().isEmpty()){
					paper = null;
					authors = new ArrayList<Author>();
					year = null;
					v = null;
					
					// start fresh
				}else if(line.startsWith("#*")){
					i++;
					if(i%10000 == 0) System.out.print(".");
					if(i%100000 == 0) break;//System.out.print(".\n");
					//if(i%1000000 == 0) break;
					paper = new Paper(line.substring(2));
					
				}else if(line.startsWith("#@")){
					String[] as = line.substring(2).split(",");
					for (String author : as) { 
						Author a = new Author(author);
						authors.add(a);						
					}
				}else if(line.startsWith("#year")){
					year = new Year(line.substring(5));					
				}else if(line.startsWith("#conf")){
					v = new Venue(line.substring(5));					
				}else if(line.startsWith("#citation")){
					paper.setCitations(line.substring(9));
				}else if(line.startsWith("#index")){
					paper.setIdx(line.substring(6));
					tsg.addVertex(paper);
					for(Author a : authors){
						tsg.addVertex(a);
						tsg.addEdge(paper, a);
					}
					tsg.addVertex(year);
					tsg.addEdge(paper, year);
					tsg.addVertex(v);
					tsg.addEdge(paper, v);
					
					idxToPaper.put(paper.getIdx().hashCode(), paper);
				}else if(line.startsWith("#%")){
					// no self citations allowed
					if(paper.getIdx().equals(line.substring(2))) continue;
					refs.add(new Pair<Integer, Integer>(paper.getIdx().hashCode(), line.substring(2).hashCode()));
				}
			}
			
			for(Pair<Integer, Integer> ref : refs){
				if(idxToPaper.containsKey(ref.p2)){
					Paper x = idxToPaper.get(ref.p1);
					Paper y = idxToPaper.get(ref.p2);
					if(!x.equals(y)){
						tsg.addEdge(x, y);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args){
		File data = new File("./data/dblp/test.txt");
			
		TypedSimpleGraph tsg = new TypedSimpleGraph(TypedEdge.class);
		try {
			loadDBLPGraphFromFile(FileHandler.toInputStream(data), tsg);
			PrintStatistics.PrintGraphStatTable(tsg, "./data/dblp/testStats");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
