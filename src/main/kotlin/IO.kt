package io

import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets

fun readInput(inputFileName: String): String =
    IOUtils.resourceToString("/inputs/$inputFileName", StandardCharsets.UTF_8)