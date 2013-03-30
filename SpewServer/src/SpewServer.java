import java.io.*;
import java.net.*;
import java.util.*;

public class SpewServer {
    public static void main(String[] args) throws IOException {
        /* Check for input */
        if (args.length != 1) {
            System.err.println("Forgot port number");
            return;
        }
        final DatagramSocket socket = new DatagramSocket(Integer.parseInt(args[0]));

        /* Start server thread */
        new Thread(new Runnable() {
            public void run() {
                InetAddress address = null;
                int port = -1;

                while (true) {
                    try {
                        // Receive packet...
                        byte[] buf = new byte[256];
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);

                        // Get sender info from packet!
                        address = packet.getAddress();
                        port = packet.getPort();

                    } catch (IOException e) {
                        e.printStackTrace();
                        socket.close();
                        return;
                    }

                    // Print out stuff...
                    if (address != null && port != -1) {
                        System.out.println(address.getHostAddress() + ": " + new Date().toString());
                    }
                }
            }
        }).start();
    }
}
