package ch.romix.quarkus;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GraphQLSetup {

  public static final TypeDefinitionRegistry typeRegistry;

  static {
    InputStream inputStream = GraphQLSetup.class.getClassLoader().getResourceAsStream("schema.graphqls");
    if (inputStream == null) {
      throw new RuntimeException("Can't find schema.graphqls");
    }
    try (InputStream is = inputStream) {
      try (Reader reader = new InputStreamReader((is))) {
        typeRegistry = new SchemaParser().parse(reader);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private GraphQLDataFetchers graphQLDataFetchers;

  GraphQLSetup(GraphQLDataFetchers graphQLDataFetchers) {
    this.graphQLDataFetchers = graphQLDataFetchers;
  }

  public GraphQLSchema buildSchema() {
    RuntimeWiring runtimeWiring = buildWiring();
    SchemaGenerator schemaGenerator = new SchemaGenerator();
    return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
  }

  private RuntimeWiring buildWiring() {
    return RuntimeWiring.newRuntimeWiring()
        .type(newTypeWiring("Query")
            .dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher())
            .dataFetcher("allBooks", graphQLDataFetchers.getAllBooksDataFetcher())
            .dataFetcher("allAuthors", graphQLDataFetchers.getAllAuthorsDataFetcher()))
        .type(newTypeWiring("Mutation")
            .dataFetcher("addBook", graphQLDataFetchers.addBookDataFetcher())
            .dataFetcher("addAuthor", graphQLDataFetchers.addAuthorDataFetcher()))
        .type(newTypeWiring("Book")
            .dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher()))
        .build();
  }
}
