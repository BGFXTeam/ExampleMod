#pragma once

namespace BGFX {

class Logger {
public:
    enum Level {
        INFO,
        DEBUG,
        ERROR,
        FATAL
    };

    static void Log(Level level, const char* format, ...);
    static void Info(const char* format, ...);
    static void Debug(const char* format, ...);
    static void Error(const char* format, ...);
    static void Fatal(const char* format, ...);
};
}

#define LOGI(str, ...)  BGFX::Logger::Info(str, ##__VA_ARGS__)
#define LOGD(str, ...) BGFX::Logger::Debug(str, ##__VA_ARGS__)
#define LOGE(str, ...) BGFX::Logger::Error(str, ##__VA_ARGS__)
#define LOGF(str, ...) BGFX::Logger::Fatal(str, ##__VA_ARGS__)

