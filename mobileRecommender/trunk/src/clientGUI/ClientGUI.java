/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ClientGUI.java
 *
 * Created on Jan 3, 2012, 11:32:32 AM
 */

package clientGUI;

import javax.swing.DefaultListModel;

/**
 * 
 * @author me
 */
public class ClientGUI extends javax.swing.JFrame {

	private ClientGUIInterface clientGUIInterface;

	/** Creates new form ClientGUI */
	public ClientGUI(ClientGUIInterface clientGUIInterface) {
		this.clientGUIInterface = clientGUIInterface;
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		edtUsername = new javax.swing.JTextField();
		jScrollPane1 = new javax.swing.JScrollPane();
		lstCurrentMovies = new javax.swing.JList();
		btnBuyTicket = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		edtUsername.setText("Username");

		lstCurrentMovies.setModel(new DefaultListModel());
		jScrollPane1.setViewportView(lstCurrentMovies);

		btnBuyTicket.setText("buy Ticket");
        btnBuyTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

		jLabel1.setText("current Movies");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
		layout.createSequentialGroup().addContainerGap().addGroup(
		layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
		layout.createSequentialGroup().addComponent(jLabel1).addContainerGap(297, Short.MAX_VALUE)).addGroup(
		layout.createSequentialGroup().addGroup(
		layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false).addComponent(jScrollPane1)
		.addComponent(edtUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)).addGap(59, 59, 59)
		.addComponent(btnBuyTicket).addGap(113, 113, 113)))));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
		layout.createSequentialGroup().addContainerGap().addComponent(edtUsername,
		javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
		javax.swing.GroupLayout.PREFERRED_SIZE).addGap(14, 14, 14).addComponent(jLabel1).addPreferredGap(
		javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
		layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(btnBuyTicket).addComponent(
		jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
		javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(16, Short.MAX_VALUE)));

		pack();
	}// </editor-fold>
	
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        clientGUIInterface.buySelectedTicket(lstCurrentMovies.getSelectedIndex());
    }


	// Variables declaration - do not modify
	javax.swing.JTextField edtUsername;
	javax.swing.JButton btnBuyTicket;
	javax.swing.JLabel jLabel1;
	javax.swing.JList lstCurrentMovies;
	javax.swing.JScrollPane jScrollPane1;
	// End of variables declaration

}