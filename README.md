# api
Second micro api service
for GoEuro.com

This is a micro service, constructed and tested with Maven and Tomcat, whose purpose is to read a local .txt file and compare the information inside to a set of parameters passed by the user in a URL. Each line of the txt file represents a bus route, with the first integer being the ID of the bus route, and subsequent integers being the IDs of bus stations.

The parameters put through by the URL indicate which bus stations the user would like to inquire about. A JSON Object is returned indicating whether or not a route exists which connects the two stations.

The location of the txt file is passed into the app via command line parameter.
