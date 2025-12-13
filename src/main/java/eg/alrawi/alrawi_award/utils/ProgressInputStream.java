package eg.alrawi.alrawi_award.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProgressInputStream extends FilterInputStream {

    private final String fileName;
    private long totalRead = 0;

    public ProgressInputStream(InputStream in, String fileName) {
        super(in);
        this.fileName = fileName;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = super.read(b, off, len);
        if (bytesRead > 0) {
            totalRead += bytesRead;
            System.out.println("Uploading " + fileName + ": " + totalRead + " bytes read");
        }
        return bytesRead;
    }

    @Override
    public int read() throws IOException {
        int byteRead = super.read();
        if (byteRead != -1) {
            totalRead++;
            System.out.println("Uploading " + fileName + ": " + totalRead + " bytes read");
        }
        return byteRead;
    }
}
