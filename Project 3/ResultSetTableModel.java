/*
Name: Ryan Ramdihal
Course: CNT 4714 Fall 2024
Assignment title: Project 3
Date: October 20, 2024
ResultSetTableModel
*/
// A TableModel that supplies ResultSet data to a JTable.
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import javax.swing.table.AbstractTableModel;
import java.util.Properties;
import com.mysql.cj.jdbc.MysqlDataSource;


// ResultSet rows and columns are counted from 1 and JTable
// rows and columns are counted from 0. When processing
// ResultSet rows or columns for use in a JTable, it is
// necessary to add 1 to the row or column number to manipulate
// the appropriate ResultSet column (i.e., JTable column 0 is
// ResultSet column 1 and JTable row 0 is ResultSet row 1).
public class ResultSetTableModel extends AbstractTableModel
{
   private Connection connection;
   private Statement statement;
   private ResultSet resultSet;
   private ResultSetMetaData metaData;
   private int numberOfRows;
   private String currentUser; // To store the current user's username

   // keep track of database connection status
   private boolean connectedToDatabase = false;

   // constructor initializes resultSet and obtains its meta data object;
   // determines number of rows
   public ResultSetTableModel( Connection connection,String query )
           throws SQLException, ClassNotFoundException
   {
         statement = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
         //this.currentUser = connection.getMetaData().getUserName(); // Get the username
         //System.out.println("Current user: " + this.currentUser); // Print to terminal for testing
         // update database connection status
         connectedToDatabase = true;

         // set query and execute it
         //setQuery( query );

         //set update and execute it
        // setUpdate( query );


   } // end constructor ResultSetTableModel

   // get class that represents column type
   public Class getColumnClass( int column ) throws IllegalStateException
   {
      // ensure database connection is available
      if ( !connectedToDatabase )
         throw new IllegalStateException( "Not Connected to Database" );

      // determine Java class of column
      try
      {
         String className = metaData.getColumnClassName( column + 1 );

         // return Class object that represents className
         return Class.forName( className );
      } // end try
      catch ( Exception exception )
      {
         exception.printStackTrace();
      } // end catch

      return Object.class; // if problems occur above, assume type Object
   } // end method getColumnClass

   // get number of columns in ResultSet
   public int getColumnCount() throws IllegalStateException
   {
      // ensure database connection is available
      if ( !connectedToDatabase )
         throw new IllegalStateException( "Not Connected to Database" );

      // determine number of columns
      try
      {
         return metaData.getColumnCount();
      } // end try
      catch ( SQLException sqlException )
      {
         sqlException.printStackTrace();
      } // end catch

      return 0; // if problems occur above, return 0 for number of columns
   } // end method getColumnCount

   // get name of a particular column in ResultSet
   public String getColumnName( int column ) throws IllegalStateException
   {
      // ensure database connection is available
      if ( !connectedToDatabase )
         throw new IllegalStateException( "Not Connected to Database" );

      // determine column name
      try
      {
         return metaData.getColumnName( column + 1 );
      } // end try
      catch ( SQLException sqlException )
      {
         sqlException.printStackTrace();
      } // end catch

      return ""; // if problems, return empty string for column name
   } // end method getColumnName

   // return number of rows in ResultSet
   public int getRowCount() throws IllegalStateException
   {
      // ensure database connection is available
      if ( !connectedToDatabase )
         throw new IllegalStateException( "Not Connected to Database" );

      return numberOfRows;
   } // end method getRowCount

   // obtain value in particular row and column
   public Object getValueAt( int row, int column )
           throws IllegalStateException
   {
      // ensure database connection is available
      if ( !connectedToDatabase )
         throw new IllegalStateException( "Not Connected to Database" );

      // obtain a value at specified ResultSet row and column
      try
      {
         resultSet.next();  /* fixes a bug in MySQL/Java with date format */
         resultSet.absolute( row + 1 );
         return resultSet.getObject( column + 1 );
      } // end try
      catch ( SQLException sqlException )
      {
         sqlException.printStackTrace();
      } // end catch

      return ""; // if problems, return empty string object
   } // end method getValueAt

   // set new database query string
   public void setQuery( String query )
           throws SQLException, IllegalStateException {
      // ensure database connection is available
      if (!connectedToDatabase)
         throw new IllegalStateException("Not Connected to Database");

      // specify query and execute it
      resultSet = statement.executeQuery(query);

      // obtain meta data for ResultSet
      metaData = resultSet.getMetaData();

      // determine number of rows in ResultSet
      resultSet.last();                   // move to last row
      numberOfRows = resultSet.getRow();  // get row number


      //use project3app.properties file to get project3app "user" connection to the operationslog database
      //establish a connection to the operationslog DB
      //use this connection for the updating of the operationscount table
   try {
      FileInputStream filein = null;
      Properties properties = new Properties();
      MysqlDataSource dataSource = new MysqlDataSource();
      Connection operationsConnection = null;
      filein = new FileInputStream("operationslog.properties");
      properties.load(filein);
      String url = properties.getProperty("MYSQL_DB_URL");
      dataSource.setURL(url);
      filein = new FileInputStream("project3app.properties");
      properties.load(filein);
      String USERname = properties.getProperty("MYSQL_DB_USERNAME");
      String PASSword = properties.getProperty("MYSQL_DB_PASSWORD");
      dataSource.setUser(USERname);
      dataSource.setPassword(PASSword);
      operationsConnection = DriverManager.getConnection(url, USERname, PASSword);

     // PreparedStatement P = operationsConnection.prepareStatement(command string);
   } catch (FileNotFoundException e) {
       throw new RuntimeException(e);
   } catch (IOException e) {
       throw new RuntimeException(e);
   }


       //2. identify user issuing query through Connection connection which is being passed through ResultSetTableModel
      //get metadata for the connection object
      //extract username from the connection object - needs to know who owns the connection

    //  this.currentUser = connection.getMetaData().getUserName(); // Get the username
      //3.using the connection send the update command
      //4. close connection to operationslog database

      //additional details for the 4 steps above

      //create preparestatement command strings
      //need one to find the user in the operationscount table
      //             "select * from operationscount where login_username = ?;
      //need one for inserting a new user into the operationscount table// username,1,0
      //need one for updating the operationscount table to increment number of queries

      //create the preparedstatement objects - one for each command string above
      //preparedstatement "name of preparedstatement object" = oeprationsConnection.prepareStatement(command string);

      //find row in the operationscount table that belongs to the user issuing the command -
      //if there is currently no row for this user, then this is their first command issued - add a new row
      //                        to the operationscount table for this user with the values:("login-username",1.0)
      //if a row already exists in the operationscount table for this user, then they have issued previously
      //       increment by 1 the number of query commands issued by this user.
      //set the username parameter for the preparedstatement object
      //       operationcountStatement1.setString(1,user_name);

      //run the preparedstatement to see if the user is in the operationscount table
      //if resultset is empty on return - then you have to add a new user and need to enter a new row into the operationscount table
      // parameter values would be (username,1,0)
      //Else ResultSet contained the username - so user issued commands before - need to update their row in the operationscount table
      //this user has already issued commands and is already represented in the operationscount table


      // notify JTable that model has changed
      fireTableStructureChanged();

   }




   // set new database update-query string
   public int setUpdate( String query )
           throws SQLException, IllegalStateException
   {
      int res;
      // ensure database connection is available
      if ( !connectedToDatabase )
         throw new IllegalStateException( "Not Connected to Database" );

      // specify query and execute it
      res = statement.executeUpdate( query );
/*
      // obtain meta data for ResultSet
      metaData = resultSet.getMetaData();
      // determine number of rows in ResultSet
      resultSet.last();                   // move to last row
      numberOfRows = resultSet.getRow();  // get row number
*/
      // notify JTable that model has changed
      //fireTableStructureChanged();
      return res;
   } // end method setUpdate

   // close Statement and Connection
   public void disconnectFromDatabase()
   {
      if ( !connectedToDatabase )
         return;
         // close Statement and Connection
      else try
      {
         statement.close();
         connection.close();
      } // end try
      catch ( SQLException sqlException )
      {
         sqlException.printStackTrace();
      } // end catch
      finally  // update database connection status
      {
         connectedToDatabase = false;
      } // end finally
   } // end method disconnectFromDatabase
}  // end class ResultSetTableModel

