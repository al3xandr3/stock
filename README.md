# Stock

Waiting for a stock price to rise to a certain value to sell it? or waiting for the price to drop to a certain value to buy it? But don't want to be checking it every day? Here's a little automation that sends an alert to email inbox.

This script will send you an email when stock price is outside the defined thresholds.


## Usage

Define the configuration on the stockConfig.txt file and keep in same dir as the uberjar file.

To get the uberjar file:

  $ lein uberjar

To run

  $ java -jar stock-0.1.0-SNAPSHOT-standalone.jar

To set it up running every day, use windows Task Scheduler or CRON for linux and Mac machines.

## License

Copyright © 2013

Distributed under the Eclipse Public License, the same as Clojure.
