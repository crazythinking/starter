package net.engining.sftp.autoconfigure.support;

import java.util.Map;

/**
 * @author Eric Lu
 * @date 2023-07-06 13:59
 **/
public interface SynchronizerChecker {
    Map<String, Object> check();

    boolean isOk();
}
