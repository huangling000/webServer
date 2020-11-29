package webServer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleHttpServer {
    private final static int TCP_PORT = 8080;

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(TCP_PORT);
        Socket socket = ss.accept();
        StringBuilder re = new StringBuilder(2048);
        int i;
        byte[] buffer = new byte[2048];
        try{
            i = socket.getInputStream().read(buffer);
        }catch(IOException e){
            e.printStackTrace();
            i = -1;
        }
        for(int j = 0;j < i;j++){
            re.append((char)buffer[j]);
        }
        String request = re.toString();
        System.out.println(request);

        BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));
        bw.write("HTTP/1.1 200 OK\n");
        bw.write("Content-Type: text/html; charset=UTF-8\n\n");
        bw.write("<html>\n" + "<head>\n" + "    <title>first page</title>\n"
                + "</head>\n" + "<body>\n" + "    <h1>Hello Web Server!</h1>\n"
                + "</body>\n" + "</html>\n");
        bw.flush();
        bw.close();

        socket.close();
        ss.close();
    }
    
}
