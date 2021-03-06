/**
 * 
 */
package uk.ac.ox.it.modelling4all.bc2nlinstaller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Displays an option with a JTextField surround by text
 * 
 * @author Ken Kahn
 *
 */
@SuppressWarnings("serial")
public class TextOption extends JPanel {

    private JTextField textField;

    public TextOption(String value, String title, String comment, int columns) {
	super(new BorderLayout());
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	JLabel titleLabel = new JLabel(title);
	titleLabel.setFont(new Font("Courier", Font.PLAIN, 14));
	add(titleLabel);
	textField = new JTextField(value, columns);
	textField.setFont(new Font("Courier", Font.BOLD, 14));
	add(textField);
	JLabel commentLabel = new JLabel(comment);
	commentLabel.setFont(new Font("Courier", Font.ITALIC, 14));
	add(commentLabel);
	setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    public String getValue() {
        return textField.getText().trim();
    }

}
