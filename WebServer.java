import java.net.Socket;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class WebServer {
    public static void main(String argv[]) throws Exception {

        final int port = 6789;
        final int threadPoolSize = 10;
        ServerSocket serverSocket = new ServerSocket(port);
        ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize);

        // Infinite loop to listen for incoming connections.
        try {
            while (true) {
                Socket socketClient = serverSocket.accept();
                httpRequest request = new httpRequest(socketClient);
                threadPool.execute(request);
            }
        } finally {
            serverSocket.close();
            threadPool.shutdown();
        }

    }
}

final class httpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    public httpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {

        // Opens the input and output streams.
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Reads the request line.
        String brLine = br.readLine();
        System.out.println("\n" + brLine);

        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        StringTokenizer lineSegments = new StringTokenizer(brLine);
        lineSegments.nextToken();
        String filename = lineSegments.nextToken();
        filename = "." + filename;

        // Opens the requested file and sends it to the client.
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            os.writeBytes("HTTP/1.1 200 OK" + CRLF);
            os.writeBytes("Content-type: " + contentType(filename) + CRLF);
            os.writeBytes(CRLF);
            sendBytes(fis, os);
            fis.close();
        } catch (FileNotFoundException e) {
            os.writeBytes("HTTP/1.1 404 Not Found" + CRLF);
            os.writeBytes("Content-type: text/html" + CRLF);
            os.writeBytes(CRLF);
            os.writeBytes("<HTML>" +
                    "<HEAD><TITLE>404 Not Found</TITLE></HEAD>" +
                    "<BODY><b>404</b> Not Found</BODY></HTML>");
        }

        os.close();
        br.close();
        socket.close();
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes = 0;

        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String filename) {
        // Returns the content type based on the file extension.
        if (filename.endsWith(".htm") || filename.endsWith(".html")) {
            return "text/html";
        }
        if (filename.endsWith(".gif")) {
            return "image/gif";
        }
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }
}