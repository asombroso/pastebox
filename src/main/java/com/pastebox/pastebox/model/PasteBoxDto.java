package com.pastebox.pastebox.model;

import org.springframework.stereotype.Service;
import javax.validation.constraints.Size;

@Service
public class PasteBoxDto {

    @Size(min = 2, max = 20000, message = "Min size is 2 symbols, max size is 20000 symbols.")
    private String data;

    private PublicStatus status;

    public PasteBoxDto() {
    }

    public PasteBoxDto(String data, PublicStatus isPublic) {
        this.data = data;
        this.status = isPublic;
    }

    public String getData() {
        return data;
    }

    public PublicStatus getStatus() {
        return status;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setStatus(PublicStatus status) {
        this.status = status;
    }
}
