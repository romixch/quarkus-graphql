package ch.romix.quarkus;

import graphql.GraphQL;
import graphql.language.TypeDefinition;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.quarkus.runtime.Startup;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.graphql.ApolloWSHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Startup
@ApplicationScoped
public class GraphQLSetup {


    private GraphQLDataFetchers graphQLDataFetchers;

    GraphQLSetup(GraphQLDataFetchers graphQLDataFetchers) throws URISyntaxException, IOException {
        this.graphQLDataFetchers = graphQLDataFetchers;
    }

    public void setupRouter(@Observes Router router) throws URISyntaxException, IOException {
        TypeDefinitionRegistry typeRegistry = loadTypeRegistry();
        GraphQLSchema graphQLSchema = buildSchema(typeRegistry);
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        router.route("/graphql").handler(ApolloWSHandler.create(graphQL));
        router.route("/graphql").handler(GraphQLHandler.create(graphQL));
    }

    private TypeDefinitionRegistry loadTypeRegistry() throws URISyntaxException, IOException {
        InputStream inputStream = GraphQLSetup.class.getClassLoader().getResourceAsStream("schema.graphqls");
        if (inputStream == null) throw new RuntimeException("Can't find schema.graphqls");
        try (InputStream is = inputStream) {
            try (Reader reader = new InputStreamReader((is))) {
                return new SchemaParser().parse(reader);
            }
        }
    }

    private GraphQLSchema buildSchema(TypeDefinitionRegistry typeRegistry) {
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
