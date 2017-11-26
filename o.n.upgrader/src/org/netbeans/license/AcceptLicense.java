/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.netbeans.license;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.netbeans.util.Util;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
/**
 * Displays LicensePanel to user. User must accept license to continue. 
 * if user does not accept license UserCancelException is thrown.
 *
 * @author  Marek Slama
 */

public final class AcceptLicense {
    
    private static String command;
    
    /** If License was not accepted during installation user must accept it here. 
     */
    public static void showLicensePanel () throws Exception {
        Util.setDefaultLookAndFeel();
        URL url = AcceptLicense.class.getResource("LICENSE.txt"); // NOI18N
        LicensePanel licensePanel = new LicensePanel(url);
        ResourceBundle bundle = NbBundle.getBundle(AcceptLicense.class);
        String yesLabel = bundle.getString("MSG_LicenseYesButton");
        String noLabel = bundle.getString("MSG_LicenseNoButton");
        JButton yesButton = new JButton();
        JButton noButton = new JButton();
        setLocalizedText(yesButton,yesLabel);
        setLocalizedText(noButton,noLabel);
        
        yesButton.setActionCommand("yes"); // NOI18N
        noButton.setActionCommand("no"); // NOI18N
        
        yesButton.getAccessibleContext().setAccessibleName(bundle.getString("ACSN_AcceptButton"));
        yesButton.getAccessibleContext().setAccessibleName(bundle.getString("ACSD_AcceptButton"));
        
        noButton.getAccessibleContext().setAccessibleName(bundle.getString("ACSN_RejectButton"));
        noButton.getAccessibleContext().setAccessibleName(bundle.getString("ACSD_RejectButton"));
        
        Dimension yesPF = yesButton.getPreferredSize();
        Dimension noPF = noButton.getPreferredSize();
        int maxWidth = Math.max(yesButton.getPreferredSize().width, noButton.getPreferredSize().width);
        int maxHeight = Math.max(yesButton.getPreferredSize().height, noButton.getPreferredSize().height);
        yesButton.setPreferredSize(new Dimension(maxWidth, maxHeight));
        noButton.setPreferredSize(new Dimension(maxWidth, maxHeight));
        
        final JDialog d = new JDialog(null, bundle.getString("MSG_LicenseDlgTitle"), Dialog.ModalityType.APPLICATION_MODAL);
        Util.initIcons(d);
        d.getAccessibleContext().setAccessibleName(bundle.getString("ACSN_LicenseDlg"));
        d.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_LicenseDlg"));
        d.getContentPane().add(licensePanel,BorderLayout.CENTER);
        ActionListener listener = new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                command = e.getActionCommand();
                d.setVisible(false);
            }
        };
        yesButton.addActionListener(listener);
        noButton.addActionListener(listener);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(17,12,11,11));
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        d.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
        d.setSize(new Dimension(600,600));
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setResizable(true);
        d.getRootPane().setDefaultButton(yesButton);
        d.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "exit"); //NOI18N
        d.getRootPane().getActionMap().put("exit", new AbstractAction() { //NOI18N
            public void actionPerformed(ActionEvent e) {
                command = "no"; //NOI18N
                d.setVisible(false);
            }
        });

        licensePanel.jEditorPane1.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "accept"); //NOI18N
        licensePanel.jEditorPane1.getActionMap().put("accept", new AbstractAction() { //NOI18N
            public void actionPerformed(ActionEvent e) {
                command = "yes"; //NOI18N
                d.setVisible(false);
            }
        });

        //Center on screen
        d.setLocationRelativeTo(null);
        d.setVisible(true);
        
        if ("yes".equals(command)) {  // NOI18N
            return;
        } else {
            throw new org.openide.util.UserCancelException();
        }
    }

    /**
     * Actual setter of the text & mnemonics for the AbstractButton or
     * their subclasses. We must copy necessary code from org.openide.awt.Mnemonics
     * because org.openide.awt module is not available yet when this code is called.
     * @param item AbstractButton
     * @param text new label
     */
    private static void setLocalizedText (AbstractButton button, String text) {
        if (text == null) {
            button.setText(null);
            return;
        }

        int i = findMnemonicAmpersand(text);

        if (i < 0) {
            // no '&' - don't set the mnemonic
            button.setText(text);
            button.setMnemonic(0);
        } else {
            button.setText(text.substring(0, i) + text.substring(i + 1));
            
            if (Utilities.isMac()) {
                // there shall be no mnemonics on macosx.
                //#55864
                return;
            }

            char ch = text.charAt(i + 1);

            // it's latin character or arabic digit,
            // setting it as mnemonics
            button.setMnemonic(ch);

            // If it's something like "Save &As", we need to set another
            // mnemonic index (at least under 1.4 or later)
            // see #29676
            button.setDisplayedMnemonicIndex(i);
        }
    }
    
    /**
     * Searches for an ampersand in a string which indicates a mnemonic.
     * Recognizes the following cases:
     * <ul>
     * <li>"Drag & Drop", "Ampersand ('&')" - don't have mnemonic ampersand.
     *      "&" is not found before " " (space), or if enclosed in "'"
     *     (single quotation marks).
     * <li>"&File", "Save &As..." - do have mnemonic ampersand.
     * <li>"Rock & Ro&ll", "Underline the '&' &character" - also do have
     *      mnemonic ampersand, but the second one.
     * </ul>
     * @param text text to search
     * @return the position of mnemonic ampersand in text, or -1 if there is none
     */
    public static int findMnemonicAmpersand(String text) {
        int i = -1;

        do {
            // searching for the next ampersand
            i = text.indexOf('&', i + 1);

            if ((i >= 0) && ((i + 1) < text.length())) {
                // before ' '
                if (text.charAt(i + 1) == ' ') {
                    continue;

                    // before ', and after '
                } else if ((text.charAt(i + 1) == '\'') && (i > 0) && (text.charAt(i - 1) == '\'')) {
                    continue;
                }

                // ampersand is marking mnemonics
                return i;
            }
        } while (i >= 0);

        return -1;
    }
}
