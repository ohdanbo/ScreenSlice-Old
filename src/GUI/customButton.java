package GUI;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class customButton extends JLabel {
	private static final long serialVersionUID = 1L;

	public Font defaultFont() throws Exception {
		Font font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResource("arial.ttf").openStream());   
		GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		genv.registerFont(font);
		font = font.deriveFont(11f);
		return font;
	}
	
	public customButton(final String name, final String path) {
		setText(name);
		setIcon(new ImageIcon(getClass().getResource(path)));
		setBounds(5,5,100,20);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,2,0,0),BorderFactory.createLineBorder(new Color(240,240,240), 1))); //
		setOpaque(true);
		try {setFont(defaultFont());} catch (Exception e2) {}
		addMouseListener(new MouseListener() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            	setText(name);
                setBackground(new Color(194, 224, 255));
                setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,2,0,0),BorderFactory.createLineBorder(new Color(51,153,255), 1)));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
            	setBackground(null);
            	setText(name);
                setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,2,0,0),BorderFactory.createLineBorder(new Color(240,240,240), 1))); //new Color(240,240,240)
            }
			public void mouseClicked(MouseEvent e) {
			}
			public void mousePressed(MouseEvent e) {
				setBackground(new Color(153, 204, 255));
			}
			public void mouseReleased(MouseEvent e) {
            	setBackground(null);
            	setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,2,0,0),BorderFactory.createLineBorder(new Color(240,240,240), 1)));
			}
        });
	}
}