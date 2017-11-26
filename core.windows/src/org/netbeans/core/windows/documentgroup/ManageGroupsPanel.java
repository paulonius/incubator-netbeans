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

package org.netbeans.core.windows.documentgroup;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author S. Aubrecht
 */
class ManageGroupsPanel extends javax.swing.JPanel {
    
    private DialogDescriptor descriptor;
    private Dialog dialog;
    private final JButton btnSelect = new JButton(NbBundle.getMessage(ManageGroupsPanel.class, "Btn_SELECT"));
    /**
     * Creates new form ManageGroupsPanel
     */
    public ManageGroupsPanel() {
        initComponents();
        fillGroups();
        btnSelect.setEnabled(false);
        listGroups.getSelectionModel().addListSelectionListener( new ListSelectionListener() {

            @Override
            public void valueChanged( ListSelectionEvent e ) {
                enableButtons();
            }
        } );
        btnSelect.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                DocumentGroupImpl group = (DocumentGroupImpl) listGroups.getSelectedValue();
                if( null != group ) {
                    GroupsManager.getDefault().openGroup( group );
                }
            }
        });
    }
    
    private void enableButtons() {
        btnRemoveAll.setEnabled( listGroups.getModel().getSize() > 0 );
        int selIndex = listGroups.getSelectedIndex();
        if( null != descriptor ) {
            descriptor.setValid( selIndex >= 0 );
        }
        btnSelect.setEnabled(selIndex>=0);
    }

    public void showDialog() {
        descriptor = new DialogDescriptor(this, NbBundle.getMessage(ManageGroupsPanel.class, "Dlg_DOCUMENT_GROUPS"), true, 
                new Object[] { btnSelect, DialogDescriptor.CANCEL_OPTION }, btnSelect, DialogDescriptor.DEFAULT_ALIGN, null, null);
        descriptor.setHelpCtx( new HelpCtx("org.netbeans.core.windows.documentgroup.ManageGroupsPanel") ); //NOI18N
        descriptor.setValid( false );
        dialog = DialogDisplayer.getDefault().createDialog( descriptor );
        dialog.setVisible( true );
        GroupsMenuAction.refreshMenu();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollGroups = new javax.swing.JScrollPane();
        listGroups = new javax.swing.JList();
        btnRemove = new javax.swing.JButton();
        btnRemoveAll = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        listGroups.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollGroups.setViewportView(listGroups);

        org.openide.awt.Mnemonics.setLocalizedText(btnRemove, org.openide.util.NbBundle.getMessage(ManageGroupsPanel.class, "ManageGroupsPanel.btnRemove.text")); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnRemoveAll, org.openide.util.NbBundle.getMessage(ManageGroupsPanel.class, "ManageGroupsPanel.btnRemoveAll.text")); // NOI18N
        btnRemoveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(scrollGroups, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnRemoveAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollGroups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnRemove)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemoveAll))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRemoveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveAllActionPerformed
        GroupsManager.getDefault().removeAllGroups();
        fillGroups();
        enableButtons();
    }//GEN-LAST:event_btnRemoveAllActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        DocumentGroupImpl group = (DocumentGroupImpl) listGroups.getSelectedValue();
        if( null != group ) {
            GroupsManager.getDefault().removeGroup( group );
            fillGroups();
            enableButtons();
        }
    }//GEN-LAST:event_btnRemoveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnRemoveAll;
    private javax.swing.JList listGroups;
    private javax.swing.JScrollPane scrollGroups;
    // End of variables declaration//GEN-END:variables


    private void fillGroups() {
        List<DocumentGroupImpl> groups = GroupsManager.getDefault().getGroups();
        DefaultListModel model = new DefaultListModel();
        for( DocumentGroupImpl group : groups ) {
            model.addElement( group );
        }
        listGroups.setModel( model );
    }
}
