package com.example.user.proba3;

/**
 * Created by jaroslaw on 24.04.2017.
 */

public interface RequestCallback<T> {
    void updateFromResponse(T response);
}
