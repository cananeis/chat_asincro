//GUERRERO CONTRERAS JESUS FERNANDO
//MATRICULA: 16440448
package servidorchatsin_bloqueos;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class servidorchatsin_bloqueos {

    private static Set<String> usuarios = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    private static HashMap<String, PrintWriter> mapa = new HashMap<String, PrintWriter>();
    private static HashMap<String, ArrayList<String>> bloqueos = new HashMap<String, ArrayList<String>>();
    
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

        public String mayor3(String mensaje) {
            String input = mensaje;
            if (input.length() > 3) {
                System.out.println("si entro a la clase prro: " + input);
                int espacio = input.indexOf(" ");
                System.out.println("si entro a la clase prro: " + espacio);
                String usuario = input.substring(espacio + 1);
                return usuario;
            } else {
                System.out.println("si entro a la clase prro");
                return "Comando incompleto";
            }
        }

        ;
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
                System.out.println("nombre aceptado");
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
                    } else if (input.toLowerCase().startsWith("/m")) {
                        String usuario, msj;
                        int ndecaracter_usuario;
                        if (input.length() > 5) {
                            ndecaracter_usuario = input.substring(3).indexOf(" ");
                            usuario = input.substring(3, ndecaracter_usuario + 3);
                            msj = input.substring(3).substring(ndecaracter_usuario + 1);

                            if (bloqueos.containsKey(name)) {
                                if (bloqueos.get(name).contains(usuario)) {
                                    mapa.get(name).println("MESSAGE " + name + " el usuario: " + usuario + " te tiene bloqueado");
                                } else {
                                    if (mapa.containsKey(usuario)) {
                                        mapa.get(usuario).println("MESSAGE " + name + "(mensaje privado): " + msj);
                                        mapa.get(name).println("MESSAGE " + name + "(mensaje privado): " + msj);
                                    }
                                }
                            } else {
                                if (mapa.containsKey(usuario)) {
                                    mapa.get(usuario).println("MESSAGE " + name + "(mensaje privado): " + msj);
                                    mapa.get(name).println("MESSAGE " + name + "(mensaje privado): " + msj);
                                }
                            }
                        } else {
                            mapa.get(name).println("MESSAGE " + name + " Comando imcompleto, porfavor especifique;"
                                    + "/m 'usernam' 'mensaje'");
                        }
                    } else if (input.toLowerCase().startsWith("/b")) {
                        String n_b = mayor3(input);
                        if (n_b.equals("Comando incompleto")) {
                            mapa.get(name).println("MESSAGE " + n_b + " " + n_b);
                        } else {
                            System.out.println("El usuario a bloquar: " + n_b);
                            ArrayList<String> usuario = new ArrayList<>();
                            synchronized (bloqueos) {
                                if (!bloqueos.containsKey(n_b)) {
                                    if (usuarios.contains(n_b)) {
                                        usuario.add(name);
                                        bloqueos.put(n_b, usuario);
                                        mapa.get(name).println("MESSAGE " + n_b + " fue bloqueado por: " + name);
                                        System.out.println("El usuario " + n_b + " lo bloquearon : " + bloqueos.get(n_b));
                                    } else {
                                        System.out.println("El usuario que intenta bloquea no existe");
                                    }
                                } else if (bloqueos.get(n_b).contains(name)) {
                                    mapa.get(name).println("MESSAGE " + n_b + " ya fue bloqueado por: " + name);
                                } else {
                                    if (usuarios.contains(n_b)) {
                                        usuario = bloqueos.get(n_b);
                                        usuario.add(name);
                                        bloqueos.put(n_b, usuario);
                                        mapa.get(name).println("MESSAGE " + n_b + " fue bloqueado por: " + name);
                                        System.out.println("El usuario " + n_b + " lo bloquearon : " + bloqueos.get(n_b));
                                    } else {
                                        System.out.println("El usuario que intenta bloquea no existe");
                                    }
                                }
                            }
                        }
                    } else if (input.toLowerCase().startsWith("/d")) {
                        String n_b = mayor3(input);
                        if (n_b.equals("Comando incompleto")) {
                            mapa.get(name).println("MESSAGE " + n_b + " " + n_b);
                        } else if (bloqueos.containsKey(n_b)) {
                            ArrayList<String> q_bloqueo = bloqueos.get(n_b);
                            System.out.println("SI ESTA BLOQUEADO: " + n_b);
                            if (q_bloqueo.contains(name)) {
                                q_bloqueo.remove(name);
                                bloqueos.put(n_b, q_bloqueo);
                            } else {
                                System.out.println("NO ESTA EN EL ARRAY: ");
                            }
                        } else {
                            System.out.println("NO ESTA BLOQUEADO: " + n_b);
                            System.out.println("A VEEEEER: " + bloqueos.containsKey(n_b));
                        }
                    } else {
                        if (bloqueos.containsKey(name)) {
                            ArrayList<String> el_q_bloqueo = bloqueos.get(name);
                            System.out.println("Mandara a todos menos: " + name);
                           
                            int cont = 0;
                            for (PrintWriter writer : writers) {
                                if (cont < el_q_bloqueo.size() && mapa.get(el_q_bloqueo.get(cont)) == writer) {
                                    System.out.println("usuario bloqueado");
                                    cont++;
                                } else {
                                    writer.println("MESSAGE " + name + ": " + input);
                                }
                            }

                        } else {
                            
                            for (PrintWriter writer : writers) {
                                writer.println("MESSAGE " + name + ": " + input);
                                System.out.println("mando: " + name + " con el escritor: " + writer);
                            }
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
                    mapa.remove(name);
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
