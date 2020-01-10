package cn.infocore.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/8 10:24
 */
public class NioServer {

    private static final HashMap<String, SocketChannel> maps = new HashMap<>();

    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 设置模式为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 监听8899端口
        serverSocketChannel.bind(new InetSocketAddress(8899));
        Selector selector = Selector.open();
        // 将ServerSocket通道注册到选择器上
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            // 阻塞请求
            selector.select();
            // 获取准备好的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // 获取迭代器
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SocketChannel client = null;
                SelectionKey key = iterator.next();
                // 连接已准备
                if (key.isAcceptable()) {
                    // 获取已准备好的ServerSocketChannel
                    ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
                    // 获取到此时真实的SocketChannel
                    client = serverSocket.accept();
                    client.configureBlocking(false);
                    // 将真实SocketChannel注册到选择器上进行后续通讯
                    client.register(selector, SelectionKey.OP_READ);
                    // 将连接成功的客户端加入到map中
                    String uuid = UUID.randomUUID().toString();
                    maps.put(uuid, client);
                    selectionKeys.remove(key);
                } else if (key.isReadable()) {
                    client = (SocketChannel) key.channel();

                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    byteBuffer.clear();
                    int messagesLength = 0;
                    while (true) {
                        int readLength = client.read(byteBuffer);
                        if (readLength==0) {
                            break;
                        }
                        messagesLength+=readLength;
                    }
                    String message = new String(byteBuffer.array(), 0, messagesLength);
                    System.out.println(message);

                    for (Map.Entry<String, SocketChannel> entry : maps.entrySet()) {
                        if (client!=entry.getValue()) {
                            byteBuffer.clear();
                            byteBuffer.put(message.getBytes());
                            byteBuffer.flip();
                            entry.getValue().write(byteBuffer);
                        }
                    }
                    selectionKeys.remove(key);
                }
            }
        }
    }
}
