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

HOW TO RUN UNIT TESTS:

During the project I ran into dependency issues running Kotlin files with mvn test, so I manually executed the Test suite class by doing the following:

1. In the IntelliJ IDE, right click the src/test directory and mark the directory as the test source
2. run an mvn clean install
3. then go to the MainVerticleTest.kt file
4. Right click and run MainVerticleTest

HOW TO EXECUTE API ENDPOINTS FROM GITHUB CODE SPACES:

1. To access the code space
https://special-couscous-7j56p9pj66x34pv.github.dev/

2. To View the orderbook
https://special-couscous-7j56p9pj66x34pv-8080.app.github.dev/api/orderbook

3. To View trade history
https://special-couscous-7j56p9pj66x34pv-8080.app.github.dev/api/tradehistory

4. To Submit an Order
https://special-couscous-7j56p9pj66x34pv-8080.app.github.dev/api/order

Run this as a POST request on Postman/Curl/ThunderClient with the following example JSON body
{
  "type": "SELL",
  "quantity": 0.7,
  "price": 157000,
  "currencyPair": "BTCZAR"
}


