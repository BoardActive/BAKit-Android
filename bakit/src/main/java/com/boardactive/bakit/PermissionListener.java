package com.boardactive.bakit;

import java.io.Serializable;

public interface PermissionListener extends Serializable {
        void permissionResult(boolean hasPermission);
    }
