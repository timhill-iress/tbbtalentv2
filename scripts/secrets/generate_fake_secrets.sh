#!/bin/sh

# Generate Private Key and Cert
openssl req -x509 -newkey rsa:1024 -nodes -keyout fake_salesforce_key.pem -out fake_salesforce_cert.pem -days 36500 -subj "/O=Talent Beyond Boundaries/CN=www.talentbeyondboundaries.org"
# Remove newlines add to tbb_secrets.txt
salesforce_key=$(cat fake_salesforce_key.pem |  tr -d '\n')
echo "#Fake SalesForce Private key to allow server to start - added $(date -u +%Y-%m-%dT%H:%M:%S%Z)" >> ~/tbb_secrets.txt
echo "export SALESFORCE_PRIVATEKEY='$salesforce_key'" >> ~/tbb_secrets.txt

# Generate Private Key and Cert
openssl req -x509 -newkey rsa:1024 -nodes -keyout fake_googledrive_key.pem -out fake_googledrive_cert.pem -days 36500 -subj "/O=Talent Beyond Boundaries/CN=www.talentbeyondboundaries.org"
# Encode newlines to '\n' and add to tbb_secrets.txt
googledrive_key=$(cat fake_googledrive_key.pem | tr '\n' '|' | sed 's/|/\\\\n/g')
echo "#Fake GoogleDrive Private key to allow server to start - added $(date -u +%Y-%m-%dT%H:%M:%S%Z)" >> ~/tbb_secrets.txt
echo "export GOOGLE_DRIVE_PRIVATEKEY='$googledrive_key'" >> ~/tbb_secrets.txt

echo "Fake SalesForce and GoogleDrive private keys added to ~/tbb_secrets.txt"