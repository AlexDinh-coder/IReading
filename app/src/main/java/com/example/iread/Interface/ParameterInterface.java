package com.example.iread.Interface;

public interface ParameterInterface<T> {
    void onSuccess(T data);         // Callback khi thành công, trả về kiểu dữ liệu T
}
