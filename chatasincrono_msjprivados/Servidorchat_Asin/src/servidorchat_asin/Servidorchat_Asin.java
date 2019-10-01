//GUERRERO CONTRERAS JESUS FERNANDO
//MATRICULA: 16440448
package servidorchat_asin;

/**
 *
 * @author FERNANDO
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

public class Servidorchat_Asin {

    private static Set<String> usuarios = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    private static HashMap<String, PrintWriter> mapa = new HashMap<String, PrintWriter>();

    public static void main(String[] args) throws Exception {
        System.out.println("El chat esta corriendo... ");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(59001)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));

            }
        }

    }

    private static class Handler implements Runnable {

        private String name;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("SUBMITNAME");
                    name = in.nextLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (usuarios) {
                        if (!usuarios.contains(name)) {
                            usuarios.add(name);
                            break;

                        }
                    }
                }
                out.println("NAMEACCEPTED " + name);
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + name + " has joined");
                }
                writers.add(out);

                synchronized (mapa) {
                    if (!mapa.containsKey(name)) {
                        mapa.put(name, out);
                    }
                }
                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    if (input.toLowerCase().startsWith("/m")) {
                        String usuario, msj;
                        if (input.length() > 5) {
                            
                            int ndecaracter_usuario = input.substring(3).indexOf(" ");
                            usuario = input.substring(3, ndecaracter_usuario + 3);
                            msj = input.substring(3).substring(ndecaracter_usuario + 1);

                            if (mapa.containsKey(usuario)) {
                                mapa.get(usuario).println("MESSAGE " + name + "(mensaje privado): " + msj);
                                mapa.get(name).println("MESSAGE " + name + "(mensaje privado): " + msj);
                            }
                        }
                        else{
                          mapa.get(name).println("MESSAGE " + name + " Comando imcompleto, porfavor especifique;"
                                  + "/m 'usernam' 'mensaje'");  
                        }
                    } else {
                        for (PrintWriter writer : writers) {
                            writer.println("MESSAGE " + name + ": " + input);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                }
                if (name != null) {
                    System.out.println("El usuario: " + name + " se a ido");
                    usuarios.remove(name);
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + " se a ido");
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

    }

}
