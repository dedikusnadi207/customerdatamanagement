/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author labuser
 */
public class validator {
    public static void handleNumberOnly(JTextField[] fields){
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent evt) {
                String[] numbers = {"0","1","2","3","4","5","6","7","8","9"};
                if (!ArrayUtils.contains(numbers, evt.getKeyChar()+"")) {
                    evt.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }
        };
        for (JTextField field : fields) {
            field.addKeyListener(keyListener);
        }
    }
}
