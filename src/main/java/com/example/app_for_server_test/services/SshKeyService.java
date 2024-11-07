package com.example.app_for_server_test.services;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;

@Service
public class SshKeyService {

	private static final String BASE_KEYS_PATH = "sshkeys";

	public void addOrUpdatePublicKey(String username, String newPublicKey) throws IOException {
		
		    // Путь к файлу authorized_keys для данного пользователя
		Path userKeyPath = Paths.get(BASE_KEYS_PATH, username, ".ssh", "authorized_keys");
		
		// Создаем директорию для пользователя, если она не существует
		Files.createDirectories(userKeyPath.getParent());
		
		// Удаляем старый ключ, если он существует
		if (Files.exists(userKeyPath)) {
		    Files.delete(userKeyPath);
		}
		
		// Записываем новый ключ в файл authorized_keys
		try (FileWriter writer = new FileWriter(userKeyPath.toFile())) {
		    writer.write(newPublicKey + "\n");
		    }
		
//	    // Путь к директории для документов данного пользователя
//	    Path userDocsPath = Paths.get("user-docs", username);
//	    // Создаем директории для документов, если они не существуют
//	    Files.createDirectories(userDocsPath);
	}
	
}
