/* PrintHuffFile.java
   CSC 225 - Spring 2019

   A diagnostic program to print the various parts of an
   encoded Huffman file.

   B. Bird - 03/19/2019
*/

import java.io.FileNotFoundException;

public class PrintHuffFile{
    public static void main(String[] args){
        if (args.length != 1){
            System.err.printf("Usage: java PrintHuffFile <filename>\n");
            return;
        }
        HuffFileReader reader;
        try{
            reader = new HuffFileReader(args[0]);
        } catch(FileNotFoundException e){
            System.err.println("Error opening file "+args[0]);
            return;
        }

        System.out.println("Symbol table:");
        int nSymbols = 0;
        for( HuffFileSymbol sym = reader.readSymbol(); sym != null; sym = reader.readSymbol() ){
            String symbolStr = new String(sym.symbol);
            String bitStr = "";
            for(int i = 0; i < sym.symbolBits.length; i++)
                bitStr += Integer.toString(sym.symbolBits[i]);
            System.out.printf("  %s: \"%s\"\n",bitStr,symbolStr);
            nSymbols++;
        }
        System.out.printf("%d symbols total.\n",nSymbols);
        System.out.println();
        System.out.println("Bit Stream:");

        int streamLength = 0;
        int bitNum = 0;
        while (true){
            int b = reader.readStreamBit();
            streamLength++;
            if (b == -1)
                break;
            System.out.print(b);
            bitNum++;
            if (bitNum == 80){
                System.out.println();
                bitNum = 0;
            }
        }
        if (bitNum > 0)
            System.out.println();

        System.out.println();
        System.out.printf("Bitstream length: %d\n", streamLength);
        reader.close();

    }
}