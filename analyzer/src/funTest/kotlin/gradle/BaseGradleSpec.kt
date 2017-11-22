package com.here.ort.analyzer.integration

import com.here.ort.analyzer.Expensive
import com.here.ort.analyzer.managers.Gradle
import com.here.ort.model.Package
import com.here.ort.analyzer.Main as AnalyzerMain
import com.here.ort.downloader.Main as DownloaderMain

import io.kotlintest.Spec
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import io.kotlintest.properties.headers
import io.kotlintest.properties.row
import io.kotlintest.properties.table
import io.kotlintest.specs.StringSpec

import java.io.File

abstract class BaseGradleSpec : StringSpec() {

    abstract val pkg: Package
    abstract val expectedResultsDir: String

    // Map here expected results files locations if for some reason, they cannot be stored in identical directories
    // as in src (ex. file paths get to long on Windows)
    protected open val expectedResultsDirsMap: Map<String, File> = mapOf()
    protected val outputDir = createTempDir()

    override fun interceptSpec(context: Spec, spec: () -> Unit) {
        spec()
        outputDir.deleteRecursively()
    }

    override val oneInstancePerTest: Boolean
        get() = false

    init {
        "analyzer produces ABCD files for all .gradle files" {
            //FIXME:  Analyzer crashes on JAVA 9 with project below Gradle 4.3
            //        (Gradle issue: https://github.com/gradle/gradle/issues/3317)
            val downloadedDir = DownloaderMain.download(pkg, outputDir)
            val analyzerResultsDir = File(outputDir, "analyzer_results");
            AnalyzerMain.main(arrayOf("-i", downloadedDir.absolutePath, "-o", analyzerResultsDir.absolutePath))

            val sourceGradleProjectFiles = downloadedDir.walkTopDown().filter { file: File ->
                Gradle.matchersForDefinitionFiles.any() { glob ->
                    glob.matches(file.toPath())
                }
            }

            val expectedResult = sourceGradleProjectFiles.map {
                val abcdFileDir = it.absolutePath.substringBeforeLast(File.separator).replace(
                        oldValue = downloadedDir.absolutePath, newValue = analyzerResultsDir.absolutePath,
                        ignoreCase = true)
                abcdFileDir + File.separator + "build-gradle-dependencies.yml"
            }.toSet()
            val generatedResultFiles = analyzerResultsDir.walkTopDown().filter { it.extension == "yml" }.map {
                it.absolutePath
            }.toSet()
            generatedResultFiles shouldBe expectedResult

        }.config(tags = setOf(Expensive))

        "analyzer results for all .gradle files match expected"{
            expectedResultsDir shouldNotBe ""
            val analyzerResultsDir = File(outputDir, "analyzer_results/")
            val testRows = analyzerResultsDir.walkTopDown().asIterable().filter { file: File ->
                file.extension == "yml" //filter yml files
            }.map {
                val fileExpectedResultPath = expectedResultsDir + it.path.substringBeforeLast(
                        File.separator).substringAfterLast("analyzer_results").replace("\\",
                        "/") + "/" + it.name //keep as unix paths
                row(it, expectedResultsDirsMap.getOrDefault(fileExpectedResultPath, File(fileExpectedResultPath)))
            }
            val gradleTable = table(headers("analyzerOutputFile", "expectedResultFile"), *testRows.toTypedArray())

            forAll(gradleTable) { analyzerOutputFile, expectedResultFile ->
                val analyzerResults = analyzerOutputFile.readText().replaceFirst(
                        "vcs_revision:\\s*\"[^#\"]+\"".toRegex(), "vcs_revision: \"\"")
                val expectedResults = expectedResultFile.readText().replaceFirst(
                        "vcs_revision:\\s*\"[^#\"]+\"".toRegex(), "vcs_revision: \"\"")
                analyzerResults shouldBe expectedResults

            }
        }.config(tags = setOf(Expensive))
    }
}
