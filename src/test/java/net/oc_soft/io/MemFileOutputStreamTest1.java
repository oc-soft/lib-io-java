
package net.oc_soft.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class MemFileOutputStreamTest1 {

    @Test
    void getMemoryBufferTest() throws IOException {
        var tmpFile = File.createTempFile("mft", null).toPath();
        try (var memFileStream = new MemFileOutputStream(10, tmpFile)) {
            var testData = new byte[] {
                (byte)1, (byte)2, (byte)3, (byte)4
            };
            memFileStream.write(testData);
            var writtenData = memFileStream.getMemoryData();
            assert Arrays.equals(writtenData, testData);

        } finally {
            tmpFile.toFile().delete();
        }
    }

    @Test
    void getFileTest() throws IOException {
        var tmpFile = File.createTempFile("mft", null).toPath();
        try (var memFileStream = new MemFileOutputStream(3, tmpFile)) {
            var testData = new byte[] {
                (byte)1, (byte)2, (byte)3, (byte)4
            };
            memFileStream.write(testData);
            memFileStream.flush();

            assert memFileStream.isSavedIntoPath();
            assert tmpFile.toFile().length() == 4;


        } finally {
            tmpFile.toFile().delete();
        }
    }
} 
// vi: se ts=4 sw=4 et:
