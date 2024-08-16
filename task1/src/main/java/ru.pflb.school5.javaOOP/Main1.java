package ru.pflb.school5.javaOOP;

import ru.pflb.mq.dummy.implementation.ConnectionImpl;
import ru.pflb.mq.dummy.interfaces.Connection;
import ru.pflb.mq.dummy.interfaces.Destination;
import ru.pflb.mq.dummy.interfaces.Producer;
import ru.pflb.mq.dummy.interfaces.Session;
import ru.pflb.mq.dummy.exception.DummyException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Main1 {

    public static void main(String[] args) {
        // Создаем список сообщений
        List<String> messages = Arrays.asList("Четыре", "Пять", "Шесть");

        // Создаем Connection, Session и Producer
        Connection connection = null;
        Session session = null;
        try {
            // Создаем Connection
            connection = new ConnectionImpl();
            connection.start();  // Подключаемся

            // Создаем Session
            session = connection.createSession(true); // true - транзакционная

            // Создаем Destination
            Destination destination = session.createDestination("MyQueue");

            // Создаем Producer
            Producer producer = session.createProducer(destination);

            // Используем итератор для обхода списка сообщений
            Iterator<String> iterator = messages.iterator();
            while (iterator.hasNext()) {
                String messageContent = iterator.next();

                // Отправляем сообщение
                producer.send(messageContent);

                // Выводим сообщение на консоль
                System.out.println("Отправлено сообщение: " + messageContent);

                // Ожидание 2 секунды между отправками
                Thread.sleep(2000);
            }

        } catch (DummyException e) {
            System.err.println("Произошла ошибка в работе с MQ: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
        } finally {
            // Закрываем ресурсы
            try {
                if (session != null) session.close();
                if (connection != null) connection.close(); // Закрываем соединение
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
