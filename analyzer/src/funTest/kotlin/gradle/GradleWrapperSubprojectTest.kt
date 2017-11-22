package com.here.ort.analyzer.integration

import com.here.ort.model.Package
import java.io.File

class GradleWrapperSubprojectTest : BaseGradleSpec() {
    override val pkg = Package(
            packageManager = "Gradle",
            namespace = "org.gradle",
            name = "wrapper",
            version = "",
            declaredLicenses = sortedSetOf(),
            description = "",
            homepageUrl = "",
            downloadUrl = "",
            vcsProvider = "Git",
            vcsUrl = "https://github.com/gradle/gradle.git",
            hashAlgorithm = "",
            hash = "",
            vcsRevision = "e4f4804807ef7c2829da51877861ff06e07e006d",
            vcsPath = "subprojects/wrapper/"
            )

    override val expectedResultsDir = "src/funTest/assets/projects/external/gradle-submodules"

    override val expectedResultsDirsMap = mapOf(
            "${expectedResultsDir}/subprojects/wrapper/wrapper-gradle-dependencies.yml"
                    to File("src/funTest/assets/projects/external/gradle-submodules/wrapper" +
                                    "/expected-wrapper-gradle-dependencies.yml")
    )
}
