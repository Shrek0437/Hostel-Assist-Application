import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class SharedFeedback {

    private static final String FILE_NAME = "feedback.dat";
    private static final int SIZE = 12; // 3 integers

    private static MappedByteBuffer buffer;
    private static FileChannel channel;

    static {
        try {
            RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw");
            channel = file.getChannel();
            buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void updateFeedback(int type) {
        try (FileLock lock = channel.lock()) {

            int good = buffer.getInt(0);
            int avg = buffer.getInt(4);
            int poor = buffer.getInt(8);

            if (type == 1) good++;
            else if (type == 2) avg++;
            else if (type == 3) poor++;

            buffer.putInt(0, good);
            buffer.putInt(4, avg);
            buffer.putInt(8, poor);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] readFeedback() {
        return new int[]{
            buffer.getInt(0),
            buffer.getInt(4),
            buffer.getInt(8)
        };
    }
}
