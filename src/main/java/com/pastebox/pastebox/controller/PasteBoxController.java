package com.pastebox.pastebox.controller;

import com.pastebox.pastebox.model.PasteBoxDto;
import com.pastebox.pastebox.security.mfa.model.MfaTokenData;
import com.pastebox.pastebox.security.model.User;
import com.pastebox.pastebox.security.model.UserDto;
import com.pastebox.pastebox.security.service.UserServiceImpl;
import com.pastebox.pastebox.service.PasteBoxServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Controller
public class PasteBoxController {

    private final UserDto userDto;
    private final PasteBoxDto pasteBoxDto;
    private final UserServiceImpl userService;
    private final PasteBoxServiceImpl pasteBoxService;

    @Autowired
    public PasteBoxController(UserDto userDto, PasteBoxDto pasteBoxDto,
                              UserServiceImpl userService, PasteBoxServiceImpl pasteBoxService) {
        this.userDto = userDto;
        this.pasteBoxDto = pasteBoxDto;
        this.userService = userService;
        this.pasteBoxService = pasteBoxService;
    }

    @GetMapping("show/{id}")
    public String getById(@PathVariable Long id, Model model) {
        model.addAttribute("paste",pasteBoxService.getById(id));
        model.addAttribute("auth", SecurityContextHolder.getContext().getAuthentication().getName());
        return "showPaste";
    }

    @PostMapping("/main")
    public String addPaste(@ModelAttribute("pasteBox") @Valid PasteBoxDto pasteBoxDto,
                           BindingResult bindingResult, Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "main";
        }
        model.addAttribute("showHash", pasteBoxService.create(pasteBoxDto, authentication).url());
        return "info";
    }

    @DeleteMapping("/show/{id}")
    public String delete(@PathVariable("id") Long id){
        pasteBoxService.deleteById(id);
        return "redirect:main";
    }

    @GetMapping("/main")
    public String main(@ModelAttribute("pasteBox") PasteBoxDto pasteBoxDto) {
        return "main";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "/login";
    }

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public String postRegistration(@ModelAttribute("user") @Valid UserDto userDto,
                                   BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        User user = userService.registerUser(userDto);
        if(!user.isMfaEnabled()){
            return "registerInfo";
        }
        MfaTokenData mfaData = userService.mfaSetup(user.getUsername());
        model.addAttribute("qrCode", mfaData.qrCode());
        model.addAttribute("qrCodeKey", mfaData.mfaCode());
        return "qrCode";
    }

    @GetMapping("/register")
    public String getRegistration(@ModelAttribute("user") UserDto userDto) {
        return "/registration";
    }

    @GetMapping("/personal")
    public String personal(Model model) {
        model.addAttribute("list", pasteBoxService.getAllById());
        return "personal";
    }

    @GetMapping("/qrCode")
    public String qrCode(){
        return "/qrCode";
    }

    @GetMapping("/logout")
    public String logout(){
        return "redirect:main";
    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("code") String code){
        userService.confirmUser(code);
        return "redirect:main";
    }

    @GetMapping("/fail")
    public String returnError(){
        return "fail";
    }

    @GetMapping("/disabled")
    public String disabled(){
        return "disabled";
    }
}
