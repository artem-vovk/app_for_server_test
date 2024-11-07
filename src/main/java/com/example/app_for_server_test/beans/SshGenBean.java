package com.example.app_for_server_test.beans;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.app_for_server_test.services.SshKeyService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.servlet.http.HttpServletResponse;

@Component
@ViewScoped
public class SshGenBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3903884012284882583L;

	@Autowired
	private SshKeyService sshKeyService;

	private String privateOpenShhKey = "";
	private String publicOpenShhKey = "";
	private String userName = "";

	@PostConstruct
	public void init() {
		Security.addProvider(new BouncyCastleProvider());
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPrivateShhKey() {
		return privateOpenShhKey;
	}

	public void setPrivateShhKey(String privateShhKey) {
		this.privateOpenShhKey = privateShhKey;
	}

	public String getPublicShhKey() {
		return publicOpenShhKey;
	}

	public void setPublicShhKey(String publicShhKey) {
		this.publicOpenShhKey = publicShhKey;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void shhGenVar2() throws IOException, InterruptedException {

		String comment = "conmment";
		// Определение пути к файлам ключей
		String privateKeyPath = "privateKey";
		String publicKeyPath = privateKeyPath + ".pub";

		// Команда для создания ключей
		ProcessBuilder processBuilder = new ProcessBuilder("ssh-keygen", "-t", "rsa", "-b", "4096", "-C", comment, "-f",
				privateKeyPath, "-N", "" // "-N" "" задает пустую парольную фразу
		);

		// Запуск команды
		Process process = processBuilder.start();
		process.waitFor();

		// Чтение ключей в строки
		privateOpenShhKey = Files.readString(Path.of(privateKeyPath));
		publicOpenShhKey = Files.readString(Path.of(publicKeyPath));

		// Вывод ключей или использование в коде
		System.out.println("Private Key:\n" + privateOpenShhKey);
		System.out.println("Public Key:\n" + publicOpenShhKey);

		// Очистка: удаление файлов ключей (по желанию)
		Files.deleteIfExists(Path.of(privateKeyPath));
		Files.deleteIfExists(Path.of(publicKeyPath));
	}

	public void shhGen() {

//		работющий код для публичного ключа
		// Генератор ключей RSA

		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(4096); // Указываем длину ключа
			// Генерация пары ключей
			KeyPair keyPair = keyGen.generateKeyPair();

			// Получение приватного ключа
			PrivateKey privateKey = keyPair.getPrivate();

			// Преобразование приватного ключа в формат OpenSSH
			PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKey.getEncoded());

			StringWriter stringWriter = new StringWriter();
			try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
				pemWriter.writeObject(privateKeyInfo);
			}

			privateOpenShhKey = stringWriter.toString();

			// Получение публичного ключа
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

			// Конвертация публичного ключа в OpenSSH формат
			publicOpenShhKey = convertToOpenSSHPublicKey(publicKey);

			// Выводим публичный ключ в формате OpenSSH
			System.out.println("OpenSSH Public Key: " + publicOpenShhKey);
			System.out.println("OpenSSH Ppivate Key: " + privateOpenShhKey);

			// add PublicKey into hard disc and into sftp-server
			sshKeyService.addOrUpdatePublicKey(userName, publicOpenShhKey);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String convertToOpenSSHPublicKey(RSAPublicKey publicKey) {
		// Префикс для формата OpenSSH
		String sshRsaPrefix = "ssh-rsa ";

		// Извлекаем модули и экспонент из публичного ключа
		byte[] modulus = publicKey.getModulus().toByteArray();
		byte[] exponent = publicKey.getPublicExponent().toByteArray();

		// Создаем байтовый массив для OpenSSH структуры
		byte[] sshKey = new byte[4 + "ssh-rsa".length() + // Тип ключа
				4 + exponent.length + // Экспонента
				4 + modulus.length // Модули
		];

		int index = 0;

		// Добавляем длину и тип ключа
		index = addLengthAndBytes(sshKey, index, "ssh-rsa".getBytes());

		// Добавляем длину и экспонент
		index = addLengthAndBytes(sshKey, index, exponent);

		// Добавляем длину и модули
		addLengthAndBytes(sshKey, index, modulus);

		// Кодируем весь ключ в Base64
		String base64Key = Base64.getEncoder().encodeToString(sshKey);

		// Возвращаем публичный ключ в формате OpenSSH
		return sshRsaPrefix + base64Key;
	}

	private static int addLengthAndBytes(byte[] destination, int index, byte[] source) {
		// Добавляем длину
		destination[index++] = (byte) (source.length >> 24);
		destination[index++] = (byte) (source.length >> 16);
		destination[index++] = (byte) (source.length >> 8);
		destination[index++] = (byte) (source.length);

		// Копируем байты
		System.arraycopy(source, 0, destination, index, source.length);

		return index + source.length;
	}

	public void downloadPrivateKey() {
		downloadKey(privateOpenShhKey, "private_ssh_key.pem");
	}

//    public void downloadPublicKey() {
//        downloadKey(publicShhKey, "public_key.txt");
//    }

	private void downloadKey(String keyContent, String fileName) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		try (PrintWriter writer = response.getWriter()) {
			writer.write(keyContent);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			facesContext.responseComplete();
		}
	}

}
