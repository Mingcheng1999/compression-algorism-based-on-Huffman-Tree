/* HuffFileSymbol.java
   CSC 225 - Summer 2019

   B. Bird - 03/19/2019
*/

import java.util.Arrays;


public class HuffFileSymbol{
    public byte[] symbol;
    public int[] symbolBits;

    public HuffFileSymbol(byte[] symbol, int[] symbolBits){
        this.symbol = Arrays.copyOf(symbol, symbol.length);
        this.symbolBits = Arrays.copyOf(symbolBits, symbolBits.length);
    }

    //Special case for symbols of length 1 (for convenience)
    public HuffFileSymbol(byte symbol, int[] symbolBits){
        this(new byte[]{symbol}, symbolBits);
    }
}