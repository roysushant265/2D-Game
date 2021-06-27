import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel{
    private Image apple;
    private Image dot;
    private Image head;

    private int dots;
    private final int DOT_SIZE = 10;    // 300 * 300 = 90000 / 100 = 900->total number of dots in the window if each dot size is 10px
    private final int ALL_DOTS = 900;
    private final int RANDOM_POSITION = 29; // max value that can be taken so that apple can generate anywhere between 1-300 in x and y axi

    private final int[] x = new int[ALL_DOTS];// holds x axis coordinates of possible dots(900 dots)
    private final int[] y = new int[ALL_DOTS];// holds y axis coordinates of possible dots(900 dots)

    private int apple_x;// x axis coordinate of apple
    private int apple_y;// y axis coordinate of apple

    private Timer timer;

    private boolean leftDirection = false;
    private boolean rightDirection =  true;// snake is moving initially in the right direction
    private boolean upDirection =  false;
    private boolean downDirection =  false;
    private boolean inGame = true;


    Board(){
        setBackground(Color.black);
        setPreferredSize(new Dimension(300,300));
        loadImages();
        initGame();
        addKeyListener(new TAdapter()); // get the key pressed
        setFocusable(true); // for KeyListener event to work setFocusable needs to be true
    }
    public void loadImages(){
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/apple.png")); // Dimension of the images already 10*10
        apple = i1.getImage();
        ImageIcon i2 = new ImageIcon(ClassLoader.getSystemResource("icons/dot.png"));
        dot = i2.getImage();
        ImageIcon i3 = new ImageIcon(ClassLoader.getSystemResource("icons/head.png"));
        head = i3.getImage();
    }
    public void initGame(){// the snake starts from a fixed position this method sets that position

        dots = 3;

        for(int z = 0 ; z < dots ; z++){
            x[z] = 50 - z * DOT_SIZE; // x[0] y[0] // x[1] y[1] // x[2] y[2] //(1st dot coordinate->50,50) (2->50-10=40),50 (3->50-20=30,50)
            y[z] = 50;
        }

        locateApple();

//        timer = new Timer(140, this); // to apply delay in snake movement
        timer = new Timer(140,new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(inGame){
                    checkApple();
                    checkCollision();
                    move();
                }

                repaint();// calls paintComponent
            }
        });
        timer.start();
    }
    public void locateApple(){

        int r = (int)(Math.random() * RANDOM_POSITION); // math.random generates number between 0 and 1 =>  say 0.6 * 29 = 17
        apple_x = (r * DOT_SIZE); // 17 * 10 = 170

        r = (int)(Math.random() * RANDOM_POSITION); // 0 and 1 =>  1 * 29 = 29
        apple_y = (r * DOT_SIZE);// 29 * 10 = 290  // after snake eats apple its coordinates (170 + 10,290 + 10) = 180,300 so still within y axis bounds

    }

    public void checkApple(){ // check if snake gets the apple
        if((x[0] == apple_x) && (y[0] == apple_y)){ // x[0] y[0] -> head
            dots++;
            locateApple();
        }
    }
    public void checkCollision(){

        for(int z = dots ; z > 0 ; z--){
            if((z > 4) && (x[0] == x[z]) && (y[0] == y[z])){
                inGame = false;
            }
        }
        if(y[0] >= 300){
            inGame = false;
        }

        if(x[0] >= 300){
            inGame = false;
        }

        if(x[0] < 0){
            inGame = false;
        }

        if(y[0] < 0 ){
            inGame = false;
        }

        if(!inGame){
            timer.stop();
        }
    }
    public void move(){// to move the snake coordinate of dots needs to be changed

        for(int z = dots ; z > 0 ; z--){ // first move the dots behind head
            x[z] = x[z - 1];
            y[z] = y[z - 1];
        }

        if(leftDirection){ // then move the head
            x[0] = x[0] -  DOT_SIZE;
        }
        if(rightDirection){
            x[0] += DOT_SIZE;
        }
        if(upDirection){
            y[0] = y[0] -  DOT_SIZE;
        }
        if(downDirection){
            y[0] += DOT_SIZE;
        }
    }
    public void paintComponent(Graphics g){

        super.paintComponent(g);

        draw(g);
    }

    public void draw(Graphics g){
        if(inGame){
            g.drawImage(apple, apple_x, apple_y, this);

            for(int z = 0; z < dots ; z++){
                if(z == 0){
                    g.drawImage(head, x[z], y[z], this);
                }else{
                    g.drawImage(dot, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();
        }else{
            gameOver(g);
        }
    }


    public void gameOver(Graphics g){
        String msg = "Game Over";
        Font font = new Font("SAN_SERIF", Font.BOLD, 14);
        FontMetrics metrices = getFontMetrics(font);

        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(msg, (300 - metrices.stringWidth(msg)) / 2 , 300/2);
    }


    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e){
            int key =  e.getKeyCode();// getKeyCode method in KeyEvent class returns the reference of key pressed

            if(key == KeyEvent.VK_LEFT && (!rightDirection)){
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if(key == KeyEvent.VK_RIGHT && (!leftDirection)){
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if(key == KeyEvent.VK_UP && (!downDirection)){
                leftDirection = false;
                upDirection = true;
                rightDirection = false;
            }

            if(key == KeyEvent.VK_DOWN && (!upDirection)){
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
