//GUERRERO CONTRERAS JESUS FERNANDO
//MATRICULA: 16440448
package clientechat_asin;

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
public class Clientechat_Asin {

    String username;
    String serverAddress;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new JTextArea(16, 5);

    public Clientechat_Asin(String serverAddress) {
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
                frame, "choose a screen name:",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE);

    }

    public boolean validarip(String msj) {
        String ip = msj;
        String[] sNumeros = ip.split("\\.");
        boolean real = false;
        while (sNumeros.length < 4 || sNumeros.length > 4) {
            System.out.println("Error esto: " + ip + "  NO es una direccion IP ");
            return true;
        }
        if (real != true) {
            return false;
        } else {
            return true;
        }
    }

    private void run() throws IOException {
        try {
            Socket socket = new Socket(serverAddress, 59001);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("SUBMITNAME")) {
                    username = getName();
                    while (username == null || username.equals("null") || username.equals("Null") || (username.replaceAll("\\s", "")).equals("")) {
                        username = getName();
                    }
                    username = username.replaceAll("\\s", "");
                    out.println(username);
                } else if (line.startsWith("NAMEACCEPTED")) {
                    this.frame.setTitle("Chatter - " + line.substring(13));
                    textField.setEditable(true);
                } else if (line.startsWith("MESSAGE")) {
                    messageArea.append(line.substring(8) + "\n");
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
            Clientechat_Asin client = new Clientechat_Asin(args[0]);
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setVisible(true);
            client.run();
        }

    }
}
