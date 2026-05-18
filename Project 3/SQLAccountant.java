/*
Name: Ryan Ramdihal
Course: CNT 4714 Fall 2024
Assignment title: Project 3 – A Specialized Accountant Application
Date: October 20, 2024
*/
import com.mysql.cj.jdbc.MysqlDataSource;
import java.awt.*;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class SQLAccountant extends JFrame {
   // default query retrieves all data from bikes table
   //static final String DEFAULT_QUERY = " ";//SELECT * FROM bikes
   private Connection connection;
   private ResultSetTableModel tableModel;
   private JTextArea queryArea;
   private JTable resultTable;
    private TableModel empty;
    private boolean connectedToDatabase =false;



    // create ResultSetTableModel and GUI
   public SQLAccountant() {
      super("SQL Accountant App - FALL 2024");

      // create ResultSetTableModel and display database table
       // create TableModel for results of query SELECT * FROM bikes
       //tableModel = new ResultSetTableModel(DEFAULT_QUERY);

       // Top Left Panel: 2-column layout (7 rows) with labels, combo boxes, and text fields
       JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));

       // Title for Connection Details
       JLabel connectionTitle = new JLabel("Connection Details");
       connectionTitle.setHorizontalAlignment(JLabel.CENTER);
       connectionTitle.setForeground(Color.BLUE);
       formPanel.add(connectionTitle);
       formPanel.add(new JLabel());  // Empty label to maintain grid structure

       // Row 1: DB URL Properties
       JLabel URL = new JLabel("DB URL Properties");
       JComboBox<String> URLitems = new JComboBox<>(new String[] { "operationslog.properties" });
       formPanel.add(URL);
       formPanel.add(URLitems);

       // Row 2: User Properties
       JLabel userProperties = new JLabel("User Properties");
       JComboBox<String> userItems = new JComboBox<>(new String[] { "project3app.properties"});
       formPanel.add(userProperties);
       formPanel.add(userItems);

       // Row 3: Username
       JLabel Username = new JLabel("Username");
       JTextField UsernameText = new JTextField();
       formPanel.add(Username);
       formPanel.add(UsernameText);

       // Row 4: Password
       JLabel Password = new JLabel("Password");
       JPasswordField PasswordText = new JPasswordField();
       formPanel.add(Password);
       formPanel.add(PasswordText);

       // Row 5: Connect and Disconnect Buttons
       JButton connectButton = new JButton("Connect to Database");
       connectButton.setBackground(Color.CYAN);
       connectButton.setForeground(Color.BLACK);
       formPanel.add(connectButton);

       // Add the Disconnect Button
       JButton disconnectButton = new JButton("Disconnect from Database");
       disconnectButton.setBackground(Color.RED);
       disconnectButton.setForeground(Color.BLACK);
       formPanel.add(disconnectButton);

       formPanel.add(new JLabel());  // Empty label to maintain grid structure

       JLabel Status = new JLabel("NOT CONNECTED");
       Status.setBackground(Color.BLACK);
       Status.setForeground(Color.RED);
       formPanel.add(Status);
       formPanel.add(new JLabel());  // Empty label to maintain grid structure

       // Top Right Panel: Query text area with clear and submit buttons
       queryArea = new JTextArea("", 3, 40);
       String query = queryArea.getText().trim();
       queryArea.setWrapStyleWord(true);
       queryArea.setLineWrap(true);

       // Create a title label for the query area
       JLabel queryTitleLabel = new JLabel("Enter an SQL Command");
       queryTitleLabel.setForeground(Color.BLUE);
       queryTitleLabel.setHorizontalAlignment(JLabel.CENTER);

       JScrollPane scrollPane = new JScrollPane(queryArea,
               ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
               ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

       // Create panel for the query text area and buttons
       JPanel queryPanel = new JPanel();
       queryPanel.setLayout(new BorderLayout());
       queryPanel.add(queryTitleLabel, BorderLayout.NORTH);
       queryPanel.add(scrollPane, BorderLayout.CENTER);

       // Clear Query Button
       JButton clearQueryButton = new JButton("Clear Query");
       clearQueryButton.setBackground(Color.YELLOW);
       clearQueryButton.setForeground(Color.BLACK);

       // Submit Button
       JButton submitButton = new JButton("Submit Query");
       submitButton.setBackground(Color.GREEN);
       submitButton.setForeground(Color.BLACK);

       // Create a button panel to hold both buttons
       JPanel buttonPanel = new JPanel();
       buttonPanel.add(clearQueryButton);
       buttonPanel.add(submitButton);
       queryPanel.add(buttonPanel, BorderLayout.SOUTH);

       // Bottom: JTable delegate for tableModel
       resultTable = new JTable(tableModel);
       resultTable.setGridColor(Color.BLACK);
       JScrollPane tableScrollPane = new JScrollPane(resultTable);

       // Main panel combining the top left (formPanel) and top right (queryPanel)
       JPanel topPanel = new JPanel(new BorderLayout());
       topPanel.add(formPanel, BorderLayout.WEST);
       topPanel.add(queryPanel, BorderLayout.EAST);

       // Add components to the frame
       add(topPanel, BorderLayout.NORTH);
       add(tableScrollPane, BorderLayout.CENTER);

       // Bottom Button: Clear Results
       JButton clearButton = new JButton("Clear Results");
       clearButton.setBackground(Color.YELLOW);
       clearButton.setForeground(Color.BLACK);

       // Add Clear Results button at the bottom
       JPanel bottomPanel = new JPanel();
       bottomPanel.add(clearButton);

       // Create and add Close Application button
       JButton closeButton = new JButton("Close Application");
       closeButton.setBackground(Color.RED);
       closeButton.setForeground(Color.BLACK);
       bottomPanel.add(closeButton);

       // Add the bottom panel to the frame
       add(bottomPanel, BorderLayout.SOUTH);

       // Event listener for the Clear Results button
       clearButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
             resultTable.setModel(new DefaultTableModel());
              resultTable.setModel(empty);
          }
       });

       // Event listener for the Clear Query button
       clearQueryButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
             queryArea.setText("");
          }
       });

       // Event listener for the Connect to Database button
       connectButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
             //System.out.println("Connect button pressed");
             FileInputStream filein = null;
             Properties properties = new Properties();
             MysqlDataSource dataSource = new MysqlDataSource();
             boolean usernameMatch = false;
             boolean passwordMatch = false;


             try {
                if (connection != null) {
                   connection.close();
                }
                Status.setText("NOT CONNECTED");
                Status.setForeground(Color.RED);

             } catch (SQLException e) {
                throw new RuntimeException(e);
             }
             try {
                String selectedUrlProps = (String) URLitems.getSelectedItem();
                String selectedUserProps = (String) userItems.getSelectedItem();
                filein = new FileInputStream(selectedUrlProps);
                properties.load(filein);
                String url = properties.getProperty("MYSQL_DB_URL");
                 dataSource.setURL(url);
                 filein = new FileInputStream(selectedUserProps);
                properties.load(filein);
                String USER = properties.getProperty("MYSQL_DB_USERNAME");
                String PASS = properties.getProperty("MYSQL_DB_PASSWORD");
                //dataSource.setUser(USER);
                //dataSource.setPassword(PASS);

                if (UsernameText.getText().equals(USER)) {
                   usernameMatch = true;
                   if (String.valueOf(PasswordText.getPassword()).equals(PASS)) {
                      passwordMatch = true;
                   }
                }
                if (passwordMatch && usernameMatch) {
                    dataSource.setUser(USER);
                   dataSource.setPassword(PASS);
                   connection = DriverManager.getConnection(url, USER, PASS);
                    connectedToDatabase = true;
                    Status.setText("Connected to " + (String) properties.getProperty("MYSQL_DB_URL"));
                    Status.setForeground(Color.GREEN);
                    clearButton.setEnabled(true);
                    submitButton.setEnabled(true);
                    clearQueryButton.setEnabled(true);
                    queryArea.setEnabled(true);


                    UsernameText.setEditable(false);
                    PasswordText.setEditable(false);
                    connectButton.setEnabled(false);
                    userItems.setEnabled(false);


                } else {
                   Status.setText("NOT CONNECTED - User Credentials Do Not Match Properties File");
                }
             } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
             } catch (SQLException e) {
                throw new RuntimeException(e);
             } catch (IOException e) {
                throw new RuntimeException(e);
             }


          }});

       // Event listener for the Disconnect from Database button
       // Event listener for the Disconnect from Database button
       disconnectButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent event) {
               try {
                   // Close the database connection
                   connection.close();
                   Status.setText("NOT CONNECTED");
                   Status.setForeground(Color.RED);

                   // Disable components after disconnecting
                   clearButton.setEnabled(false);
                   submitButton.setEnabled(false);
                   clearQueryButton.setEnabled(false);
                   queryArea.setEnabled(false);

                   // Re-enable the connection-related components
                   UsernameText.setEditable(true);
                   PasswordText.setEditable(true);
                   connectButton.setEnabled(true);
                   userItems.setEnabled(true);
                   URLitems.setEnabled(true);
               } catch (SQLException e) {
                   JOptionPane.showMessageDialog(null, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
               }
           }


       });


       // create event listener for submitButton
       submitButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
             try {
                 // activate result table
                 resultTable.setEnabled(true);
                 //scrolling
                 resultTable.setAutoscrolls(true);
                 // connect TableModel for results
                 tableModel = new ResultSetTableModel(connection, queryArea.getText());

                 // if select command is used, use setQuery method
                 // all other commands will use setUpdate method
                 if(queryArea.getText().toLowerCase().startsWith("select")) {
                     tableModel.setQuery(queryArea.getText());
                     resultTable.setModel(tableModel);
                 }
                 else {
                     tableModel.setUpdate(queryArea.getText());
                     empty = new DefaultTableModel();
                     resultTable.setModel(empty);
                 }


             }catch (SQLException e) { // catch database error
                 JOptionPane.showMessageDialog(null, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
             }catch(ClassNotFoundException NotFound) { // catch driver error
                 JOptionPane.showMessageDialog(null, "MySQL driver not found", "Driver not found", JOptionPane.ERROR_MESSAGE);
             }
          }

       });

       // Event listener for the Close Application button
       closeButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent event) {
               try {
                   if(connectedToDatabase) {
                       // Close the database connection
                       connection.close();
                       System.exit(0); // Close the application
                   }
                   else{
                       System.exit(0); // Close the application
                   }
               }catch (SQLException e) {
                   JOptionPane.showMessageDialog(null, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
               }
           }
       });

       setSize(1200, 800);
       setVisible(true);

   }

   // Main method to run the program
   public static void main(String[] args) {
      new SQLAccountant();  // Create and display the application

   }
}

