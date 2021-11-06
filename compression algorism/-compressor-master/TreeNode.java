//the node in Hufftree
public class TreeNode  {  
  
  
    public int frequency;  //used to store the frequency
    public byte[] value;   //used to store the symbol
	public int LR;	//used to store left right 0means left and 1 means right
    public TreeNode leftChild;   //leftchild
    public TreeNode rightChild;  //rightchild
  
  //initialize
  public TreeNode(){
	  frequency=0;
	  value=null;
	  LR=0;
	  leftChild=null;
	  rightChild=null;
  }
  //initialize and set the symbolvalue and frequency
  public TreeNode(byte b,int f){
	  frequency=f;
	  value=new byte[1];
	  value[0]=b;
	  LR=0;
	  leftChild=null;
	  rightChild=null;
  }
}  