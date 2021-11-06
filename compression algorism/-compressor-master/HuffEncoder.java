/* HuffEncoder.java

   Starter code for compressed file encoder. You do not have to use this
   program as a starting point if you don't want to, but your implementation
   must have the same command line interface. Do not modify the HuffFileReader
   or HuffFileWriter classes (provided separately).
   
   Mingcheng Wu V00908376 2019/7/6
*/

import java.io.*;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map; 
import java.util.ArrayList;
import java.util.*;

public class HuffEncoder{

    private BufferedInputStream inputFile;
    private HuffFileWriter outputWriter;

    public HuffEncoder(String inputFilename, String outputFilename) throws FileNotFoundException {
        inputFile = new BufferedInputStream(new FileInputStream(inputFilename));
        outputWriter = new HuffFileWriter(outputFilename);
    }


    public void encode() throws IOException{

        //You may want to start by reading the entire file into a list to make it easier
        //to navigate.
        LinkedList<Byte> input_bytes = new LinkedList<Byte>();
        for(int nextByte = inputFile.read(); nextByte != -1; nextByte = inputFile.read()){
            input_bytes.add((byte)nextByte);
        }
		
		//initialize the hashmap
        Map<Byte, Integer> map = new HashMap<Byte, Integer>();   
		int size=input_bytes.size();
		
		
		//fill the hash map
		for(Byte key:input_bytes){
			map.put(key, map.get(key) == null ? 1 : map.get(key) + 1);
		}
		
		
		
		//initialize the piority queue
		PriorityQueue<TreeNode> queue = new PriorityQueue<TreeNode>(new TreeComparator()); 
		
		//same the hashmap into piority queue
		if (map != null) {   
            Set<Byte> set = map.keySet();  
            for (Byte b : set) {  
               
				TreeNode node=new TreeNode(b,map.get(b));
				
				queue.add(node);
				
            }  
        }  
		
		
		
		
		//create the HuffTree
		while (queue.size() > 1) {  
            
            TreeNode n1 = queue.poll();  
            
            TreeNode n2 = queue.poll();  
             
            TreeNode newNode = new TreeNode();
			newNode.frequency=n1.frequency+n2.frequency; //the frequency of node is the sum of leftchild's and rightchild's frequency
             
            newNode.leftChild = n1;  
            newNode.rightChild = n2;  
            n1.LR = 0;  //the left one is 0
            n2.LR = 1;  //the rightone is 1
   
            queue.add(newNode);  //add the new node to the piority queue
        }  
		//since after the process there's only one node in the queue,we can use poll to get the root
		TreeNode root=queue.poll();
		
		//initialize the hashmap and int linked list(used to store the symbol)
		HashMap<Byte, int[]> hfmMap = new HashMap<Byte, int[]>(); 
		LinkedList<Integer> route = new LinkedList<Integer>();
		
		//look througth the Huff map and convert it into a Hashmap
		getHufmanCode(root, route, hfmMap);
		
		
		//
		for (byte name : hfmMap.keySet()) {
		HuffFileSymbol tempSym=new HuffFileSymbol(name,hfmMap.get(name));
		outputWriter.writeSymbol(tempSym);
		}
		//end of writing symbols
		outputWriter.finalizeSymbols();
		
		
		
		
		//read the hashmap and convert the symbol
		for(Byte key:input_bytes){
			int[] inttemp = hfmMap.get(key);
			for(int n=0;n<inttemp.length;n++){
				outputWriter.writeStreamBit(inttemp[n]);
			}
		}
		
		
		//close the file
		outputWriter.close();
		
		
	}
		//this function is used to convert the Hufftree into Hashmap,use recurtion to traverse the Huffmap
		private void getHufmanCode(TreeNode node, LinkedList<Integer> b, HashMap<Byte, int[]> hfmMap) {  
			if (node != null) {
				
				b.add(node.LR);
				
 
				if (node.leftChild == null && node.rightChild == null) { //if it is the end node,save the symbol and value into hashmap
				
					
					int last=b.size();
					//save the int linkedlist of path into int[]
					int[] temp=new int[last-1];
					for (int k=0;k<b.size();k++){
						if(k!=0){
							temp[k-1]=b.get(k);
							int length1=b.get(k);
					
					}
					}
					//convert symbol and int[] into hashmap 
					hfmMap.put(node.value[0], temp);  
					
				}  
			//call the leftchild	
            getHufmanCode(node.leftChild, b, hfmMap);  
			//call the rightchild
            getHufmanCode(node.rightChild, b, hfmMap); 
			
			//when go back, -1 
			int c=b.size();
			b.remove(c-1);
			}  
		} 
		
        //Suggested algorithm:

        //Compute the frequency of each input symbol. Since symbols are one character long,
        //you can simply iterate through input_bytes to see each symbol.
        
        //Build a prefix code for the encoding scheme (if using Huffman Coding, build a 
        //Huffman tree).
        
        //Write the symbol table to the output file

        //Call outputWriter.finalizeSymbols() to end the symbol table

        //Iterate through each input byte and determine its encode bitstring representation,
        //then write that to the output file with outputWriter.writeStreamBit()

        //Call outputWriter.close() to end the output file
		
   


    public static void main(String[] args) throws IOException{
        if (args.length != 2){
            System.err.println("Usage: java HuffEncoder <input file> <output file>");
            return;
        }
        String inputFilename = args[0];
        String outputFilename = args[1];

        try{
            HuffEncoder encoder = new HuffEncoder(inputFilename, outputFilename);
            encoder.encode();
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: "+e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: "+e.getMessage());
        }

    }
}



