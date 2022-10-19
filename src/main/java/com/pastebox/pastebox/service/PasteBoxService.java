package com.pastebox.pastebox.service;

import com.pastebox.pastebox.model.PasteBox;
import com.pastebox.pastebox.model.PasteBoxDto;
import com.pastebox.pastebox.model.PasteBoxUrlResponse;
import org.springframework.security.core.Authentication;
import java.util.List;

public interface PasteBoxService {

    PasteBox getById(Long id);
    List<PasteBox> getAllById();
    PasteBoxUrlResponse create(PasteBoxDto pasteBoxDto, Authentication authentication);
    void deleteById(Long id);
}
