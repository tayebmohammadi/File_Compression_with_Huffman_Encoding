import java.util.Comparator;
import java.util.Objects;

public class BinaryTreeaComparator implements Comparator<BinaryTree<CodeTreeElement>> {
    public int compare(BinaryTree<CodeTreeElement> T1, BinaryTree<CodeTreeElement> T2){
        CodeTreeElement Node1 = T1.data;
        CodeTreeElement Node2 = T2.data;
         if(T1.data.myFrequency > T2.data.myFrequency) return 1;
         if(T1.data.myFrequency.equals(T2.data.myFrequency)) return 0;
         return -1;
    }
}
