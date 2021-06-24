# Inventory manager

## Description

The goal of this project is to have a simple inventory manager. Users should be able to have multiple inventories, which
can contain different items with a quantity associated.

## Development

This project uses Java 11 with Maven, and relies on MongoDB.

If you want to start it locally, you will need to have MongoDB running (a basic instance with default parameters is
enough, but you can customize it in the application.yaml).

It is not necessary to have MongoDB running during unit tests, in that case the project will start an embedded version
of the database.

To compile, run all unit tests and package the code, run:

```bash
mvn clean install
```

or if you don't have maven on your PATH:

```bash
mvnw clean install
```

Best way to experiment would be to start this project in your IDE. You can then run all test and start the project. This
will start a web server listening on `localhost:8080`

For convenience, a Swagger UI is included, at the address:

```
http://localhost:8080/swagger-ui/
```

## Notes

To run the test, I use an embedded MongoDB database (pulled by the dependency `de.flapdoodle.embed.mongo`). No need to
configure anything.

Initially, I planned to keep the allowed categories in a separate MongoDB collection. However, this seemed unnecessary,
so to keep it simple I used a configuration file instead.

Also for simplicity, I did not create a DTO to return from the controller. I return directly the entity (without the
MongoDB ID).

About the controllers, they return either a List of InventoryEntity, or a single object. I could also, for example,
return a map of the items per inventory, but the approach I chose gives us a few advantages:

- It is easy to parse for the client, for example to filter or aggregate the items
- It can be easily extended later, with more fields. Unlike a map of the quantities per subcategory per category (`Map<
  String, Map<String, Integer>>`), for example.

I also made the choice to format parameters (inventory name, etc) at controller level, assuming the Service layer is
under our control.

### About the embedded MongoDB used in tests

If this causes issues on your side, you can disable the dependency (`de.flapdoodle.embed.mongo`) in the pom.xml, and run
your own MongoDB server.
