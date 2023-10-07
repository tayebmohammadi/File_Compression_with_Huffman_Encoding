import java.io.*;
import java.util.*;
/**
 * Problem Set 3
 *
 * @author Tayeb Mohammadi
 */


public class MapAndHuffman implements Huffman{
    PriorityQueue<BinaryTree<CodeTreeElement>> theQueue = new PriorityQueue<>( new BinaryTreeaComparator()); //priority queue to create the code tree
    String PathName;
    BinaryTree<CodeTreeElement> holder;
    String codeName = "";
    public MapAndHuffman(String PathName){
        this.PathName = PathName;
    }

    public static void main(String[] args) throws IOException {
        MapAndHuffman Testing = new MapAndHuffman("USConstitution.txt");
        Map<Character, Long> map1 = Testing.countFrequencies("USConstitution.txt");
        BinaryTree<CodeTreeElement> tree1 = Testing.makeCodeTree(map1);
        Map<Character, String> compComplete = Testing.computeCodes(tree1);
        Testing.compressFile(compComplete, "USConstitution.txt", "constitutionCompressed.txt");
        Testing.decompressFile("constitutionCompressed.txt", "constitutionDecompressed.txt",tree1);

    }

    /**
     * Counting frequencies of characters and adding to a map
     * @param pathName
     */

    @Override
    public Map<Character, Long> countFrequencies(String pathName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("USConstitution.txt"));
        Map<Character, Long> mapValues = new HashMap<Character, Long>();
        int charVal;
        Long freq = 1L;
        // as long as there is something to read
        while ((charVal = reader.read()) != -1){
            char myChar = (char) charVal;
            if(!mapValues.containsKey(myChar)){ // if there is no same kind of key create another key, and start frequency
                mapValues.put(myChar, freq);
            }
            else{
                mapValues.put(myChar, mapValues.get(myChar) + 1);} // else increment the frequency

        }
        return mapValues;
    }

    /**
     * Making the codeTree
     * @param frequencies the map of characters and frequencies
     */

    @Override
    public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies) {
        for(Map.Entry<Character, Long> Record: frequencies.entrySet()){ // Loop through the records of the Map
            Character charEntry = Record.getKey();
            Long longKey = Record.getValue();
            holder = new BinaryTree<>(new CodeTreeElement(longKey, charEntry)); // create the binary tree element from the record content
            theQueue.add(holder);// add it to the queue
        }
        addToTree();
        return theQueue.poll();
    }

    /**
     * The helper function to create the tree from the queue elements (trees)
     *
     */
    public void addToTree(){
        if(theQueue.size() == 1){
            BinaryTree<CodeTreeElement> lowest = theQueue.remove(); // get the lowest from the queue
            theQueue.add(new BinaryTree<>(new CodeTreeElement(lowest.data.myFrequency, null), lowest, null)); // create the tree and add it to the queue, as a new element of the queue
        }
        else{
            while(theQueue.size() > 1){
                BinaryTree<CodeTreeElement> lowest = theQueue.remove(); // get the lowest from the queue
                BinaryTree<CodeTreeElement> secLowest = theQueue.remove(); // get the second-lowest element from the queue
                theQueue.add(new BinaryTree<>(new CodeTreeElement(lowest.data.myFrequency + secLowest.data.myFrequency, null), lowest, secLowest)); // create the tree and add it to the queue, as a new element of the queue
            }
        }


    }

    /**
     * A method to compute the codes for by tree traversal
     * @param codeTree
     */

    @Override
    public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree) {
        Map<Character, String> bitsMap = new HashMap<>();
        computeHelper(codeTree, codeName, bitsMap);
        return bitsMap;
    }
    private void computeHelper(BinaryTree<CodeTreeElement> tree, String name,Map<Character, String> map ) {
        try {
            if (tree.isLeaf()) {
                map.put(tree.data.myChar, name); // when you hit the leaf
            } else {
                if (tree.hasLeft())
                    computeHelper(tree.getLeft(), name + "0", map); // go to the left, code 0, and recurse
                if (tree.hasRight())
                    computeHelper(tree.getRight(), name + "1", map); // go to the right, code 0, and recurse
            }
        } catch (Exception e){
            System.out.println("The file is empty");
        }
    }

    /**
     * Method to compress the file text
     * @param pathName
     * @param codeMap
     * @param compressedPathName
     *
     */
    @Override
    public void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(pathName));
        BufferedBitWriter bitOutput = new BufferedBitWriter(compressedPathName);
        try{
            int charVal;
            while ((charVal = reader.read()) != -1){
                char myChar = (char) charVal;
                String code = codeMap.get(myChar); // read the Codes from the Map using the Key
                for (int i = 0; i < code.length(); i++ ) {
                    bitOutput.writeBit(code.charAt(i) == '1'); // pass the boolean to the function
                }
            }
        } catch (Exception e){
            throw new IOException(e);
        } finally {
            reader.close(); // close the files
            bitOutput.close();
        }
    }

    /**
     * Function to decompress the file into the normal text
     * @param compressedPathName
     * @param decompressedPathName
     * @param codeTree
     */

    @Override
    public void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException {
        BufferedBitReader bitInput = new BufferedBitReader(compressedPathName);
        BufferedWriter output = new BufferedWriter(new FileWriter(decompressedPathName));
        BinaryTree<CodeTreeElement> currentNode = codeTree;
        try { // handle the error
            while(bitInput.hasNext()){
                boolean bit = bitInput.readBit();
                if (currentNode !=null) { // as long as I haven't reached the leaf node
                    if (bit) { // if the bit is 1, go to the right
                        currentNode = currentNode.getRight();
                    } else{ // or go to the left
                        currentNode = currentNode.getLeft();
                    }
                    if (currentNode.isLeaf()) {
                        output.write(currentNode.getData().getChar());
                        currentNode = codeTree;
                    }
                }
            }
        } catch(Exception e){
            throw new EOFException("Error in decompressing the file");
        } finally {
            // close the files
            bitInput.close();
            output.close();
        };

    }
    }
