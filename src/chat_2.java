import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class chat_2 {
    public static void main(String[] args) throws IOException {
        chat_1.main(new String[0]);
    }
}
class readonly implements Runnable{
    Socket socket;
    String status;
    readonly(Socket socket,String status) {
        this.socket = socket;
        this.status = status;
    }
    public void run() {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            String response;
            while((response = in.readLine() )!=null){
                if ("quit".equalsIgnoreCase(response)) {
                    break;
                }
                System.out.println(status+": "+response);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
class writeonly implements Runnable{
    Socket socket;
    writeonly(Socket socket) {
        this.socket = socket;
    }
    public void run(){
        try(PrintWriter out = new PrintWriter(socket.getOutputStream(), true);){
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            String message = "";
            while(!message.equalsIgnoreCase("quit")){
                message = console.readLine();
                out.println(message);
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
            try {
                chat_1.main(new String[0]);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

