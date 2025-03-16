VALR Order Book 

Features:
-Handles Buy & Sell Orders (FIFO matching)
-Automated Order Matching (Executes trades instantly)
-Partial Order Fulfillment (Remaining quantities persist in order book)
-Order Book Aggregation (Bids/Asks grouped by price level)
-Trade History Retrieval (Stores last 20 trades)
-High Precision Handling (8 decimal places for quantity, 2 for price)
-Comprehensive Test Suite (14 test cases covering all scenarios)

Installation & Setup:
1.Prerequisites
Ensure you have the following installed:
-Java 17
-Maven
-Kotlin
-Vert.x Framework

2.Clone the Repository
git clone https://github.com/DiveshanN/valr-orderbook.git
cd valr-orderbook

3.Build the Project
mvn clean install

4️.Run the Application
mvn exec:java
The server will start on port 8080.

5.Additional Documentation
For a detailed guide, including API specifications, system architecture, and unit test results, refer to:
VALR Order Book Guide (PDF)
