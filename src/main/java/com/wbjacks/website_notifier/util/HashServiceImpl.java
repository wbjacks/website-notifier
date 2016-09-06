package com.wbjacks.website_notifier.util;

import jodd.petite.meta.PetiteBean;
import org.apache.log4j.Logger;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@PetiteBean("hashService")
public class HashServiceImpl implements HashService {
    private static final String HASHING_ALGORITHM = "MD5";
    private static final Logger LOGGER = Logger.getLogger(HashServiceImpl.class);

    @Override
    public String getHash(String input) {
        try {
            return new HexBinaryAdapter().marshal(MessageDigest.getInstance(HASHING_ALGORITHM).digest(input.getBytes
                    ()));
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(String.format("Specified hashing algorithm [%s] doesn't exist, returning empty string.",
                    HASHING_ALGORITHM));
            return "";
        }
    }
}
