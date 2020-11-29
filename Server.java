package webServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;

public class Server {
    public static void main(String[] args) throws IOException{
        //创建服务器socket,绑定端口号
        ServerSocket serverSocket = new ServerSocket(12345);
        //监听端口，等待浏览器请求，收到请求创建一个新的socket与浏览器进行连接通信，创建了一个新的TCP连接
        Socket socket = serverSocket.accept();
        //从socket端口中读取输入流
        InputStream input = socket.getInputStream();
        //将数据流转化为String
        /*
        //以下代码是有问题的，在读取socket的getinputstream（）时，read不会返回-1，所以程序一直阻塞在read方法
        服务器无法判断数据是否传输完毕，一直等待，客户端也会一直处于等待状态，进入死锁
        
        StringBuffer sb = new StringBuffer();
        byte[] bytes = new byte[2048];
        while(input.read(bytes) != -1){//表示一次读1024个字节，知道所有数据流被读取
            String str = new String(bytes,"utf-8");
            sb.append(str);
        }
        //转化为String
        String request = sb.toString();
        System.out.println(request);
        */
        
        
        StringBuilder re = new StringBuilder(2048);
        int i;
        byte[] buffer = new byte[2048];
        try{
            i = input.read(buffer);
        }catch(IOException e){
            e.printStackTrace();
            i = -1;
        }
        for(int j = 0;j < i;j++){
            re.append((char)buffer[j]);
        }
        String request = re.toString();
        System.out.println(request);
        
        //提取出url，即浏览器要访问的文件位置
        int index1,index2;
        index1 = request.indexOf(' ', 0);
        index2 = request.indexOf(' ', index1+1);
        String url = request.substring(index1+2, index2);
        System.out.println(url);
        //获取当前位置的该文件
        File directory = new File("."); 
        url = directory.getCanonicalPath()+File.separator+url;
        System.out.println(url);
        //获取端口的输出流，用于将文件内容传输出去
        OutputStream out = socket.getOutputStream();
        byte[] bt = new byte[1024];
        File file = new File(url);
        FileInputStream fis = null;
        if(file.exists()){
            try {
                StringBuilder head =new StringBuilder("HTTP/1.1 200 OK\r\n");
                head.append("Content-Type:text/html\r\n");
                StringBuilder body = new StringBuilder();

                fis = new FileInputStream(file);
                int ch = fis.read(bt,0,1024);
                while(ch != -1){
                    body.append(new String(bt,"utf-8"));
                    ch = fis.read(bt,0,1024);
                }
                head.append(String.format("Content-Length:%d\n", body.toString().getBytes().length));
                head.append("\r\n");
                out.write(head.toString().getBytes());
                out.write(body.toString().getBytes());

                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                if(fis !=null){
                    fis.close();
                }
            }
            
        }else{
            //找不到文件
             String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
     "Content-Type: text/html\r\n" +
     "Content-Length: 23\r\n" +
     "\r\n" +
     "<h1>File Not Found</h1>";
             try {
                out.write(errorMessage.getBytes());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.shutdownOutput();
        socket.close();
        serverSocket.close();
    }
}
