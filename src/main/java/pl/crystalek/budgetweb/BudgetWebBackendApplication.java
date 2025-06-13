package pl.crystalek.budgetweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.MalformedURLException;

@SpringBootApplication
public class BudgetWebBackendApplication {

    public static void main(String[] args) throws MalformedURLException {
        final ConfigurableApplicationContext context = SpringApplication.run(BudgetWebBackendApplication.class, args);
        //todo obslugyzc filenotfoundexception gdy plik avatara nie moze zostac znaleziony
//        final ChatModel bean = context.getBean(ChatModel.class);
        final String promptText = """
                Wysłałem Ci zdjęcie paragonu zawierającego nazwe sklepu, date zakupu oraz informacje o produktach które kupiłem.
                Podaj mi proszę dane na temat nazwy sklepu, daty zakupów, dane te mają być w formacie JSON.
                Gdy nazwa produktu jest niekompletna albo dziwna, to postaraj się podać prawidłową nazwę w polu proponowana nazwa, a odczytaną wartość dać w polu odczytana nazwa.
                Następnie daj mi listę produktów (nazwa produktu, ilość, cena, suma), które kupiłem w formacie JSON.
                Gdy znajdziesz pod jakimś produktem opust/rabat to dodaj do produktu wyżej pole opust i nie twórz dodatkowej pozycji na opust.
                """;
//        final Media media = new Media(MimeTypeUtils.IMAGE_PNG, new FileSystemResource("C:\\Users\\Crystal\\Desktop\\paragon2.jpg"));
//        final String call = bean.call(new UserMessage(promptText, media));
//        System.out.println(call);
    }

}
