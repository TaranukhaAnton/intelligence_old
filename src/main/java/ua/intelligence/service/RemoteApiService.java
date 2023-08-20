package ua.intelligence.service;



import ua.intelligence.domain.signal.SignalMessage;

import java.util.List;


public interface RemoteApiService {

    List<SignalMessage> getMessages();
}
