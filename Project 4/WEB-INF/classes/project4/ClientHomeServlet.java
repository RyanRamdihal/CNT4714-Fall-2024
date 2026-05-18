package project4;
/* Name:Ryan Ramdihal
Course: CNT 4714 – Fall 2024 – Project Four
Assignment title: A Three-Tier Distributed Web-Based Application
Date: December 1, 2024
*/
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.Properties;

public class ClientHomeServlet extends HttpServlet {
    private Connection connection;
    private Statement statement;

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sqlQuery = request.getParameter("sql");
        String message = "";

        if (sqlQuery != null && !sqlQuery.trim().isEmpty()) {
            getDBConnection();
            try {
                if (sqlQuery.toLowerCase().startsWith("select")) {
                    message = executeSelectQuery(sqlQuery);
                } else {
                    String commandType = sqlQuery.split("\\s+")[0];
                    message = "<div style='color: red;'>Error executing the SQL statement: " + commandType.toUpperCase() + " command denied to user.</div>";
                }
            } catch (SQLException e) {
                message = "<div style='color: red;'>Error executing the SQL statement: " + e.getMessage() + "</div>";
            }
        }

        request.setAttribute("message", message);
        request.setAttribute("sqlQuery", sqlQuery);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/clientHome.jsp");
        dispatcher.forward(request, response);
    }

    private String executeSelectQuery(String sqlQuery) throws SQLException {
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        ResultSetMetaData metaData = resultSet.getMetaData();
        int numOfColumns = metaData.getColumnCount();
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<table>");

        // Table header
        htmlBuilder.append("<tr>");
        for (int i = 1; i <= numOfColumns; i++) {
            htmlBuilder.append("<th>").append(metaData.getColumnName(i)).append("</th>");
        }
        htmlBuilder.append("</tr>");

        // Table body
        while (resultSet.next()) {
            htmlBuilder.append("<tr>");
            for (int i = 1; i <= numOfColumns; i++) {
                htmlBuilder.append("<td>").append(resultSet.getString(i)).append("</td>");
            }
            htmlBuilder.append("</tr>");
        }
        htmlBuilder.append("</table>");

        return htmlBuilder.toString();
    }

    private void getDBConnection() throws ServletException {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(getServletContext().getRealPath("/WEB-INF/lib/client.properties")));

            String driver = props.getProperty("MYSQL_DB_DRIVER_CLASS");
            String url = props.getProperty("MYSQL_DB_URL");
            String dbUsername = props.getProperty("MYSQL_DB_USERNAME");
            String dbPassword = props.getProperty("MYSQL_DB_PASSWORD");

            Class.forName(driver);
            connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            statement = connection.createStatement();
        } catch (Exception e) {
            throw new ServletException("Error connecting to database: " + e.getMessage(), e);
        }
    }

    @Override
    public void destroy() {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        // Empty main method
    }
}
