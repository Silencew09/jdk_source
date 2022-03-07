package interview;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Silence_w
 */
public class MySocketServer {

    private static final int ECHO_SERVER_PORT = 6789;
    public static void main(String[] args) {
        try(ServerSocket server = new ServerSocket(ECHO_SERVER_PORT)){
            System.out.println("服务器已经启动");
            while(true){
                Socket client = server.accept();
                new Thread(new ClientHandler(client)).start();
            }
        }catch (IOException e){

        }
    }
    private static class ClientHandler implements Runnable{
        private Socket client;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter pw = new PrintWriter(client.getOutputStream())
            ){
                String msg = br.readLine();
                System.out.println("收到"+client.getInetAddress()+"发送的:"+msg);
                pw.println(msg);
                pw.flush();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
