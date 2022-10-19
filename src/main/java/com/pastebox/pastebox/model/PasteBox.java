package com.pastebox.pastebox.model;

import com.pastebox.pastebox.security.model.User;
import javax.persistence.*;

@Entity
@Table(name = "pastebox")
public class PasteBox {

    @Id
    @SequenceGenerator(name = "pastebox_sequence",
            sequenceName = "pastebox_sequence",
            initialValue = 1, allocationSize = 25)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pastebox_sequence")
    Long id;

    public String data;

    @Enumerated(EnumType.STRING)
    public PublicStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public PublicStatus getStatus() {
        return status;
    }

    public void setStatus(PublicStatus publicStatus) {
        this.status = publicStatus;
    }

    public User getUserCredentials() {
        return user;
    }

    public void setUserCredentials(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasteBox)) return false;
        return id != null && id.equals(((PasteBox) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
