package com.gmc.config.file;

import com.alibaba.fastjson.asm.FieldWriter;
import com.gmc.config.Location;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.listeners.LoggingListener;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@EnabledOnOs({OS.MAC, OS.LINUX})
class FileLocationParserTest {
    @TempDir
    File tmpDir;


    @org.junit.jupiter.api.Test
    void testSupport() {
        FileLocationParser fileLocationParser = new FileLocationParser();
        String path = "file://./testfile";
        assertTrue(fileLocationParser.support(path));

        path = "http://aaa.bbb.ccc";
        assertFalse(fileLocationParser.support(path));


    }

    @org.junit.jupiter.api.Test
    void testExactParse() throws IOException {

        FileLocationParser fileLocationParser = new FileLocationParser();
        File testFile = File.createTempFile("testFile", "", tmpDir);
        String absolutePath = testFile.getAbsolutePath();
        String path = "file://" + absolutePath;
        FileLocation location = (FileLocation) fileLocationParser.parse(path);
        assertTrue(location.getFileList().size() == 1);
        assertTrue(location.getFileList().get(0).exists());

        LogCheck.LogMatch logMatch = LogCheck.check(fileLocationParser.getClass());
        String noExistFile = tmpDir.getAbsolutePath() + "/aabbcc";
        logMatch.begin();
        location = fileLocationParser.parse(noExistFile);
        assertTrue(location.getFileList().size() == 0);
        assertTrue(logMatch.match(Level.ERROR, "file path not exist or is directory.*"));
        logMatch.clear();
    }

    @Test
    void testInexactParse() throws IOException {
        File file = new File(tmpDir, "/testInexactParse");
        Files.createDirectories(file.toPath());

        File testFile = Files.createFile( Path.of(file.getAbsolutePath(),"/testfile")).toFile();
        File fileB = new File(file, "/b");
        Files.createDirectories(fileB.toPath());

        File fileA = new File(file, "/a");
        Files.createDirectories(fileA.toPath());

        File testFileA = Files.createFile( Path.of(fileA.getAbsolutePath(),"/testfile")).toFile();

        File testFileB = Files.createFile( Path.of(fileB.getAbsolutePath(),"/testfile")).toFile();


        String path = "file://" + tmpDir.getAbsolutePath() + "/**/testfile";
        testInexactParse0(path,3, Arrays.asList(testFile,testFileA,testFileB));

         path = "file://" + tmpDir.getAbsolutePath() + "/*/**/testfile";
        testInexactParse0(path,3, Arrays.asList(testFile,testFileA,testFileB));


        path = "file://" + tmpDir.getAbsolutePath() + "/*/*/**/testfile";
        testInexactParse0(path,2, Arrays.asList(testFileA,testFileB));



    }

    private void testInexactParse0(String path, int size, List<File> files) {
        FileLocationParser fileLocationParser = new FileLocationParser();
        FileLocation fileLocation = fileLocationParser.parse(path);
        assertEquals(size, fileLocation.getFileList().size());

        for (int i = 0; i < files.size(); i++) {
            assertTrue(fileLocation.getFileList().contains(files.get(i)));
        }

    }


}