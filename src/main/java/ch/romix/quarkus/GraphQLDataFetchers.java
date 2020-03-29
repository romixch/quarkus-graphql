package ch.romix.quarkus;

import graphql.schema.DataFetcher;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ApplicationScoped
public class GraphQLDataFetchers {
    private static List<Author> authors = new ArrayList<Author>();
    private static List<Book> books = new ArrayList<Book>();

    static {
        authors.add(new Author("author-1", "Joanne", "Rowling"));
        authors.add(new Author("author-2", "Herman", "Melville"));
        authors.add(new Author("author-3", "Anne", "Rice"));

        books.add(new Book("book-1", "Harry Potter and the Philosopher's Stone", 223, "author-1"));
        books.add(new Book("book-2", "Moby Dick", 635, "author-2"));
        books.add(new Book("book-3", "Interview with the vampire", 371, "author-3"));
    }

    public DataFetcher<List<Book>> getAllBooksDataFetcher() {
        return dataFetchingEnvironment -> books;
    }

    public DataFetcher<Book> getBookByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String bookId = dataFetchingEnvironment.getArgument("id");
            return books
                    .stream()
                    .filter(book -> book.getId().equals(bookId))
                    .findFirst()
                    .orElse(null);
        };
    }

    public DataFetcher<Author> getAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            Book book = dataFetchingEnvironment.getSource();
            String authorId = book.getAuthorId();
            return authors
                    .stream()
                    .filter(author -> author.getId().equals(authorId))
                    .findFirst()
                    .orElse(null);
        };
    }

    public DataFetcher<List<Author>> getAllAuthorsDataFetcher() {
        return dataFetchingEnvironment -> authors;
    }

    public DataFetcher<Book> addBookDataFetcher() {
        return dataFetchingEnvironment -> {
            HashMap<String, Object> bookInput = dataFetchingEnvironment.getArgument("book");
            Book newBook = new Book((String)bookInput.get("id"), (String)bookInput.get("name"), (Integer)bookInput.get("pageCount"), (String)bookInput.get("authorId"));
            books.add(newBook);
            return newBook;
        };
    }

    public DataFetcher<Author> addAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            HashMap<String, String> authorInput = dataFetchingEnvironment.getArgument("author");
            Author newAuthor = new Author(authorInput.get("id"), authorInput.get("firstName"), authorInput.get("lastName"));
            authors.add(newAuthor);
            return newAuthor;
        };
    }
}
