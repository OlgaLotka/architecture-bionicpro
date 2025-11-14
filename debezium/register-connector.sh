#!/bin/bash

# Wait for Kafka Connect to be ready
echo "Waiting for Kafka Connect to start listening on localhost:8083..."

while :; do
  curl -sS "http://localhost:8083/connectors" > /dev/null
  if [ $? -eq 0 ]; then
    echo "Kafka Connect is ready!"
    break
  fi
  sleep 5
done

# Submit the connector configuration
echo "Registering the bionicpro-connector..."
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8083/connectors -d "@./debezium/connector-config.json"

if [ $? -eq 0 ]; then
  echo "Connector registered successfully!"
else
  echo "Failed to register connector."
fi