package com.test.chess;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.EventObject;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.JTextField;

class Posi {
    public int x;
    public int y;

    Posi( int px, int py ) {
        x = px;
        y = py;
    }
}

public class MyChess extends JFrame implements MouseMotionListener, MouseListener, Runnable {
    int x = 0;
    int y = 0;// 保存棋子的在棋盘上的 x y 坐标
    int drawx, drawy;// 鼠标的位置
    int[][] allChess = new int[ 10 ][ 10 ];// 棋盘上棋子的坐标
    // 其中数据内容 0： 表示这个点并没有棋子， 1： 表示这个点是黑子， 2：表示这个点是白子
    boolean isBlack = true;// 本轮是否是黑手
    boolean isMove = false;//是否可以移动
    int MODE = 1;// 1：人人 2：人机
    JLabel clockstr = new JLabel( );//计时器文本标签
    JPanel clocktime = new JPanel( new BorderLayout( ) );//计时器容器
    JMenuBar menubar;//菜单容器
    JPanel chessboard = new JPanel( );// 棋盘画板容器

    Timing t = new Timing( );
    Mstc mstcwhite = new Mstc( 2 );
    Mstc mstcblack = new Mstc( 1 );
    String message = "黑方先下";

    class Timing extends Thread {
        public int count = 60;
        public String timestr;
        public int iseverStart = 0;
        public int isStart = 0;
        public int timeout = 0;


        public void run( ) {
            while (true) {
                while (count >= 0) {
                    try {
                        Thread.sleep( (long) 1000 );
                    } catch (InterruptedException e) {
                        e.printStackTrace( );
                        System.exit( 1 );
                    }
                    if (isStart == 1) {
                        count--;
                        countupdate( );
                        timestr = String.format( "剩余时间：%d", count );
                        if (isBlack) {
                            timestr = "黑棋 " + timestr;
                        } else {
                            timestr = "白棋 " + timestr;
                        }
                        System.out.println( timestr );
                    }
                }
                System.out.println( "Time extends" );
                isStart = 0;
                timeout = 1;
                timeoutend( );
            }
        }

        public void restart( ) {
            count = 60;
            timeout = 0;
            isStart = 1;
        }

        public void tstart( ) {
            isStart = 1;
            timeout = 0;
            iseverStart = 1;
            start( );
        }

        public void tstop( ) {
            isStart = 0;
            count = 60;
            timeout = 0;
        }
    }

    public MyChess( ) {

        StartGame( );
        InitialWindow( );
    }

    private void InitialWindow( ) {
        //窗口基础设置
        this.setTitle( "LOA棋" );
        this.setSize( 400, 420 );// 设置窗口大小
        this.setResizable( false );// 设置窗口大小不可被改变
        this.setLocationRelativeTo( null );// 设置窗口初始在屏幕正中间
        this.addMouseListener( this );
        this.addMouseMotionListener( this );

        setBoard( );
        setMenu( );
        setClock( );
        //this.repaint();

        this.setVisible( true ); // 设置窗口显示
    }

    private void StartGame( ) {
        for (int i = 2; i < 8; i++) {
            allChess[ i ][ 1 ] = 1;
            allChess[ i ][ 8 ] = 1;
            allChess[ 1 ][ i ] = 2;
            allChess[ 8 ][ i ] = 2;
        }// initial the chess(black and white)
        this.repaint( );
    }

    private void EndGame( ) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                allChess[ i ][ j ] = 0;// 棋盘清空
                isBlack = true;
                isMove = false;
            }
        }
        t.tstop( );
        this.repaint( );
    }

    public void setMenu( ) {
        // 实现菜单↓
        JMenu menu;
        JMenuItem item1, item2, item3, item4;

        menubar = new JMenuBar( );
        menu = new JMenu( "菜单" );
        item1 = new JMenuItem( "重新开始" );
        item2 = new JMenuItem( "退出" );
        item3 = new JMenuItem( "关于" );
        item4 = new JMenuItem( "人机博弈" );

        item1.addActionListener( new ActionListener( ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                String str = "是否要重新开始游戏?";

                if (JOptionPane.showConfirmDialog( null, str, "重新开始",
                        JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION) {
                    System.out.println( "123" );
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            allChess[ i ][ j ] = 0;// 棋盘清空

                        }
                    }
                    for (int i = 2; i < 8; i++) {
                        allChess[ i ][ 1 ] = 1;
                        allChess[ i ][ 8 ] = 1;
                        allChess[ 1 ][ i ] = 2;
                        allChess[ 8 ][ i ] = 2;
                    }
                    t.tstop( );
                    isMove = false;
                    isBlack = true;
//					repaint();
                }
//				repaint();
            }
        } );
        item2.addActionListener( new ActionListener( ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                String str = "是否要退出游戏?";

                if (JOptionPane.showConfirmDialog( null, str, "退出游戏",
                        JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION) {
                    System.exit( 0 ); // 退出
                }
                repaint( );
            }
        } );
        item3.addActionListener( new ActionListener( ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                String str = "关于";
                JOptionPane.showMessageDialog( null, "吼吼吼吼" );
                repaint( );
            }

        } );

        item4.addActionListener( new ActionListener( ) {
            public void actionPerformed( ActionEvent e ) {
                String str = "人机模式？";
                if (JOptionPane.showConfirmDialog( null, str, "是", JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION) {
                    EndGame( );
                    StartGame( );
                    MODE = 2;
                } else if (JOptionPane.showConfirmDialog( null, str, "是", JOptionPane.YES_NO_OPTION ) == JOptionPane.NO_OPTION) {
                    EndGame( );
                    StartGame( );
                    MODE = 1;
                }
            }
        } );


        item1.setAccelerator( KeyStroke.getKeyStroke( "1" ) );
        item2.setAccelerator( KeyStroke.getKeyStroke( '2' ) );
        item3.setAccelerator( KeyStroke.getKeyStroke( '3' ) );
        item4.setAccelerator( KeyStroke.getKeyStroke( '4' ) );

        menu.add( item1 );
        menu.addSeparator( );
        menu.add( item2 );
        menu.addSeparator( );
        menu.add( item3 );
        menu.addSeparator( );
        menu.add( item4 );
        menubar.add( menu );
        setJMenuBar( menubar );

        validate( );
//		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//		this.setVisible(true);

    }

    public void setClock( ) {
        clocktime.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        clocktime.add( clockstr );
        //clocktime.setPreferredSize(new Dimension(5,10));
        //clocktime.setLocation(200, 200);
        Container c = this.getContentPane( );
        //c.setLayout(null);
        //c.add(clocktime);
        c.setLayout( new BorderLayout( ) );
        c.add( clocktime, BorderLayout.NORTH );
    }

    public void setBoard( ) {
        chessboard.setLayout( new BorderLayout( ) );
        chessboard.setSize( 400, 420 );
        Container c = this.getContentPane( );
        c.setLayout( new BorderLayout( ) );
        c.add( chessboard, BorderLayout.SOUTH );
    }

    public static void main( String[] args ) {
        MyChess mc = new MyChess( );
    }

    /**
     * 调整bufferedimage大小
     *
     * @param source  BufferedImage 原始image
     * @param targetW int 目标宽
     * @param targetH int 目标高
     * @param flag    boolean 是否同比例调整
     * @return BufferedImage 返回新image
     */
    private static BufferedImage resizeBufferedImage( BufferedImage source, int targetW, int targetH, boolean flag ) {
        int type = source.getType( );
        BufferedImage target = null;
        double sx = (double) targetW / source.getWidth( );
        double sy = (double) targetH / source.getHeight( );
        if (flag && sx > sy) {
            sx = sy;
            targetW = (int) ( sx * source.getWidth( ) );
        } else if (flag && sx <= sy) {
            sy = sx;
            targetH = (int) ( sy * source.getHeight( ) );
        }
        if (type == BufferedImage.TYPE_CUSTOM) { // handmade
            ColorModel cm = source.getColorModel( );
            WritableRaster raster = cm.createCompatibleWritableRaster( targetW, targetH );
            boolean alphaPremultiplied = cm.isAlphaPremultiplied( );
            target = new BufferedImage( cm, raster, alphaPremultiplied, null );
        } else {
            target = new BufferedImage( targetW, targetH, type );
        }
        Graphics2D g = target.createGraphics( );
        g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g.drawRenderedImage( source, AffineTransform.getScaleInstance( sx, sy ) );
        g.dispose( );
        return target;
    }

    @Override
    public void paint( Graphics g ) {

        BufferedImage bg = null;
        BufferedImage black = null;
        BufferedImage white = null;
        BufferedImage move = null;
        try {
            bg = ImageIO.read( this.getClass( ).getResource( "/res/drawable/chessboard.png" ) );// 背景图片
            black = ImageIO.read( this.getClass( ).getResource( "/res/drawable/black.png" ) );// 黑色棋子
            black = resizeBufferedImage( black, 20, 20, true );
            white = ImageIO.read( this.getClass( ).getResource( "/res/drawable/write.png" ) );// 白色棋子
            white = resizeBufferedImage( white, 20, 20, true );

        } catch (IOException e) {
            e.printStackTrace( );
        }

        Graphics g2 = bg.createGraphics( );
        for (int i = 0; i < 11; i++) {
            g2.drawLine( 40, 40 + i * 30, 340, 40 + i * 30 );
            g2.drawLine( 40 + i * 30, 40, 40 + i * 30, 340 );// 绘制棋盘上的线

        }
        g2.setFont( new Font( "黑体", Font.BOLD, 20 ) );
        for (int i = 0; i < 8; i++) {
            g2.drawString( String.valueOf( 8 - i ), 48, 92 + 30 * i );
            g2.drawString( String.valueOf( 8 - i ), 48 + 30 * 9, 92 + 30 * i );
        }// 棋盘坐标
        for (int i = 0; i < 8; i++) {
            g2.drawString( String.valueOf( (char) ( i + 'A' ) ), 78 + 30 * i, 62 );
            g2.drawString( String.valueOf( (char) ( i + 'A' ) ), 78 + 30 * i, 62 + 30 * 9 );
        }// 棋盘坐标

        //棋子移动
        if (isMove) {
            if (isBlack)
                g2.drawImage( black, drawx - 10, drawy - 30, null );
            else
                g2.drawImage( white, drawx - 10, drawy - 30, null );
        }

        // 绘制全部棋子 ↓
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (allChess[ i ][ j ] == 1) {
                    int tempx = 45 + 30 * i;
                    int tempy = 45 + 30 * j;
                    g2.drawImage( black, tempx, tempy, null );// 绘制黑子

                }
                if (allChess[ i ][ j ] == 2) {
                    int tempx = 45 + 30 * i;
                    int tempy = 45 + 30 * j;
                    g2.drawImage( white, tempx, tempy, null );// 绘制白子
                }
            }
        }

        g.drawImage( bg, 0, 20, chessboard );

        clockstr.requestFocus( );
        menubar.requestFocus( );
    }

    private void countupdate( ) {
        clockstr.setHorizontalAlignment( SwingConstants.CENTER );
        clockstr.setFont( new Font( clockstr.getFont( ).getName( ), clockstr.getFont( ).getStyle( ), 20 ) );
        clockstr.setText( t.timestr );
    }

    private void timeoutend( ) {
        int over;
        //yong mstc panduan youmeiyou next
        over = isBlack ? 2 : 1;
        if (t.timeout == 1) {
            if (over == 1) {
                if (JOptionPane.showConfirmDialog( null, "游戏结束，黑子获胜", "重新开始",
                        JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION) {
                    EndGame( );
                    StartGame( );
                }
            } else if (over == 2) {
                if (JOptionPane.showConfirmDialog( null, "游戏结束，白子获胜", "重新开始",
                        JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION) {
                    EndGame( );
                    StartGame( );
                }
            }
        }
    }

    @Override

    public void mouseClicked( MouseEvent e ) {
        // TODO 自动生成的方法存根
    }

    //游戏结束条件的判断,一方棋子被吃完后，结束游戏
    //0,没结束，1黑子赢，2白子赢
    public int judgeOver( ) {
        int numberBlack = 0;
        int Blackx = 0, Blacky = 0;
        int numberWhite = 0;
        int Whitex = 0, Whitey = 0;
        int type;// 当轮棋子颜色
        Posi[] posi = new Posi[ 12 ];
        int[][] isVisited = new int[ 10 ][ 10 ];
        int positop = -1;
        int isNext;

        type = isBlack ? 1 : 2;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                isVisited[ i ][ j ] = 0;
                if (allChess[ i ][ j ] == 1) {
                    if (numberBlack == 0) {
                        Blackx = i;
                        Blacky = j;
                    }
                    numberBlack++;
                } else if (allChess[ i ][ j ] == 2) {
                    if (numberWhite == 0) {
                        Whitex = i;
                        Whitey = j;
                    }
                    numberWhite++;
                }
            }
        }
        if (type == 1) {
            if (numberWhite <= 1) return type;    // 对方只剩一枚棋子
            isVisited[ Blackx ][ Blacky ] = 1;
            numberBlack--;
            do//判断是否所有 棋子连成一片
            {
                isNext = 0;
                if (Blackx - 1 >= 1 && Blacky - 1 >= 1 && allChess[ Blackx - 1 ][ Blacky - 1 ] == type && isVisited[ Blackx - 1 ][ Blacky - 1 ] == 0) {
                    posi[ ++positop ] = new Posi( Blackx - 1, Blacky - 1 );
                    isVisited[ Blackx - 1 ][ Blacky - 1 ] = 1;// 已经访问过
                    numberBlack--;
                }
                if (Blackx - 1 >= 1 && Blacky + 1 <= 8 && allChess[ Blackx - 1 ][ Blacky + 1 ] == type && isVisited[ Blackx - 1 ][ Blacky + 1 ] == 0) {
                    posi[ ++positop ] = new Posi( Blackx - 1, Blacky + 1 );
                    isVisited[ Blackx - 1 ][ Blacky + 1 ] = 1;// 已经访问过
                    numberBlack--;
                }
                if (Blackx + 1 >= 1 && Blacky + 1 <= 8 && allChess[ Blackx + 1 ][ Blacky + 1 ] == type && isVisited[ Blackx + 1 ][ Blacky + 1 ] == 0) {
                    posi[ ++positop ] = new Posi( Blackx + 1, Blacky + 1 );
                    isVisited[ Blackx + 1 ][ Blacky + 1 ] = 1;// 已经访问过
                    numberBlack--;
                }
                if (Blackx + 1 <= 8 && Blacky - 1 >= 1 && allChess[ Blackx + 1 ][ Blacky - 1 ] == type && isVisited[ Blackx + 1 ][ Blacky - 1 ] == 0) {
                    posi[ ++positop ] = new Posi( Blackx + 1, Blacky - 1 );
                    isVisited[ Blackx + 1 ][ Blacky - 1 ] = 1;// 已经访问过
                    numberBlack--;
                }
                if (Blackx - 1 >= 1 && allChess[ Blackx - 1 ][ Blacky ] == type && isVisited[ Blackx - 1 ][ Blacky ] == 0) {
                    posi[ ++positop ] = new Posi( Blackx - 1, Blacky );
                    isVisited[ Blackx - 1 ][ Blacky ] = 1;// 已经访问过
                    numberBlack--;
                }
                if (Blackx + 1 <= 8 && allChess[ Blackx + 1 ][ Blacky ] == type && isVisited[ Blackx + 1 ][ Blacky ] == 0) {
                    posi[ ++positop ] = new Posi( Blackx + 1, Blacky );
                    isVisited[ Blackx + 1 ][ Blacky ] = 1;// 已经访问过
                    numberBlack--;
                }
                if (Blacky - 1 >= 1 && allChess[ Blackx ][ Blacky - 1 ] == type && isVisited[ Blackx ][ Blacky - 1 ] == 0) {
                    posi[ ++positop ] = new Posi( Blackx, Blacky - 1 );
                    isVisited[ Blackx ][ Blacky - 1 ] = 1;// 已经访问过
                    numberBlack--;
                }
                if (Blacky + 1 <= 8 && allChess[ Blackx ][ Blacky + 1 ] == type && isVisited[ Blackx ][ Blacky + 1 ] == 0) {
                    posi[ ++positop ] = new Posi( Blackx, Blacky + 1 );
                    isVisited[ Blackx ][ Blacky + 1 ] = 1;// 已经访问过
                    numberBlack--;
                }

                if (numberBlack < 1) {
                    return type;
                } else {
                    if (positop >= 0) {
                        Blackx = posi[ positop ].x;
                        Blacky = posi[ positop ].y;
                        positop--;
                        isNext = 1;
                    }
                }
            } while (isNext == 1);
        } else if (type == 2) {
            if (numberBlack <= 1) return type;    // 对方只剩一枚棋子
            isVisited[ Whitex ][ Whitey ] = 1;
            numberWhite--;
            do//判断是否所有 棋子连成一片
            {
                isNext = 0;
                if (Whitex - 1 >= 1 && Whitey - 1 >= 1 && allChess[ Whitex - 1 ][ Whitey - 1 ] == type && isVisited[ Whitex - 1 ][ Whitey - 1 ] == 0) {
                    posi[ ++positop ] = new Posi( Whitex - 1, Whitey - 1 );
                    isVisited[ Whitex - 1 ][ Whitey - 1 ] = 1;// 已经访问过
                    numberWhite--;
                }
                if (Whitex - 1 >= 1 && Whitey + 1 <= 8 && allChess[ Whitex - 1 ][ Whitey + 1 ] == type && isVisited[ Whitex - 1 ][ Whitey + 1 ] == 0) {
                    posi[ ++positop ] = new Posi( Whitex - 1, Whitey + 1 );
                    isVisited[ Whitex - 1 ][ Whitey + 1 ] = 1;// 已经访问过
                    numberWhite--;
                }
                if (Whitex + 1 >= 1 && Whitey + 1 <= 8 && allChess[ Whitex + 1 ][ Whitey + 1 ] == type && isVisited[ Whitex + 1 ][ Whitey + 1 ] == 0) {
                    posi[ ++positop ] = new Posi( Whitex + 1, Whitey + 1 );
                    isVisited[ Whitex + 1 ][ Whitey + 1 ] = 1;// 已经访问过
                    numberWhite--;
                }
                if (Whitex + 1 <= 8 && Whitey - 1 >= 1 && allChess[ Whitex + 1 ][ Whitey - 1 ] == type && isVisited[ Whitex + 1 ][ Whitey - 1 ] == 0) {
                    posi[ ++positop ] = new Posi( Whitex + 1, Whitey - 1 );
                    isVisited[ Whitex + 1 ][ Whitey - 1 ] = 1;// 已经访问过
                    numberWhite--;
                }
                if (Whitex - 1 >= 1 && allChess[ Whitex - 1 ][ Whitey ] == type && isVisited[ Whitex - 1 ][ Whitey ] == 0) {
                    posi[ ++positop ] = new Posi( Whitex - 1, Whitey );
                    isVisited[ Whitex - 1 ][ Whitey ] = 1;// 已经访问过
                    numberWhite--;
                }
                if (Whitex + 1 <= 8 && allChess[ Whitex + 1 ][ Whitey ] == type && isVisited[ Whitex + 1 ][ Whitey ] == 0) {
                    posi[ ++positop ] = new Posi( Whitex + 1, Whitey );
                    isVisited[ Whitex + 1 ][ Whitey ] = 1;// 已经访问过
                    numberWhite--;
                }
                if (Whitey - 1 >= 1 && allChess[ Whitex ][ Whitey - 1 ] == type && isVisited[ Whitex ][ Whitey - 1 ] == 0) {
                    posi[ ++positop ] = new Posi( Whitex, Whitey - 1 );
                    isVisited[ Whitex ][ Whitey - 1 ] = 1;// 已经访问过
                    numberWhite--;
                }
                if (Whitey + 1 <= 8 && allChess[ Whitex ][ Whitey + 1 ] == type && isVisited[ Whitex ][ Whitey + 1 ] == 0) {
                    posi[ ++positop ] = new Posi( Whitex, Whitey + 1 );
                    isVisited[ Whitex ][ Whitey + 1 ] = 1;// 已经访问过
                    numberWhite--;
                }

                if (numberWhite < 1) {
                    return type;
                } else {
                    if (positop >= 0) {
                        Whitex = posi[ positop ].x;
                        Whitey = posi[ positop ].y;
                        positop--;
                        isNext = 1;
                    }
                }
            } while (isNext == 1);
        }
        //0：没有结束  1：黑子获胜 2：白子获胜
        return 0;
    }

    //判断落子的合法性，步数为该直线上所有棋子的个数
    public boolean judge( int tx, int ty ) {
        int type = 2;
        if (isBlack)
            type = 1;
        //如果落子位置为原棋子，直接返回false
        if (allChess[ tx ][ ty ] == type) {
            return false;
        }
        //如果棋子没有启动，返回false
        if (tx == x && ty == y)
            return false;
        int number = 0;
        //x==tx，横向移动
        if (tx == x) {
            for (int i = 1; i <= 8; i++) {
                if (allChess[ tx ][ i ] != 0) {
                    number++;
                }
            }
            if (Math.abs( ty - y ) != number + 1) {
                return false;
            }
            return true;
        }
        //竖向移动
        if (ty == y) {
            number = 0;
            for (int i = 1; i <= 8; i++) {
                if (allChess[ i ][ ty ] != 0) {
                    number++;
                }
            }
            if (Math.abs( tx - x ) != number + 1) {
                return false;
            }
            return true;
        }
        //判断是否是斜着移动
        if (Math.abs( ty - y ) != Math.abs( tx - x ))
            return false;
        //斜着分两种，此种为左上、右下45度
        if (( ty - y ) == ( tx - x )) {
            number = 0;
            int a = tx, b = ty;
            a--;
            b--;
            while (a >= 1 && b >= 1) {
                if (allChess[ a ][ b ] != 0) {
                    number++;
                }
                a--;
                b--;
            }
            a = tx + 1;
            b = ty + 1;
            while (a <= 8 && b <= 8) {
                if (allChess[ a ][ b ] != 0) {
                    number++;
                }
                a++;
                b++;
            }
            if (Math.abs( tx - x ) != number + 1) {
                return false;
            }
            return true;
        }
        //此处为左下右上45度移动
        number = 0;
        int a, b;
        a = tx + 1;
        b = ty - 1;
        while (a <= 8 && b >= 1) {
            if (allChess[ a ][ b ] != 0) {
                number++;
            }
            a++;
            b--;
        }
        a = tx - 1;
        b = ty + 1;
        while (a >= 1 && b <= 8) {
            if (allChess[ a ][ b ] != 0) {
                number++;
            }
            a--;
            b++;
        }
        if (Math.abs( tx - x ) != number + 1) {
            return false;
        }
        return true;
    }


    //移除棋子，落子合法后，移除(x1,y1)到(x2,y2)之间的所有另一种类型的棋子，这个函数废了。。。开始题目理解错误
/*	public void killChess(int x1,int y1,int x2,int y2,int type){
		int l,r;
		//横移
		if(x1==x2){
			l = y1;r = y2;
			if(y1>y2){
				l = y2;r=y1;
			}
			for(int i=l;i<=r;i++){
				if(allChess[x1][i]!=type){
					allChess[x1][i] = 0;
				}
			}
			//竖移
		}else if(y1==y2){
			l = x1;r = x2;
			if(x1>x2){
				l = x2;r=x1;
			}
			for(int i=l;i<=r;i++){
				if(allChess[i][y1]!=type){
					allChess[i][y1] = 0;
				}
			}
		}else{
			//斜着移动
			if((y2-y1)==(x2-x1)){
				l = x1;r = x2;
				int tl;
				tl = y1;
				if(x1>x2){
					l=x2;r=x1;
					tl=y2;
				}
				for(int i=l;i<=r;i++,tl++){
					if(allChess[i][tl]!=type){
						allChess[i][tl]=0;
					}
				}
			}else{
				l=x1;r=x2;
				int tl=y1;
				if(x1>x2){
					l=x2;r=x1;
					tl=y2; 
				}
				for(int i=l;i<=r;i++,tl--){
					if(allChess[i][tl]!=type){
						allChess[i][tl]=0;
					}
				}
			}
			
			
		}
	}
	*/
    //鼠标点击之后，获取棋子的初始坐标
    @Override
    public void mousePressed( MouseEvent e ) {
        // TODO 自动生成的方法存根
        int type = 0;    //表示棋子类型
        if (isBlack) {
            type = 1;
        } else {
            type = 2;
        }
        //System.out.println(tx+" "+ty);
        //System.out.println("棋子："+allChess[tx][ty]);
        if (MODE == 1 || ( MODE == 2 && type == 1 )) {
            x = e.getX( );
            y = e.getY( );
            int tx = ( x - 40 ) / 30;
            int ty = ( y - 60 ) / 30;
            x = tx;
            y = ty;
            if (tx >= 1 && tx <= 9 && ty >= 1 && ty <= 9) {
                System.out.println( "type: " + type );
                if (allChess[ tx ][ ty ] == type) {
                    allChess[ x ][ y ] = 0;
                    isMove = true;
                    //this.repaint();
                }
            }
        }

    }

    //鼠标释放后，获取棋子的结束坐标，判断棋子的落子合法性
    @Override
    public void mouseReleased( MouseEvent e ) {
        // TODO 自动生成的方法存根
        int type = 0;    //表示棋子类型
        type = isBlack ? 1 : 2;
        if (t.iseverStart == 0) t.tstart( );// 程序首次启动，启动计时线程；若非首次启动，则已经在上次released时启动计时

        if (( MODE == 1 ) || ( MODE == 2 && type == 1 )) {
            if (!isMove) {
                return;
            }
            int tx = e.getX( );
            int ty = e.getY( );
            tx = ( tx - 40 ) / 30;
            ty = ( ty - 60 ) / 30;
            if (tx >= 1 && tx <= 9 && ty >= 1 && ty <= 9) {
                if (judge( tx, ty )) {
                    allChess[ tx ][ ty ] = type;
                    allChess[ x ][ y ] = 0;
                    int over = judgeOver( );
                    isBlack = !isBlack;
                    t.restart( );
                    if (over == 1) {
                        if (JOptionPane.showConfirmDialog( null, "游戏结束，黑子获胜", "重新开始",
                                JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION) {
                            EndGame( );
                            StartGame( );
                        }
                    } else if (over == 2) {
                        if (JOptionPane.showConfirmDialog( null, "游戏结束，白子获胜", "重新开始",
                                JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION) {
                            EndGame( );
                            StartGame( );
                        }
                    }

                    if (MODE == 2) {
                        this.repaint( );
                        System.out.println( "机器下棋时间" );
                        Move move;
                        int[] Block = new int[ 64 ];
                        int k = 0;
                        for (int j = 1; j < 9; j++) {
                            for (int i = 1; i < 9; i++) {
                                Block[ k++ ] = allChess[ j ][ i ];
                            }
                        }
                        move = mstcwhite.makeMove( Block );
//                        System.out.println( move );
                        if (move != null) {
                            allChess[ move.y1 + 1 ][ move.x1 + 1 ] = allChess[ move.y0 + 1 ][ move.x0 + 1 ];
                            allChess[ move.y0 + 1 ][ move.x0 + 1 ] = 0;
                            System.out.println( "完成：" + move.x0 + "," + move.y0 + " " + move.x1 + "," + move.y1 );
                        } else {
                            System.out.println( "完成：" );
                        }

                        int over1 = judgeOver( );
                        isBlack = !isBlack;
                        t.restart( );
                        if (over1 == 1) {
                            if (JOptionPane.showConfirmDialog( null, "游戏结束，黑子获胜", "重新开始",
                                    JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION) {
                                EndGame( );
                                StartGame( );
                            }
                        } else if (over1 == 2) {
                            if (JOptionPane.showConfirmDialog( null, "游戏结束，白子获胜", "重新开始",
                                    JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION) {
                                EndGame( );
                                StartGame( );
                            }
                        }
                    }
                } else {
                    allChess[ x ][ y ] = type;// 不合法的移动位置，放回原位
                }
            } else {
                allChess[ x ][ y ] = type;// 不合法的移动位置，放回原位
            }
            isMove = false;

        }
        this.repaint( );
    }

    @Override
    public void mouseEntered( MouseEvent e ) {
        // TODO 自动生成的方法存根
    }

    @Override
    public void mouseExited( MouseEvent e ) {
        // TODO 自动生成的方法存根

    }

    @Override
    public void run( ) {
    }

    //鼠标拖动，时刻获取鼠标坐标，存入drawx,drawy，绘制
    @Override
    public void mouseDragged( MouseEvent e ) {
        // TODO Auto-generated method stub
        //左上角定点位置40，60
        int tx = e.getX( );
        int ty = e.getY( );
        if (isMove) {
//			System.out.println(tx+" "+ty);
            drawx = tx;
            drawy = ty;
            this.repaint( );
        }
    }

    @Override
    public void mouseMoved( MouseEvent e ) {
        // TODO Auto-generated method stub
    }

}
