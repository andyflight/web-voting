# JEE Application

### Requirements:
- Java 17+
- Wildfly 36+
- Docker

#### To run the application, you need to install WildFly server and then run:

```bash
$JBOSS_HOME/bin/standalone.sh
```

```bash
mvn clean package wildfly:deploy
```

#### For 3+ Labs setting database to server is required:

```bash
docker-compose up -d
```

```bash
wget https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar
--2025-05-18 01:37:56--  https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar
```

```bash
./jboss-cli.sh 
```

```jbosscli 
module add --name=org.postgres --resources=postgresql-42.7.4.jar --dependencies=javax.api,javax.transaction.api
```

```jbosscli
/subsystem=datasources/jdbc-driver=postgres:add(driver-name="postgres",driver-module-name="org.postgres",driver-class-name=org.postgresql.Driver)
```

```jbosscli
data-source add --jndi-name=java:/voting_db  --name=PostgresDS --connection-url=jdbc:postgresql://localhost:5432/voting_db --driver-name=postgres --user-name=postgres --password=postgres
```

```bash
mvn clean install wildfly:deploy
```


open http://localhost:8080/core for website
open http://localhost:8080/core/api for REST API