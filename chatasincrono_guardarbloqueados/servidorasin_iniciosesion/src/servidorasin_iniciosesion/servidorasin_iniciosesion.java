//GUERRERO CONTRERAS JESUS FERNANDO
//MATRICULA: 16440448
package servidorasin_iniciosesion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author FERNANDO
 */
public class servidorasin_iniciosesion {

    private static Set<String> usuarios = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    private static HashMap<String, PrintWriter> mapa = new HashMap<String, PrintWriter>();
    private static HashMap<String, ArrayList<String>> bloqueos = new HashMap<String, ArrayList<String>>();
    static String direccion_usuario = System.getProperty("user.home") + "\\filename.txt";

    public static void main(String[] args) throws Exception {
        System.out.println("El chat esta corriendo... ");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(59001)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));

            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

    private static class Handler implements Runnable {

        private String name;
        private String contra;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public String mayor3(String mensaje) {
            String input = mensaje;
            if (input.length() > 3) {
                int espacio = input.indexOf(" ");
                String usuario = input.substring(espacio + 1);
                return usuario;
            } else {
                return "Comando incompleto";
            }
        }

        public void subir_bloqueados() {
            try {
                File file = new File(direccion_usuario);
                // Si el archivo no existe es creado
                if (file.exists()) {
                    FileReader f = new FileReader(direccion_usuario);
                    //Hacemos la conexion al archivo a guardar con los datos
                    BufferedReader b = new BufferedReader(f);
                    String cadena;
                    //un ciclo donde a la cadena le daremos lo que tiene escribo el txt y si no es null
                    ArrayList<String> usuario123 = new ArrayList<>();
                    while ((cadena = b.readLine()) != null) {
                        //System.out.println("El archivo tiene: " + cadena);

                        int usuario = numerodc_u(cadena);
                        String n_u = nombre_u(cadena, usuario);

                        String contrayb = cadena.substring(usuario + 1);
                        int msj = contrayb.indexOf(":");
                        String contraseña = contrayb.substring(0, msj);

                        String bloqueado = contrayb.substring(msj + 1);

                        usuario123 = new ArrayList<>();
                        if (bloqueado.length() > 1) {
                            StringTokenizer tokens = new StringTokenizer(bloqueado, ",");
                            while (tokens.hasMoreTokens()) {
                                usuario123.add(tokens.nextToken());
                            }
                        }
                        if (usuario123.size() >= 1) {
                            bloqueos.put(n_u, usuario123);
                        } else {
                            System.out.println("NO TIENE BLOQUEADOS");
                        }
                    }
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }

        public int numerodc_u(String mensaje) {
            String cadena = mensaje;
            int usuario = cadena.indexOf(",");
            return usuario;
        }

        public String nombre_u(String mensaje, int carac_usuario) {
            String cadena = mensaje;
            int carac_user = carac_usuario;
            if (cadena.length() > 3) {
                String n_u = cadena.substring(0, carac_user);
                return n_u;
            } else {
                return "Comando incompleto";
            }
        }

        ;
        public void run() {
            subir_bloqueados();
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("SUBMITNAME");
                    name = in.nextLine();
                    contra = in.nextLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (usuarios) {
                        String cadena;
                        boolean bandera = false, existe = false;
                        File file = new File(direccion_usuario);
                        // Si el archivo no existe es creado
                        if (!file.exists()) {
                            file.createNewFile();
                            if (!usuarios.contains(name)) {
                                PrintWriter escritorArchivo = new PrintWriter(direccion_usuario, "UTF-8");
                                escritorArchivo.println(name + "," + contra + ":");
                                escritorArchivo.close();
                                usuarios.add(name);
                            }
                            out.println("NAMEACCEPTED " + name);
                            bandera = true;
                        } else {
                            FileReader f = new FileReader(direccion_usuario);

                            BufferedReader b = new BufferedReader(f);

                            ArrayList<String> usuario123 = new ArrayList<>();
                            while ((cadena = b.readLine()) != null) {
                                int usuario = cadena.indexOf(",");
                                String n_u = cadena.substring(0, usuario);

                                String contrayb = cadena.substring(usuario + 1);
                                int msj = contrayb.indexOf(":");
                                String contraseña = contrayb.substring(0, msj);

                                if (n_u.equals(name)) {
                                    existe = true;
                                    if (!contraseña.equals(contra)) {
                                        out.println("ERROR " + name + " contraseña incorrecta");
                                    }

                                }
                                if (n_u.equals(name) && contraseña.equals(contra)) {
                                    if (!usuarios.contains(n_u)) {
                                        usuarios.add(n_u);
                                        System.out.println("SI LO AGREGO");

                                        String bloqueado = contrayb.substring(msj + 1);

                                        if (bloqueado.length() > 1) {

                                            StringTokenizer tokens = new StringTokenizer(bloqueado, ",");

                                            usuario123 = new ArrayList<>();
                                            while (tokens.hasMoreTokens()) {
                                                usuario123.add(tokens.nextToken());
                                            }
                                        }

                                        if (usuario123.size() >= 1) {
                                            bloqueos.put(n_u, usuario123);
                                        } else {
                                            System.out.println("NO TIENE BLOQUEADOS");
                                        }
                                        bandera = true;
                                        out.println("NAMEACCEPTED " + name);
                                        break;
                                    }
                                }
                            }
                        }
                        if (bandera == true) {
                            break;
                        } else if (!usuarios.contains(name) && existe != true) {
                            System.out.println("usuario:" + name);
                            System.out.println("contraseña: " + contra);
                            usuarios.add(name);
                            if (!file.exists()) {
                                file.createNewFile();
                                PrintWriter escritorArchivo = new PrintWriter(direccion_usuario, "UTF-8");
                                escritorArchivo.println(name + "," + contra + ":");
                                escritorArchivo.close();
                            } else {
                                FileReader f = new FileReader(direccion_usuario);
                                
                                BufferedReader b = new BufferedReader(f);
                                ArrayList<String> registros = new ArrayList<>();
                                
                                while ((cadena = b.readLine()) != null) {
                                    registros.add(cadena);
                                }
                                int c = 0;
                                PrintWriter escritorArchivo = new PrintWriter(direccion_usuario, "UTF-8");
                                while (c < registros.size()) {
                                    escritorArchivo.println(registros.get(c));
                                    c++;
                                }
                                escritorArchivo.println(name + "," + contra + ":");
                                escritorArchivo.close();
                            }
                            out.println("NAMEACCEPTED " + name);
                            break;
                        }
                    }
                }
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
                            ArrayList<String> usuario_bloquear = new ArrayList<>();
                            synchronized (bloqueos) {
                                if (!bloqueos.containsKey(n_b)) {
                                    if (usuarios.contains(n_b)) {
                                        //funciona bien la primera vez que bloqueas a uno sin que este bloqueado
                                        usuario_bloquear.add(name);
                                        bloqueos.put(n_b, usuario_bloquear);
                                        mapa.get(name).println("MESSAGE " + n_b + " fue bloqueado por: " + name);
                                        
                                        String cadena;
                                       
                                        FileReader f = new FileReader(direccion_usuario);
                                        
                                        BufferedReader b = new BufferedReader(f);
                                        ArrayList<String> registros = new ArrayList<>();
                                        
                                        while ((cadena = b.readLine()) != null) {
                                            int c_u = cadena.indexOf(",");
                                            String n_u = cadena.substring(0, c_u);
                                           

                                            String contrayb = cadena.substring(c_u + 1);
                                            int msj = contrayb.indexOf(":");
                                            String contraseña = contrayb.substring(0, msj);
                                           

                                            String bloqueado = contrayb.substring(msj + 1);
                                           

                                            ArrayList<String> usuariosbloqueados = new ArrayList<>();
                                            if (n_u.equals(n_b)) {
                                                if (bloqueado.length() > 0) {
                                                    StringTokenizer tokens = new StringTokenizer(bloqueado, ",");
                                                   
                                                    String los_bloqueadores = "";
                                                    int c = 0;
                                                    usuariosbloqueados = new ArrayList<>();
                                                    while (tokens.hasMoreTokens()) {
                                                        usuariosbloqueados.add(tokens.nextToken());
                                                    }
                                                    while (c < usuariosbloqueados.size()) {
                                                        if (c == 0) {
                                                            los_bloqueadores = los_bloqueadores + usuariosbloqueados.get(c) + ",";
                                                        } else {
                                                            los_bloqueadores = los_bloqueadores + usuariosbloqueados.get(c) + ",";
                                                        }
                                                    }
                                                    registros.add(n_u + "," + contraseña + ":" + los_bloqueadores);
                                                } else {
                                                    registros.add(n_u + "," + contraseña + ":" + name + ",");
                                                }
                                            } else {
                                                registros.add(cadena);
                                            }
                                        }
                                        int c = 0;
                                        PrintWriter escritorArchivo = new PrintWriter(direccion_usuario, "UTF-8");
                                        while (c < registros.size()) {
                                            escritorArchivo.println(registros.get(c));
                                            c++;
                                        }
                                        escritorArchivo.close();
                                        //y aqui termina
                                    } else {
                                        System.out.println("El usuario que intenta bloquea no existe");
                                    }
                                } else if (bloqueos.get(n_b).contains(name)) {
                                    mapa.get(name).println("MESSAGE " + n_b + " ya fue bloqueado por: " + name);
                                } else {
                                    System.out.println("CUANDO BLOQUEA A UNO NUEVOO Y YA EXISTE EL BLOQUEADO 122121");
                                    usuario_bloquear = bloqueos.get(n_b);
                                    usuario_bloquear.add(name);
                                    bloqueos.put(n_b, usuario_bloquear);
                                    mapa.get(name).println("MESSAGE " + n_b + " fue bloqueado por: " + name);
                                    System.out.println("El usuario " + n_b + " lo bloquearon : " + bloqueos.get(n_b));

                                    String cadena;
                                    FileReader f = new FileReader(direccion_usuario);
                                    
                                    BufferedReader b = new BufferedReader(f);
                                    
                                    ArrayList<String> registros = new ArrayList<>();
                                    while ((cadena = b.readLine()) != null) {
                                        
                                        int c_usuario = cadena.indexOf(",");
                                        String n_u = cadena.substring(0, c_usuario);
                                        
                                        String contrayb = cadena.substring(c_usuario + 1);
                                        int msj = contrayb.indexOf(":");
                                        String contraseña = contrayb.substring(0, msj);

                                        String bloqueado = contrayb.substring(msj + 1);
                                        
                                        if (n_u.equals(n_b)) {
                                            //StringTokenizer tokens = new StringTokenizer(bloqueado, ",");
                                            //int nDatos = tokens.countTokens();
                                            ArrayList<String> usuario123 = bloqueos.get(n_b);
                                            String los_bloqueadores = "";
                                            int c = 0;
                                            while (c < usuario123.size()) {
                                                if (c == 0) {
                                                    los_bloqueadores = los_bloqueadores + usuario123.get(c) + ",";
                                                } else {
                                                    los_bloqueadores = los_bloqueadores + usuario123.get(c) + ",";
                                                }
                                                c++;
                                            }
                                            registros.add(n_u + "," + contraseña + ":" + los_bloqueadores);
                                        } else {
                                            registros.add(cadena);
                                        }
                                    }
                                    try {
                                        int c = 0;
                                        PrintWriter escritorArchivo = new PrintWriter(direccion_usuario, "UTF-8");
                                        while (c < registros.size()) {
                                            escritorArchivo.println(registros.get(c));
                                            c++;
                                        }
                                        escritorArchivo.close();
                                        //y aqui termina
                                    } catch (IOException ex) {
                                        System.err.println(ex);
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

                                String cadena;
                                FileReader f = new FileReader(direccion_usuario);
                                
                                BufferedReader b = new BufferedReader(f);
                                
                                ArrayList<String> registros = new ArrayList<>();
                                while ((cadena = b.readLine()) != null) {
                                   
                                    int c_usuario = cadena.indexOf(",");
                                    String n_u = cadena.substring(0, c_usuario);
                                    System.out.println("Nombre del usuario: " + n_u);

                                    
                                    String contrayb = cadena.substring(c_usuario + 1);
                                    int msj = contrayb.indexOf(":");
                                    String contraseña = contrayb.substring(0, msj);

                                    String bloqueado = contrayb.substring(msj + 1);
                                   
                                    if (n_u.equals(n_b)) {
                                        ArrayList<String> usuario123 = bloqueos.get(n_b);
                                        String los_bloqueadores = "";
                                        int c = 0;
                                        while (c < usuario123.size()) {
                                            if (c == 0) {
                                                los_bloqueadores = los_bloqueadores + usuario123.get(c) + ",";
                                            } else {
                                                los_bloqueadores = los_bloqueadores + usuario123.get(c) + ",";
                                            }
                                            c++;
                                        }
                                        registros.add(n_u + "," + contraseña + ":" + los_bloqueadores);
                                    } else {
                                        registros.add(cadena);
                                    }
                                    try {
                                        int c = 0;
                                        PrintWriter escritorArchivo = new PrintWriter(direccion_usuario, "UTF-8");
                                        while (c < registros.size()) {
                                            escritorArchivo.println(registros.get(c));
                                            c++;
                                        }
                                        escritorArchivo.close();
                                        //y aqui termina
                                    } catch (IOException ex) {
                                        System.err.println(ex);
                                    }
                                }
                            } else {
                                mapa.get(name).println("MESSAGE " + n_b + " no esta bloqueado por: " + name);
                            }
                        } else {
                            System.out.println("NO ESTA BLOQUEADO: " + n_b);
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
                                System.out.println("QUE PONE A VER " + bloqueos);
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
