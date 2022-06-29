cd localNode/nodes/
cd BoardLendNotary/
java -jar corda.jar run-migration-scripts --core-schemas --app-schemas --allow-hibernate-to-manage-app-schema

cd ..
cd Borrower/
java -jar corda.jar run-migration-scripts --core-schemas --app-schemas --allow-hibernate-to-manage-app-schema

cd ..
cd Lender/
java -jar corda.jar run-migration-scripts --core-schemas --app-schemas --allow-hibernate-to-manage-app-schema