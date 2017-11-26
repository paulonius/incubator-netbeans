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
package org.netbeans.modules.hibernate.loaders.cfg.multiview;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import org.netbeans.api.project.SourceGroup;
import org.openide.util.NbBundle;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.FilterNode;

/**
 * A panel to allow the user to browse the folders 
 * 
 * @author  Dongmei Cao
 */
public class BrowseFolders extends javax.swing.JPanel implements ExplorerManager.Provider {

    private ExplorerManager manager;
    private SourceGroup[] folders;
    private static JScrollPane SAMPLE_SCROLL_PANE = new JScrollPane();

    /** Creates new form BrowseFolders */
    public BrowseFolders(SourceGroup[] folders) {
        initComponents();
        this.folders = folders;
        manager = new ExplorerManager();
        AbstractNode rootNode = new AbstractNode(new SourceGroupsChildren(folders));
        manager.setRootContext(rootNode);

        // Create the templates view
        BeanTreeView btv = new BeanTreeView();
        btv.setRootVisible(false);
        btv.setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
        btv.setBorder(SAMPLE_SCROLL_PANE.getBorder());
        folderPanel.add(btv, java.awt.BorderLayout.CENTER);
    }

    // ExplorerManager.Provider implementation ---------------------------------
    public ExplorerManager getExplorerManager() {
        return manager;
    }

    public static FileObject showDialog(SourceGroup[] folders) {

        BrowseFolders bf = new BrowseFolders(folders);

        JButton options[] = new JButton[]{
            new JButton(NbBundle.getMessage(BrowseFolders.class, "LBL_SelectFile")),
            new JButton(NbBundle.getMessage(BrowseFolders.class, "LBL_Cancel"))
        };

        OptionsListener optionsListener = new OptionsListener(bf);

        options[ 0].setActionCommand(OptionsListener.COMMAND_SELECT);
        options[ 0].addActionListener(optionsListener);
        options[ 0].getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(BrowseFolders.class, "ACSD_SelectFile"));
        options[ 1].setActionCommand(OptionsListener.COMMAND_CANCEL);
        options[ 1].addActionListener(optionsListener);
        options[ 1].getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(BrowseFolders.class, "ACSD_Cancel"));

        DialogDescriptor dialogDescriptor = new DialogDescriptor(
                bf, // innerPane
                NbBundle.getMessage(BrowseFolders.class, "LBL_BrowseFiles"), // displayName
                true, // modal
                options, // options
                options[ 0], // initial value
                DialogDescriptor.BOTTOM_ALIGN, // options align
                null, // helpCtx
                null);                                 // listener 

        dialogDescriptor.setClosingOptions(new Object[]{options[ 0], options[ 1]});

        Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        dialog.setVisible(true);

        return optionsListener.getResult();

    }

    // Innerclasses ------------------------------------------------------------
    /** Children to be used to show FileObjects from given SourceGroups
     */
    private final class SourceGroupsChildren extends Children.Keys {

        private SourceGroup[] groups;
        private SourceGroup group;
        private FileObject fo;

        public SourceGroupsChildren(SourceGroup[] groups) {
            this.groups = groups;
        }

        public SourceGroupsChildren(FileObject fo, SourceGroup group) {
            this.fo = fo;
            this.group = group;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void addNotify() {
            super.addNotify();
            setKeys(getKeys());
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
            super.removeNotify();
        }

        protected Node[] createNodes(Object key) {

            FileObject fObj = null;
            SourceGroup grp = null;
            boolean isFile = false;

            if (key instanceof SourceGroup) {
                fObj = ((SourceGroup) key).getRootFolder();
                grp = (SourceGroup) key;
            } else if (key instanceof Key) {
                fObj = ((Key) key).folder;
                grp = ((Key) key).group;
                if (!fObj.isFolder()) {
                    isFile = true;
                }
            }

            try {
                DataObject dobj = DataObject.find(fObj);
                FilterNode fn = (isFile ? new SimpleFilterNode(dobj.getNodeDelegate(), Children.LEAF) : new SimpleFilterNode(dobj.getNodeDelegate(), new SourceGroupsChildren(fObj, grp)));
                if (key instanceof SourceGroup) {
                    fn.setDisplayName(grp.getDisplayName());
                }

                return new Node[]{fn};
            } catch (DataObjectNotFoundException e) {
                return null;
            }
        }

        private Collection getKeys() {

            if (groups != null) {
                return Arrays.asList(groups);
            } else {
                FileObject files[] = fo.getChildren();
                Arrays.sort(files, new Comparator<FileObject>(){
                    public int compare(FileObject f1, FileObject f2) {
                        return f1.getName().compareTo( f2.getName());
                    }
                });
                
                ArrayList<Key> children = new ArrayList<Key>(files.length);

                for (int i = 0; i < files.length; i++) {
                    if (group.contains(files[i]) && files[i].isFolder()) {
                        children.add(new Key(files[i], group));
                    }
                }
                // add files
                for (int i = 0; i < files.length; i++) {
                    if (group.contains(files[i]) && !files[i].isFolder()) {
                        children.add(new Key(files[i], group));
                    }
                }

                return children;
            }
        }

        private class Key {

            private FileObject folder;
            private SourceGroup group;

            private Key(FileObject folder, SourceGroup group) {
                this.folder = folder;
                this.group = group;
            }
        }
    }

    private static final class OptionsListener implements ActionListener {

        public static final String COMMAND_SELECT = "SELECT"; //NOI18N
        public static final String COMMAND_CANCEL = "CANCEL"; //NOI18N
        private BrowseFolders browsePanel;
        private FileObject result;

        public OptionsListener(BrowseFolders browsePanel) {
            this.browsePanel = browsePanel;
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (COMMAND_SELECT.equals(command)) {
                Node selection[] = browsePanel.getExplorerManager().getSelectedNodes();

                if (selection != null && selection.length > 0) {
                    DataObject dobj = (DataObject) selection[0].getLookup().lookup(DataObject.class);
                    result = dobj.getPrimaryFile();
                }
            }
        }

        public FileObject getResult() {
            return result;
        }
    }

    class SimpleFilterNode extends FilterNode {

        public SimpleFilterNode(org.openide.nodes.Node node, org.openide.nodes.Children children) {
            super(node, children);

        }

        @Override
        public org.openide.util.actions.SystemAction[] getActions(boolean context) {
            return new org.openide.util.actions.SystemAction[]{};
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        folderPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(BrowseFolders.class, "LBL_Folders")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        add(jLabel1, gridBagConstraints);

        folderPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(folderPanel, gridBagConstraints);

        getAccessibleContext().setAccessibleDescription("null");
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel folderPanel;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
