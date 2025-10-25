import entities.Book;
import entities.Library;
import files.FileManagement;
import utilities.Input;
import java.util.List;

public class Main {
    private static Library library;
    private static FileManagement fileManager;

    public static void main(String[] args) {
        library = new Library();
        fileManager = new FileManagement();
        start();
    }

    private static void start() {
        // Загружаем данные при старте
        fileManager.loadFromFile(library);

        // Главный цикл программы
        while (true) {
            printMenu();
            System.out.print("Введите пункт меню: ");
            int choice = Input.getInt();
            switch (choice) {
                case 1 -> addBook();
                case 2 -> editBook();
                case 3 -> printAllBooks();
                case 4 -> searchBooks();
                case 5 -> fileManager.saveToFile(library);
                case 6 -> fileManager.loadFromFile(library);
                case 0 -> {
                    exitProgram();
                    return;
                }
                default -> System.out.println("Неверный пункт меню!");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nМенеджер библиотеки");
        System.out.println("1. Добавить книгу");
        System.out.println("2. Редактировать книгу");
        System.out.println("3. Вывести список книг");
        System.out.println("4. Найти книгу");
        System.out.println("5. Сохранить в файл");
        System.out.println("6. Загрузить из файла");
        System.out.println("0. Выход");
    }

    private static void addBook() {
        System.out.println("\nДобавление книги");
        System.out.print("Введите название книги: ");
        String title = Input.getString();
        System.out.print("Введите автора книги: ");
        String author = Input.getString();
        System.out.print("Введите год издания: ");
        int year = Input.getInt();
        System.out.print("Введите жанр книги: ");
        String genre = Input.getString();
        Book book = new Book(title, author, year, genre);
        library.add(book);
    }

    private static void editBook() {
        if (library.isEmpty()) {
            System.out.println("\nВ библиотеке нет книг!");
            return;
        }
        printAllBooks();
        System.out.print("Введите номер книги для редактирования: ");
        int num = Input.getInt();
        Book book = library.searchForNum(num);
        if (book == null) {
            System.out.println("Книга с таким номером не найдена!");
            return;
        }
        System.out.println("Оставьте поле пустым, чтобы не изменять значение:");
        System.out.print("Новое название: ");
        String newTitle = Input.getOptionalString();
        System.out.print("Новый автор: ");
        String newAuthor = Input.getOptionalString();
        System.out.print("Новый год издания: ");
        Integer newYear = Input.getOptionalYear();
        System.out.print("Новый жанр: ");
        String newGenre = Input.getOptionalString();
        // Применяем изменения только если введены новые значения
        if (!newTitle.isEmpty()) book.setTitle(newTitle);
        if (!newAuthor.isEmpty()) book.setAuthor(newAuthor);
        if (newYear != null) book.setYear(newYear);
        if (!newGenre.isEmpty()) book.setGenres(newGenre);
        System.out.println("Книга отредактирована!");
    }
    private static void printAllBooks() {
        if (library.isEmpty()) {
            System.out.println("\nВ библиотеке нет книг!");
            return;
        }
        System.out.println("\nСписок всех книг:");
        List<Book> books = library.getBooks();
        for (Book book : books){
            System.out.println(book);
        }
    }

    private static void searchBooks() {
        if (library.isEmpty()) {
            System.out.println("\nВ библиотеке нет книг!");
            return;
        }
        System.out.println("\nПоиск книг");
        System.out.println("1. По названию");
        System.out.println("2. По автору");
        System.out.println("3. По жанру");
        System.out.println("4. По году издания");
        System.out.print("По какому признаку искать: ");
        int choice = Input.getInt();
        List<Book> results;
        switch (choice) {
            case 1 -> {
                System.out.print("Введите название: ");
                String title = Input.getString();
                results = library.searchForTitle(title);
            }
            case 2 -> {
                System.out.print("Укажите автора: ");
                String author = Input.getString();
                results = library.searchForAuthor(author);
            }
            case 3 -> {
                System.out.print("Введите жанр: ");
                String genre = Input.getString();
                results = library.searchForGenres(genre);
            }
            case 4 -> {
                System.out.print("Введите год издания: ");
                int year = Input.getYear();
                results = library.searchForYear(year);
            }
            default -> {
                System.out.println("Неверный тип поиска!");
                return;
            }
        }
        if (results.isEmpty()){
            System.out.println("Подходящие книги не найдены!");
        } else {
            for (Book book : results){
                System.out.println(book);
            }
        }
    }

    private static void exitProgram() {
        System.out.println("Сохранение данных...");
        fileManager.saveToFile(library);
        Input.close();
        System.out.println("Программа завершилась!");
    }
}
