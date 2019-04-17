# Process Instance Migration application 

## for PIM React GUI please check pim-react/README.md
Which explains two modes PIM React GUI can run

## for PIM service please check pim-service/README.md
Which contains details on setup pim-service and custom Thorntail configuration file for it. 

## Build

```
$ mvn clean package -DskipTests
```
## Run

```
$ java -jar pim-service/target/process-migration-thorntail.jar -s./myconfig.yml
```







