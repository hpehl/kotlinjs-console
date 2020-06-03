import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.DevServer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.js") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.70"
}

group = "org.wildfly.halos"
version = "0.0.1"

repositories {
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0")
    implementation("org.jetbrains:kotlin-react:16.13.0-pre.94-kotlin-1.3.70")
    implementation("org.jetbrains:kotlin-react-dom:16.13.0-pre.94-kotlin-1.3.70")
    implementation("org.jetbrains:kotlin-styled:1.0.0-pre.94-kotlin-1.3.70")
    implementation("io.github.microutils:kotlin-logging-js:1.7.9")

    implementation(npm("@patternfly/patternfly", "2.71.6"))
    implementation(npm("@patternfly/react-charts", "5.3.21"))
    implementation(npm("css-loader", "3.5.3"))
    implementation(npm("inline-style-prefixer", "6.0.0"))
    implementation(npm("file-loader", "6.0.0"))
    implementation(npm("react", "16.13.1"))
    implementation(npm("react-dom", "16.13.1"))
    implementation(npm("style-loader", "1.2.1"))
    implementation(npm("styled-components", "5.1.1"))
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

kotlin.target.browser {
    runTask {
        devServer = DevServer(
            port = 9999,
            contentBase = listOf("$buildDir/processedResources/Js/main")
        )
    }
}
