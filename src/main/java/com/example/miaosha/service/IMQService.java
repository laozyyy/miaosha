package com.example.miaosha.service;

import org.springframework.stereotype.Service;

public interface IMQService {
    void sendUpdateMessage(long goodID);
}
