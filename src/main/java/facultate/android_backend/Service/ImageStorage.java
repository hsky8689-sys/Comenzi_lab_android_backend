package facultate.android_backend.Service;

import facultate.android_backend.Config.ImagesConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStorage {
    public String store(MultipartFile file, String folder, String prefix) throws IOException {
        String type = file.getContentType();
        if (file.isEmpty() || type == null || !type.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        String extension = switch (type) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
        String name = prefix + "-" + UUID.randomUUID() + extension;
        Path dir = ImagesConfig.IMAGES_DIR.resolve(folder);
        Files.createDirectories(dir);
        try (var in = file.getInputStream()) {
            Files.copy(in, dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        }
        return ImagesConfig.IMAGES_URL + folder + "/" + name;
    }
}
