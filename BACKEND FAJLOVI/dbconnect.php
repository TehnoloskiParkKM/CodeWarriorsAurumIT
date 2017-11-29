<?php
// database connection variables
$servername = "localhost";
$username = "root";
$password = "Aurumit$987";
$database = "cw";


// Connecting to mysqli database
$con = new mysqli($servername, $username, $password, $database);

if ($con->connect_error) {
    die("Connection failed: " . $con->connect_error);
}

?>