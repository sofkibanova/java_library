package files;

import entities.Book;
import entities.Library;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class FileManagement {

    private final String FILE_NAME = "lib.txt";

    public void saveToFile(Library lib){
        if (lib == null || lib.isEmpty()){
            System.out.println("\nВ библиотеке нет книг!");
            return;
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            List<Book> books = lib.getBooks();
            for (Book book : books){
                writer.println(book.toFileString());
            }
        } catch (IOException e){
            System.out.println("Не удалось сохранить данные в файл!");
        }
    }

    public void loadFromFile(Library lib){
        File file = new File(FILE_NAME);
        try (Scanner scanner = new Scanner(new FileReader(file))){
            while (scanner.hasNext()){
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()){
                    Book book = Book.fromFileString(line);
                    if (book != null){
                        lib.add(book);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не удалось прочитать данные из файла!");
        }
    }

}
