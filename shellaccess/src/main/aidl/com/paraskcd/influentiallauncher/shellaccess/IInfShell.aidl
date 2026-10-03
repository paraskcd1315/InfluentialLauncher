// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess;

interface IInfShell {
    int version();
    Bundle run(String command);
    void exit();
}
