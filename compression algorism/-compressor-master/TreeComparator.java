import java.util.Comparator;
//a comparator used to compare the frequency for piority queue
class TreeComparator implements Comparator<TreeNode>{
	public int compare(TreeNode x, TreeNode y){
		return x.frequency-y.frequency;
	}		
}