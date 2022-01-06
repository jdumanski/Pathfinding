package application;
import java.util.Comparator;

//comparator returns negative, 0, or positive number if the first variable is less than, equal, or greater than the second variable respectively
public class NodeComparator implements Comparator<Node>{
    @Override
    public int compare(Node n1, Node n2) {
        if(n1.getCombinedWeight()-n2.getCombinedWeight()>0) {
        	return 1;
        }
        else if(n1.getCombinedWeight()-n2.getCombinedWeight()<0) {
        	return -1;
        }
        return 0;
    }
    
   
}
