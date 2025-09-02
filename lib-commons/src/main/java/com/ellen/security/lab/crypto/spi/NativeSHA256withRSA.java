package com.ellen.security.lab.crypto.spi;

import com.squareup.crypto.rsa.NativeRSAEngine;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi;

import static org.bouncycastle.jcajce.provider.util.DigestFactory.getDigest;

public class NativeSHA256withRSA extends DigestSignatureSpi {
    public NativeSHA256withRSA() {
        super(NISTObjectIdentifiers.id_sha256,
                getDigest("SHA-256"),
                 new PKCS1Encoding(new NativeRSAEngine()));
    }
}
