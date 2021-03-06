package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import database.Database;

/**
 * Finestra della GUI per cambiare la password dell'account attualmente loggato.
 */
public class FinestraCambiaPassword extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPasswordField pass;
	private String user;
	
	/**
	 * Crea la finestra e inizializza tutti i suoi componenti.
	 * Facendo clic sul pulsanti di conferma viene sovrascritto il campo password sul database, 
	 * nella tupla corrispondente all'utente passato come parametro.
	 * @param user L'username dell'utente attualmente loggato.
	 */
	public FinestraCambiaPassword(String user) {
		
		super();

		final int LARGHEZZA_FINESTRA = 600;
		final int ALTEZZA_FINESTRA = 150;
		final int POSIZIONE_FINESTRA_X = 600;
		final int POSIZIONE_FINESTRA_Y = 300;
		final int LARGHEZZA_CAMPO_PASS = 200;
		final int ALTEZZA_CAMPO_PASS = 40;
		final int LARGHEZZA_PULSANTE_CONFERMA = 150;
		final int ALTEZZA_PULSANTE_CONFERMA = 40;
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		setTitle("Cambia password");
		setResizable(false);
		this.setIconImage(new ImageIcon("icon.png").getImage());
		setSize(LARGHEZZA_FINESTRA, ALTEZZA_FINESTRA);
		setLocation(POSIZIONE_FINESTRA_X, POSIZIONE_FINESTRA_Y);
		setLayout(new FlowLayout());
		
		this.user = user;
		
		final String passwordText = "Password";
		pass = new JPasswordField(passwordText);
		pass.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {

				String password = "";
				char[] passchar = pass.getPassword();
				
				for (int i = 0; i < passchar.length; i++) {
					password = password.concat(String.valueOf(passchar[i]));
				}
				if (password.equals(passwordText)) {
					pass.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {}
			
		});
		pass.setPreferredSize(new Dimension(LARGHEZZA_CAMPO_PASS, ALTEZZA_CAMPO_PASS));
		
		JButton conferma = new JButton("Conferma");
		conferma.addActionListener(this);
		conferma.setPreferredSize(new Dimension(LARGHEZZA_PULSANTE_CONFERMA, ALTEZZA_PULSANTE_CONFERMA));
		
		add(new JLabel("Nuova password (almeno 8 caratteri)"));
		add(pass);
		add(conferma);
	}

	/**
	 * Metodo eseguito al clic sul pulsante di conferma.
	 * Esegue le operazioni di basso livello necessarie a cambiare la password.
	 * In particolare invia al database una query di update e mostra delle finestre in caso di errore.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			Database dbUtility = new Database(true);
			String password = "";
			
			char[] charPassword = pass.getPassword();
			for (int i = 0; i < charPassword.length; i++) {
				password = password.concat(String.valueOf(pass.getPassword()[i]));
			}

			boolean inputValido = true;
			if (password.length() < 8 || password.contains(" ")) {
				inputValido = false;
			}
			if (!inputValido) {
				throw new InputInvalidoException(null);
			}
			
			String query = "UPDATE Utente SET password = ? WHERE nomeUtente = ?";
			PreparedStatement ps = dbUtility.preparaQuery(query);
			ps.setString(1, password);
			ps.setString(2, FinestraCambiaPassword.this.user);
			dbUtility.eseguiQueryPreparata(ps);
			
			dispose();
			JOptionPane.showMessageDialog(null, "Password cambiata con successo", "Successo", JOptionPane.INFORMATION_MESSAGE);
		} catch (ClassNotFoundException | IOException | SQLException e1) {
			JOptionPane.showMessageDialog(null, "Errore di connessione al server", "Errore", JOptionPane.ERROR_MESSAGE);
		} catch (InputInvalidoException f) {;}
	}
}
