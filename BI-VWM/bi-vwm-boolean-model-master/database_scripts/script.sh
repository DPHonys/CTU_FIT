#!/bin/bash
echo ""
echo "Creating tables"
if [ $(mysql -h localhost -P 3306 --protocol=tcp -u springuser -ppassword boolean-model-db < boolean_model_db_tables.sql ; echo $?) -eq "1" ]; then
    echo ""
    echo "Error occurred cancelling next operation"
    exit 1
fi

echo ""
echo "Inserting articles"
if [ $(mysql -h localhost -P 3306 --protocol=tcp -u springuser -ppassword boolean-model-db < boolean_model_db_article.sql ; echo $?) -eq "1" ]; then
  echo ""
  echo "Error occurred cancelling next operation"
  exit 1
fi

echo ""
echo "Inserting terms"
if [ $(mysql -h localhost -P 3306 --protocol=tcp -u springuser -ppassword boolean-model-db < boolean_model_db_term.sql ; echo $?) -eq "1" ]; then
  echo ""
  echo "Error occurred cancelling next operation"
  exit 1
fi

echo ""
echo "Inserting relations"
if [ $(mysql -h localhost -P 3306 --protocol=tcp -u springuser -ppassword boolean-model-db < boolean_model_db_article_term.sql ; echo $?) -eq "1" ]; then
  echo ""
  echo "Error occurred"
  exit 1
fi

echo ""
echo "Done"
exit 0
