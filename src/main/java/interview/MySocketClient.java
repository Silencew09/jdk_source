package interview;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Silence_w
 */
public class MySocketClient {
    public static void main(String[] args) throws Exception{
        Socket client = new Socket("localhost", 6789);
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入内容: ");
        String msg = scanner.nextLine();
        scanner.close();
        PrintWriter pw = new PrintWriter(client.getOutputStream());
        pw.println(msg);
        pw.flush();
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        System.out.println(br.readLine());
        client.close();

    }
}
