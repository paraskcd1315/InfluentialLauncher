// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>

static const char *kServerClass =
    "com.paraskcd.influentiallauncher.shellaccess.infrastructure.server.InfShellServer";
static const char *kNiceName = "--nice-name=influential_shell";

static const char *value_of(const char *arg, const char *key) {
    size_t len = strlen(key);
    if (strncmp(arg, key, len) == 0) {
        return arg + len;
    }
    return NULL;
}

static int exec_helper(const char *apk, const char *pkg, const char *uid) {
    setsid();
    (void) chdir("/");
    int fd = open("/dev/null", O_RDWR);
    if (fd != -1) {
        dup2(fd, STDIN_FILENO);
        dup2(fd, STDOUT_FILENO);
        dup2(fd, STDERR_FILENO);
        if (fd > 2) {
            close(fd);
        }
    }

    setenv("CLASSPATH", apk, 1);

    char classpath_arg[4096];
    snprintf(classpath_arg, sizeof(classpath_arg), "-Djava.class.path=%s", apk);

    char *app_args[] = {
        (char *) "/system/bin/app_process",
        classpath_arg,
        (char *) "/system/bin",
        (char *) kNiceName,
        (char *) kServerClass,
        (char *) pkg,
        (char *) uid,
        (char *) apk,
        NULL
    };
    execvp(app_args[0], app_args);
    _exit(4);
}

int main(int argc, char **argv) {
    const char *apk = NULL;
    const char *pkg = NULL;
    const char *uid = NULL;
    for (int i = 1; i < argc; i++) {
        const char *v;
        if ((v = value_of(argv[i], "--apk=")) != NULL) {
            apk = v;
        } else if ((v = value_of(argv[i], "--pkg=")) != NULL) {
            pkg = v;
        } else if ((v = value_of(argv[i], "--uid=")) != NULL) {
            uid = v;
        }
    }
    if (apk == NULL || pkg == NULL || uid == NULL) {
        fprintf(stderr, "usage: libinfluential.so --apk=<path> --pkg=<name> --uid=<n>\n");
        return 2;
    }

    pid_t pid = fork();
    if (pid < 0) {
        perror("fork");
        return 3;
    }
    if (pid > 0) {
        printf("info: influential_starter pid is %d\n", pid);
        printf("info: influential_starter exit 0\n");
        fflush(stdout);
        return 0;
    }

    return exec_helper(apk, pkg, uid);
}
