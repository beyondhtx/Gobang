package Client;

import javax.swing.*;
import java.awt.*;

public class BlankPanel extends JPanel {
    private int trans = 40;
    public BlankPanel(int trans) {
        System.out.println("BlankPanel����");
        this.trans = trans;
        setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;// ת��Ϊ2d
        // �����
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setColor(new Color(255,255,255,trans));
        g2d.setStroke(new BasicStroke(3));
        g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
    }



}
