package ru.pflb.school5.javaOOP;

import ru.pflb.mq.dummy.implementation.ConnectionImpl;
import ru.pflb.mq.dummy.interfaces.Connection;
import ru.pflb.mq.dummy.interfaces.Destination;
import ru.pflb.mq.dummy.interfaces.Producer;
import ru.pflb.mq.dummy.interfaces.Session;
import ru.pflb.mq.dummy.exception.DummyException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main2 {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Использование: java Main2 <путь к файлу>");
            return;
        }

        String filePath = args[0];
        List<String> messages = readMessagesFromFile(filePath);

        if (messages.isEmpty()) {
            System.err.println("Файл пуст или не существует.");
            return;
        }

        sendMessages(messages);
    }

    private static List<String> readMessagesFromFile(String filePath) {
        List<String> messages = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                messages.add(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
        return messages;
    }

    private static void sendMessages(List<String> messages) {
        Connection connection = null;
        Session session = null;
        try {
            connection = new ConnectionImpl();
            connection.start();  // Подключаемся

            session = connection.createSession(true); // true - транзакционная
            Destination destination = session.createDestination("MyQueue");
            Producer producer = session.createProducer(destination);

            while (true) {
                for (String messageContent : messages) {
                    producer.send(messageContent); // Отправляем сообщение
                    System.out.println("Отправлено сообщение: " + messageContent);
                    Thread.sleep(2000); // Ожидание 2 секунды между отправками
                }
            }
        } catch (DummyException e) {
            System.err.println("Произошла ошибка в работе с MQ: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Поток был прерван: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            // Закрываем ресурсы
            closeResources(session, connection);
        }
    }

    private static void closeResources(Session session, Connection connection) {
        try {
            if (session != null) session.close();
            if (connection != null) connection.close(); // Закрываем соединение
        } catch (Exception e) {
            System.err.println("Ошибка при закрытии ресурсов: " + e.getMessage());
        }
    }
}
