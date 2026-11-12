package facultate.android_backend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;

// pozele stau in <proiect>/images/{products,restaurants}/nume-produs.jpg
// si sunt servite la http://localhost:8080/images/products/nume-produs.jpg
// nu le punem in resources/static pentru ca acolo nu putem scrie la runtime (upload-ul de la lab 3)
@Configuration
public class ImagesConfig implements WebMvcConfigurer {
    public static final Path IMAGES_DIR = Path.of("images").toAbsolutePath();
    public static final String IMAGES_URL = "/images/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(IMAGES_URL + "**")
                .addResourceLocations(IMAGES_DIR.toUri().toString());
    }

    // "quattro formaggi" -> "quattro-formaggi"
    public static String slug(String name) {
        return name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-");
    }

    // url-ul pozei daca fisierul exista, altfel null (frontend-ul pune un placeholder)
    public static String findImage(String folder, String name) {
        String file = slug(name) + ".jpg";
        return Files.exists(IMAGES_DIR.resolve(folder).resolve(file))
                ? IMAGES_URL + folder + "/" + file
                : null;
    }
}
