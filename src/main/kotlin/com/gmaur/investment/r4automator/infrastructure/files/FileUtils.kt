package com.gmaur.investment.r4automator.infrastructure.files

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path

object FileUtils {
    fun readAllLinesAsString(path: Path): String {
        return Files.readAllLines(path).joinToString("")
    }

    fun newFile(): Path {
        return File.createTempFile("out", ".html").toPath()
    }

    fun saveTemporaryFile(pageSource: String) {
        val path = newFile()
        saveFile(path, pageSource)
        println("Wrote temporal page source to " + path)
    }

    fun saveFile(path: Path, pageSource: String) {
        Files.write(path, pageSource.toByteArray())
    }
}

class FilePickerProvider(private val input: BufferedReader, private val out: PrintStream) {
    fun request(message: String): String {
        out.print("Input the absolute path to the $message file: ")
        return input.readLine()
    }

    companion object Factory {
        fun aNew(): FilePickerProvider = FilePickerProvider(BufferedReader(InputStreamReader(System.`in`)), System.out)
    }
}