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
import com.mysql.cj.jdbc.MysqlDataSource;

public class AccountantHomeServlet extends HttpServlet {
    private Connection connection;
    private CallableStatement callableStatement;

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String selectedQuery = request.getParameter("query");
        String message = "";

        if (selectedQuery != null && !selectedQuery.trim().isEmpty()) {
            getDBConnection();
            try {
                int queryNumber = Integer.parseInt(selectedQuery);
                message = executeStoredProc(queryNumber);
            } catch (NumberFormatException | SQLException e) {
                message = "<div style='color: red;'>Error executing the stored procedure: " + e.getMessage() + "</div>";
            }
        } else {
            message = "<div style='color: red;'>Please select a query to execute.</div>";
        }

        request.setAttribute("message", message);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/accountantHome.jsp");
        dispatcher.forward(request, response);
    }

    private String executeStoredProc(int queryNumber) throws SQLException {
        String result = "";
        switch (queryNumber) {
            case 1:
                callableStatement = connection.prepareCall("{call Get_The_Maximum_Status_Of_All_Suppliers()}");
                break;
            case 2:
                callableStatement = connection.prepareCall("{call Get_The_Sum_Of_All_Parts_Weights()}");
                break;
            case 3:
                callableStatement = connection.prepareCall("{call Get_The_Total_Number_Of_Shipments()}");
                break;
            case 4:
                callableStatement = connection.prepareCall("{call Get_The_Name_Of_The_Job_With_The_Most_Workers()}");
                break;
            case 5:
                callableStatement = connection.prepareCall("{call List_The_Name_And_Status_Of_All_Suppliers()}");
                break;
            default:
                return "<div style='color: red;'>Invalid query selection.</div>";
        }

        boolean hasResults = callableStatement.execute();
        if (hasResults) {
            ResultSet resultSet = callableStatement.getResultSet();
            result = formatResultSet(resultSet);
        }
        return result;
    }

    private String formatResultSet(ResultSet resultSet) throws SQLException {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<table>");
        ResultSetMetaData metaData = resultSet.getMetaData();
        int numOfColumns = metaData.getColumnCount();

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
            props.load(new FileInputStream(getServletContext().getRealPath("/WEB-INF/lib/accountant.properties")));

            String driver = props.getProperty("MYSQL_DB_DRIVER_CLASS");
            String url = props.getProperty("MYSQL_DB_URL");
            String dbUsername = props.getProperty("MYSQL_DB_USERNAME");
            String dbPassword = props.getProperty("MYSQL_DB_PASSWORD");

            Class.forName(driver);
            connection = DriverManager.getConnection(url, dbUsername, dbPassword);
        } catch (Exception e) {
            throw new ServletException("Error connecting to database: " + e.getMessage(), e);
        }
    }

    @Override
    public void destroy() {
        try {
            if (callableStatement != null) callableStatement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        // Empty main method
    }
}
