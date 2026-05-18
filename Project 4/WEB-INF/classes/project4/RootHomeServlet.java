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

public class RootHomeServlet extends HttpServlet {
    private Connection connection;
    private Statement statement;

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sqlQuery = request.getParameter("sql");
        String message = "";

        if (sqlQuery != null && !sqlQuery.trim().isEmpty()) {
            getDBConnection();
            try {
                if (sqlQuery.trim().toLowerCase().startsWith("select")) {
                    ResultSet rs = statement.executeQuery(sqlQuery);
                    message = formatResultSetToHTML(rs);
                } else {
                    int rowsAffected = statement.executeUpdate(sqlQuery);
                    message = "<div>" + rowsAffected + " row(s) affected.</div>";

                    // Check and apply business logic if necessary
                    if (sqlQuery.toLowerCase().matches(".*(insert|update).*shipments.*")) {
                        int updatedRows = applyBusinessLogic();
                        message += "<div>Business Logic Detected!- Updating Supplier Status</div>";
                        message += "<div>Business Logic updated " + updatedRows + " supplier status marks.</div>";
                    }
                }
            } catch (SQLException e) {
                message = "<div style='color: red;'>Error executing the SQL statement: " + e.getMessage() + "</div>";
            }
        } else {
            message = "<div style='color: red;'>Invalid SQL command.</div>";
        }

        request.setAttribute("message", message);
        request.setAttribute("sqlQuery", sqlQuery);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/rootHome.jsp");
        dispatcher.forward(request, response);
    }

    private int applyBusinessLogic() throws SQLException {
        String updateQuery = "UPDATE suppliers SET status = status + 5 WHERE snum IN (SELECT snum FROM shipments WHERE quantity >= 100)";
        return statement.executeUpdate(updateQuery);
    }

    private String formatResultSetToHTML(ResultSet resultSet) throws SQLException {
        StringBuilder htmlBuilder = new StringBuilder("<table>");
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Table header
        htmlBuilder.append("<tr>");
        for (int i = 1; i <= columnCount; i++) {
            htmlBuilder.append("<th>").append(metaData.getColumnName(i)).append("</th>");
        }
        htmlBuilder.append("</tr>");

        // Table body
        while (resultSet.next()) {
            htmlBuilder.append("<tr>");
            for (int i = 1; i <= columnCount; i++) {
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
            props.load(new FileInputStream(getServletContext().getRealPath("/WEB-INF/lib/root.properties")));

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