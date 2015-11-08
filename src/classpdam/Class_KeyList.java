/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classpdam;

/**
 *
 * @author Engkid
 */

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


public class Class_KeyList implements ActionListener, KeyListener {
    
    

    //JTextField nameInput;


    public Class_KeyList(JTextField textfield){
        //nameInput = textfield;
    }

    @Override
    public void actionPerformed(ActionEvent submitClicked) {

        Component frame = new JFrame();
        //JOptionPane.showMessageDialog(frame , "You've Submitted the name " + nameInput.getText());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ENTER){
            System.out.println("Hello");
        }
        Component frame = new JFrame();
        //JOptionPane.showMessageDialog(frame , "You've Submitted the name " + nameInput.getText());

    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }
}

