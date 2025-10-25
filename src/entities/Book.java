package entities;

public class Book {
    public static int nextNum = 1;
    private final int num;
    private String title;
    private int year;
    private String author;
    private String genres;

    public Book(String title, String author, int year, String genres){
        this.title = title;
        this.year = year;
        this.author = author;
        this.genres = genres;
        this.num = nextNum++;
    }

    public int getNum(){
        return num;
    }

    public String getTitle(){
        return title;
    }

    public int getYear(){
        return year;
    }

    public String getAuthor(){
        return author;
    }

    public String getGenres(){
        return genres;
    }

    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
    }

    public void setYear(int year) {
        if (year > 0 && year <= java.time.Year.now().getValue()) {
            this.year = year;
        }
    }

    public void setAuthor(String author) {
        if (author != null && !author.trim().isEmpty()) {
            this.author = author.trim();
        }
    }

    public void setGenres(String genres) {
        if (genres != null && !genres.trim().isEmpty()) {
            this.genres = genres.trim();
        }
    }

    @id86240433 (@Override)
    public String toString(){
        return "Номер: " + num + "; Название: " + title + "; Автор: " + author + "; Год издания: " + year + "; Жанр: " + genres;
    }

    public String toFileString(){
        return String.join(";", String.valueOf(num), title, author, String.valueOf(year), genres);
    }

    public static Book fromFileString(String file_line){
        try {
            String[] str = file_line.split(";");
            if (str.length != 5){
                return null;
            }
            Book book = new Book(str[1], str[2], Integer.parseInt(str[3]), str[4]);
            nextNum = Math.max(nextNum, Integer.parseInt(str[0]) + 1);
            return book;
        } catch (Exception e){
            System.out.println("Не удалось прочитать строку файла!");
            return null;
        }
    }
}
