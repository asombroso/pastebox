package com.pastebox.pastebox.security.mfa.manager;

import dev.samstevens.totp.exceptions.QrGenerationException;

public interface MfaTokenManager {

    String generateSecretKey();
    String getQrCode(final String secret) throws QrGenerationException;
    boolean verifyCode(final String code, final String secret);
}
