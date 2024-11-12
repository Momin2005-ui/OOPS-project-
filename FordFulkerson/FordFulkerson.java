import java.io.*;
import java.util.*;

public class FordFulkerson {

    private int[][] graph;
    private int V; // Number of vertices

    public FordFulkerson(int V) {
        this.V = V;
        this.graph = new int[V][V];
    }
    
    public static int getNumberOfNodes(String filePath) {
        int numberOfNodes = -1; 
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
            	System.out.println("Inside while"+line);
                if (line.contains("<NUMBER OF NODES>")) {
                    String[] parts = line.split(">");
                    if (parts.length > 1) {
                        String numberString = parts[1].trim(); 
                        numberOfNodes = Integer.parseInt(numberString); 
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format in dataset.");
        }
        return numberOfNodes;
    }
    
    // Read graph edges from file and create an adjacency matrix
    public void loadGraphFromFile(String fileName) throws IOException {
    		System.out.println("Opening input file: " + fileName);
    	    BufferedReader br = new BufferedReader(new FileReader(fileName));
    	    String line;
    	    
    	    
    	    while ((line = br.readLine()) != null) {
    	        
	            String[] parts = line.trim().split("\\s+");
	            
	            try {
	                
	                int u = Integer.parseInt(parts[0]);
	                
	                int v = Integer.parseInt(parts[1]);
	              
	                int capacity = (int) Float.parseFloat(parts[2]);
	                graph[u][v] = capacity;
	                
	                System.out.println("Added edge: " + u + " -> " + v + " with capacity: " + capacity);
	            } catch (NumberFormatException e) {
	                System.out.println("Skipping invalid line: " + line);
	                continue;
	            }
    	    }
    	    br.close();
    	    System.out.println("Input file " + fileName + " closed.");
    }

    // BFS to check for path with residual capacity
    private boolean bfs(int[][] residualGraph, int source, int sink, int[] parent) {
        boolean[] visited = new boolean[V];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        visited[source] = true;
        parent[source] = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < V; v++) {
                if (!visited[v] && residualGraph[u][v] > 0) {
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;

                    if (v == sink) return true;
                }
            }
        }
        return false;
    }

    // Ford-Fulkerson algorithm to find maximum flow
    public int fordFulkerson(int source, int sink) {
        int[][] residualGraph = new int[V][V];
        for (int u = 0; u < V; u++) {
            System.arraycopy(graph[u], 0, residualGraph[u], 0, V);
        }

        int[] parent = new int[V];
        int maxFlow = 0;

        while (bfs(residualGraph, source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residualGraph[u][v]);
            }

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
        }
        return maxFlow;
    }

    // Write the maximum flow result to an output file
    public void writeOutputToFile(String fileName, int maxFlow) throws IOException {
        System.out.println("Opening output file: " + fileName);
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write("The maximum number of cars that can travel from first node to last node(based on number)in the city of Chicago is"+" "+ maxFlow);
        bw.close();
        System.out.println("Output file " + fileName + " closed.");
    }

    public static void main(String[] args) {
        try {
            String store=System.getProperty("user.dir");
            String inputFileName =store+"\\OOPSproject\\FordFulkerson\\Chicago.tntp";
            System.out.println(inputFileName);
            int V = getNumberOfNodes(inputFileName); 
            if(V==-1) {
            	System.out.println("THe file either has differnet input format or does not have number of nodes specified");
            	return;
            }
            FordFulkerson ff = new FordFulkerson(V+1);
            ff.loadGraphFromFile(inputFileName);

            int source = 1;
            int sink = V - 1; 
            long startTime = System.currentTimeMillis();
            int maxFlow = ff.fordFulkerson(source, sink);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Execution time in milliseconds: " + duration);
            String outputFileName = store+"\\OOPSproject\\FordFulkerson\\output.txt";
            ff.writeOutputToFile(outputFileName, maxFlow);

            System.out. println("The maximum flow has been calculated and written to " + outputFileName);

        } catch (IOException e) {
            System.err.println("Error reading or writing file: " + e.getMessage());
        }
    }
}



