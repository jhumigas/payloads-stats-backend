mvn-clean-install:
	./mvnw clean install

mvn-test:
	./mvnw test

mvn-clean:
	./mvnw clean

spring-boot-run: mvn-clean
	./mvnw spring-boot:run

mvn-package:
	./mvnw clean package -DskipTests

start-db:
	docker-compose -f docker-compose.yml up -d

stop-db:
	docker-compose -f docker-compose.yml down

server-up: mvn-package
	docker-compose -f docker-compose.yml --profile backend up -d

server-down:
	docker-compose -f docker-compose.yml --profile backend down