package cn.infocore.nio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/9 17:00
 */
public class NioClient {
    public static void main(String[] args) throws Exception {
        // 创建SocketChannel对象
        SocketChannel socketChannel = SocketChannel.open();
        // 设置模式为非阻塞
        socketChannel.configureBlocking(false);
        // 向服务器发起连接
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8899));
        Selector selector = Selector.open();
        // 此时是客户端向服务端发起连接，需要使用--SelectionKey.OP_CONNECT
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        // 循环监听服务端返回来的数据
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            for (SelectionKey key : selectionKeys) {
                // 获取到基于事件的SocketChannel
                SocketChannel client = (SocketChannel) key.channel();
                ByteBuffer byteBuffer  = ByteBuffer.allocate(1024);
                // 当客户端连接正在进行时
                if (client.isConnectionPending()) {
                    // 完成这个SocketChannel连接的过程
                    client.finishConnect();

                    byteBuffer.put((LocalDateTime.now() + " : " + client.getRemoteAddress() + " 连接成功").getBytes());
                    byteBuffer.flip();
                    client.write(byteBuffer);
                    // 用线程池开启一个线程
                    ExecutorService executorService = Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());
                    executorService.submit(() -> {
                        try {
                            // 循环获取控制台的输入信息
                            while (true){
                                InputStreamReader inputStreamReader = new InputStreamReader(System.in);
                                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                                String sendMessage = bufferedReader.readLine();

                                byteBuffer.clear();
                                byteBuffer.put(sendMessage.getBytes());
                                byteBuffer.flip();
                                client.write(byteBuffer);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    client.register(selector, SelectionKey.OP_READ);
                    selectionKeys.remove(key);
                } else if (key.isReadable()) { // 服务端发送数据过来时
                    int readMessage = 0;
                    while (true) {
                        int readLength = client.read(byteBuffer);
                        if (readLength==0){
                            break;
                        }
                        readMessage += readLength;
                    }
                    System.out.println(new String(byteBuffer.array(), 0, readMessage));
                    selectionKeys.remove(key);
                }
            }
        }
    }
}
