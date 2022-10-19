package com.pastebox.pastebox.security.mfa.model;

import java.io.Serializable;

public record MfaTokenData(String qrCode, String mfaCode) implements Serializable {}