
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class chat_1 {
    public static void main(String[] args) throws IOException {
        Host_and_Join obj = new Host_and_Join();
        Scanner sc = new Scanner(System.in);
        System.out.println("\n**To Host Network enter 1 | To Join Network enter 2 | To Quit**");
        String order=sc.nextLine();
        if(order.equals("1")){
            obj.hosting();
        }
        else if(order.equals("2")){
            obj.joining();
        }
        else{
            System.out.println("System exit");
        }

    }
}
class Host_and_Join{
    Scanner sc = new Scanner(System.in);
    void hosting(){
        int port;
        System.out.println("\n** Enter the port Number You wants to create **");
        port = sc.nextInt();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Waiting for a connection...at port: " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("User connected.");

            ChatHandler chatHandler = new ChatHandler(clientSocket);
            new Thread(chatHandler.new ReadOnly()).start();
            new Thread(chatHandler.new WriteOnly()).start();
        } catch (IOException e) {
            System.out.println("|| unexpected error happened please try again ||");
            hosting();
        }catch (IllegalArgumentException illegalArgumentException){
            System.out.println("|| The port you write is out of range please try again ||");
        }
    }void joining(){
        try{
        System.out.println("\n** enter the port number you want to join **");
        int port = sc.nextInt();
        Socket socket = new Socket("localhost", port);
        System.out.println("connected to server");
        new Thread(new readonly(socket,"Host")).start();
        new Thread(new writeonly(socket)).start();
        }
        catch (IOException | IllegalArgumentException e ){
            System.out.println("|| There is no host Available at the port you want to join try again ||");
            joining();
        }
    }
}
class ChatHandler {
    private final Socket socket;
    private boolean isRunning = true;

    ChatHandler(Socket socket) {
        this.socket = socket;
    }
    class ReadOnly implements Runnable {
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String response;
                while ((response = in.readLine()) != null) {
                    if ("quit".equalsIgnoreCase(response)) {
                        break;
                    }
                    System.out.println("User: " + response);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                closeSocket();
            }
        }
    }

    class WriteOnly implements Runnable {
        public void run() {
            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
                String message = "";
                while (isRunning) {
                    message = console.readLine();
                    if (message.equalsIgnoreCase("quit")) {
                        isRunning = false;
                        System.exit(0);
                    }
                    out.println(message);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                closeSocket();
            }
        }
    }
    private void closeSocket() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

