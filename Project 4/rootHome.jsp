<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Root User Home</title>
    <style>
        body {
            background-color: black;
            color: #ffffff;
            font-family: 'Roboto', sans-serif;
            text-align: center;
        }

        .welcome-section h1 {
            color: yellow;
            font-size: 42px;
        }

        .welcome-section h2 {
            color: lime;
            font-size: 38px;
        }

        .command-section p,
        .command-section label {
            font-size: 20px;
        }

        .command-section .root-user {
            color: red;
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
        }

        .results-section {
            background-color: #ADD8E6;
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
            background-color: #3e3e3e;
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
    <p>You are connected to the Project 4 Enterprise System database as a <span class="root-user">root-level</span> user.</p>
    <p>Please enter any valid SQL query or update command in the box below.</p>
    <form action="RootHomeServlet" method="post" id="sqlForm">
        <textarea name="sql" id="sql" placeholder="" style="color: #ffffff; background-color: #263238;"></textarea>
        <div class="form-buttons">
            <button type="submit" style="background-color: darkgray; color:lime;">Execute Command</button>
            <button type="reset" style="background-color: darkgray; color: red;">Reset Form</button>
            <button type="button" id="clearBtn" style="background-color: darkgray; color: yellow;">Clear Results</button>
        </div>
    </form>
</section>
<hr>
<section id="results-section" class="results-section">
    <h3>Execution Results:</h3>
    <div id="resultContent">
        <%= request.getAttribute("message") != null ? request.getAttribute("message") : "No results to display." %>
    </div>
</section>
<script>
    document.getElementById("clearBtn").addEventListener("click", function() {
        document.getElementById("resultContent").innerHTML = 'No results to display.';
    });
    document.getElementById("sqlForm").addEventListener("reset", function() {
        document.getElementById("sql").value = '';
    });
</script>
</body>
</html>