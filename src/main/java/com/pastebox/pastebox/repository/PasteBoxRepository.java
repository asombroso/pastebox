package com.pastebox.pastebox.repository;

import com.pastebox.pastebox.model.PasteBox;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasteBoxRepository extends JpaRepository<PasteBox, Long> {

    Optional<PasteBox> findById(Long id);
}
