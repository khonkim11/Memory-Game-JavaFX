			Memory Game for Kids – JavaFX & MySQL

A fun, interactive memory card game built with JavaFX and MySQL. This project features user authentication, multiple game modes, progress tracking, and a feedback system.

___________________________________________________________________________________________

Features

-User Accounts: Secure login and profile management.

-Game Selection Mode: * Difficulty Levels: Choose between different grid sizes (e.g., Easy, Medium, Hard) which adjust the challenge.
	Access Control: Logged-in users can access all modes, while Guest Mode is restricted to a single introductory method.

-Gameplay: Interactive features including a live timer, 3 hints per session, and a progress bar to track card matches.

-Leaderboard: Dynamic view of the top 10 players globally, sorted by high_score and best_time_seconds from the database.

-History: Detailed logs stored in the game_history table, tracking every session's accuracy, XP earned, and difficulty level.

-Guest Mode: A quick-play option that allows immediate gameplay without an account, though stats are not saved to the permanent history.

___________________________________________________________________________________________

Technology Stack

-IDE: Eclipse
-UI: JavaFX (FXML)
-Database: MySQL (via XAMPP)
-Connector: JDBC MySQL Connector
___________________________________________________________________________________________

Prerequisites

-XAMPP: Download and install from Apache Friends.
-Java JDK: Ensure JDK 11 or higher is installed.
-MySQL Connector: mysql-connector-j-9.6.0

___________________________________________________________________________________________

Database Setup (XAMPP)

	Importing the Database

-Open the XAMPP Control Panel and start Apache and MySQL.
-Click the Admin button next to MySQL to open phpMyAdmin.
-Click New in the left sidebar and create a database named "memorygame".
-Select the new database, click the Import tab at the top.
-Click Choose File, select memorygame.sql from the project folder, and click Import/Go.

___________________________________________________________________________________________

Project Setup (Eclipse)

	Adding the MySQL Connector JAR

	To allow Java to talk to XAMPP, you must add the JDBC driver to your project arguments:

-Right-click your project in Eclipse → Build Path → Configure Build Path.
-Go to the Libraries tab.
-Select Classpath (or Modulepath depending on your setup) and click Add External JARs....
-Navigate to and select "mysql-connector-j-9.6.0" file.
-Click Apply and Close.

___________________________________________________________________________________________

System Modules

-Identity & Profile: Handles players table authentication and stats.
-Game Engine: Manages core gameplay logic and "Selection Game Mode".
-Persistence: Uses JDBC to save results to game_history.
-Feedback: Manages the game_reviews table for player feedback.
___________________________________________________________________________________________

How to Run

-Ensure XAMPP (MySQL) is running.
-Open the project in Eclipse.
-Locate your main class (containing the start method) and click Run.
-Login or "Play as Guest" to begin!

___________________________________________________________________________________________


