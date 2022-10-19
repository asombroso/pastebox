package com.pastebox.pastebox.service;

import com.pastebox.pastebox.exception.CustomAuthenticationException;
import com.pastebox.pastebox.exception.NotFoundEntityException;
import com.pastebox.pastebox.model.PasteBoxDto;
import com.pastebox.pastebox.model.PublicStatus;
import com.pastebox.pastebox.model.PasteBoxUrlResponse;
import com.pastebox.pastebox.model.PasteBox;
import com.pastebox.pastebox.repository.PasteBoxRepository;
import com.pastebox.pastebox.security.repository.UserRepository;
import com.pastebox.pastebox.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PasteBoxServiceImpl implements PasteBoxService {

    @Value("${app.host}")
    private String host;
    private final PasteBoxRepository pasteBoxRepository;
    private final UserRepository userRepository;

    @Autowired
    public PasteBoxServiceImpl(PasteBoxRepository pasteBoxRepository, UserRepository userRepository) {
        this.pasteBoxRepository = pasteBoxRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PasteBox getById(Long id) {
        Optional<PasteBox> paste = pasteBoxRepository.findById(id);
        PasteBox pasteBox = paste.orElseThrow(() ->
                new NotFoundEntityException("Pastebox with this id doesn't exist."));
        if (pasteBox.getStatus() == PublicStatus.PUBLIC) {
            return pasteBox;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        if (name.equals("anonymousUser")) {
            throw new CustomAuthenticationException("Insufficient rights to view a paste.");
        }
        Optional<User> user = userRepository.findUserByUsername(name);
        User foundUser = user.orElseThrow(() -> new UsernameNotFoundException("Username isn't found"));
        if (pasteBox.getUserCredentials().equals(foundUser)){
            return pasteBox;
        } else {
            throw new CustomAuthenticationException("Insufficient rights to view a paste.");
        }
    }

    @Override
    public List<PasteBox> getAllById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        if (authentication.getName().equals("anonymousUser")) {
            throw new CustomAuthenticationException("Should be authenticated.");
        }
        Optional<User> user = userRepository.findUserByUsername(name);
        User foundUser = user.orElseThrow(() -> new UsernameNotFoundException("Email isn't found."));
        return foundUser.getPastes();
    }

    @Override
    public PasteBoxUrlResponse create(PasteBoxDto pasteBoxDto, Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        if (pasteBoxDto.getStatus() == PublicStatus.PRIVATE &&
                authentication instanceof AnonymousAuthenticationToken) {
            throw new CustomAuthenticationException("User should be registered to create private pastes.");
        } else {
            String name = authentication.getName();
            Optional<User> user = userRepository.findUserByUsername(name);
            PasteBox pasteBox = new PasteBox();
            String data = pasteBoxDto.getData();
            pasteBox.setData(data);
            pasteBox.setStatus(pasteBoxDto.getStatus());
            user.ifPresent(userPastes -> userPastes.addPaste(pasteBox));
            pasteBoxRepository.save(pasteBox);
            return new PasteBoxUrlResponse(host + "/" + pasteBox.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getName().equals("anonymousUser")) {
            throw new CustomAuthenticationException("Anonymous users can't delete pastes.");
        }
        String name = authentication.getName();
        Optional<User> user = userRepository.findUserByUsername(name);
        User foundUser = user.orElseThrow(() -> new UsernameNotFoundException("Username isn't found"));
        Optional<PasteBox> pastebox = pasteBoxRepository.findById(id);
        PasteBox paste = pastebox.orElseThrow(() ->
                new NotFoundEntityException("Pastebox with this id doesn't exist."));
        if (paste.getUserCredentials() != null &&
                Objects.equals(foundUser.getId(), paste.getUserCredentials().getId())){
            pasteBoxRepository.deleteById(id);
        } else {
            throw new CustomAuthenticationException("Insufficient rights to delete this pastebox.");
        }
    }
}




