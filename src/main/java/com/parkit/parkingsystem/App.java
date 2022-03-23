package com.parkit.parkingsystem;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.service.InteractiveShell;

public class App {
  private static final Logger logger = LogManager.getLogger("App");

  public static void main(String args[]) throws IOException {
    logger.info("Initializing Parking System");
    InteractiveShell.loadInterface();
  }
}