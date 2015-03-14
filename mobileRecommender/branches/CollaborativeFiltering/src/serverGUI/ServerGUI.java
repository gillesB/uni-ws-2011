package serverGUI;

import javax.swing.DefaultListModel;

/**
 * Initializes only the Swing GUI
 * 
 * 
 */
public class ServerGUI extends javax.swing.JFrame {

	private ServerGUIInterface serverGUIInterface;

	/** Creates new form NewJFrame */
	public ServerGUI(ServerGUIInterface serverGUIInterface) {
		this.serverGUIInterface = serverGUIInterface;
		initComponents();
	}

	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {
		btnGetRecommendation = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstMovies = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        btnSelectMovie = new javax.swing.JButton();
        txtAmountVisitors = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        edtSelectedMovie = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rosbud Server");

        btnGetRecommendation.setText("Recommend Movie");
        btnGetRecommendation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetRecommendationActionPerformed(evt);
            }
        });

        jLabel2.setText("Amount of Visitors for today's surprise movie:");

        lstMovies.setModel(new DefaultListModel());
        jScrollPane2.setViewportView(lstMovies);

        jLabel3.setText("Recommended Movies");

        btnSelectMovie.setText("Select Movie");
        btnSelectMovie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectMovieActionPerformed(evt);
            }
        });

        txtAmountVisitors.setText("");

        edtSelectedMovie.setColumns(20);
        edtSelectedMovie.setEditable(false);
        edtSelectedMovie.setLineWrap(true);
        edtSelectedMovie.setRows(5);
        edtSelectedMovie.setText("No movie was selected as today's Surprise Movie");
        edtSelectedMovie.setWrapStyleWord(true);
        jScrollPane1.setViewportView(edtSelectedMovie);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(btnGetRecommendation)
                            .addGap(18, 18, 18)
                            .addComponent(btnSelectMovie))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAmountVisitors)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtAmountVisitors))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGetRecommendation)
                    .addComponent(btnSelectMovie))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        pack();

	}// </editor-fold>

	private void btnGetRecommendationActionPerformed(java.awt.event.ActionEvent evt) {
		serverGUIInterface.getRecommendation();
		txtAmountVisitors.setText(serverGUIInterface.getAmountVisitors());
	}

	private void btnSelectMovieActionPerformed(java.awt.event.ActionEvent evt) {
		if (!lstMovies.isSelectionEmpty()) {
			MovieListItem m = (MovieListItem) lstMovies.getSelectedValue();
			edtSelectedMovie.setText(m.moviename + " was selected as today's Surprise Movie");

			serverGUIInterface.selectSurpriseMovie(m.movieID);
		}
	}

	public class MovieListItem {
		String moviename, rating;
		int movieID;

		public MovieListItem(String moviename, int movieID, String rating) {
			super();
			this.moviename = moviename;
			this.movieID = movieID;
			this.rating = rating;
		}

		@Override
		public String toString() {
			return moviename + " (" + rating + ")";
		}

	}

	// Variables declaration - do not modify
	javax.swing.JButton btnGetRecommendation;
	javax.swing.JLabel jLabel2;
	javax.swing.JLabel jLabel3;
	javax.swing.JScrollPane jScrollPane1;
	javax.swing.JScrollPane jScrollPane2;
	javax.swing.JList lstMovies;
	javax.swing.JButton btnSelectMovie;
	javax.swing.JTextArea edtSelectedMovie;
	javax.swing.JLabel txtAmountVisitors;
	// End of variables declaration

}
