package net.oc_soft.io;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * output stream can switch memory buffer to file if buffer size over threshold.
 */
public class MemFileOutputStream extends OutputStream {

    /**
     * logger
     */
    static Logger LOGGER;

    /**
     * get logger
     */
    private synchronized static Logger getLogger() {
        if (LOGGER == null) {
            LOGGER = Logger.getLogger(MemFileOutputStream.class.getName());
        }
        return LOGGER;
    }



    /**
     * memory output stream
     */
    private ByteArrayOutputStream memoryOutputStream;

    /**
     * file output stream
     */
    private FileOutputStream fileOutputStream;

    /**
     * memory threshold. if memoryOutputStream size greater than this value,
     * switch writing target from memoryOutputStream to fileOutputStream.
     */
    private int memoryThreshold;

    /**
     * file output path to be written into
     */
    private Path outputPath;

    /**
     * data size
     */
    private int size;
    /**
     * construct memory file output stream.
     * @param memoryThreshold maximum memory buffer size.
     * @param outputPath output path into which data if kept data in memory
     * exceeds mamximum memory size
     */
    public
    MemFileOutputStream(int memoryThreshold,
        Path outputPath) {
        if (memoryThreshold < 0) {
            throw new IllegalArgumentException(
                "memoryThreshold must be greater equal than 0");
        }
        this.memoryThreshold = memoryThreshold;
        this.outputPath = outputPath;
        this.memoryOutputStream = new ByteArrayOutputStream();
    }



    /**
     * You get memory threshold. if memoryOutputStream size greater than
     * this value, this objeject switches writing target from
     * memoryOutputStream to fileOutputStream.
     */
    public int getMemoryThreshold() {
        return memoryThreshold;
    }

    /**
     * file output path to be written into
     */
    public Path getOutputPath() {
        return outputPath;
    }

    @Override
    public synchronized void close() throws IOException {
        if (memoryOutputStream != null) {
            memoryOutputStream.close();
        }
        if (fileOutputStream != null) {
            fileOutputStream.flush();
            fileOutputStream.close();
            fileOutputStream = null;
        } 
        this.size = -1;
        super.close();
    }


    @Override
    public synchronized void flush() throws IOException {
        if (fileOutputStream != null) {
            fileOutputStream.flush();
        }
        super.flush();
    }


    @Override
    public synchronized void
    write(int byteData) throws IOException {
        if (fileOutputStream == null) {
            if (memoryOutputStream.size() < getMemoryThreshold()) {
                memoryOutputStream.write(byteData);
            } else if (memoryOutputStream.size() == getMemoryThreshold()) {
                fileOutputStream = new FileOutputStream(
                    getOutputPath().toFile());
                memoryOutputStream.flush();
                if (memoryOutputStream.size() > 0) {
                    fileOutputStream.write(memoryOutputStream.toByteArray());
                }
                
                fileOutputStream.write(byteData);
                memoryOutputStream.close();
                memoryOutputStream = null;
            }
            size++;
        } else {
            fileOutputStream.write(byteData);
        } 
    }


    /**
     * You get true if stream has data in file path.
     * @return You get true if stream has data in file path.
     */
    public synchronized boolean isSavedIntoPath() {
        var result = fileOutputStream != null;
        return result;    
    }

    /**
     * get written data as byte array if data is not in file.
     * @return You get written data as byte array if data is not in file.
     */
    public synchronized byte[] getMemoryData() {
        byte[] result = null;
        if (memoryOutputStream != null) {
            result = memoryOutputStream.toByteArray();
        }
        return result; 
    }

    /**
     * get data size
     */
    public int getSize() {
        return size;
    }
}


// vi: se ts=4 sw=4 et:
