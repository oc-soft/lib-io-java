package net.oc_soft.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;


/**
 * every byte data were kept into specified output stream
 */
public class BackupInputStream extends InputStream {

    /**
     * logger
     */
    static Logger LOGGER;

    /**
     * get logger
     */
    private synchronized static Logger getLogger() {
        if (LOGGER == null) {
            LOGGER = Logger.getLogger(BackupInputStream.class.getName());
        }
        return LOGGER;
    }


    /**
     * source input stream
     */
    private InputStream inputStream;

    /**
     * backup stream
     */
    private OutputStream backupStream;

    /**
     * flag to close inputStream
     */
    private boolean closeInputStream;
    
    /**
     * flag to close backupStream
     */
    private boolean closeBackupStream;
 
    /**
     * constructor
     * @param inputStream source input stream.
     * @param backupStream backup output stream.
     * @param closeInputStream flag to close inputStream
     * @param closeBackupStream flag to close backupStream
     */
    public BackupInputStream(
        InputStream inputStream,
        OutputStream backupStream,
        boolean closeInputStream,
        boolean closeBackupStream)  {
        this.inputStream = inputStream;
        this.backupStream = backupStream;
        this.closeInputStream = closeInputStream;
        this.closeBackupStream = closeBackupStream;
    }

    /**
     * constructor. The inputStream and backupStream does not close either.
     * @param inputStream source input stream.
     * @param backupStream backup output stream.
     */
    public BackupInputStream(
        InputStream inputStream,
        OutputStream backupStream)  {
        this(inputStream, backupStream, false, false);
    }

    @Override
    public void close() throws IOException {
        if (closeInputStream) {
            inputStream.close();
        }
        if (closeBackupStream) {
            synchronized(this) {
                backupStream.close();
            }
        }
        super.close();
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }


    @Override
    public int read() throws IOException {
        int result = inputStream.read();
        if (result != -1) {
            synchronized(this) {
                if (backupStream != null) {
                    backupStream.write(result);
                }
            }
        }
        return result;
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        var result = 0l;

        var savedBackupStream = this.backupStream;
        this.backupStream = null;

        result = super.skip(n);

        this.backupStream = savedBackupStream;

        return result;
    }
}

// vi: se ts=4 sw=4 et:
