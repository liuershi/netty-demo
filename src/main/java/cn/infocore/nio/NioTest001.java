package cn.infocore.nio;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.SortedMap;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/10 8:55
 * @instructions 深入理解Java字符集编码
 */
public class NioTest001 {

    public static void main(String[] args) throws Exception{
        String inputFile = "NioTest001_input.txt";
        String outputFile = "NioTest001_output.txt";

        RandomAccessFile randomAccessFile_input = new RandomAccessFile(inputFile, "r");
        RandomAccessFile randomAccessFile_output = new RandomAccessFile(outputFile, "rw");

        long inputLength = new File(inputFile).length();

        FileChannel channel_input = randomAccessFile_input.getChannel();
        FileChannel channel_output = randomAccessFile_output.getChannel();

        MappedByteBuffer inputData = channel_input.map(FileChannel.MapMode.READ_ONLY, 0, inputLength);

        Charset charset = Charset.forName("iso-8859-1");
        CharsetDecoder decode = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();

        CharBuffer charBuffer = decode.decode(inputData);
        ByteBuffer outputData = encoder.encode(charBuffer);

        channel_output.write(outputData);

        randomAccessFile_input.close();
        randomAccessFile_output.close();


        System.out.println("=========================");
        SortedMap<String, Charset> stringCharsetSortedMap = Charset.availableCharsets();
        stringCharsetSortedMap.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        });
    }
}
