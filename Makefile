spring-boot-run:
	./mvnw spring-boot:run

mvn-clean-install:
	./mvnw clean install

launch-postgres:
	docker-compose -f docker-compose.yml up -d 

psql-shell:
	psql -d postgres