/* HuffFileWriter.java
   CSC 225 - Summer 2019

   Class for writing compressed data files. You will need to use the methods below, but you do not have 
   to understand how the file is internally structured.

   For information only (not needed for the assignment):
   The file format defined by the behavior of this class has the following inherent limits:
    - Each symbol may contain at most 255 characters and have at most 255 bits in its bit encoding
    - There is no limit on the number of symbols.
    - No symbol may have a zero-length bit encoding, but zero-length symbols are allowed.
    - The total number of bits in the bitstream can be at most 2^31 - 1 (i.e. Integer.MAX_VALUE).
      The code makes no effort to enforce this limit (if it is exceeded, the generated file will
      be invalid). The number of bytes of compressed data is therefore limited to 2^28 - 1

   B. Bird - 03/19/2019
*/

import java.io.*;

public class HuffFileWriter{

    private boolean writingSymbols = false;
    private BufferedOutputStream outputStream;
    
    private int streamByteIndex;
    private byte streamByte;

    //The total bit stream length (in bits)
    private int totalStreamLength;

    /* constructor(filename)
       Open the file with the provided name for writing.
       If the file already exists, its contents will be erased.

       The constructor may throw FileNotFoundException instances
       if the file cannot be opened for writing. 
    */
    public HuffFileWriter(String filename) throws FileNotFoundException {
        writingSymbols = true;
        outputStream = new BufferedOutputStream(new FileOutputStream(filename));
        streamByteIndex = 7;
        streamByte = 0;

        totalStreamLength = 0;
    }

    /* writeSymbol(sym)
       Write the symbol provided to the symbol table. Note that symbols cannot be 
       written after finalizeSymbols() has been called.
    */
    public void writeSymbol(HuffFileSymbol sym){
        if (!writingSymbols){
            //It is considered an error to write symbols after calling finalizeSymbols
            throw new HuffFileException("Attempt to write symbols after finalizing symbol table.");
        }
        byte[] symbol = sym.symbol;
        int[] symbolBits = sym.symbolBits;
        if (symbolBits.length == 0)
            throw new HuffFileException(String.format("Symbol %s has zero-length bit mapping.",new String(symbol)));
        if (symbolBits.length > 255)
            throw new HuffFileException(String.format("Symbol %s has too many bits (%d) in its mapping.",new String(symbol), symbolBits.length));
        if (symbol.length > 255)
            throw new HuffFileException(String.format("Symbol %s is too long (%d bytes).", new String(symbol), symbol.length));
        for(int b: symbolBits){
            if (b != 0 && b != 1)
                throw new HuffFileException(String.format("Invalid bit value %d in bit mapping for symbol %s",b, new String(symbol)));
        }
        //Write a symbol record:
        // - bit sequence length in bits (1 byte) - Must be greater than zero
        // - bit sequence (packed into bytes). If the sequence length is L, the number bytes will be ceil(L/8)
        // - symbol length in bytes (1 byte)
        // - symbol bytes
        try{
            outputStream.write(symbolBits.length);
            byte[] packedBits = new byte[(symbolBits.length+7)/8];
            for(int i = 0; i < symbolBits.length; i++)
                packedBits[i/8] |= (symbolBits[i]&1)<<(7-(i%8));
            outputStream.write(packedBits);
            outputStream.write(symbol.length);
            if (symbol.length > 0)
                outputStream.write(symbol);
        } catch (IOException e){
            //As a convenience, transform any IOException (which otherwise would have to be caught outside this function)
            //into a HuffFileException (which is unchecked).
            throw new HuffFileException("IOException while writing symbol.");
        }
    }

    /* finalizeSymbols()
       Finish the symbol table and prepare to write the bit stream. After calling
       this, no more symbols can be written to the file.
    */
    public void finalizeSymbols(){
        try{
            outputStream.write(0);
            writingSymbols = false;
        } catch(IOException e){
            throw new HuffFileException("IOException while finalizing symbol table.");
        }
    }

    /* writeStreamBit(bit)
       Given a single 0/1 bit (stored as an int), write the bit to the file's bit stream.
       Note that stream bits cannot be written until after the symbol table is finished
       and finalizeSymbols() has been called.
    */
    public void writeStreamBit(int bit) {
        if (writingSymbols){
            //It is considered an error to write to the bitstream before calling finalizeSymbols
            throw new HuffFileException("Attempt to write to bitstream before finalizing symbol table.");
        }
        if (bit != 0 && bit != 1){
            throw new HuffFileException(String.format("Invalid bit value %d.",bit));
        }

        totalStreamLength++;

        streamByte |= (bit&1)<<streamByteIndex;
        streamByteIndex--;
        if (streamByteIndex < 0){
            try{
                outputStream.write(streamByte);
            } catch(IOException e){
                throw new HuffFileException("IOException while writing stream bits.");
            }
            streamByte = 0;
            streamByteIndex = 7;
        }
    }
    
    /* close()
       Finish writing the file and close the output stream. If this is not called,
       the file will likely not be valid.
    */
    public void close(){
        try{
            if (streamByteIndex < 7){
                outputStream.write(streamByte);
            }
        } catch(IOException e){
            throw new HuffFileException("IOException while writing final stream bits.");
        }
        streamByteIndex = 7;
        streamByte = 0;

        try{
            //Now write the total stream size as a 4 byte big endian value
            outputStream.write(0xff&( totalStreamLength>>24 ));
            outputStream.write(0xff&( totalStreamLength>>16 ));
            outputStream.write(0xff&( totalStreamLength>>8 ));
            outputStream.write(0xff&( totalStreamLength ));
            outputStream.close();
        } catch(IOException e){
            throw new HuffFileException("IOException while finalizing output file.");
        }
        outputStream = null;
    }
    
}
