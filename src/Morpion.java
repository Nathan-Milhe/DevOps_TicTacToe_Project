import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Random;


/**
 *
 * @author remy
 */

public class Morpion extends JFrame implements ChangeListener, ActionListener 
{
  private JSlider slider;
  private JButton oButton, xButton;
  public Board plateau;
  private int lineThickness=4;
  private Color oColor=Color.BLUE, xColor=Color.RED;
  static final char BLANK=' ', O='O', X='X';
  private char position[]={  
    BLANK, BLANK, BLANK,
    BLANK, BLANK, BLANK,
    BLANK, BLANK, BLANK};
  private int gagnees=0, perdues=0, egalites=0;  

  // Start the game
  public static void main(String args[]) {
    new Morpion();
  }

  // Initialize
  public Morpion() {
    super("Jeu du Morpion");
    JPanel topPanel=new JPanel();
    topPanel.setLayout(new FlowLayout());
    topPanel.add(new JLabel("Largeur des lignes:"));
    topPanel.add(slider=new JSlider(SwingConstants.HORIZONTAL, 1, 20, 4));
    slider.setMajorTickSpacing(1);
    slider.setPaintTicks(true);
    slider.addChangeListener(this);
    topPanel.add(oButton=new JButton("Couleur du O"));
    topPanel.add(xButton=new JButton("Couleur du X"));
    oButton.addActionListener(this);
    xButton.addActionListener(this);
    add(topPanel, BorderLayout.NORTH);
    add(plateau=new Board(), BorderLayout.CENTER);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 500);
    setVisible(true);
  }

  // Change line thickness
  public void stateChanged(ChangeEvent e) {
    lineThickness = slider.getValue();
    plateau.repaint();
  }

  // Change color of O or X
  public void actionPerformed(ActionEvent e) {
    if (e.getSource()==oButton) {
      Color newColor = JColorChooser.showDialog(this, "Choix de la couleur du O", oColor);
      if (newColor!=null)
        oColor=newColor;
    }
    else if (e.getSource()==xButton) {
      Color newColor = JColorChooser.showDialog(this, "Choix de la couleur du X", xColor);
      if (newColor!=null)
        xColor=newColor;
    }
    plateau.repaint();
  }

  // le plateau affiche les résultats 
  public  class Board extends JPanel implements MouseListener {
    private Random random=new Random();
    private int rows[][]={{0,2},{3,5},{6,8},{0,6},{1,7},{2,8},{0,8},{2,6}};
     

    public Board() {
      addMouseListener(this);
    }

    //redessine la grille
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      int w=getWidth();
      int h=getHeight();
      Graphics2D g2d = (Graphics2D) g;

      //dessine la grille du morpion
      g2d.setPaint(Color.WHITE);
      g2d.fill(new Rectangle2D.Double(0, 0, w, h));
      g2d.setPaint(Color.BLACK);
      g2d.setStroke(new BasicStroke(lineThickness));
      g2d.draw(new Line2D.Double(0, h/3, w, h/3));
      g2d.draw(new Line2D.Double(0, h*2/3, w, h*2/3));
      g2d.draw(new Line2D.Double(w/3, 0, w/3, h));
      g2d.draw(new Line2D.Double(w*2/3, 0, w*2/3, h));

      // Affichage des X et des 0;
      for (int i=0; i<9; ++i) {
        double xpos=(i%3+0.5)*w/3.0;
        double ypos=(i/3+0.5)*h/3.0;
        double xr=w/8.0;
        double yr=h/8.0;
        if (position[i]==O) {
          g2d.setPaint(oColor);
          g2d.draw(new Ellipse2D.Double(xpos-xr, ypos-yr, xr*2, yr*2));
        }
        else if (position[i]==X) {
          g2d.setPaint(xColor);
          g2d.draw(new Line2D.Double(xpos-xr, ypos-yr, xpos+xr, ypos+yr));
          g2d.draw(new Line2D.Double(xpos-xr, ypos+yr, xpos+xr, ypos-yr));
        }
      }
    }

    //place un O sur l'écran où la souris a clickée
    public void mouseClicked(MouseEvent e) {
      int xpos=e.getX()*3/getWidth();
      int ypos=e.getY()*3/getHeight();
      int pos=xpos+3*ypos;
      if (pos>=0 && pos<9 && position[pos]==BLANK) {
        position[pos]=O;
        repaint();
        putX();  // computer plays
        repaint();
      }
    }

    // Ignore les autres inputs de la souris
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    //IA joue un X
    void putX() {
      
      //Vérification si la game a pris fin
      if (won(O))
        newGame(O);
      else if (isDraw())
        newGame(BLANK);

      //positionnement d'un X qui peut faire se terminer la game
      else {
        nextMove();
        if (won(X))
          newGame(X);
        else if (isDraw())
          newGame(BLANK);
      }
    }

    // Retourne vrai si le joueur a gagné
    boolean won(char player) {
      for (int i=0; i<8; ++i)
        if (testRow(player, rows[i][0], rows[i][1]))
          return true;
      return false;
    }

    //Est-ce que le joueur a gagné dans la ligne depuis la pose[a] à la pose[b] ?
    boolean testRow(char player, int a, int b) {
      return position[a]==player && position[b]==player 
          && position[(a+b)/2]==player;
    }

    //met un X au meilleur endroit
    void nextMove() {
      int r=findRow(X);  // complète une ligne de X pour gagner si possible
      if (r<0)
        r=findRow(O);  // Ou essaie de bloquer O pour l'empêcher de gagner
            if (r<0) {  //Joue aléatoirement
        do
          r=random.nextInt(9);
        while (position[r]!=BLANK);
      }
      position[r]=X;
    }

    //retourne entre 0 et 8 pour les spots d'une positon vide dans une ligne si
    // Deux autres spots sont occupés par le joueur , sinon -1 si le spot 
    int findRow(char player) {
      for (int i=0; i<8; ++i) {
        int result=find1Row(player, rows[i][0], rows[i][1]);
        if (result>=0)
          return result;
      }
      return -1;
    }

    //Si deux ou trois spots dans la ligne depuis position a à position b 
    //sont occupés par le joueur et le 3ième est vide, alors retourne l'index du spot vide. 
    //sinon retourne -1
    int find1Row(char player, int a, int b) {
      int c=(a+b)/2;  // middle spot
      if (position[a]==player && position[b]==player && position[c]==BLANK)
        return c;
      if (position[a]==player && position[c]==player && position[b]==BLANK)
        return b;
      if (position[b]==player && position[c]==player && position[a]==BLANK)
        return a;
      return -1;
    }

    //Si les 9 spots sont remplis?
    public boolean isDraw() {
      for (int i=0; i<9; ++i)
        if (position[i]==BLANK)
          return false;
      return true;
    }

    // Démarre une nouvelle partie
    void newGame(char winner) {
      repaint();

      // annonce les résultats de la dernière game et demande à l'user si il veut rejouer
      String resultat;
      if (winner==O) {
        ++gagnees;
        resultat = "Vous avez gagné !";
      }
      else if (winner==X) {
        ++perdues;
        resultat = "Vous avez gagné !";
      }
      else {
        resultat = "Tie";
        ++egalites;
      }
      if (JOptionPane.showConfirmDialog(null, 
          "Vous avez  "+gagnees+ " victoires , et "+perdues+" défaites, et  "+egalites+" égalités\n"
          +"Rejouer ?", resultat, JOptionPane.YES_NO_OPTION)
          !=JOptionPane.YES_OPTION) {
        System.exit(0);
      }

      // nettoyage du tableau
      for (int i=0; i<9; ++i)
        position[i]=BLANK;

      
      //L'ordinateur démarre la partie une fois sur deux
      if ((gagnees+perdues+egalites)%2 == 1)
        nextMove();
    }
  } 
} 