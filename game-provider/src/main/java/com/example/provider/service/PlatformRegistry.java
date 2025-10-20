package com.example.provider.service;

import com.example.provider.model.PlatformInfo;

/**
 * The PlatformRegistry interface defines a contract for managing platform-related information
 * and serves as a lookup mechanism for retrieving details about registered platforms.
 */
public interface PlatformRegistry {
    PlatformInfo getPlatformInfo(String platformId);
}
