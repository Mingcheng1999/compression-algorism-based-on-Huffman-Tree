/* HuffDecoder.java

   Starter code for compressed file decoder. You do not have to use this
   program as a starting point if you don't want to, but your implementation
   must have the same command line interface. Do not modify the HuffFileReader
   or HuffFileWriter classes (provided separately).
   
   Mingchengwu/V00908376/2019.7.6
   (Add your name/studentID/date here)
*/

import java.io.*;


public class HuffDecoder{

    private HuffFileReader inputReader;
    private BufferedOutputStream outputFile;

    /* Basic constructor to open input and output files. */
    public HuffDecoder(String inputFilename, String outputFilename) throws FileNotFoundException {
        inputReader = new HuffFileReader(inputFilename);
        outputFile = new BufferedOutputStream(new FileOutputStream(outputFilename));
    }


    public void decode() throws IOException{
		TreeNode root=new TreeNode();//create the tree
        /* This is where actual decoding should happen. */
		
		/*my code achieves the running time of O(s + n),read the symboltable and construct the tree takes O(s), then it takes 
		O(n) to convert and decode.So the total running time is O(n+s)*/
		int i=0;
		
		/*readthesymbol takes O(s) to read each symbol and find each symbol's path*/
		HuffFileSymbol temp= inputReader.readSymbol();
		while(temp!=null){
			TreeNode tempnode=root;//set tempnode to the root
			
			for(i=0;i<temp.symbolBits.length;i++){

				if(temp.symbolBits[i]==0){//0 means go to the left
					if(tempnode.leftChild==null){//if there is no leftchild, create one and go to the left.
						tempnode.leftChild=new TreeNode();
						tempnode=tempnode.leftChild;
					}
					else{
						tempnode=tempnode.leftChild;//if there is left child just go to the left
					}
				}
				else{//1 means go to the right
					if(tempnode.rightChild==null){//if there is no rightchild, create one and go to the right
						tempnode.rightChild=new TreeNode();
						tempnode=tempnode.rightChild;
					}
					else{
						tempnode=tempnode.rightChild;//if there is rightchild just go to the right.
					}
				}
				 
			}
			//set the treenode
			tempnode.value=temp.symbol;
			//read nextone
			temp= inputReader.readSymbol();
		}
		//end of create map
		
		
		
		//read the streambit and convert tempint=-1 means stop,it takes O(n),read each n and use the n to judge left or right
		int tempint=inputReader.readStreamBit();
		TreeNode tempN=root;
		while(tempint!=-1){
			if(tempint==0){
				if(tempN.leftChild!=null&&tempN.rightChild!=null){//it is not the endnode,go to the left
					tempN=tempN.leftChild;
				}
				else{
				
					for(int j=0;j<tempN.value.length;j++){
						outputFile.write(tempN.value[j]);//if it is the end,get the symbol and write
					}
					tempN=root;//reset and go to the root
					tempN=tempN.leftChild;
				}
				
			}
			else{
				if(tempN.leftChild!=null&&tempN.rightChild!=null){//it is not the endnode,go to the right
					tempN=tempN.rightChild;
				}
				else{
					
					for(int j=0;j<tempN.value.length;j++){
						outputFile.write(tempN.value[j]);//if it is the end,get the symbol and write
					}
					tempN=root;//reset and go to the root
					tempN=tempN.rightChild;
				}
				
			}
			
			//read the next one
			tempint=inputReader.readStreamBit();
		}
		
		
		
		
		
		//this this used to Decode the last symbol, the same as one in the loop
		if(tempint==0){
				if(tempN.leftChild!=null&&tempN.rightChild!=null){//it is not the endnode,go to the left
					tempN=tempN.leftChild;
				}
				else{
					for(int j=0;j<tempN.value.length;j++){
;
						outputFile.write(tempN.value[j]);//if it is the end,get the symbol and write
					}
					tempN=root;
					tempN=tempN.leftChild;
				}
				
			}
			else{
				if(tempN.leftChild!=null&&tempN.rightChild!=null){//it is not the endnode,go to the right
					tempN=tempN.rightChild;
				}
				else{
					
					for(int j=0;j<tempN.value.length;j++){

						outputFile.write(tempN.value[j]);//if it is the end,get the symbol and write
					}
					tempN=root;
					tempN=tempN.rightChild;
				}
				
			}
			

		


		
		
		
		//done and close the file
		outputFile.close();
		
		
		
		
        /* The outputFile.write() method can be used to write individual bytes to the output file.*/
        
    }


    public static void main(String[] args) throws IOException{
        if (args.length != 2){
            System.err.println("Usage: java HuffDecoder <input file> <output file>");
            return;
        }
        String inputFilename = args[0];
        String outputFilename = args[1];

        try {
            HuffDecoder decoder = new HuffDecoder(inputFilename, outputFilename);
            decoder.decode();
        } catch (FileNotFoundException e) {
            System.err.println("Error: "+e.getMessage());
        }
    }
}
