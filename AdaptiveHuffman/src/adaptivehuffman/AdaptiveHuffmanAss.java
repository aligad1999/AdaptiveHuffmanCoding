package adaptivehuffmanass;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 * @author ALI Gad
 */
public class AdaptiveHuffmanAss {

    private JFrame frame;
    private JTextField Stext;
    private JTextField Dtext;
    public String Source;
    public String Destination;
    File file;
    public ArrayList<Node> obj = new ArrayList<Node>();
    public ArrayList<Node> pcg = new ArrayList<Node>();
    public ArrayList<Node> check = new ArrayList<Node>();
    private JTable table;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    AdaptiveHuffmanAss window = new AdaptiveHuffmanAss();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public AdaptiveHuffmanAss() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setForeground(Color.ORANGE);
        frame.getContentPane().setBackground(new Color(112, 128, 144));
        frame.getContentPane().setFont(new Font("Sitka Small", Font.BOLD | Font.ITALIC, 11));
        frame.setBackground(new Color(128, 128, 0));
        frame.getContentPane().setForeground(new Color(128, 128, 0));
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        Stext = new JTextField();
        Stext.setBackground(new Color(240, 230, 140));
        Stext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });
        Stext.setBounds(160, 53, 128, 20);
        frame.getContentPane().add(Stext);
        Stext.setColumns(10);

        JLabel lblNewLabel = new JLabel("Source Path:");
        lblNewLabel.setFont(new Font("Felix Titling", Font.BOLD, 9));
        lblNewLabel.setBounds(52, 56, 80, 14);
        frame.getContentPane().add(lblNewLabel);

        Dtext = new JTextField();
        Dtext.setBackground(new Color(240, 230, 140));
        Dtext.setBounds(160, 105, 128, 20);
        frame.getContentPane().add(Dtext);
        Dtext.setColumns(10);

        JLabel lblNewLabel_1 = new JLabel("Destination Path:");
        lblNewLabel_1.setFont(new Font("Felix Titling", Font.BOLD, 9));
        lblNewLabel_1.setBounds(41, 108, 91, 14);
        frame.getContentPane().add(lblNewLabel_1);
        
        
 //==========================Compress=========================
 
        JButton btnCompress = new JButton("Compress");
        btnCompress.setFont(new Font("Yu Gothic UI Semilight", Font.BOLD | Font.ITALIC, 11));
        btnCompress.addActionListener(new ActionListener() {
            private String buffer;

            public void actionPerformed(ActionEvent arg0) {
                Source = Stext.getText();
                Destination = Dtext.getText();
                String buffer = "", str = "";
                try {
                    file = new File(Source);
                    Scanner fread = new Scanner(file);
                    while (fread.hasNextLine()) {
                        buffer = fread.nextLine();
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                char[] chars = buffer.toCharArray();
                Node ch = new Node();
                obj.add(ch);  //TO MAKE SIZE=1
                for (int i = 0; i < chars.length; i++) // TO GET THE COUNTERS OF ALL CHAR
                {
                    str += chars[i];
                    for (int y = 0; y < obj.size(); y++) {
                        if (obj.get(y).symbol.equals(str)) //NOT FIRST TIME APPEAR
                        {
                            obj.get(y).counter++;
                            str = "";
                            break;
                        } else if (y + 1 == obj.size() && !obj.get(y).symbol.equals(str)) //1ST APPEAR
                        {
                            if (i == 0) //FOR FIRST ELEMENT ONLY
                            {
                                obj.get(y).counter++;
                                obj.get(y).symbol += str;
                                str = "";
                                break;
                            } else //FOR N=2ND TO .... ELEMENT
                            {
                                Node n = new Node();
                                n.counter++;
                                n.symbol += str;
                                obj.add(n);
                                str = "";
                                break;
                            }
                        }
                    }
                }

                pcg = (ArrayList<Node>) obj.clone();   //PCG=OBJ
                sort(pcg, 0);
                int levels = pcg.size();
                //to know how many levels require
                for (int i = 1, y = 0; levels > 2; i++, y = y + 2) //to get 1st&2nd smallest and sum 
                {
                    Node n = new Node();
                    n.symbol = pcg.get(y).symbol + pcg.get(y + 1).symbol;
                    n.counter = pcg.get(y).counter + pcg.get(y + 1).counter;
                    n.lchild = y;
                    n.rchild = y + 1;
                    pcg.add(n);
                    levels -= 2;
                    levels++;
                    sort(pcg, y);
                }
                sort(pcg, 0);
                givecode(pcg, pcg.get(pcg.size() - 1), "", "0");  //first root
                givecode(pcg, pcg.get(pcg.size() - 2), "", "1");  //second root
                for (int i = 0; i < obj.size(); i++) //To update obj from pcg
                {
                    for (int y = 0; y < pcg.size(); y++) {
                        if (obj.get(i).symbol.equals(pcg.get(y).symbol)) {
                            obj.set(i, pcg.get(y));
                            break;
                        }
                    }
                    try { //write on the file
                        BufferedWriter writer = new BufferedWriter(new FileWriter(Destination, true));
                        writer.write(obj.get(i).symbol + obj.get(i).code + " "); //write overhead
                        if (i + 1 == obj.size()) {
                            writer.write("#");  //Delimeter between code& overhead
                        }
                        writer.close();
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
                for (int i = 0; i < chars.length; i++) //write code
                {
                    for (int y = 0; y < obj.size(); y++) {
                        if (obj.get(y).symbol.equals(chars[i] + "")) {
                            try {
                                BufferedWriter writer = new BufferedWriter(new FileWriter(Destination, true));
                                writer.write(obj.get(y).code);
                                writer.close();
                            } catch (IOException e) {
                                System.out.println(e);
                            }
                            break;
                        }
                    }
                }
                JOptionPane.showMessageDialog(null, "Compressed Successfully");
            }

            public void sort(ArrayList<Node> pcg, int y) {
                for (int i = y; i < pcg.size() - 1; i++) //Bubble Sort           
                {
                    for (int j = y; j < pcg.size() - i - 1; j++) {
                        if (pcg.get(j).counter > pcg.get(j + 1).counter) {
                            Node temp = pcg.get(j);
                            pcg.set(j, pcg.get(j + 1));
                            pcg.set(j + 1, temp);
                        }
                    }
                }
            }

            public void givecode(ArrayList<Node> pcg, Node p, String pcode, String ncode) {
                p.code += pcode + ncode;   // previous + next(0,1)
                if (p.lchild != 0 || p.rchild != 0) {
                    givecode(pcg, pcg.get(p.lchild), p.code, "1"); //to give the leftside 1 
                    givecode(pcg, pcg.get(p.rchild), p.code, "0"); //to give the rightside 0
                }
            }
        });
        btnCompress.setForeground(new Color(107, 142, 35));
        btnCompress.setBackground(new Color(72, 61, 139));
        btnCompress.setBounds(88, 190, 89, 23);
        frame.getContentPane().add(btnCompress);

        //==========================Decompress=========================
        JButton btnDecompress = new JButton("Decompress");
        btnDecompress.setFont(new Font("Yu Gothic UI Semilight", Font.BOLD, 11));
        btnDecompress.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String buffer = "", concat = "", result = "";
                Source = Stext.getText();
                Destination = Dtext.getText();
                try {
                    file = new File(Source);
                    Scanner s = new Scanner(file);
                    buffer = s.nextLine();
                } catch (Exception e) {
                    System.out.println(e);
                }
                String[] parser = buffer.split("#");  //to split code and dictionary
                String[] dict = parser[0].split(" ");   //to split dictionary from spaces
                for (int i = 0; i < dict.length; i++) //to Fill nodes
                {
                    Node n = new Node();
                    n.symbol += dict[i].charAt(0);// to get first char as symbol
                    n.code += dict[i].substring(1);// from 2nd char to end as code
                    obj.add(n);
                }
                char[] chars = parser[1].toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    concat += chars[i];  //to concatanate until it = any code
                    for (int y = 0; y < obj.size(); y++) {
                        if (concat.equals(obj.get(y).code)) {
                            result += obj.get(y).symbol;
                            concat = "";
                            break;
                        }
                    }
                }
                try {
                    PrintWriter writer = new PrintWriter(Destination, "UTF-8");
                    writer.println(result);
                    writer.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
                JOptionPane.showMessageDialog(null, "Decmpressed Successfully");
            }
        });
        btnDecompress.setForeground(new Color(107, 142, 35));
        btnDecompress.setBackground(new Color(72, 61, 139));
        btnDecompress.setBounds(239, 190, 98, 23);
        frame.getContentPane().add(btnDecompress);

        JLabel lblHuffmanAlgrothim = new JLabel("HUFFMAN ALGROTHIM");
        lblHuffmanAlgrothim.setFont(new Font("Tempus Sans ITC", Font.BOLD | Font.ITALIC, 13));
        lblHuffmanAlgrothim.setBackground(Color.LIGHT_GRAY);
        lblHuffmanAlgrothim.setForeground(new Color(127, 255, 0));
        lblHuffmanAlgrothim.setBounds(137, 11, 170, 14);
        frame.getContentPane().add(lblHuffmanAlgrothim);

        table = new JTable();
        table.setBounds(131, 133, -101, 32);
        frame.getContentPane().add(table);
    }

}
