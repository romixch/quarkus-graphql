package ch.romix.quarkus;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.quarkus.runtime.Startup;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.graphql.ApolloWSHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@Startup
@ApplicationScoped
public class GraphQLRouter {
  private GraphQLSetup graphQLSetup;

  GraphQLRouter(GraphQLSetup graphQLSetup) {
    this.graphQLSetup = graphQLSetup;
  }

  public void setupRouter(@Observes Router router) {
    GraphQLSchema graphQLSchema = graphQLSetup.buildSchema();
    GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

    router.route("/graphql").handler(ApolloWSHandler.create(graphQL));
    router.route("/graphql").handler(GraphQLHandler.create(graphQL));
  }




}
