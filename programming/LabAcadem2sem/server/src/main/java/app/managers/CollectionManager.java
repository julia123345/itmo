package app.managers;

import app.User;
import app.model.Person;
import app.server.Server;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Управление коллекцией Person с использованием PriorityQueue и ReadWriteLock.
 */
public class CollectionManager {

    private static ZonedDateTime creationDate;
    private final PriorityQueue<Person> collection = new PriorityQueue<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final CollectionFileManager fileManager;
    private final Server server;

    public CollectionManager(Server server, CollectionFileManager fileManager) {
        this.server = server;
        this.fileManager = fileManager;
        creationDate = ZonedDateTime.now();
    }

    public synchronized void add(Person person) {
        lock.writeLock().lock();
        try {
            if (person.getId() <= 0) {
                person.setId(generateId());
            }
            if (person.getCreationDate() == null) person.setCreationDate(ZonedDateTime.now());
            collection.add(person);
            fileManager.saveCollection(new ArrayList<>(collection));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean loadFromFile() {
        PriorityQueue<Person> loaded = fileManager.readCollection();
        if (loaded == null) return false;
        lock.writeLock().lock();
        try {
            collection.clear();
            collection.addAll(loaded);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeLower(User user, Person person) {
        lock.writeLock().lock();
        try {
            List<Person> toRemove = collection.stream()
                    .filter(p -> p.compareTo(person) < 0)
                    .filter(p -> user.getLogin().equals(p.getOwnerLogin()))
                    .collect(Collectors.toList());
            toRemove.forEach(collection::remove);
            if (!toRemove.isEmpty()) {
                fileManager.saveCollection(new ArrayList<>(collection));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Удаляет элементы коллекции, которые строго больше заданного и принадлежат пользователю.
     */
    public void removeGreater(User user, Person person) {
        lock.writeLock().lock();
        try {
            List<Person> toRemove = collection.stream()
                    .filter(p -> p.compareTo(person) > 0)
                    .filter(p -> user.getLogin().equals(p.getOwnerLogin()))
                    .collect(Collectors.toList());
            toRemove.forEach(collection::remove);
            if (!toRemove.isEmpty()) {
                fileManager.saveCollection(new ArrayList<>(collection));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void update(Person person) {
        lock.writeLock().lock();
        try {
            collection.removeIf(p -> p.getId() == person.getId());
            collection.add(person);
            fileManager.saveCollection(new ArrayList<>(collection));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Person> filterStartsWithName(String name) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(p -> p.getName().startsWith(name))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public Person getMaxElement() {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .max(Comparator.naturalOrder())
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Person getById(long id) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void deleteById(long id) {
        lock.writeLock().lock();
        try {
            collection.removeIf(p -> p.getId() == id);
            fileManager.saveCollection(new ArrayList<>(collection));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear(User user) {
        lock.writeLock().lock();
        try {
            collection.removeIf(p -> user.getLogin().equals(p.getOwnerLogin()));
            fileManager.saveCollection(new ArrayList<>(collection));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static ZonedDateTime getCreationDate() {
        return creationDate;
    }

    /** Коллекция, отсортированная по имени (для выдачи клиенту). */
    public List<Person> getCollectionSortedByName() {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .sorted(Comparator.comparing(Person::getName))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public Collection<Person> getUnmodifiableCollection() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableCollection(new ArrayList<>(collection));
        } finally {
            lock.readLock().unlock();
        }
    }

    /** Сохранение в файл (команда save только на сервере). */
    public boolean saveToFile() {
        lock.readLock().lock();
        try {
            return fileManager.saveCollection(new ArrayList<>(collection));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Генерация уникального id (ищем максимум и +1)
     */
    private long generateId() {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .mapToLong(Person::getId)
                    .max()
                    .orElse(0L) + 1;
        } finally {
            lock.readLock().unlock();
        }
    }
}