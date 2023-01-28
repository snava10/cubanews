package com.snava.cubanews;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RatedLogger {

  private enum LogLevel {
    DEBUG,
    INFO,
    WARNING,
    ERROR
  }

  private final Logger logger;
  private final Map<LogLevel, Float> rateMap;
  private final Random r;

  public RatedLogger(Class<?> clazz) {
    this(clazz, 0, 0.1f, 1f);
  }

  public RatedLogger(Class<?> clazz, float debugRate, float infoRate, float warningRate) {
    this.logger = LoggerFactory.getLogger(clazz);
    rateMap = new HashMap<>() {{
      put(LogLevel.DEBUG, debugRate);
      put(LogLevel.INFO, infoRate);
      put(LogLevel.WARNING, warningRate);
      put(LogLevel.ERROR, 1f);
    }};
    r = new Random();
  }

  public void debug(String message, Object... args) {
    log(LogLevel.INFO, message, args);
  }

  public void info(String message, Object... args) {
    log(LogLevel.INFO, message, args);
  }

  public void warning(String message, Object... args) {
    log(LogLevel.WARNING, message, args);
  }

  public void error(String message, Object... args) {
    log(LogLevel.ERROR, message, args);
  }

  public void error(String message, Throwable ex) {
    logger.error(message, ex);
  }

  public void setRate(LogLevel logLevel, float rate) {
    if (rate > 1) {
      rate = 1;
    } else if (rate < 0) {
      rate = 0;
    }
    rateMap.put(logLevel, rate);
  }

  private void log(LogLevel level, String message, Object... args) {
    try {
      float prob = r.nextFloat();
      if (rateMap.get(level) == 1 || prob < rateMap.get(level)) {
        switch (level) {
          case DEBUG -> logger.debug(message, args);
          case INFO -> logger.info(message, args);
          case WARNING -> logger.info(message, args);
          case ERROR -> logger.error(message, args);
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
