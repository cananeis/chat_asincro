//GUERRERO CONTRERAS JESUS FERNANDO
//MATRICULA: 16440448
package clienteasin_iniciosesion;
/**
 *
 * @author FERNANDO
 */
import java.awt.BorderLayout;
import static java.awt.BorderLayout.SOUTH;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 *
 * @author FERNANDO
 */
public class Clienteasin_iniciosesion {
    
    String username,password;
    String serverAddress;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new JTextArea(16, 5);

    public Clienteasin_iniciosesion(String serverAddress) {
        this.serverAddress = serverAddress;
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }

    private String getName() {
        return JOptionPane.showInputDialog(
                frame, "Escriba un nombre de usuario",
                "Nombre de usuario",
                JOptionPane.PLAIN_MESSAGE);

    }
    
    private String getPass() {
        return JOptionPane.showInputDialog(
                frame, "Contraseña: ",
                "Ingrese contraseña",
                JOptionPane.PLAIN_MESSAGE);

    }

  
     private void run() throws IOException {
        try {
            Socket socket = new Socket(serverAddress, 59001);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("SUBMITNAME")) {
                    username=getName();
                    while(username==null || username.equals("null") || username.equals("Null") || (username.replaceAll("\\s","")).equals("")){
                        username=getName();
                    }
                    username=username.replaceAll("\\s","");
                    password=getPass();
                    while(password==null || password.equals("null") || password.equals("Null") || (password.replaceAll("\\s","")).equals("")){
                        password=getPass();
                    }
                    out.println(username);
                    out.println(password);
                } else if (line.startsWith("NAMEACCEPTED")) {
                    this.frame.setTitle("Usuario - " + line.substring(13));
                    textField.setEditable(true);
                } else if (line.startsWith("MESSAGE")){
                    messageArea.append(line.substring(8) + "\n");
                }else if (line.startsWith("ERROR")){
                    JOptionPane.showMessageDialog(null,line.substring(6) + "\n");
                }
            }
        } finally {
            frame.setVisible(false);
            frame.dispose();
        }

    }

    public static void main(String[] args) throws Exception {
         boolean v_ip = false;
        System.out.println("tamaño" + args.length);
        if (args.length != 1) {
            System.err.println("Inserte una direccion valida");
        } else if (args.length == 1) {
            String ip = args[0];
            String[] sNumeros = ip.split("\\.");
            if (sNumeros.length < 4 || sNumeros.length > 4) {
                System.out.println(ip + "  NO es una direccion IP ");
                v_ip = false;
            } else {
                v_ip = true;
            }
        }
        if (v_ip == true) {
            Clienteasin_iniciosesion client = new Clienteasin_iniciosesion(args[0]);
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setVisible(true);
            client.run();
        }

    }
}

