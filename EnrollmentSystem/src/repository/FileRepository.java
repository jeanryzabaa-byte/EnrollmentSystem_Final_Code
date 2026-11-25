package repository;

import java.util.List;

public interface FileRepository<T> {
    List<T> findAll();
    void saveAll(List<T> items);
}