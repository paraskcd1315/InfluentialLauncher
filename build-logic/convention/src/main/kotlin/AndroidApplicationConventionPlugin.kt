import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply("com.android.application")

        extensions.configure<ApplicationExtension> {
            compileSdk = InfluentialSdk.COMPILE
            defaultConfig {
                minSdk = InfluentialSdk.MIN
                targetSdk = InfluentialSdk.TARGET
                testInstrumentationRunner = InfluentialSdk.TEST_RUNNER
            }
            compileOptions {
                sourceCompatibility = InfluentialSdk.JAVA
                targetCompatibility = InfluentialSdk.JAVA
            }
        }
        configureKotlinAndroid()
    }
}
