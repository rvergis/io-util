/*
 * Copyright 2013 Babak Farhang 
 */
package com.gnahraf.io.store.ks;


import static org.junit.Assert.*;
import static com.gnahraf.test.TestHelper.method;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.gnahraf.io.store.ks.Keystone;
import com.gnahraf.io.store.ks.KeystoneImpl;
import com.gnahraf.test.TestDirs;


/**
 * 
 * @author Babak
 */
public class KeystoneImplTest {

  private final Logger log;

  private final File testDir;


  public KeystoneImplTest() {
    testDir = TestDirs.getTestDir(getClass());
    log = Logger.getLogger(getClass());
  }


  @Test
  public void testBadArgs() {
    verifyCreateKeystoneFails("testBadArgs", -1, 0);
  }


  @Test
  public void testBadArgs2() throws IOException {
    String filename = method(new Object() { });
    createKeystone(filename, 0, 0);
    verifyLoadKeystoneFails(filename, 5);
  }


  @Test
  public void testRoundtrip() throws IOException {
    String filename = method(new Object() { });
    long initValue = 5;
    Keystone keystone = createKeystone(filename, 0, initValue);
    assertEquals(initValue, keystone.get());
  }


  @Test
  public void testRoundtrip2() throws IOException {
    String filename = method(new Object() { });
    long initValue = 5;
    createKeystone(filename, 0, initValue);
    Keystone keystone = loadKeystone(filename, 0);
    assertEquals(initValue, keystone.get());
  }


  @Test
  public void testRoundtrip3() throws IOException {
    String filename = method(new Object() { });
    long value = 5;
    Keystone keystone = createKeystone(filename, 0, value);
    value = -9;
    keystone.set(value);
    keystone = loadKeystone(filename, 0);
    assertEquals(value, keystone.get());

  }


  @Test
  public void testRoundtrip4() throws IOException {
    String filename = method(new Object() { });
    long value = 5;
    Keystone keystone = createKeystone(filename, 0, value);
    value = -9;
    keystone.set(value);
    value = 11;
    keystone.set(value);
    keystone = loadKeystone(filename, 0);
    assertEquals(value, keystone.get());
  }


  @Test
  public void testIncrement() throws IOException {
    String filename = method(new Object() { });
    long value = 5;
    Keystone keystone = createKeystone(filename, 0, value);
    assertEquals(value, keystone.get());

    long incr = 10;
    value += incr;
    assertEquals(value, keystone.increment(incr));
    assertEquals(value, keystone.get());

    keystone = loadKeystone(filename, 0);
    assertEquals(value, keystone.get());
  }


  @Test
  public void testDecrement() throws IOException {
    String filename = method(new Object() { });
    long value = 5;
    Keystone keystone = createKeystone(filename, 0, value);
    assertEquals(value, keystone.get());

    long incr = -10;
    value += incr;
    assertEquals(value, keystone.increment(incr));
    assertEquals(value, keystone.get());

    keystone = loadKeystone(filename, 0);
    assertEquals(value, keystone.get());
  }


  protected Keystone createKeystone(String filename, long startOffset, long initValue) throws IOException {

    File testFile = new File(testDir, filename);
    if (testFile.exists())
      fail("test file already exists: " + testFile);
    FileChannel fileChannel = new RandomAccessFile(testFile, "rw").getChannel();
    return new KeystoneImpl(fileChannel, startOffset, initValue);
  }


  protected Keystone loadKeystone(String filename, long startOffset) throws IOException {
    File testFile = new File(testDir, filename);
    if (!testFile.exists())
      fail("test file does not exist: " + testFile);
    FileChannel fileChannel = new RandomAccessFile(testFile, "rw").getChannel();
    return new KeystoneImpl(fileChannel, startOffset);
  }


  private void verifyCreateKeystoneFails(String filename, long startOffset, long initValue) {
    try {
      createKeystone(filename, startOffset, initValue);
      fail();
    } catch (Exception x) {
      // expected
      log.info("expected error: " + x.getMessage() + "; " + x.getClass().getName());
    }
  }


  private void verifyLoadKeystoneFails(String filename, long startOffset) throws IOException {
    try {
      loadKeystone(filename, startOffset);
      fail();
    } catch (Exception x) {
      // expected
      log.info("expected error: " + x.getMessage() + "; " + x.getClass().getName());
    }
  }

}
