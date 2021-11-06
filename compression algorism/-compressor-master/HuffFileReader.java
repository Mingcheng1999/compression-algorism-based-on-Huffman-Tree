/* HuffFileReader.java
   CSC 225 - Summer 2019

   Class for reading compressed data files. You will need to use the methods below, but you do not have 
   to understand how the file is internally structured.

   B. Bird - 03/19/2019
*/

import java.io.*;
import java.util.Random;

public class HuffFileReader{

    private boolean readingSymbols = false;
    private BufferedInputStream inputStream;
    
    int streamByteIndex;
    byte streamByte;

    int streamBitsRead;
    int streamLength;
    
    /* constructor(filename)
    Open the file with the provided name for reading.

    The constructor may throw FileNotFoundException instances
    if the file cannot be opened for reading. 
    */
    public HuffFileReader(String filename) throws FileNotFoundException {
        readingSymbols = true;
        streamByteIndex = -1;
        streamByte = 0;
        streamBitsRead = 0;

        computeStreamLength(filename);
        
        inputStream = new BufferedInputStream(new FileInputStream(filename));

    }

    /* getStreamLength() 
       An internal function to determine the total length of the bit stream.
    */
    private void computeStreamLength(String filename){
        try{
            RandomAccessFile f = new RandomAccessFile(filename, "r");
            f.seek( f.length() - 4 );
            streamLength  = f.readUnsignedByte()<<24;
            streamLength |= f.readUnsignedByte()<<16;
            streamLength |= f.readUnsignedByte()<<8;
            streamLength |= f.readUnsignedByte();
        } catch (IOException e){
            //As a convenience, transform any IOException (which otherwise would have to be caught outside this function)
            //into a HuffFileException (which is unchecked).
            throw new HuffFileException("IOException while determining bit stream length.");
        }
    }

    /* readSymbol()
       If there are any symbols remaining in the symbol table to be read, read one
       symbol (as a HuffFileSymbol object) and return it.
       If the symbol table has already been completely read, return null.
       Once this method returns null, the bit stream can be read using the 
       readStreamBit() method below.
    */
    public HuffFileSymbol readSymbol(){
        if (!readingSymbols){
            return null;
        }
        try{
            int symbolBitsLength = inputStream.read();
            if (symbolBitsLength == -1)
                throw new HuffFileException("EOF encountered while reading bit map length.");
            if (symbolBitsLength == 0){
                //A symbol with zero bits in its encoding signals the end of the symbol table.
                readingSymbols = false;
                return null;
            }
            int symbolBitsBytes = (symbolBitsLength+7)/8;
            byte[] packedBits = new byte[symbolBitsBytes];
            if (inputStream.read(packedBits) != symbolBitsBytes){
                throw new HuffFileException("Unable to read symbol bit map of length "+symbolBitsLength);
            }
            int[] symbolBits = new int[symbolBitsLength];
            for (int i = 0; i < symbolBitsLength; i++)
                symbolBits[i] = (packedBits[i/8]>>(7-(i%8)))&1;

            int symbolLength = inputStream.read();
            if (symbolLength == -1)
                throw new HuffFileException("EOF encountered while reading symbol length.");
            byte[] symbol = new byte[symbolLength];
            if (inputStream.read(symbol) != symbolLength)
                throw new HuffFileException("Unable to read symbol of length "+symbolLength);

            return new HuffFileSymbol(symbol,symbolBits);
        } catch (IOException e){
            //As a convenience, transform any IOException (which otherwise would have to be caught outside this function)
            //into a HuffFileException (which is unchecked).
            throw new HuffFileException("IOException while reading symbol.");
        }
    }

    /* readStreamBit()
       Reads one bit from the bit stream and returns it as an int.
       If no more bits are available, the return value is -1. 
       Otherwise, the return value will always be 0 or 1.
    */
    public int readStreamBit(){
        if (readingSymbols)
            throw new HuffFileException("Attempt to read from bit stream before encountering end of symbol table.");
        if (streamBitsRead == streamLength)
            return -1;
        if (streamByteIndex < 0){
            try{
                int nextByte = inputStream.read();
                if (nextByte == -1)
                    throw new HuffFileException("Attempt to read bits past EOF.");
                streamByte = (byte)nextByte;
                streamByteIndex = 7;
            }catch(IOException e){
                throw new HuffFileException("IOException while reading bit stream.");
            }
        }
        int bit = (streamByte>>streamByteIndex)&1;
        streamByteIndex--;

        streamBitsRead++;

        return bit;
    }
    
    public void close(){
        try{
            inputStream.close();
        } catch(IOException e){
        }
    }

}
  
