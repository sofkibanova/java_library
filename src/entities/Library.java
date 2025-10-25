package entities;

import java.util.ArrayList;
import java.util.List;

public class Library{
    private List<Book> books;

    public Library(){
        this.books = new ArrayList<>();
    }

    public List<Book> getBooks(){
        return new ArrayList<Book>(books);
    }

    public void add(Book book){
        if (book != null) {
            books.add(book);
        } else {
            System.out.println("Не удалось добавить книгу!");
        }
    }

    // Стрим заменяет цикл
    // После -> идут условия, благодаря которым программа понимает, что книга подходит под заданный запрос
    // Если условие даёт true, то добавляется в List, который и возвращается как список книг, подходящих под условие
    public List<Book> searchForAttribute(String toSearch, String attribute){
        String lower_toSearch = toSearch.toLowerCase();
        String lower_attribute = attribute.toLowerCase();
        return books.stream().filter(book -> {
            return switch (lower_attribute) {
                case "title" -> book.getTitle().toLowerCase().contains(lower_toSearch);
                case "year" -> book.getYear() == Integer.parseInt(lower_toSearch);
                case "author" -> book.getAuthor().toLowerCase().contains(lower_toSearch);
                case "genres" -> book.getGenres().toLowerCase().contains(lower_toSearch);
                default -> false;
            };
        }).toList();
    }

    // Возвращает книгу с номером, который указан в параметре
    // Если такой нет, то orElse вернёт null
    public Book searchForNum(int num) {
        return books.stream().filter(book -> book.getNum() == num).findFirst().orElse(null);
    }

    public List<Book> searchForTitle(String title) {
        return searchForAttribute(title, "title");
    }

    public List<Book> searchForAuthor(String name) {
        return searchForAttribute(name, "author");
    }

    public List<Book> searchForYear(int year) {
        String str_year = String.valueOf(year);
        return searchForAttribute(str_year, "year");
    }

    public List<Book> searchForGenres(String genres) {
        return searchForAttribute(genres, "genres");
    }

    public boolean isEmpty(){
        return books.isEmpty();
    }

}
