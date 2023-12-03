package com.wallet;

import java.util.List;

public interface CrudOperations<T>  {
    public List<T> findAll();
    public List<T> saveAll(List<T> toSave);
    public T save(T toSave);
    public T delete(T toDelete);
}
