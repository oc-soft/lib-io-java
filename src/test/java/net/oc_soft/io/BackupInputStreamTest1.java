package net.oc_soft.io;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class BackupInputStreamTest1 {


    @Test
    void backupTest1() throws IOException {

        var sourceData = new byte[32]; 
        for (var idx = 0; idx < sourceData.length; idx++) {
            sourceData[idx] = (byte)idx;
        }

        try (var sourceInput = new ByteArrayInputStream(sourceData);
            var backupStream = new ByteArrayOutputStream();
            var testStream = 
                new BackupInputStream(sourceInput, backupStream)) {
            var readData1 = new byte[7];
            var readData2 = new byte[10]; 

            testStream.read(readData1);
            testStream.skip(3);
            testStream.read(readData2);

            var backupBuf = backupStream.toByteArray();
            assert Arrays.equals(backupBuf, 0, readData1.length,
                readData1, 0, readData1.length);

            assert Arrays.equals(backupBuf,
                readData1.length, readData1.length + readData2.length,
                readData2, 0, readData2.length);

        }
    }
}


// vi: se ts=4 sw=4 et:
