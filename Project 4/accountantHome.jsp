<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Accountant User Home</title>
    <style>
        body {
            background-color: black;
            color: #ffffff;
            font-family: 'Roboto', sans-serif;
            text-align: center;
        }

        .welcome-section h1 {
            color: lime;
            font-size: 42px;
        }

        .welcome-section h2 {
            color: orange;
            font-size: 38px;
        }

        .command-section,
        .results-section {
            text-align: center;
            background-color: gray;
            margin: 20px;
        }

        .form-buttons button {
            font-size: 20px;
            background-color: #FF5722;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 10px;
            margin-right: 10px;
        }

        .results-section {
            background-color: gray;
            padding: 10px;
            border-radius: 5px;
        }

        #resultContent {
            background-color: #263238;
            color: #FFF;
            padding: 10px;
            border-radius: 5px;
            margin-top: 10px;
        }

        label {
            color: blue; /* Set the text color to blue */
            font-size: 18px; /* Optional: Adjust font size for readability */
            margin-bottom: 10px; /* Add spacing between options */
            display: block; /* Ensure each label is on a new line */
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            border: 1px solid #ffffff;
            padding: 8px;
            text-align: left;
        }

        tr:nth-child(even) {
            background-color: #2e2e2e;
        }

        tr:hover {
            background-color: #3e3e2e;
        }

        .accountant-level {
            color: red;
        }
    </style>
</head>
<body>
<section class="welcome-section">
    <h1>Welcome to the Fall 2024 Project 4 Enterprise System</h1>
    <h2>A Servlet/JSP-based Multi-tiered Enterprise Application Using A Tomcat Container</h2>
</section>
<hr>
<section class="command-section">
    <p>You are connected to the Project 4 Enterprise System database as an <span class="accountant-level">accountant-level</span> user.</p>
    <p>Please select the operation you would like to perform from the list below.</p>
    <form action="AccountantHomeServlet" method="post" id="queryForm">
        <label><input type="radio" name="query" value="1"> Get The Maximum Status Value Of All Suppliers</label><br>
        <label><input type="radio" name="query" value="2"> Get The Total Weight Of All Parts</label><br>
        <label><input type="radio" name="query" value="3"> Get The Total Number of Shipments</label><br>
        <label><input type="radio" name="query" value="4"> Get The Name And Number Of Workers Of The Job With The Most Workers</label><br>
        <label><input type="radio" name="query" value="5"> List The Name And Status Of Every Supplier</label><br>
        <div class="form-buttons">
            <button type="submit" style="background-color: gray; color: lime;">Execute Command</button>
            <button type="button" id="clearBtn" style="background-color: gray; color: red;">Clear Results</button>
        </div>
    </form>
</section>
<hr>
<section id="results-section" class="results-section">
    <h3>Execution Results:</h3>
    <div id="resultContent">
        <%= request.getAttribute("message") != null ? (String)request.getAttribute("message") : "No results to display." %>
    </div>
</section>
<script>
    document.getElementById("clearBtn").addEventListener("click", function() {
        document.getElementById("resultContent").innerHTML = 'No results to display.';
    });
    document.getElementById("queryForm").addEventListener("reset", function() {
        document.querySelectorAll('input[type="radio"]').forEach(radio => radio.checked = false);
    });
</script>
</body>
</html>
